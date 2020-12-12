#ifndef __GR8_NULLNODE_H__
#define __GR8_NULLNODE_H__

#include <cdk/ast/expression_node.h>

namespace gr8 {

  /**
   * Class for describing syntactic tree leaves for null literal.
   */
  class null_node: public virtual cdk::expression_node {
  public:
	  null_node(int lineno) :
		  expression_node(lineno) {
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_null_node(this, level);
    }

  };

} // gr8

#endif
