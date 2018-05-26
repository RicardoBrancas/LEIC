#include "symbol.h"

#include "ast/variable_declaration_node.h"
#include "ast/function_declaration_node.h"
#include "ast/function_definition_node.h"

gr8::symbol::symbol(gr8::variable_declaration_node *node) :
    symbol(node->type(), node->name(), false, node->global(), node->is_public()) {}

gr8::symbol::symbol(gr8::function_declaration_node *node) :
    symbol(node->type(), node->name(), true, true, node->is_public(), false) {}

gr8::symbol::symbol(gr8::function_definition_node *node) :
    symbol(node->type(), node->name(), true, true, node->is_public(), true) {}

