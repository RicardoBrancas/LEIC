#ifndef __GR8SCANNER_H__
#define __GR8SCANNER_H__

#undef yyFlexLexer
#define yyFlexLexer gr8_scanner_FlexLexer
#include <FlexLexer.h>

typedef gr8_scanner_FlexLexer gr8_scanner;

#endif
