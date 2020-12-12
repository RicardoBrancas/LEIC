#ifndef __GR8_STOPNODE_H__
#define __GR8_STOPNODE_H__

#include <cdk/ast/basic_node.h>

namespace gr8 {

  /**
   * Class for describing stop nodes.
   */
  class stop_node: public cdk::basic_node {
    int _n;

  public:
    inline stop_node(int lineno, int n) :
        cdk::basic_node(lineno), _n(n) {
    }

  public:
    inline int n() {
      return _n;
    }

    void accept(basic_ast_visitor *sp, int level) {
      sp->do_stop_node(this, level);
    }

  };

} // gr8

#endif
