#ifndef __GR8_SEMANTICS_POSTFIX_WRITER_H__
#define __GR8_SEMANTICS_POSTFIX_WRITER_H__

#include <string>
#include <iostream>
#include <set>
#include <vector>
#include <cdk/symbol_table.h>
#include <cdk/emitters/basic_postfix_emitter.h>
#include "targets/basic_ast_visitor.h"
#include "targets/symbol.h"

namespace gr8 {

  //!
  //! Traverse syntax tree and generate the corresponding assembly code.
  //!
  class postfix_writer : public basic_ast_visitor {
    cdk::symbol_table<gr8::symbol> &_symtab;
    cdk::basic_postfix_emitter &_pf;
    std::set<std::string> &_undefined_functions;
    int _lbl;
    std::vector<int> _again_labels;
    std::vector<int> _stop_labels;


  public:
    postfix_writer(std::shared_ptr<cdk::compiler> compiler, cdk::symbol_table<gr8::symbol> &symtab,
                   cdk::basic_postfix_emitter &pf, std::set<std::string> &undefined_functions) :
        basic_ast_visitor(compiler), _symtab(symtab), _pf(pf),
        _undefined_functions(undefined_functions), _lbl(0) {
    }

  public:
    ~postfix_writer() {
      os().flush();
    }

  private:
    /** Method used to generate sequential labels. */
    inline std::string mklbl(int lbl) {
      std::ostringstream oss;
      if (lbl < 0)
        oss << ".L" << -lbl;
      else
        oss << "_L" << lbl;
      return oss.str();
    }

    inline void error(cdk::basic_node *node, std::string msg = "", bool fatal = false) {
      std::cerr << node->lineno() << ": " << node->label() << ": " << msg << std::endl;
      if (fatal)
        exit(1);
    }

  public:
    // do not edit these lines
#define __IN_VISITOR_HEADER__
#include "ast/visitor_decls.h"       // automatically generated
#undef __IN_VISITOR_HEADER__
    // do not edit these lines: end

  };

} // gr8

#endif
