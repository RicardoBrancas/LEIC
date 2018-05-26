#ifndef __GR8_FUNCTIONCALLNODE_H__
#define __GR8_FUNCTIONCALLNODE_H__

#include <string>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/sequence_node.h>

namespace gr8 {

	/**
	 * Class for describing function call nodes.
	 */
	class function_call_node: public cdk::expression_node {
		std::string _name;
		cdk::sequence_node *_arguments;

	public:
		inline function_call_node(int lineno, const std::string &identifier, cdk::sequence_node *arguments) :
			cdk::expression_node(lineno), _name(identifier), _arguments(arguments) {}

	public:

		inline const std::string &name() const {
			return _name;
		}

		inline cdk::sequence_node *arguments() const {
			return _arguments;
		}

		void accept(basic_ast_visitor *sp, int level) {
			sp->do_function_call_node(this, level);
		}

	};

} // gr8

#endif
