#include <string>
#include <sstream>
#include "cdk/basic_type.h"
#include "targets/type_checker.h"
#include "targets/function_peeker.h"
#include "targets/postfix_writer.h"
#include "ast/all.h"  // all.h is automatically generated

std::string convert_name(const std::string &name) {
  if (name == "covfefe")
    return "_main";
  else if (name == "printi")
    return "_printi";
  else if (name == "println")
    return "_println";
  else if (name == "printd")
    return "_printd";
  else if (name == "prints")
    return "_prints";
  else if (name == "readi")
    return "_readi";
  else if (name == "readd")
    return "_readd";
  else
    return name;
}

void gr8::postfix_writer::do_block_node(gr8::block_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  _symtab.push();
  node->declarations()->accept(this, lvl);
  node->instructions()->accept(this, lvl);
  _symtab.pop();
}

void gr8::postfix_writer::do_sequence_node(cdk::sequence_node *const node, int lvl) {
  for (auto n : node->nodes()) {
    n->accept(this, lvl);
  }
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::postfix_writer::do_indexation_node(gr8::indexation_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  node->pointer()->accept(this, lvl);                        // $ ADDR
  node->position()->accept(this, lvl);                       // $ ADDR     N_OFFSET
  _pf.INT((int) node->pointer()->type()->subtype()->size()); // $ ADDR     N_OFFSET    MULTIPLIER
  _pf.MUL();                                                 // $ ADDR     BYTE_OFFSET
  _pf.ADD();                                                 // $ NEW_ADDR
}

void gr8::postfix_writer::do_address_of_node(gr8::address_of_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->lvalue()->accept(this, lvl);
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::postfix_writer::do_stop_node(gr8::stop_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  _pf.JMP(mklbl(_stop_labels[_stop_labels.size() - node->n()]));
}

void gr8::postfix_writer::do_again_node(gr8::again_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  _pf.JMP(mklbl(_again_labels[_again_labels.size() - node->n()]));
}

void gr8::postfix_writer::do_return_node(gr8::return_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  if (node->value() != nullptr) {
    node->value()->accept(this, lvl);

    if (node->value()->type()->name() == basic_type::TYPE_INT &&
        current_function()->type()->name() == basic_type::TYPE_DOUBLE)
      _pf.I2D();

    if (current_function()->type()->size() == 4)
      _pf.STFVAL32();

    else if (current_function()->type()->size() == 8)
      _pf.STFVAL64();
  }
  _pf.LEAVE();
  _pf.RET();
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::postfix_writer::do_nil_node(cdk::nil_node *const node, int lvl) {
  // EMPTY
}

void gr8::postfix_writer::do_data_node(cdk::data_node *const node, int lvl) {
  // EMPTY
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::postfix_writer::do_integer_node(cdk::integer_node *const node, int lvl) {
  _pf.INT(node->value()); // push an integer
}

void gr8::postfix_writer::do_string_node(cdk::string_node *const node, int lvl) {
  int lbl1;

  _pf.RODATA();
  _pf.ALIGN();
  _pf.LABEL(mklbl(lbl1 = ++_lbl));
  _pf.SSTRING(node->value());

  _pf.TEXT();
  _pf.ALIGN();
  _pf.ADDR(mklbl(lbl1));
}

void gr8::postfix_writer::do_double_node(cdk::double_node *const node, int lvl) {
  _pf.DOUBLE(node->value());
}

void gr8::postfix_writer::do_null_node(gr8::null_node *const node, int lvl) {
  _pf.INT(0);
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::postfix_writer::do_neg_node(cdk::neg_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl); // determine the value
  if (node->type()->name() == basic_type::TYPE_INT)
    _pf.NEG(); // 2-complement

  else if (node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.DNEG();
}

void gr8::postfix_writer::do_identity_node(gr8::identity_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);
}

void gr8::postfix_writer::do_not_node(cdk::not_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  int l_else, l_end;
  node->argument()->accept(this, lvl);
  _pf.JZ(mklbl(l_else = ++_lbl));
  _pf.INT(0);
  _pf.JMP(mklbl(l_end = ++_lbl));
  _pf.LABEL(mklbl(l_else));
  _pf.INT(1);
  _pf.LABEL(mklbl(l_end));
}

void gr8::postfix_writer::do_objects_node(gr8::objects_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);
  _pf.INT((int) node->type()->subtype()->size());
  _pf.MUL();
  _pf.ALLOC();
  _pf.SP();
}

//----------------------------------------------------------------------------------------------------------------------

void gr8::postfix_writer::do_add_node(cdk::add_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  if (node->type()->name() != basic_type::TYPE_POINTER) {
    node->left()->accept(this, lvl);
    if (node->left()->type()->name() == basic_type::TYPE_INT && node->type()->name() == basic_type::TYPE_DOUBLE)
      _pf.I2D();
    node->right()->accept(this, lvl);
    if (node->right()->type()->name() == basic_type::TYPE_INT && node->type()->name() == basic_type::TYPE_DOUBLE)
      _pf.I2D();

    if (node->type()->name() == basic_type::TYPE_INT)
      _pf.ADD();

    else if (node->type()->name() == basic_type::TYPE_DOUBLE)
      _pf.DADD();

  } else {

    node->left()->accept(this, lvl);
    if (node->left()->type()->name() == basic_type::TYPE_INT) {
      _pf.INT((int) node->type()->subtype()->size());
      _pf.MUL();
    }

    node->right()->accept(this, lvl);
    if (node->right()->type()->name() == basic_type::TYPE_INT) {
      _pf.INT((int) node->type()->subtype()->size());
      _pf.MUL();
    }

    _pf.ADD();
  }
}

void gr8::postfix_writer::do_sub_node(cdk::sub_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  if (node->left()->type()->name() == basic_type::TYPE_INT && node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();
  node->right()->accept(this, lvl);
  if (node->right()->type()->name() == basic_type::TYPE_INT && node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();

  if (node->right()->type()->name() == basic_type::TYPE_POINTER) { // no need to check if left is pointer, as they both must be
    auto label = ++_lbl;

    _pf.SUB();
    _pf.DUP32();
    _pf.INT(0);
    _pf.LT();
    _pf.JZ(mklbl(label));
    _pf.NEG();
    _pf.LABEL(mklbl(label));
    _pf.INT((int) node->left()->type()->subtype()->size());
    _pf.DIV();

  } else {

    if (node->type()->name() == basic_type::TYPE_INT)
      _pf.SUB();

    else if (node->type()->name() == basic_type::TYPE_DOUBLE)
      _pf.DSUB();

    else if (node->type()->name() == basic_type::TYPE_POINTER) {
      _pf.INT((int) node->type()->subtype()->size());
      _pf.MUL();
      _pf.SUB();
    }

  }
}

void gr8::postfix_writer::do_mul_node(cdk::mul_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  if (node->left()->type()->name() == basic_type::TYPE_INT && node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();
  node->right()->accept(this, lvl);
  if (node->right()->type()->name() == basic_type::TYPE_INT && node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();

  if (node->type()->name() == basic_type::TYPE_INT)
    _pf.MUL();

  else if (node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.DMUL();
}

void gr8::postfix_writer::do_div_node(cdk::div_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  if (node->left()->type()->name() == basic_type::TYPE_INT && node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();
  node->right()->accept(this, lvl);
  if (node->right()->type()->name() == basic_type::TYPE_INT && node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();

  if (node->type()->name() == basic_type::TYPE_INT)
    _pf.DIV();

  else if (node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.DDIV();
}

void gr8::postfix_writer::do_mod_node(cdk::mod_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  node->right()->accept(this, lvl);
  _pf.MOD();
}

void gr8::postfix_writer::do_lt_node(cdk::lt_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  if (node->left()->type()->name() == basic_type::TYPE_INT && node->right()->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();
  node->right()->accept(this, lvl);
  if (node->right()->type()->name() == basic_type::TYPE_INT && node->left()->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();

  if (node->left()->type()->name() == basic_type::TYPE_DOUBLE ||
      node->right()->type()->name() == basic_type::TYPE_DOUBLE) {
    _pf.DCMP();
    _pf.INT(0);
  }
  _pf.LT();
}

void gr8::postfix_writer::do_le_node(cdk::le_node *const node, int lvl) {
  error(node, "should not happen", true);
}

void gr8::postfix_writer::do_ge_node(cdk::ge_node *const node, int lvl) {
  error(node, "should not happen", true);
}

void gr8::postfix_writer::do_gt_node(cdk::gt_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  if (node->left()->type()->name() == basic_type::TYPE_INT && node->right()->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();
  node->right()->accept(this, lvl);
  if (node->right()->type()->name() == basic_type::TYPE_INT && node->left()->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();

  if (node->left()->type()->name() == basic_type::TYPE_DOUBLE ||
      node->right()->type()->name() == basic_type::TYPE_DOUBLE) {
    _pf.DCMP();
    _pf.INT(0);
  }
  _pf.GT();
}

void gr8::postfix_writer::do_ne_node(cdk::ne_node *const node, int lvl) {
  error(node, "should not happen", true);
}

void gr8::postfix_writer::do_eq_node(cdk::eq_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->left()->accept(this, lvl);
  if (node->left()->type()->name() == basic_type::TYPE_INT && node->right()->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();
  node->right()->accept(this, lvl);
  if (node->right()->type()->name() == basic_type::TYPE_INT && node->left()->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();

  if (node->left()->type()->name() == basic_type::TYPE_DOUBLE ||
      node->right()->type()->name() == basic_type::TYPE_DOUBLE) {
    _pf.DCMP();
    _pf.INT(0);
  }

  _pf.EQ();
}

void gr8::postfix_writer::do_and_node(cdk::and_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int l_end = ++_lbl;

  node->left()->accept(this, lvl);
  _pf.DUP32();
  _pf.JZ(mklbl(l_end));
  node->right()->accept(this, lvl);
  _pf.AND();
  _pf.LABEL(mklbl(l_end));
}

void gr8::postfix_writer::do_or_node(cdk::or_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int l_end;

  node->left()->accept(this, lvl);
  _pf.DUP32();
  _pf.JNZ(mklbl(l_end = ++_lbl));
  node->right()->accept(this, lvl);
  _pf.OR();
  _pf.LABEL(mklbl(l_end));
}

//---------------------------------------------------------------------------

void gr8::postfix_writer::do_identifier_node(cdk::identifier_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  auto symbol = _symtab.find(node->name());

  if (symbol->global())
    _pf.ADDR(node->name());

  else
    _pf.LOCAL(symbol->offset());
}

void gr8::postfix_writer::do_rvalue_node(cdk::rvalue_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->lvalue()->accept(this, lvl);

  switch (node->type()->name()) {
    case basic_type::TYPE_INT:
    case basic_type::TYPE_POINTER:
    case basic_type::TYPE_STRING:
      _pf.LDINT();
      break;

    case basic_type::TYPE_DOUBLE:
      _pf.LDDOUBLE();
      break;
  }
}

void gr8::postfix_writer::do_assignment_node(cdk::assignment_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->rvalue()->accept(this, lvl); // determine the new value
  if (node->rvalue()->type()->name() == basic_type::TYPE_INT && node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.I2D();
  node->lvalue()->accept(this, lvl); // determine the address

  basic_type::type t = node->type()->name();
  if (t == basic_type::TYPE_INT || t == basic_type::TYPE_POINTER || t == basic_type::TYPE_STRING)
    _pf.STINT(); // store the value at address

  else if (t == basic_type::TYPE_DOUBLE)
    _pf.STDOUBLE();
}

//---------------------------------------------------------------------------

void gr8::postfix_writer::do_function_definition_node(gr8::function_definition_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  COMPUTE_STACK_SIZE;

  auto f = _undefined_functions.find(node->name());
  if (f != _undefined_functions.end())
    _undefined_functions.erase(f);

  _pf.TEXT();
  _pf.ALIGN();

  if (node->is_public())
    _pf.GLOBAL(convert_name(node->name()), _pf.FUNC());

  _pf.LABEL(convert_name(node->name()));
  _pf.ENTER(_symtab.find(node->name())->stack_size());

  node->block()->accept(this, lvl);

  _pf.LEAVE();
  _pf.RET();

  if (node->name() == "covfefe") { //only include this in main function as linker wouldn't work anyway without _main
    _pf.EXTERN("readi");
    _pf.EXTERN("readd");
    _pf.EXTERN("printi");
    _pf.EXTERN("prints");
    _pf.EXTERN("printd");
    _pf.EXTERN("println");
  }

  _symtab.pop(); // match to the other weirdly placed symtab push
}

void gr8::postfix_writer::do_function_declaration_node(gr8::function_declaration_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  // just check types
}

void gr8::postfix_writer::do_function_call_node(gr8::function_call_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  auto symbol = _symtab.find(node->name());

  if (!symbol->defined())
    _undefined_functions.insert(node->name());

  auto actual_it = node->arguments()->nodes().rbegin();
  auto formal_it = symbol->arguments().rbegin();
  while (actual_it != node->arguments()->nodes().rend() && formal_it != symbol->arguments().rend()) {
    if (*actual_it != nullptr && *formal_it != nullptr) {
      (*actual_it)->accept(this, lvl);
      auto actual_expr = dynamic_cast<cdk::expression_node *>(*actual_it);
      if (actual_expr->type()->name() == basic_type::TYPE_INT && (*formal_it)->name() == basic_type::TYPE_DOUBLE)
        _pf.I2D();
    }

    ++actual_it;
    ++formal_it;
  }

  _pf.CALL(convert_name(node->name()));

  int trash = 0;
  for (auto it = node->arguments()->nodes().rbegin(); it != node->arguments()->nodes().rend(); ++it) {
    trash += (int) dynamic_cast<cdk::typed_node *>(*it)->type()->size();
  }
  _pf.TRASH(trash);

  if (node->type()->name() == basic_type::TYPE_INT || node->type()->name() == basic_type::TYPE_POINTER ||
      node->type()->name() == basic_type::TYPE_STRING)
    _pf.LDFVAL32();

  else if (node->type()->name() == basic_type::TYPE_DOUBLE)
    _pf.LDFVAL64();
}

void gr8::postfix_writer::do_variable_declaration_node(gr8::variable_declaration_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  if (node->is_extern()) {
    _pf.EXTERN(node->name());

  } else if (node->global()) {

    switch (node->type()->name()) {
      case basic_type::TYPE_INT: {
        auto initial = dynamic_cast<cdk::literal_node<int> *>(node->initial());
        _pf.DATA();
        _pf.ALIGN();
        _pf.LABEL(node->name());
        _pf.SINT(initial ? initial->value() : 0);
      }
        break;

      case basic_type::TYPE_DOUBLE: {
        _pf.DATA();
        _pf.ALIGN();
        _pf.LABEL(node->name());
        if (auto initial = dynamic_cast<cdk::double_node *>(node->initial())) {
          _pf.SDOUBLE(initial->value());
        } else if (auto initial = dynamic_cast<cdk::integer_node *>(node->initial())) {
          _pf.SDOUBLE(initial->value());
        } else
          _pf.SDOUBLE(0);
      }
        break;

      case basic_type::TYPE_STRING: {
        auto initial = dynamic_cast<cdk::string_node *>(node->initial());
        int lbl;
        _pf.RODATA();
        _pf.ALIGN();
        _pf.LABEL(mklbl(lbl = ++_lbl));
        _pf.SSTRING(initial ? initial->value() : "");
        _pf.DATA();
        _pf.ALIGN();
        _pf.LABEL(node->name());
        _pf.SADDR(mklbl(lbl));
      }
        break;

      case basic_type::TYPE_POINTER:
        _pf.DATA();
        _pf.ALIGN();
        _pf.LABEL(node->name());
        _pf.SINT(0);
        break;
    }

    _pf.TEXT(); // return to the TEXT segment
  } else {
    if (node->initial() != nullptr) {
      node->initial()->accept(this, lvl);
      if(node->initial()->type()->name() == basic_type::TYPE_INT && node->type()->name() == basic_type::TYPE_DOUBLE)
        _pf.I2D();

      _pf.LOCAL(_symtab.find(node->name())->offset());
      if (node->type()->name() == basic_type::TYPE_INT || node->type()->name() == basic_type::TYPE_STRING ||
          node->type()->name() == basic_type::TYPE_POINTER)
        _pf.STINT();

      else if (node->type()->name() == basic_type::TYPE_DOUBLE)
        _pf.STDOUBLE();
    }
  }
}

//---------------------------------------------------------------------------

void gr8::postfix_writer::do_evaluation_node(gr8::evaluation_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);
  _pf.TRASH((int) node->argument()->type()->size());
}

void gr8::postfix_writer::do_post_node(gr8::post_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);

  switch (node->argument()->type()->name()) {
    case basic_type::TYPE_INT:
      _pf.CALL("printi");
      _pf.TRASH(4);
      break;

    case basic_type::TYPE_STRING:
      _pf.CALL("prints");
      _pf.TRASH(4);
      break;

    case basic_type::TYPE_DOUBLE:
      _pf.CALL("printd");
      _pf.TRASH(8);
      break;
  }

  _pf.CALL("println"); // print a newline
}

void gr8::postfix_writer::do_tweet_node(gr8::tweet_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  node->argument()->accept(this, lvl);

  switch (node->argument()->type()->name()) {
    case basic_type::TYPE_INT:
      _pf.CALL("printi");
      _pf.TRASH(4);
      break;

    case basic_type::TYPE_STRING:
      _pf.CALL("prints");
      _pf.TRASH(4);
      break;

    case basic_type::TYPE_DOUBLE:
      _pf.CALL("printd");
      _pf.TRASH(8);
      break;
  }
}

//---------------------------------------------------------------------------

void gr8::postfix_writer::do_read_node(gr8::read_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  switch(node->type()->name()) {
    case basic_type::TYPE_INT:
      _pf.CALL("readi");
      _pf.LDFVAL32();
      break;

    case basic_type::TYPE_DOUBLE:
      _pf.CALL("readd");
      _pf.LDFVAL64();
      break;
  }
}

//---------------------------------------------------------------------------


void gr8::postfix_writer::do_sweep_node(gr8::sweep_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;

  int l_test = ++_lbl, l_end = ++_lbl, l_incr = ++_lbl;
  _again_labels.push_back(l_incr);
  _stop_labels.push_back(l_end);

  if (node->lvalue()->type()->name() == basic_type::TYPE_INT ||
      node->lvalue()->type()->name() == basic_type::TYPE_POINTER) {
    node->from()->accept(this, lvl);
    node->lvalue()->accept(this, lvl);
    _pf.STINT();
    _pf.LABEL(mklbl(l_test));
    node->to()->accept(this, lvl);         // $ TO
    node->lvalue()->accept(this, lvl);     // $ TO  ADDR
    _pf.LDINT();                           // $ TO [ADDR]
    _pf.SUB();                             // $ TO-[ADDR]
    node->by()->accept(this, lvl);         // $ TO-[ADDR] BY
    _pf.MUL();                             // $ (TO-[ADDR])*BY
    _pf.INT(0);                            // $ (TO-[ADDR])*BY 0
    _pf.JGT(mklbl(l_end));                 // JMP if (TO-[ADDR])*BY > 0
    node->block()->accept(this, lvl + 2);
    _pf.LABEL(mklbl(l_incr));
    node->lvalue()->accept(this, lvl);     // $  ADDR
    _pf.LDINT();                           // $ [ADDR]
    node->by()->accept(this, lvl);         // $ [ADDR] BY
    if (node->lvalue()->type()->name() == basic_type::TYPE_POINTER) {
      _pf.INT((int) node->lvalue()->type()->subtype()->size());
      _pf.MUL();
    }
    _pf.ADD();                             // $ [ADDR]+BY
    node->lvalue()->accept(this, lvl);     // $ [ADDR]+BY ADDR
    _pf.STINT();                           // ADDR = [ADDR]+BY
    _pf.JMP(mklbl(l_test));
    _pf.LABEL(mklbl(l_end));

  } else if (node->lvalue()->type()->name() == basic_type::TYPE_DOUBLE) {
    node->from()->accept(this, lvl);
    if (node->from()->type()->name() == basic_type::TYPE_INT)
      _pf.I2D();
    node->lvalue()->accept(this, lvl);
    _pf.STDOUBLE();
    _pf.LABEL(mklbl(l_test));
    node->to()->accept(this, lvl);         // $ TO
    if (node->to()->type()->name() == basic_type::TYPE_INT)
      _pf.I2D();
    node->lvalue()->accept(this, lvl);     // $ TO  ADDR
    _pf.LDDOUBLE();                        // $ TO [ADDR]
    _pf.DSUB();                            // $ TO-[ADDR]
    node->by()->accept(this, lvl);         // $ TO-[ADDR] BY
    if (node->by()->type()->name() == basic_type::TYPE_INT)
      _pf.I2D();
    _pf.DMUL();                            // $ (TO-[ADDR])*BY
    _pf.DOUBLE(0);                         // $ (TO-[ADDR])*BY 0
    _pf.DCMP();
    _pf.INT(0);
    _pf.JGT(mklbl(l_end));                 // JMP if (TO-[ADDR])*BY > 0
    node->block()->accept(this, lvl + 2);
    _pf.LABEL(mklbl(l_incr));
    node->lvalue()->accept(this, lvl);     // $  ADDR
    _pf.LDDOUBLE();                        // $ [ADDR]
    node->by()->accept(this, lvl);         // $ [ADDR] BY
    if (node->by()->type()->name() == basic_type::TYPE_INT)
      _pf.I2D();
    _pf.DADD();                            // $ [ADDR]+BY
    node->lvalue()->accept(this, lvl);     // $ [ADDR]+BY ADDR
    _pf.STDOUBLE();                        // ADDR = [ADDR]+BY
    _pf.JMP(mklbl(l_test));
    _pf.LABEL(mklbl(l_end));
  }

  _again_labels.pop_back();
  _stop_labels.pop_back();
}

//---------------------------------------------------------------------------

void gr8::postfix_writer::do_if_node(gr8::if_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int l_end;
  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(l_end = ++_lbl));
  node->block()->accept(this, lvl + 2);
  _pf.LABEL(mklbl(l_end));
}

//---------------------------------------------------------------------------

void gr8::postfix_writer::do_if_else_node(gr8::if_else_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  int l_else, l_end;
  node->condition()->accept(this, lvl);
  _pf.JZ(mklbl(l_else = ++_lbl));
  node->thenblock()->accept(this, lvl + 2);
  _pf.JMP(mklbl(l_end = ++_lbl));
  _pf.LABEL(mklbl(l_else));
  node->elseblock()->accept(this, lvl + 2);
  _pf.LABEL(mklbl(l_end));
}
