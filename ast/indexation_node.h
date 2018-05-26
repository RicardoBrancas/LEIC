#ifndef __GR8_INDEXATIONNODE_H__
#define __GR8_INDEXATIONNODE_H__

#include <cdk/ast/lvalue_node.h>
#include <cdk/ast/expression_node.h>

namespace gr8 {

  /**
   * Class for describing indexation nodes.
   */
  class indexation_node: public cdk::lvalue_node {
    cdk::expression_node *_pointer;
    cdk::expression_node *_position;

  public:
    inline indexation_node(int lineno,  cdk::expression_node *pointer, cdk::expression_node *position) :
        cdk::lvalue_node(lineno), _pointer(pointer), _position(position) {
    }

  public:
     inline cdk::expression_node *pointer() {
       return _pointer;
     }

    inline cdk::expression_node *position() {
      return _position;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_indexation_node(this, level);
    }

  };

} // gr8

#endif
