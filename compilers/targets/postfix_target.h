#ifndef __GR8_SEMANTICS_POSTFIX_TARGET_H__
#define __GR8_SEMANTICS_POSTFIX_TARGET_H__

#include <cdk/basic_target.h>
#include <cdk/symbol_table.h>
#include <cdk/ast/basic_node.h>
#include <cdk/compiler.h>
#include <cdk/emitters/postfix_ix86_emitter.h>
#include "targets/postfix_writer.h"
#include "targets/symbol.h"

namespace gr8 {

  class postfix_target: public cdk::basic_target {
    static postfix_target _self;

  private:
    postfix_target() :
        cdk::basic_target("asm") {
    }

  public:
    bool evaluate(std::shared_ptr<cdk::compiler> compiler) {
      // this symbol table will be used to check identifiers
      // during code generation
      cdk::symbol_table<gr8::symbol> symtab;

      // this is the backend postfix machine
      cdk::postfix_ix86_emitter pf(compiler);

      std::set<std::string> undef_functions;

      // generate assembly code from the syntax tree
      postfix_writer writer(compiler, symtab, pf, undef_functions);
      compiler->ast()->accept(&writer, 0);

      for(const auto &fun : undef_functions) {
        pf.EXTERN(fun);
      }

      compiler->ostream()->flush();

      return true;
    }

  };

} // gr8

#endif
