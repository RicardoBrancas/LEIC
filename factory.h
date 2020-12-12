#ifndef __GR8_FACTORY_H__
#define __GR8_FACTORY_H__

#include <memory>
#include <cdk/yy_factory.h>
#include "gr8_scanner.h"

namespace gr8 {

  /**
   * This class implements the compiler factory for the GR8 compiler.
   */
  class factory: public cdk::yy_factory<gr8_scanner> {
    /**
     * This object is automatically registered by the constructor in the
     * superclass' language registry.
     */
    static factory _self;

  protected:
    /**
     * @param language name of the language handled by this factory (see .cpp file)
     */
    factory(const std::string &language = "gr8") :
        cdk::yy_factory<gr8_scanner>(language) {
    }

  };

} // gr8

#endif
