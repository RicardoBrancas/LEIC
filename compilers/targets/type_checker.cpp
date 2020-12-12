#include <string>
#include <sstream>
#include <utility>
#include "targets/type_checker.h"
#include "ast/all.h"  // automatically generated

#define ASSERT_UNSPEC \
    { if (node->type() != nullptr && \
          node->type()->name() != basic_type::TYPE_UNSPEC) return; }


std::string error_msg(cdk::basic_node *node, const std::string &msg) {
  return (node ? node->label() : "nullptr") + ": " + msg;
}

bool compare_types(basic_type *t1, basic_type *t2) {
  if (t1 == nullptr || t2 == nullptr)
    return false;

  if (t1->_subtype && t2->_subtype) {
    return (compare_types(t1->_subtype, t2->_subtype)
            || t1->subtype()->name() == basic_type::TYPE_VOID
            || t2->subtype()->name() == basic_type::TYPE_VOID)
           && t1->name() == t2->name();

  } else if (!(t1->subtype()) && !(t2->subtype())) {
    return t1->name() == t2->name();

  } else return false;
}

std::string to_string(basic_type *type) {
  if (!type)
    return "nullptr";

  switch (type->name()) {
    case basic_type::TYPE_UNSPEC:
      return "internal_unspec";
    case basic_type::TYPE_INT:
      return "int";
    case basic_type::TYPE_DOUBLE:
      return "double";
    case basic_type::TYPE_STRING:
      return "string";
    case basic_type::TYPE_POINTER:
      return "*" + to_string(type->subtype());
    default:
      return "unknown_type";
  }
}

void gr8::type_checker::try_coalesce(basic_type *type, cdk::typed_node *node) {
  if (node->type()->name() == basic_type::TYPE_UNSPEC)
    node->type(type);
}

void gr8::type_checker::try_coalesce(cdk::typed_node *t1, cdk::typed_node *t2) {
  if (t1->type()->name() != basic_type::TYPE_UNSPEC
      && t2->type()->name() != basic_type::TYPE_UNSPEC)
    return;

  else if (t1->type()->name() == basic_type::TYPE_UNSPEC)
    try_coalesce(t2->type(), t1);

  else if (t2->type()->name() == basic_type::TYPE_UNSPEC)
    try_coalesce(t1->type(), t2);

  else
    throw error_msg(nullptr, "could not coalesce types");
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::do_block_node(gr8::block_node *const node, int lvl) {
  cdk::basic_node *last_n = nullptr;

  for (cdk::basic_node *n : node->instructions()->nodes()) {
    if (last_n)
      if (dynamic_cast<gr8::return_node *>(last_n) || dynamic_cast<gr8::again_node *>(last_n) ||
          dynamic_cast<gr8::stop_node *>(last_n))
        throw error_msg(last_n, "is not the last instruction in it's block!");

    last_n = n;
  }
}

void gr8::type_checker::do_sequence_node(cdk::sequence_node *const node, int lvl) {
}

// ---------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::do_indexation_node(gr8::indexation_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->pointer()->accept(this, lvl);
  node->position()->accept(this, lvl);

  if (node->pointer()->type()->name() != basic_type::TYPE_POINTER || !node->pointer()->type()->subtype())
    throw error_msg(node,
                    "Trying to index an expression that is not a valid pointer: " + to_string(node->pointer()->type()));

  if (node->position()->type()->name() != basic_type::TYPE_INT)
    throw error_msg(node, "Trying to index by offset that is not an integer: " + to_string(node->position()->type()));

  node->type(node->pointer()->type()->subtype());
}

void gr8::type_checker::do_address_of_node(gr8::address_of_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->lvalue()->accept(this, lvl);

  auto type = new basic_type(4, basic_type::TYPE_POINTER);
  type->_subtype = node->lvalue()->type();

  node->type(type);
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::do_stop_node(gr8::stop_node *const node, int lvl) {
  if (node->n() <= 0)
    throw error_msg(node, "invalid argument. Must be > 0.");
}

void gr8::type_checker::do_again_node(gr8::again_node *const node, int lvl) {
  if (node->n() <= 0)
    throw error_msg(node, "invalid argument. Must be > 0.");
}

void gr8::type_checker::do_return_node(gr8::return_node *const node, int lvl) {
  if (node->value() == nullptr) {
    if(_parent->current_function()->type()->name() != basic_type::TYPE_VOID)
      throw error_msg(node, "empty return on non-void function");

  } else {
    node->value()->accept(this, lvl);

    try_coalesce(_parent->current_function()->type(), node->value());

    if (!compare_types(_parent->current_function()->type(), node->value()->type())
        && !(_parent->current_function()->type()->name() == basic_type::TYPE_DOUBLE
             && node->value()->type()->name() == basic_type::TYPE_INT))
      throw error_msg(node, "expected: " + to_string(_parent->current_function()->type()) + "; found: " +
                            to_string(node->value()->type()));
  }
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::do_nil_node(cdk::nil_node *const node, int lvl) {
  // EMPTY
}

void gr8::type_checker::do_data_node(cdk::data_node *const node, int lvl) {
  // EMPTY
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::do_integer_node(cdk::integer_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(new basic_type(4, basic_type::TYPE_INT));
}

void gr8::type_checker::do_string_node(cdk::string_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(new basic_type(4, basic_type::TYPE_STRING));
}

void gr8::type_checker::do_double_node(cdk::double_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(new basic_type(8, basic_type::TYPE_DOUBLE));
}

void gr8::type_checker::do_null_node(gr8::null_node *const node, int lvl) {
  ASSERT_UNSPEC;
  auto *type = new basic_type(4, basic_type::TYPE_POINTER);
  type->_subtype = new basic_type(0, basic_type::TYPE_VOID);
  node->type(type);
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::do_neg_node(cdk::neg_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl + 2);

  try_coalesce(new basic_type(4, basic_type::TYPE_INT), node->argument());

  if (node->argument()->type()->name() != basic_type::TYPE_INT
      && node->argument()->type()->name() != basic_type::TYPE_DOUBLE)
    throw error_msg(node, "argument has invalid type: " + to_string(node->argument()->type()));

  node->type(node->argument()->type());
}

void gr8::type_checker::do_identity_node(gr8::identity_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl + 2);

  try_coalesce(new basic_type(4, basic_type::TYPE_INT), node->argument());

  if (node->argument()->type()->name() != basic_type::TYPE_INT
      && node->argument()->type()->name() != basic_type::TYPE_DOUBLE)
    throw error_msg(node, "argument has invalid type: " + to_string(node->argument()->type()));

  node->type(node->argument()->type());
}

void gr8::type_checker::do_not_node(cdk::not_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl + 2);

  try_coalesce(new basic_type(4, basic_type::TYPE_INT), node->argument());

  if (node->argument()->type()->name() != basic_type::TYPE_INT)
    throw error_msg(node, "argument has invalid type: " + to_string(node->argument()->type()));

  node->type(new basic_type(4, basic_type::TYPE_INT));
}

void gr8::type_checker::do_objects_node(gr8::objects_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->argument()->accept(this, lvl + 2);

  try_coalesce(new basic_type(4, basic_type::TYPE_INT), node->argument());

  if (node->argument()->type()->name() != basic_type::TYPE_INT)
    throw error_msg(node, "argument has invalid type: " + to_string(node->argument()->type()));

  node->type(new basic_type(4, basic_type::TYPE_UNSPEC));
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::processBinaryIntegerExpression(cdk::binary_expression_node *const node, int lvl) {
  ASSERT_UNSPEC;
  basic_type *type;

  node->left()->accept(this, lvl + 2);
  node->right()->accept(this, lvl + 2);
  try_coalesce(node->left(), node->right());
  basic_type::type l_type = node->left()->type()->name();
  basic_type::type r_type = node->right()->type()->name();

  if (l_type == basic_type::TYPE_INT && r_type == basic_type::TYPE_INT)
    type = new basic_type(4, basic_type::TYPE_INT);

  else
    throw error_msg(node,
                    "wrong types: (" + to_string(node->left()->type()) + ", " + to_string(node->right()->type()) + ")");

  node->type(type);
}

void gr8::type_checker::processBinaryComparisonExpression(cdk::binary_expression_node *const node, int lvl) {
  ASSERT_UNSPEC;
  basic_type *type;

  node->left()->accept(this, lvl + 2);
  node->right()->accept(this, lvl + 2);
  try_coalesce(node->left(), node->right());
  basic_type::type l_type = node->left()->type()->name();
  basic_type::type r_type = node->right()->type()->name();

  if (l_type == basic_type::TYPE_INT && r_type == basic_type::TYPE_INT)
    type = new basic_type(4, basic_type::TYPE_INT);

  else if (l_type == basic_type::TYPE_INT && r_type == basic_type::TYPE_DOUBLE)
    type = new basic_type(4, basic_type::TYPE_INT);

  else if (l_type == basic_type::TYPE_DOUBLE && r_type == basic_type::TYPE_INT)
    type = new basic_type(4, basic_type::TYPE_INT);

  else if (l_type == basic_type::TYPE_DOUBLE && r_type == basic_type::TYPE_DOUBLE)
    type = new basic_type(4, basic_type::TYPE_INT);

  else
    throw error_msg(node,
                    "wrong types: (" + to_string(node->left()->type()) + ", " + to_string(node->right()->type()) + ")");

  node->type(type);
}

void gr8::type_checker::processBinaryNumericExpression(cdk::binary_expression_node *const node, int lvl) {
  ASSERT_UNSPEC;
  basic_type *type;

  node->left()->accept(this, lvl + 2);
  node->right()->accept(this, lvl + 2);
  try_coalesce(node->left(), node->right());
  basic_type::type l_type = node->left()->type()->name();
  basic_type::type r_type = node->right()->type()->name();

  if (l_type == basic_type::TYPE_INT && r_type == basic_type::TYPE_INT)
    type = new basic_type(4, basic_type::TYPE_INT);

  else if (l_type == basic_type::TYPE_INT && r_type == basic_type::TYPE_DOUBLE)
    type = new basic_type(8, basic_type::TYPE_DOUBLE);

  else if (l_type == basic_type::TYPE_DOUBLE && r_type == basic_type::TYPE_INT)
    type = new basic_type(8, basic_type::TYPE_DOUBLE);

  else if (l_type == basic_type::TYPE_DOUBLE && r_type == basic_type::TYPE_DOUBLE)
    type = new basic_type(8, basic_type::TYPE_DOUBLE);

  else
    throw error_msg(node,
                    "wrong types: (" + to_string(node->left()->type()) + ", " + to_string(node->right()->type()) + ")");

  node->type(type);
}

void gr8::type_checker::do_add_node(cdk::add_node *const node, int lvl) {
  ASSERT_UNSPEC;
  basic_type *type;

  node->left()->accept(this, lvl + 2);
  node->right()->accept(this, lvl + 2);
  try_coalesce(node->left(), node->right());
  basic_type::type l_type = node->left()->type()->name();
  basic_type::type r_type = node->right()->type()->name();

  if (l_type == basic_type::TYPE_POINTER && r_type == basic_type::TYPE_INT)
    type = node->left()->type();

  else if (l_type == basic_type::TYPE_INT && r_type == basic_type::TYPE_POINTER)
    type = node->right()->type();

  else if (l_type == basic_type::TYPE_INT && r_type == basic_type::TYPE_INT)
    type = new basic_type(4, basic_type::TYPE_INT);

  else if (l_type == basic_type::TYPE_INT && r_type == basic_type::TYPE_DOUBLE)
    type = new basic_type(8, basic_type::TYPE_DOUBLE);

  else if (l_type == basic_type::TYPE_DOUBLE && r_type == basic_type::TYPE_INT)
    type = new basic_type(8, basic_type::TYPE_DOUBLE);

  else if (l_type == basic_type::TYPE_DOUBLE && r_type == basic_type::TYPE_DOUBLE)
    type = new basic_type(8, basic_type::TYPE_DOUBLE);

  else
    throw error_msg(node,
                    "wrong types: (" + to_string(node->left()->type()) + ", " + to_string(node->right()->type()) + ")");

  node->type(type);
}

void gr8::type_checker::do_sub_node(cdk::sub_node *const node, int lvl) {
  ASSERT_UNSPEC;
  basic_type *type;

  node->left()->accept(this, lvl + 2);
  node->right()->accept(this, lvl + 2);
  try_coalesce(node->left(), node->right());
  basic_type::type l_type = node->left()->type()->name();
  basic_type::type r_type = node->right()->type()->name();

  if (compare_types(node->left()->type(), node->right()->type()))
    type = new basic_type(4, basic_type::TYPE_INT);

  else if (l_type == basic_type::TYPE_POINTER && r_type == basic_type::TYPE_INT)
    type = node->left()->type();

  else if (l_type == basic_type::TYPE_INT && r_type == basic_type::TYPE_INT)
    type = new basic_type(4, basic_type::TYPE_INT);

  else if (l_type == basic_type::TYPE_INT && r_type == basic_type::TYPE_DOUBLE)
    type = new basic_type(8, basic_type::TYPE_DOUBLE);

  else if (l_type == basic_type::TYPE_DOUBLE && r_type == basic_type::TYPE_INT)
    type = new basic_type(8, basic_type::TYPE_DOUBLE);

  else if (l_type == basic_type::TYPE_DOUBLE && r_type == basic_type::TYPE_DOUBLE)
    type = new basic_type(8, basic_type::TYPE_DOUBLE);

  else
    throw error_msg(node,
                    "wrong types: (" + to_string(node->left()->type()) + ", " + to_string(node->right()->type()) + ")");

  node->type(type);
}

void gr8::type_checker::do_mul_node(cdk::mul_node *const node, int lvl) {
  processBinaryNumericExpression(node, lvl);
}

void gr8::type_checker::do_div_node(cdk::div_node *const node, int lvl) {
  processBinaryNumericExpression(node, lvl);
}

void gr8::type_checker::do_mod_node(cdk::mod_node *const node, int lvl) {
  processBinaryIntegerExpression(node, lvl);
}

void gr8::type_checker::do_lt_node(cdk::lt_node *const node, int lvl) {
  processBinaryComparisonExpression(node, lvl);
}

void gr8::type_checker::do_le_node(cdk::le_node *const node, int lvl) {
  throw error_msg(node, "should not happen in gr8");
}

void gr8::type_checker::do_ge_node(cdk::ge_node *const node, int lvl) {
  throw error_msg(node, "should not happen in gr8");
}

void gr8::type_checker::do_gt_node(cdk::gt_node *const node, int lvl) {
  processBinaryComparisonExpression(node, lvl);
}

void gr8::type_checker::do_ne_node(cdk::ne_node *const node, int lvl) {
  throw error_msg(node, "should not happen in gr8");
}

void gr8::type_checker::do_eq_node(cdk::eq_node *const node, int lvl) {
  ASSERT_UNSPEC;
  basic_type *type;

  node->left()->accept(this, lvl + 2);
  node->right()->accept(this, lvl + 2);
  try_coalesce(node->left(), node->right());
  basic_type::type l_type = node->left()->type()->name();
  basic_type::type r_type = node->right()->type()->name();

  if (compare_types(node->left()->type(), node->right()->type()))
    type = new basic_type(4, basic_type::TYPE_INT);

  else if (l_type == basic_type::TYPE_INT && r_type == basic_type::TYPE_INT)
    type = new basic_type(4, basic_type::TYPE_INT);

  else if (l_type == basic_type::TYPE_INT && r_type == basic_type::TYPE_DOUBLE)
    type = new basic_type(4, basic_type::TYPE_INT);

  else if (l_type == basic_type::TYPE_DOUBLE && r_type == basic_type::TYPE_INT)
    type = new basic_type(4, basic_type::TYPE_INT);

  else if (l_type == basic_type::TYPE_DOUBLE && r_type == basic_type::TYPE_DOUBLE)
    type = new basic_type(4, basic_type::TYPE_INT);

  else
    throw error_msg(node,
                    "wrong types: (" + to_string(node->left()->type()) + ", " + to_string(node->right()->type()) + ")");

  node->type(type);
}

void gr8::type_checker::do_and_node(cdk::and_node *const node, int lvl) {
  processBinaryIntegerExpression(node, lvl);
}

void gr8::type_checker::do_or_node(cdk::or_node *const node, int lvl) {
  processBinaryIntegerExpression(node, lvl);
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::do_identifier_node(cdk::identifier_node *const node, int lvl) {
  ASSERT_UNSPEC;
  const std::string &id = node->name();
  auto symbol = _symtab.find(id);

  if (symbol)
    node->type(symbol->type());

  else
    throw error_msg(node, "invalid identifier '" + id + "'");
}

void gr8::type_checker::do_rvalue_node(cdk::rvalue_node *const node, int lvl) {
  ASSERT_UNSPEC;
  try {
    node->lvalue()->accept(this, lvl);
    node->type(node->lvalue()->type());

  } catch (const std::string &msg) {
    throw error_msg(node, "invalid lvalue:\n\t" + msg);
  }
}

void gr8::type_checker::do_assignment_node(cdk::assignment_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->lvalue()->accept(this, lvl);
  node->rvalue()->accept(this, lvl + 2);

  try_coalesce(node->lvalue(), node->rvalue());

  if (!compare_types(node->lvalue()->type(), node->rvalue()->type())
      && !(node->lvalue()->type()->name() == basic_type::TYPE_DOUBLE &&
           node->rvalue()->type()->name() == basic_type::TYPE_INT))
    throw error_msg(node, "incompatible types: (" + to_string(node->lvalue()->type()) + ", " +
                          to_string(node->rvalue()->type()) + ")");

  node->type(node->lvalue()->type());
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::do_function_definition_node(gr8::function_definition_node *const node, int lvl) {
  auto symbol = std::make_shared<gr8::symbol>(node);
  if (!_symtab.insert(node->name(), symbol)) {
    if (_symtab.find(node->name())->defined())
      throw error_msg(node, "redefined symbol!");
    else
      _symtab.replace(node->name(), symbol);
  }

  _symtab.push(); //fixme weird push location

  int offset = 8;
  for (auto n : node->variables()->nodes()) {
    auto var = dynamic_cast<gr8::variable_declaration_node *>(n);
    auto s = std::make_shared<gr8::symbol>(var);

    symbol->add_argument(var->type());
    s->offset(offset);
    _symtab.insert(var->name(), s);
    offset += var->type()->size();
  }

  _parent->set_current_function(symbol);
  _parent->current_function()->offset(0);
}

void gr8::type_checker::do_function_declaration_node(gr8::function_declaration_node *const node, int lvl) {
  auto symbol = std::make_shared<gr8::symbol>(node);
  _symtab.insert(node->name(), symbol); //re-declaration ignored

  for (auto n : node->variables()->nodes()) {
    auto var = dynamic_cast<gr8::variable_declaration_node *>(n);
    symbol->add_argument(var->type());
  }
}

void gr8::type_checker::do_function_call_node(gr8::function_call_node *const node, int lvl) {
  ASSERT_UNSPEC;
  auto function = _symtab.find(node->name());

  if (!function)
    throw error_msg(node, "unknown function '" + node->name() + "'");

  if (!function->is_function())
    throw error_msg(node, "identifier is a variable; not a function.");

  auto actual_it = node->arguments()->nodes().begin();
  auto formal_it = function->arguments().begin();
  for (; formal_it != function->arguments().end(); ++actual_it, ++formal_it) {
    if (actual_it == node->arguments()->nodes().end() || !(*actual_it))
      throw error_msg(node, "too few arguments");

    (*actual_it)->accept(this, lvl);
    auto actual_expr = dynamic_cast<cdk::expression_node *>(*actual_it);
    try_coalesce(*formal_it, actual_expr);
    if (!compare_types(actual_expr->type(), *formal_it) &&
        !(actual_expr->type()->name() == basic_type::TYPE_INT && (*formal_it)->name() == basic_type::TYPE_DOUBLE))
      throw error_msg(node, "expected: " + to_string(*formal_it) + "; found: " + to_string(actual_expr->type()));
  }

  if (actual_it != node->arguments()->nodes().end())
    throw error_msg(node, "too many arguments");

  node->type(function->type());
}

void gr8::type_checker::do_variable_declaration_node(gr8::variable_declaration_node *const node, int lvl) {
  auto symbol = std::make_shared<gr8::symbol>(node);
  if (!_symtab.insert(node->name(), symbol))
    throw error_msg(node, "re-declared symbol!");

  if (_parent->current_function())
    if (node->global() || node->is_public() || node->is_extern())
      throw error_msg(node, "illegal qualifiers");

  if (!node->is_extern()) {
    if (!node->global()) {
      _parent->current_function()->offset(_parent->current_function()->offset() - (int) node->type()->size());
      symbol->offset(_parent->current_function()->offset());
    }

    if (node->initial()) {
      node->initial()->accept(this, lvl);

      try_coalesce(node, node->initial());

      if (!compare_types(node->type(), node->initial()->type()) &&
          !(node->initial()->type()->name() == basic_type::TYPE_INT && node->type()->name() == basic_type::TYPE_DOUBLE))
        throw error_msg(node, "invalid type of initial expression: " + to_string(node->type()));

      if (node->global()) {
        if (node->type()->name() == basic_type::TYPE_INT &&
            !dynamic_cast<cdk::literal_node<int> *>(node->initial()))
          throw error_msg(node, "int global variable initialized with non literal expression");

        else if (node->type()->name() == basic_type::TYPE_DOUBLE &&
                 !dynamic_cast<cdk::literal_node<int> *>(node->initial()) &&
                 !dynamic_cast<cdk::literal_node<double> *>(node->initial()))
          throw error_msg(node, "double global variable initialized with non literal expression");

        else if (node->type()->name() == basic_type::TYPE_STRING &&
                 !dynamic_cast<cdk::literal_node<std::string> *>(node->initial()))
          throw error_msg(node, "string global variable initialized with non literal expression");

        else if (node->type()->name() == basic_type::TYPE_POINTER &&
                 !dynamic_cast<gr8::null_node *>(node->initial()))
          throw error_msg(node, "pointer global variable initialized with non literal expression");

      }
    }
  } else {
    if (node->initial())
      throw error_msg(node, "extern variables can't be initialized");
  }
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::do_evaluation_node(gr8::evaluation_node *const node, int lvl) {
  node->argument()->accept(this, lvl + 2);

  if (node->argument()->type() == nullptr || node->argument()->type()->name() == basic_type::TYPE_UNSPEC)
    throw error_msg(node, "invalid expression type: " + to_string(node->argument()->type()));
}

void gr8::type_checker::do_post_node(gr8::post_node *const node, int lvl) {
  node->argument()->accept(this, lvl + 2);

  try_coalesce(new basic_type(4, basic_type::TYPE_INT), node->argument());

  if (node->argument()->type()->name() != basic_type::TYPE_INT
      && node->argument()->type()->name() != basic_type::TYPE_DOUBLE
      && node->argument()->type()->name() != basic_type::TYPE_STRING)
    throw error_msg(node, "illegal type: " + to_string(node->argument()->type()));
}

void gr8::type_checker::do_tweet_node(gr8::tweet_node *const node, int lvl) {
  node->argument()->accept(this, lvl + 2);

  try_coalesce(new basic_type(4, basic_type::TYPE_INT), node->argument());

  if (node->argument()->type()->name() != basic_type::TYPE_INT
      && node->argument()->type()->name() != basic_type::TYPE_DOUBLE
      && node->argument()->type()->name() != basic_type::TYPE_STRING)
    throw error_msg(node, "illegal type: " + to_string(node->argument()->type()));
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::do_read_node(gr8::read_node *const node, int lvl) {
  ASSERT_UNSPEC;
  node->type(new basic_type(0, basic_type::TYPE_UNSPEC));
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::do_sweep_node(gr8::sweep_node *const node, int lvl) {
  try {
    node->lvalue()->accept(this, lvl);
  } catch (const std::string &msg) {
    throw error_msg(node, "invalid lvalue:\n\t" + msg);
  }
  node->from()->accept(this, lvl);
  node->to()->accept(this, lvl);
  node->by()->accept(this, lvl);

  auto l_type = node->lvalue()->type()->name(),
      f_type = node->from()->type()->name(),
      t_type = node->to()->type()->name(),
      b_type = node->by()->type()->name();

  if (l_type == basic_type::TYPE_INT) {

    if (f_type != basic_type::TYPE_INT || t_type != basic_type::TYPE_INT || b_type != basic_type::TYPE_INT)
      throw error_msg(node, "expected int");

  } else if (l_type == basic_type::TYPE_DOUBLE) {

    if (f_type != basic_type::TYPE_INT && f_type != basic_type::TYPE_DOUBLE)
      throw error_msg(node, "expected int or double; found: " + to_string(node->from()->type()));

    if (t_type != basic_type::TYPE_INT && t_type != basic_type::TYPE_DOUBLE)
      throw error_msg(node, "expected int or double; found: " + to_string(node->to()->type()));

    if (b_type != basic_type::TYPE_INT && b_type != basic_type::TYPE_DOUBLE)
      throw error_msg(node, "expected int or double; found: " + to_string(node->by()->type()));

  } else if (l_type == basic_type::TYPE_POINTER) {

    if (!compare_types(node->lvalue()->type(), node->from()->type()))
      throw error_msg(node,
                      "expected: " + to_string(node->lvalue()->type()) + "; found: " + to_string(node->from()->type()));

    if (!compare_types(node->lvalue()->type(), node->to()->type()))
      throw error_msg(node,
                      "expected: " + to_string(node->lvalue()->type()) + "; found: " + to_string(node->to()->type()));

    if (b_type != basic_type::TYPE_INT)
      throw error_msg(node, "expected: int; found: " + to_string(node->from()->type()));

  } else {
    throw error_msg(node, "invalid lvalue type:" + to_string(node->lvalue()->type()));
  }
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::type_checker::do_if_node(gr8::if_node *const node, int lvl) {
  node->condition()->accept(this, lvl + 4);

  try_coalesce(new basic_type(4, basic_type::TYPE_INT), node->condition());

  if (node->condition()->type()->name() != basic_type::TYPE_INT)
    throw error_msg(node, "illegal type: " + to_string(node->condition()->type()));
}

void gr8::type_checker::do_if_else_node(gr8::if_else_node *const node, int lvl) {
  node->condition()->accept(this, lvl + 4);

  try_coalesce(new basic_type(4, basic_type::TYPE_INT), node->condition());

  if (node->condition()->type()->name() != basic_type::TYPE_INT)
    throw error_msg(node, "illegal type: " + to_string(node->condition()->type()));
}
