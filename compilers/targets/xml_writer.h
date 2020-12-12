#ifndef __GR8_SEMANTICS_XMLWRITER_H__
#define __GR8_SEMANTICS_XMLWRITER_H__

#include <string>
#include <iostream>
#include <map>
#include <cdk/ast/basic_node.h>
#include <cdk/symbol_table.h>
#include "targets/basic_ast_visitor.h"
#include "targets/symbol.h"

namespace gr8 {

  /**
   * Print nodes as XML elements to the output stream.
   */
  class xml_writer: public basic_ast_visitor {
    cdk::symbol_table<gr8::symbol> &_symtab;

  public:
    xml_writer(std::shared_ptr<cdk::compiler> compiler, cdk::symbol_table<gr8::symbol> &symtab) :
        basic_ast_visitor(compiler), _symtab(symtab) {
    }

  public:
    ~xml_writer() {
      os().flush();
    }

  private:
    void openTag(const std::string &tag, int lvl) {
      os() << std::string(lvl, ' ') + "<" + tag + ">" << std::endl;
    }
    void openTag(const cdk::basic_node *node, int lvl) {
      openTag(node->label(), lvl);
    }
    void openTag(const cdk::basic_node *node, int lvl, std::map<std::string, std::string> attrs) {
      os() << std::string(lvl, ' ') << "<" << node->label();

      for (auto attr : attrs) {
        os() << " " << attr.first << "=\"" << attr.second << "\"";
      }

      os() << ">" << std::endl;
    }

    void closeTag(const std::string &tag, int lvl) {
      os() << std::string(lvl, ' ') + "</" + tag + ">" << std::endl;
    }
    void closeTag(const cdk::basic_node *node, int lvl) {
      closeTag(node->label(), lvl);
    }

    void openCloseTag(const std::string &tag, int lvl) {
      os() << std::string(lvl, ' ') + "<" + tag + "/>" << std::endl;
    }
    void openCloseTag(const cdk::basic_node *node, int lvl) {
      openCloseTag(node->label(), lvl);
    }
    void openCloseTag(const cdk::basic_node *node, int lvl, std::map<std::string, std::string> attrs) {
      os() << std::string(lvl, ' ') << "<" << node->label();

      for (auto attr : attrs) {
        os() << " " << attr.first << "=\"" << attr.second << "\"";
      }

      os() << "/>" << std::endl;
    }


    std::string textual_representation(basic_type *t) {
      if(!t)
        return "invalid type";

      if (t->name() == basic_type::TYPE_POINTER)
        return "pointer to " + textual_representation(t->subtype());

      else if (t->name() == basic_type::TYPE_INT)
        return "integer";

      else if (t->name() == basic_type::TYPE_DOUBLE)
        return "double";

      else if (t->name() == basic_type::TYPE_STRING)
        return "string";

      else
        return "unknown type";
    }

  protected:
    void do_binary_expression(cdk::binary_expression_node * const node, int lvl);
    void do_unary_expression(cdk::unary_expression_node * const node, int lvl);
    template<typename T>
    void process_literal(cdk::literal_node<T> * const node, int lvl) {
      os() << std::string(lvl, ' ') << "<" << node->label() << ">" << node->value() << "</" << node->label() << ">" << std::endl;
    }

  public:
  // do not edit these lines
#define __IN_VISITOR_HEADER__
#include "ast/visitor_decls.h"       // automatically generated
#undef __IN_VISITOR_HEADER__
  // do not edit these lines: end

  };

} // gr8

#endif
