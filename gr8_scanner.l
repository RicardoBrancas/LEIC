%option c++ prefix="gr8_scanner_" outfile="gr8_scanner.cpp"
%option stack noyywrap yylineno 8bit
%option debug
%{
// make relevant includes before including the parser's tab file
#include <limits>
#include <string>
#include <deque>
#include <cdk/ast/sequence_node.h>
#include <cdk/ast/expression_node.h>
#include <cdk/ast/lvalue_node.h>
#include "gr8_parser.tab.h"

// don't change this
#define yyerror LexerError

static bool found_zero = false;

static std::deque<int> scopes;

static int n = 0;
%}
%x X_COMMENT X_COMMENT_OUT X_STRING X_STR_SPECIAL X_CLOSE_BLOCKS X_NOT_FIRST
%%
                                                      { set_debug(0); }

"!!".*\n?                                             ; /* ignore comments */

"<<"                                                  yy_push_state(X_COMMENT);
<X_COMMENT>"<<"                                       yy_push_state(X_COMMENT);
<X_COMMENT>">>"                                       yy_pop_state(); if(YYSTATE != X_COMMENT) {yy_push_state(X_COMMENT_OUT);}
<X_COMMENT>.|\n                                       ; /* ignore comments */

<X_COMMENT_OUT>[ \t]*\n?                              yy_pop_state();
<X_COMMENT_OUT>.                                      yyless(0); yy_pop_state();

"small"                                               return tSMALL;
"huge"                                                return tHUGE;
"news"                                                return tNEWS;
"fake"                                                return tFAKE;
"initially"                                           return tINITALLY;

"use"                                                 return tUSE;
"public"                                              return tPUBLIC;
"define"                                              return tDEFINE;
"procedure"                                           return tPROCEDURE;
"function"                                            return tFUNCTION;
"on"                                                  return tON;
"as"                                                  return tAS;
"do"                                                  return tDO;
"uses"                                                return tUSES;
"for"                                                 return tFOR;
"return"                                              return tRETURN;

"plus"                                                return tPLUS;
"minus"                                               return tMINUS;
"times"                                               return tTIMES;
"over"                                                return tOVER;
"modulus"                                             return tMODULUS;
"not"                                                 return tNOT;
"and"                                                 return tAND;
"or"                                                  return tOR;
"assign"                                              return tASSIGN;
"to"                                                  return tTO;
"cell"                                                return tCELL;
"at"                                                  return tAT;
"above"                                               return tABOVE;
"below"                                               return tBELOW;
"equals"                                              return tEQ;
"input"                                               return tINPUT;

"if"                                                  return tIF;
"then"                                                return tTHEN;
"elsif"                                               return tELSIF;
"else"                                                return tELSE;
"stop"                                                return tSTOP;
"again"                                               return tAGAIN;
"post"                                                return tPOST;
"tweet"                                               return tTWEET;
"sweeping"                                            return tSWEEPING;
"from"                                                return tFROM;
"by"                                                  return tBY;

"objects"                                             return tOBJECTS;
"null"                                                return tNULL;

[A-Za-z][A-Za-z0-9\-]*                                yylval.s = new std::string(yytext); return tIDENTIFIER;

\"                                                    yy_push_state(X_STRING); found_zero = false; yylval.s = new std::string("");
<X_STRING>\"                                          yy_pop_state(); return tSTRING;
<X_STRING>"~"                                         yy_push_state(X_STR_SPECIAL);
<X_STRING>\n                                          yyerror("newline in string");
<X_STRING>.                                           if (!found_zero) *yylval.s += yytext;

<INITIAL,X_STRING>"..."\n                             ; /* line continuation */

<X_STR_SPECIAL>n                                      if (!found_zero) {*yylval.s += "\n";} yy_pop_state();
<X_STR_SPECIAL>r                                      if (!found_zero) {*yylval.s += "\r";} yy_pop_state();
<X_STR_SPECIAL>t                                      if (!found_zero) {*yylval.s += "\t";} yy_pop_state();
<X_STR_SPECIAL>\"|"~"                                 if (!found_zero) {*yylval.s += yytext;} yy_pop_state();
<X_STR_SPECIAL>[0-6]{1,3}                             {
                                                         if (!found_zero) {
                                                           long l = strtol(yytext, nullptr, 7);
                                                           if (errno == ERANGE || l > std::numeric_limits<unsigned char>::max())
                                                             yyerror("number out of range");

                                                           if (l == 0)
                                                             found_zero = true;
                                                           else
                                                             *yylval.s += (unsigned char) l;
                                                         }

                                                         yy_pop_state();
                                                      }

"0"                                                   yylval.i = 0; return tINTEGER;
[1-9][0-9]*                                           {
                                                        yylval.i = strtol(yytext, nullptr, 10);
                                                        if(errno == ERANGE)
                                                          yyerror("integer literal out of bounds");
                                                        return tINTEGER;
                                                      }
"0"[0-6]+                                             {
                                                        yylval.i = strtol(yytext, nullptr, 7);
                                                        if(errno == ERANGE)
                                                          yyerror("integer literal out of bounds");
                                                        return tINTEGER;
                                                      }

([0-9]*\.[0-9]+|[0-9]+\.[0-9]*)([Ee]([-+])?[0-9]+)?   yylval.d = strtod(yytext, nullptr); return tDOUBLE;
[0-9]+[Ee]([+-])?[0-9]+                               yylval.d = strtod(yytext, nullptr); return tDOUBLE;

[()?,]                                                return *yytext;

(^[ \t]*$)                                            {
                                                         char *a = yytext;
                                                         n = 0;

                                                         while(*a != '\0') {
                                                           a++; //discart \n
                                                           if (*a == ' ')
                                                             ++n;
                                                           else if (*a == '\t')
                                                             n += 8 - n%8;
                                                         }

                                                         if (n < scopes.front())
                                                           return ';';
                                                      }

\n[ \t]*[^ \t\n]                                      {
                                                         yyless(yyleng-1);
                                                         char *a = yytext;
                                                         n = 0;

                                                         while(*a != '\0') {
                                                           a++;
                                                           if (*a == ' ')
                                                             n++;
                                                           else if (*a == '\t')
                                                             n += 8 - n%8;
                                                         }

                                                         if ( (scopes.empty() && n > 0 ) || n > scopes.front()) {
                                                           scopes.push_front(n);
                                                           return '{';
                                                         } else if ((scopes.empty() && n == 0) || n == scopes.front()) {
                                                           return ';';
                                                         } else if (n < scopes.front()) {
                                                           yy_push_state(X_CLOSE_BLOCKS);
                                                           return ';';
                                                         }
                                                      }

<X_CLOSE_BLOCKS>.                                     {
                                                        yyless(0);
                                                        scopes.pop_front();

                                                        if (n == scopes.front()) //it was enough
                                                          yy_pop_state();
                                                        else if (n > scopes.front())
                                                          yyerror("identation error");

                                                        return '}';
                                                      }

[ \t\n]                                               ;

<X_NOT_FIRST><<EOF>>                                  {
                                                        if(!scopes.empty()) {
                                                          scopes.pop_front();
                                                          return '}';
                                                        }
                                                        yyterminate();
                                                      }

<<EOF>>                                               {
                                                         yy_push_state(X_NOT_FIRST);
                                                         return ';';
                                                      }

.|\n                                                  yyerror("Unknown character");

%%
