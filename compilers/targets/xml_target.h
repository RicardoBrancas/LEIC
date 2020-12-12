#ifndef __GR8_SEMANTICS_XML_TARGET_H__
#define __GR8_SEMANTICS_XML_TARGET_H__

#include <cdk/basic_target.h>
#include <cdk/compiler.h>
#include "targets/xml_writer.h"

namespace gr8 {

  class xml_target: public cdk::basic_target {
    static xml_target _self;

  private:
    xml_target() :
        cdk::basic_target("xml") {
    }

  public:
    bool evaluate(std::shared_ptr<cdk::compiler> compiler) {
      // this symbol table will be used to check identifiers
      // an exception will be thrown if identifiers are used before declaration
      cdk::symbol_table<gr8::symbol> symtab;

      xml_writer writer(compiler, symtab);
      compiler->ast()->accept(&writer, 0);
      return true;
    }

  };

} // gr8

#endif
