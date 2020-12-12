#ifndef __GR8_SEMANTICS_SYMBOL_H__
#define __GR8_SEMANTICS_SYMBOL_H__

#include <string>
#include <vector>
#include <cdk/basic_type.h>

namespace gr8 {

  class variable_declaration_node;
  class function_declaration_node;
  class function_definition_node;

  class symbol {
    basic_type *_type;
    std::string _name;
    bool _function;
    bool _global;
    bool _public;
    bool _defined;
    size_t _stack_size = 0;
    int _offset = 0;
    std::vector<basic_type *> _arguments;

  public:
    symbol(basic_type *type, const std::string &name, bool func, bool global, bool publc, bool defined) :
        _type(type), _name(name), _function(func), _global(global), _public(publc), _defined(defined) {
    }

    symbol(basic_type *type, const std::string &name, bool func, bool global, bool publc) :
        symbol(type, name, func, global, publc, false) {

    }

    symbol(variable_declaration_node *node);
    symbol(function_declaration_node *node);
    symbol(function_definition_node *node);

    virtual ~symbol() {
      //delete _type;
    }

    basic_type *type() const {
      return _type;
    }

    const std::string &name() const {
      return _name;
    }

    bool global() const {
      return _global;
    }

    void global(bool _global) {
      symbol::_global = _global;
    }

    bool is_public() const { //todo inconsistent naming
      return _public;
    }

    void set_public(bool _public) { //todo inconsistent naming
      symbol::_public = _public;
    }

    bool defined() const {
      return _defined;
    }

    void defined(bool defined) {
      _defined = defined;
    }

    bool is_function() const {
      return _function;
    }

    void increase_stack_size(size_t s) {
      _stack_size += s;
    }

    size_t stack_size() {
      return _stack_size;
    }

    void offset(int off) {
      _offset = off;
    }

    int offset() {
      return _offset;
    }

    const std::vector<basic_type *> &arguments() const {
      return _arguments;
    }

    void add_argument(basic_type *type) {
      _arguments.push_back(type);
    }
  };

} // gr8

#endif
