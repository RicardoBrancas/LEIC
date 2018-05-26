#ifndef __GR8_VARIABLEDECLARATIONNODE_H__
#define __GR8_VARIABLEDECLARATIONNODE_H__

#include <cdk/ast/typed_node.h>
#include <cdk/ast/identifier_node.h>
#include <cdk/ast/expression_node.h>
#include <cdk/basic_type.h>

namespace gr8 {

  /**
   * Class for describing variable declaration nodes.
   */
  class variable_declaration_node : public cdk::typed_node {
    bool _global, _extern, _public;
    std::string _name;
    cdk::expression_node *_initial;

  public:
    inline variable_declaration_node(int lineno, basic_type *t, bool global, bool extr, bool pub,
                                     const std::string &identifier, cdk::expression_node *initial) :
        cdk::typed_node(lineno), _global(global), _extern(extr), _public(pub), _name(identifier), _initial(initial) {
      type(t);
    }

  public:

    inline bool global() {
      return _global;
    }

    inline void global(bool g) {
      _global = g;
    }

    inline bool is_extern() {
      return _extern;
    }

    inline bool is_public() {
      return _public;
    }

    inline const std::string &name() {
      return _name;
    }

    inline cdk::expression_node *initial() {
      return _initial;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_variable_declaration_node(this, level);
    }

  };

} // gr8

#endif
