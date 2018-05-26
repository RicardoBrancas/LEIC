// $Id: evaluation_node.h,v 1.2 2018/04/18 18:02:05 ist424860 Exp $
#ifndef __GR8_EVALUATIONNODE_H__
#define __GR8_EVALUATIONNODE_H__

#include <cdk/ast/expression_node.h>

namespace gr8 {

  /**
   * Class for describing evaluation nodes.
   */
  class evaluation_node : public cdk::basic_node {
    cdk::expression_node *_argument;

  public:
    inline evaluation_node(int lineno, cdk::expression_node *argument) :
        cdk::basic_node(lineno), _argument(argument) {
    }

  public:
    inline cdk::expression_node *argument() {
      return _argument;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_evaluation_node(this, level);
    }

  };

} // gr8

#endif
