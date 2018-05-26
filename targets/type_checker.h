// $Id: type_checker.h,v 1.6 2018/05/21 12:40:45 ist424860 Exp $ -*- c++ -*-
#ifndef __GR8_SEMANTICS_TYPE_CHECKER_H__
#define __GR8_SEMANTICS_TYPE_CHECKER_H__

#include <string>
#include <iostream>
#include <cdk/symbol_table.h>
#include <cdk/ast/basic_node.h>
#include <cdk/ast/typed_node.h>
#include <cdk/ast/binary_expression_node.h>
#include <cdk/basic_type.h>
#include "targets/symbol.h"
#include "targets/basic_ast_visitor.h"

namespace gr8 {

  /**
   * Checks if nodes are semantically correct.
   */
  class type_checker: public basic_ast_visitor {
    cdk::symbol_table<gr8::symbol> &_symtab;

    basic_ast_visitor *_parent;

  public:
    type_checker(std::shared_ptr<cdk::compiler> compiler, cdk::symbol_table<gr8::symbol> &symtab, basic_ast_visitor *parent) :
        basic_ast_visitor(compiler), _symtab(symtab), _parent(parent) {
    }

  public:
    ~type_checker() {
      os().flush();
    }

  protected:
    void try_coalesce(basic_type* type, cdk::typed_node *node);
    void try_coalesce(cdk::typed_node *t1, cdk::typed_node *t2);
    void processBinaryIntegerExpression(cdk::binary_expression_node * const node, int lvl);
    void processBinaryComparisonExpression(cdk::binary_expression_node * const node, int lvl);
    void processBinaryNumericExpression(cdk::binary_expression_node * const node, int lvl);

  public:
  // do not edit these lines
#define __IN_VISITOR_HEADER__
#include "ast/visitor_decls.h"       // automatically generated
#undef __IN_VISITOR_HEADER__
  // do not edit these lines: end

  };

} // gr8

//---------------------------------------------------------------------------
//     HELPER MACRO FOR TYPE CHECKING
//---------------------------------------------------------------------------

#define CHECK_TYPES(compiler, symtab, node) { \
  try { \
    gr8::type_checker checker(compiler, symtab, this); \
    (node)->accept(&checker, 0); \
  } \
  catch (const std::string &problem) { \
    std::cerr << (node)->lineno() << ": " << problem << std::endl; \
    return; \
  } \
}

#define ASSERT_SAFE_EXPRESSIONS CHECK_TYPES(_compiler, _symtab, node)

#endif
