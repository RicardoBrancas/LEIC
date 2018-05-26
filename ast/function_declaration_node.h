// $Id: function_declaration_node.h,v 1.7 2018/05/06 21:37:14 ist424860 Exp $ -*- c++ -*-
#ifndef __GR8_FUNCTIONDECLARATIONNODE_H__
#define __GR8_FUNCTIONDECLARATIONNODE_H__

#include <string>
#include <cdk/ast/typed_node.h>
#include <cdk/ast/sequence_node.h>
#include <cdk/basic_type.h>

namespace gr8 {

	/**
	 * Class for describing function declaration nodes.
	 */
	class function_declaration_node: public cdk::typed_node {
		bool _public;
		std::string _name;
		cdk::sequence_node   *_variables;

	public:
		inline function_declaration_node(int lineno, basic_type *t, bool publ, const std::string &identifier, cdk::sequence_node *variables) :
			cdk::typed_node(lineno), _public(publ), _name(identifier), _variables(variables) {
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

		void accept(basic_ast_visitor *sp, int level) {
			sp->do_function_declaration_node(this, level);
		}

	};

} // gr8

#endif
