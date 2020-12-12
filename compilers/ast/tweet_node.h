// $Id: tweet_node.h,v 1.2 2018/03/21 21:21:03 ist424860 Exp $ -*- c++ -*-
#ifndef __GR8_TWEETNODE_H__
#define __GR8_TWEETNODE_H__

#include <cdk/ast/expression_node.h>

namespace gr8 {

/**
 * Class for describing tweet nodes.
 */
class tweet_node: public cdk::basic_node {
	cdk::expression_node *_argument;

public:
	inline tweet_node(int lineno, cdk::expression_node *argument) :
			cdk::basic_node(lineno), _argument(argument) {
	}

public:
	inline cdk::expression_node *argument() {
		return _argument;
	}

	void accept(basic_ast_visitor *sp, int level) {
		sp->do_tweet_node(this, level);
	}

};

} // gr8

#endif
