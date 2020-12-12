#include <string>
#include "targets/xml_writer.h"
#include "targets/type_checker.h"
#include "ast/all.h"  // automatically generated


void gr8::xml_writer::do_block_node(gr8::block_node *const node, int lvl) {
  openTag(node, lvl);
  openTag("declarations", lvl + 2);
  node->declarations()->accept(this, lvl + 4);
  closeTag("declarations", lvl + 2);
  openTag("instructions", lvl + 2);
  node->instructions()->accept(this, lvl + 4);
  closeTag("instructions", lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void gr8::xml_writer::do_nil_node(cdk::nil_node *const node, int lvl) {
  openCloseTag(node, lvl);
}

void gr8::xml_writer::do_data_node(cdk::data_node *const node, int lvl) {
  //EMPTY
}

//---------------------------------------------------------------------------

void gr8::xml_writer::do_sequence_node(cdk::sequence_node *const node, int lvl) {
  openTag(node, lvl, {
      {"size", std::to_string(node->size())}
  });
  for (size_t i = 0; i < node->size(); i++)
    node->node(i)->accept(this, lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void gr8::xml_writer::do_integer_node(cdk::integer_node *const node, int lvl) {
  process_literal(node, lvl);
}

void gr8::xml_writer::do_string_node(cdk::string_node *const node, int lvl) {
  process_literal(node, lvl);
}

void gr8::xml_writer::do_double_node(cdk::double_node *const node, int lvl) {
  process_literal(node, lvl);
}

void gr8::xml_writer::do_null_node(gr8::null_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openCloseTag(node, lvl);
}

//---------------------------------------------------------------------------

void gr8::xml_writer::do_unary_expression(cdk::unary_expression_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void gr8::xml_writer::do_neg_node(cdk::neg_node *const node, int lvl) {
  do_unary_expression(node, lvl);
}

void gr8::xml_writer::do_objects_node(gr8::objects_node *const node, int lvl) {
  do_unary_expression(node, lvl);
}

void gr8::xml_writer::do_not_node(cdk::not_node *const node, int lvl) {
  do_unary_expression(node, lvl);
}

void gr8::xml_writer::do_identity_node(gr8::identity_node *const node, int lvl) {
  do_unary_expression(node, lvl);
}

//---------------------------------------------------------------------------

void gr8::xml_writer::do_binary_expression(cdk::binary_expression_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->left()->accept(this, lvl + 2);
  node->right()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void gr8::xml_writer::do_add_node(cdk::add_node *const node, int lvl) {
  do_binary_expression(node, lvl);
}

void gr8::xml_writer::do_sub_node(cdk::sub_node *const node, int lvl) {
  do_binary_expression(node, lvl);
}

void gr8::xml_writer::do_mul_node(cdk::mul_node *const node, int lvl) {
  do_binary_expression(node, lvl);
}

void gr8::xml_writer::do_div_node(cdk::div_node *const node, int lvl) {
  do_binary_expression(node, lvl);
}

void gr8::xml_writer::do_mod_node(cdk::mod_node *const node, int lvl) {
  do_binary_expression(node, lvl);
}

void gr8::xml_writer::do_lt_node(cdk::lt_node *const node, int lvl) {
  do_binary_expression(node, lvl);
}

void gr8::xml_writer::do_le_node(cdk::le_node *const node, int lvl) {
  throw std::string("should not happen");
}

void gr8::xml_writer::do_ge_node(cdk::ge_node *const node, int lvl) {
  throw std::string("should not happen");
}

void gr8::xml_writer::do_gt_node(cdk::gt_node *const node, int lvl) {
  do_binary_expression(node, lvl);
}

void gr8::xml_writer::do_ne_node(cdk::ne_node *const node, int lvl) {
  throw std::string("should not happen");
}

void gr8::xml_writer::do_eq_node(cdk::eq_node *const node, int lvl) {
  do_binary_expression(node, lvl);
}

void gr8::xml_writer::do_and_node(cdk::and_node *const node, int lvl) {
  do_binary_expression(node, lvl);
}

void gr8::xml_writer::do_or_node(cdk::or_node *const node, int lvl) {
  do_binary_expression(node, lvl);
}

//---------------------------------------------------------------------------

void gr8::xml_writer::do_indexation_node(gr8::indexation_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("base", lvl + 2);
  node->pointer()->accept(this, lvl + 4);
  closeTag("base", lvl + 2);
  openTag("offset", lvl + 2);
  node->position()->accept(this, lvl + 4);
  closeTag("offset", lvl + 2);
  closeTag(node, lvl);
}

void gr8::xml_writer::do_identifier_node(cdk::identifier_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  os() << std::string(lvl, ' ') << "<" << node->label() << ">" << node->name() << "</" << node->label() << ">"
       << std::endl;
}

void gr8::xml_writer::do_rvalue_node(cdk::rvalue_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->lvalue()->accept(this, lvl + 4);
  closeTag(node, lvl);
}

void gr8::xml_writer::do_assignment_node(cdk::assignment_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);

  node->lvalue()->accept(this, lvl + 4);
  reset_new_symbol();

  node->rvalue()->accept(this, lvl + 4);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void gr8::xml_writer::do_function_definition_node(gr8::function_definition_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl, {
      {"name",       node->name()},
      {"visibility", node->is_public() ? "public" : ""},
      {"type",       textual_representation(node->type())}
  });
  openTag("parameters", lvl + 2);
  node->variables()->accept(this, lvl + 4);
  closeTag("parameters", lvl + 2);
  openTag("body", lvl + 2);
  node->block()->accept(this, lvl + 4);
  closeTag("body", lvl + 2);
  closeTag(node, lvl);
}

void gr8::xml_writer::do_function_declaration_node(gr8::function_declaration_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl, {
      {"name",       node->name()},
      {"visibility", node->is_public() ? "public" : ""},
      {"type",       textual_representation(node->type())}
  });
  openTag("parameters", lvl + 2);
  node->variables()->accept(this, lvl + 4);
  closeTag("parameters", lvl + 2);
  closeTag(node, lvl);
}

void gr8::xml_writer::do_variable_declaration_node(gr8::variable_declaration_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl, {
      {"name",       node->name()},
      {"extern",     node->is_extern() ? "true" : "false"},
      {"visibility", node->is_public() ? "public" : ""},
      {"type",       textual_representation(node->type())},
      {"global",     node->global() ? "true" : "false"}
  });
  if (node->initial()) { //todo surround with tags maybe?
    node->initial()->accept(this, lvl + 4);
  }
  closeTag(node, lvl);
}

void gr8::xml_writer::do_function_call_node(gr8::function_call_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl, {
      {"name", node->name()}
  });
  openTag("arguments", lvl + 2);
  node->arguments()->accept(this, lvl + 4);
  closeTag("arguments", lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void gr8::xml_writer::do_evaluation_node(gr8::evaluation_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void gr8::xml_writer::do_post_node(gr8::post_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void gr8::xml_writer::do_tweet_node(gr8::tweet_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->argument()->accept(this, lvl + 2);
  closeTag(node, lvl);
}

void gr8::xml_writer::do_address_of_node(gr8::address_of_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  node->lvalue()->accept(this, lvl + 4);
  closeTag(node, lvl);
}

void gr8::xml_writer::do_stop_node(gr8::stop_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openCloseTag(node, lvl, {
      {"n", std::to_string(node->n())}
  });
}

void gr8::xml_writer::do_again_node(gr8::again_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openCloseTag(node, lvl, {
      {"n", std::to_string(node->n())}
  });
}

void gr8::xml_writer::do_return_node(gr8::return_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  if (node->value())
    node->value()->accept(this, lvl + 4);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void gr8::xml_writer::do_read_node(gr8::read_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openCloseTag(node, lvl);
}

//---------------------------------------------------------------------------

void gr8::xml_writer::do_sweep_node(gr8::sweep_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("variable", lvl + 2);
  node->lvalue()->accept(this, lvl + 4);
  closeTag("variable", lvl + 2);
  openTag("from", lvl + 2);
  node->from()->accept(this, lvl + 4);
  closeTag("from", lvl + 2);
  openTag("to", lvl + 2);
  node->to()->accept(this, lvl + 4);
  closeTag("to", lvl + 2);
  openTag("by", lvl + 2);
  node->by()->accept(this, lvl + 4);
  closeTag("by", lvl + 2);
  openTag("body", lvl + 2);
  node->block()->accept(this, lvl + 4);
  closeTag("body", lvl + 2);
  closeTag(node, lvl);
}

//---------------------------------------------------------------------------

void gr8::xml_writer::do_if_node(gr8::if_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("condition", lvl + 2);
  node->condition()->accept(this, lvl + 4);
  closeTag("condition", lvl + 2);
  openTag("then", lvl + 2);
  node->block()->accept(this, lvl + 4);
  closeTag("then", lvl + 2);
  closeTag(node, lvl);
}

void gr8::xml_writer::do_if_else_node(gr8::if_else_node *const node, int lvl) {
  ASSERT_SAFE_EXPRESSIONS;
  openTag(node, lvl);
  openTag("condition", lvl + 2);
  node->condition()->accept(this, lvl + 4);
  closeTag("condition", lvl + 2);
  openTag("then", lvl + 2);
  node->thenblock()->accept(this, lvl + 4);
  closeTag("then", lvl + 2);
  openTag("else", lvl + 2);
  node->elseblock()->accept(this, lvl + 4);
  closeTag("else", lvl + 2);
  closeTag(node, lvl);
}