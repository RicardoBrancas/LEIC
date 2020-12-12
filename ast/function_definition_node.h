// $Id: function_definition_node.h,v 1.7 2018/05/06 21:37:14 ist424860 Exp $ -*- c++ -*-
#ifndef __GR8_FUNCTIONDEFINITIONNODE_H__
#define __GR8_FUNCTIONDEFINITIONNODE_H__

#include <string>
#include <cdk/ast/typed_node.h>
#include <cdk/ast/sequence_node.h>
#include "block_node.h"

namespace gr8 {

  /**
   * Class for describing function definition nodes.
   */
  class function_definition_node : public cdk::typed_node {
    bool _public;
    std::string _name;
    cdk::sequence_node *_variables;
    gr8::block_node *_block;

  public:
    inline function_definition_node(int lineno, basic_type *t, bool publ, const std::string &identifier,
                                    cdk::sequence_node *variables, gr8::block_node *block) :
        cdk::typed_node(lineno), _public(publ), _name(identifier), _variables(variables), _block(block) {
      type(t);
    }

  public:

    inline bool is_public() {
      return _public;
    }

    inline const std::string &name() {
      return _name;
    }

    inline cdk::sequence_node *variables() {
      return _variables;
    }

    inline gr8::block_node *block() {
      return _block;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_function_definition_node(this, level);
    }

  };

} // gr8

#endif
