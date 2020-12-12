#include "targets/function_peeker.h"
#include "ast/all.h"  // automatically generated

void gr8::function_peeker::do_block_node(gr8::block_node *const node, int lvl) {
  node->declarations()->accept(this, lvl);
  node->instructions()->accept(this, lvl);
}

void gr8::function_peeker::do_sequence_node(cdk::sequence_node *const node, int lvl) {
  for (auto n : node->nodes()) {
    n->accept(this, lvl);
  }
}

void gr8::function_peeker::do_indexation_node(gr8::indexation_node *const node, int lvl) {
}

void gr8::function_peeker::do_address_of_node(gr8::address_of_node *const node, int lvl) {
}

void gr8::function_peeker::do_stop_node(gr8::stop_node *const node, int lvl) {
}

void gr8::function_peeker::do_again_node(gr8::again_node *const node, int lvl) {
}

void gr8::function_peeker::do_return_node(gr8::return_node *const node, int lvl) {
}

void gr8::function_peeker::do_nil_node(cdk::nil_node *const node, int lvl) {
}

void gr8::function_peeker::do_data_node(cdk::data_node *const node, int lvl) {
}

void gr8::function_peeker::do_integer_node(cdk::integer_node *const node, int lvl) {
}

void gr8::function_peeker::do_string_node(cdk::string_node *const node, int lvl) {
}

void gr8::function_peeker::do_double_node(cdk::double_node *const node, int lvl) {
}

void gr8::function_peeker::do_null_node(gr8::null_node *const node, int lvl) {
}

void gr8::function_peeker::do_neg_node(cdk::neg_node *const node, int lvl) {
}

void gr8::function_peeker::do_not_node(cdk::not_node *const node, int lvl) {
}

void gr8::function_peeker::do_objects_node(gr8::objects_node *const node, int lvl) {
}

void gr8::function_peeker::do_identity_node(gr8::identity_node *const node, int lvl) {
}

void gr8::function_peeker::do_add_node(cdk::add_node *const node, int lvl) {
}

void gr8::function_peeker::do_sub_node(cdk::sub_node *const node, int lvl) {
}

void gr8::function_peeker::do_mul_node(cdk::mul_node *const node, int lvl) {
}

void gr8::function_peeker::do_div_node(cdk::div_node *const node, int lvl) {
}

void gr8::function_peeker::do_mod_node(cdk::mod_node *const node, int lvl) {
}

void gr8::function_peeker::do_lt_node(cdk::lt_node *const node, int lvl) {
}

void gr8::function_peeker::do_le_node(cdk::le_node *const node, int lvl) {
}

void gr8::function_peeker::do_ge_node(cdk::ge_node *const node, int lvl) {
}

void gr8::function_peeker::do_gt_node(cdk::gt_node *const node, int lvl) {
}

void gr8::function_peeker::do_ne_node(cdk::ne_node *const node, int lvl) {
}

void gr8::function_peeker::do_eq_node(cdk::eq_node *const node, int lvl) {
}

void gr8::function_peeker::do_and_node(cdk::and_node *const node, int lvl) {
}

void gr8::function_peeker::do_or_node(cdk::or_node *const node, int lvl) {
}

void gr8::function_peeker::do_identifier_node(cdk::identifier_node *const node, int lvl) {
}

void gr8::function_peeker::do_rvalue_node(cdk::rvalue_node *const node, int lvl) {
}

void gr8::function_peeker::do_assignment_node(cdk::assignment_node *const node, int lvl) {
}

void gr8::function_peeker::do_function_definition_node(gr8::function_definition_node *const node, int lvl) {
  node->block()->accept(this, lvl);
}

void gr8::function_peeker::do_function_declaration_node(gr8::function_declaration_node *const node, int lvl) {
}

void gr8::function_peeker::do_function_call_node(gr8::function_call_node *const node, int lvl) {
}

void gr8::function_peeker::do_variable_declaration_node(gr8::variable_declaration_node *const node, int lvl) {
  _parent->current_function()->increase_stack_size(node->type()->size());
}

void gr8::function_peeker::do_evaluation_node(gr8::evaluation_node *const node, int lvl) {
}

void gr8::function_peeker::do_post_node(gr8::post_node *const node, int lvl) {
}

void gr8::function_peeker::do_tweet_node(gr8::tweet_node *const node, int lvl) {
}

void gr8::function_peeker::do_read_node(gr8::read_node *const node, int lvl) {
}

void gr8::function_peeker::do_sweep_node(gr8::sweep_node *const node, int lvl) {
  node->block()->accept(this, lvl);
}

void gr8::function_peeker::do_if_node(gr8::if_node *const node, int lvl) {
  node->block()->accept(this, lvl);
}

void gr8::function_peeker::do_if_else_node(gr8::if_else_node *const node, int lvl) {
  node->thenblock()->accept(this, lvl);
  node->elseblock()->accept(this, lvl);
}
