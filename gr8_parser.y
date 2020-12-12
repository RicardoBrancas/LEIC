%{
//-- don't change *any* of these: if you do, you'll break the compiler.
#include <cdk/compiler.h>
#include "ast/all.h"
#define LINE               compiler->scanner()->lineno()
#define yylex()            compiler->scanner()->scan()
#define yyerror(s)         compiler->scanner()->error(s)
#define YYPARSE_PARAM_TYPE std::shared_ptr<cdk::compiler>
#define YYPARSE_PARAM      compiler
//-- don't change *any* of these --- END!
%}

%union {
  int                             i; /* integer value */
  double                          d;
  std::string                    *s; /* symbol name or string literal */
  cdk::basic_node                *node; /* node pointer */
  cdk::sequence_node             *sequence;
  gr8::block_node                *b;
  cdk::expression_node           *expression; /* expression nodes */
  cdk::lvalue_node               *lvalue;
  basic_type                     *t;
  gr8::variable_declaration_node *v;
};

%token <i> tINTEGER
%token <d> tDOUBLE
%token <s> tIDENTIFIER tSTRING
%token tSMALL tHUGE tNEWS tFAKE tINITALLY tUSE tPUBLIC tDEFINE tPROCEDURE tFUNCTION tON tAS tDO tUSES tFOR tRETURN tPLUS tMINUS tTIMES tOVER tMODULUS tNOT tAND tOR tASSIGN tTO tCELL tAT tABOVE tBELOW tEQ tINPUT tIF tTHEN tELSIF tELSE tSTOP tAGAIN tPOST tTWEET tSWEEPING tFROM tBY tNULL
%token tOBJECTS

%nonassoc tLVALX

%left tOR
%left tAND
%nonassoc tNOT
%left tEQ
%left tABOVE tBELOW
%left tPLUS tMINUS
%left tTIMES tOVER tMODULUS
%nonassoc tINDEXX
%nonassoc tUNARY '?' tOBJECTS
%nonassoc tAT

%type <s> string
%type <node> stmt file decl if
%type <sequence> stmts decls fvars bvars exprs
%type <expression> expr
%type <lvalue> lval
%type <t> type smalls huges fakes
%type <b> block
%type <v> var fvar bvar

%{
//-- The rules below will be included in yyparse, the main parsing function.
%}
%%

file   : decls { compiler->ast($1);                      }
       | ';'   { compiler->ast(new cdk::nil_node(LINE)); }
       ;

decls  : decl       { $$ = new cdk::sequence_node(LINE, $1);     }
       | decls decl { $$ = new cdk::sequence_node(LINE, $2, $1); }
       ;

decl   : var ';'                                                        { $1->global(true); $$ = $1;                                                                                                                       }
       | type tFUNCTION tIDENTIFIER ';'                                 { $$ = new gr8::function_declaration_node(LINE, $1, false, *$3, new cdk::sequence_node(LINE)); delete $3;                                          }
       | tPUBLIC type tFUNCTION tIDENTIFIER ';'                         { $$ = new gr8::function_declaration_node(LINE, $2, true, *$4, new cdk::sequence_node(LINE)); delete $4;                                           }
       | type tFUNCTION tIDENTIFIER tUSES fvars ';'                     { $$ = new gr8::function_declaration_node(LINE, $1, false, *$3, $5); delete $3;                                                                    }
       | tPUBLIC type tFUNCTION tIDENTIFIER tUSES fvars ';'             { $$ = new gr8::function_declaration_node(LINE, $2, true, *$4, $6); delete $4;                                                                     }
       | tPROCEDURE tIDENTIFIER ';'                                     { $$ = new gr8::function_declaration_node(LINE, new basic_type(0, basic_type::TYPE_VOID), false, *$2, new cdk::sequence_node(LINE)); delete $2;    }
       | tPUBLIC tPROCEDURE tIDENTIFIER ';'                             { $$ = new gr8::function_declaration_node(LINE, new basic_type(0, basic_type::TYPE_VOID), true, *$3, new cdk::sequence_node(LINE)); delete $3;     }
       | tPROCEDURE tIDENTIFIER tUSES fvars ';'                         { $$ = new gr8::function_declaration_node(LINE, new basic_type(0, basic_type::TYPE_VOID), false, *$2, $4); delete $2;                              }
       | tPUBLIC tPROCEDURE tIDENTIFIER tUSES fvars ';'                 { $$ = new gr8::function_declaration_node(LINE, new basic_type(0, basic_type::TYPE_VOID), true, *$3, $5); delete $3;                               }
       | tDEFINE type tFUNCTION tIDENTIFIER tAS block                   { $$ = new gr8::function_definition_node(LINE, $2, false, *$4, new cdk::sequence_node(LINE), $6); delete $4;                                       }
       | tDEFINE tPUBLIC type tFUNCTION tIDENTIFIER tAS block           { $$ = new gr8::function_definition_node(LINE, $3, true, *$5, new cdk::sequence_node(LINE), $7); delete $5;                                        }
       | tDEFINE type tFUNCTION tIDENTIFIER tON fvars tAS block         { $$ = new gr8::function_definition_node(LINE, $2, false, *$4, $6, $8); delete $4;                                                                 }
       | tDEFINE tPUBLIC type tFUNCTION tIDENTIFIER tON fvars tAS block { $$ = new gr8::function_definition_node(LINE, $3, true, *$5, $7, $9); delete $5;                                                                  }
       | tDEFINE tPROCEDURE tIDENTIFIER tAS block                       { $$ = new gr8::function_definition_node(LINE, new basic_type(0, basic_type::TYPE_VOID), false, *$3, new cdk::sequence_node(LINE), $5); delete $3; }
       | tDEFINE tPUBLIC tPROCEDURE tIDENTIFIER tAS block               { $$ = new gr8::function_definition_node(LINE, new basic_type(0, basic_type::TYPE_VOID), true, *$4, new cdk::sequence_node(LINE), $6); delete $4;  }
       | tDEFINE tPROCEDURE tIDENTIFIER tON fvars tAS block             { $$ = new gr8::function_definition_node(LINE, new basic_type(0, basic_type::TYPE_VOID), false, *$3, $5, $7); delete $3;                           }
       | tDEFINE tPUBLIC tPROCEDURE tIDENTIFIER tON fvars tAS block     { $$ = new gr8::function_definition_node(LINE, new basic_type(0, basic_type::TYPE_VOID), true, *$4, $6, $8); delete $4;                            }
       ;

block  : '{' '}'              { $$ = new gr8::block_node(LINE, new cdk::sequence_node(LINE), new cdk::sequence_node(LINE)); }
       | '{' stmts '}'        { $$ = new gr8::block_node(LINE, new cdk::sequence_node(LINE), $2);                           }
       | '{' bvars '}'        { $$ = new gr8::block_node(LINE, $2, new cdk::sequence_node(LINE));                           }
       | '{' bvars stmts '}'  { $$ = new gr8::block_node(LINE, $2, $3);                                                     }
       ;

fvars  : fvar            { $$ = new cdk::sequence_node(LINE, $1);     }
       | fvars ',' fvar  { $$ = new cdk::sequence_node(LINE, $3, $1); }
       ;

bvars  : bvar ';'        { $$ = new cdk::sequence_node(LINE, $1);     }
       | bvars bvar ';'  { $$ = new cdk::sequence_node(LINE, $2, $1); }
       ;

fvar   : type tIDENTIFIER                                 { $$ = new gr8::variable_declaration_node(LINE, $1, false, false, false, *$2, nullptr); delete $2; }
       ;

bvar   : fvar                                             { $$ = $1;                                                                                    }
       | type tIDENTIFIER '(' tINITALLY expr ')'          { $$ = new gr8::variable_declaration_node(LINE, $1, false, false, false, *$2, $5); delete $2; }
       ;

var    : bvar                                             { $$ = $1;                                                                                        }
       | tPUBLIC type tIDENTIFIER                         { $$ = new gr8::variable_declaration_node(LINE, $2, false, false, true, *$3, nullptr); delete $3; }
       | tUSE type tIDENTIFIER                            { $$ = new gr8::variable_declaration_node(LINE, $2, false, true, true, *$3, nullptr); delete $3;  }
       | tPUBLIC type tIDENTIFIER '(' tINITALLY expr ')'  { $$ = new gr8::variable_declaration_node(LINE, $2, false, false, true, *$3, $6); delete $3;      }
       ;

stmts  : stmt        { $$ = new cdk::sequence_node(LINE, $1);     }
       | stmts stmt  { $$ = new cdk::sequence_node(LINE, $2, $1); }
       ;

stmt   : expr ';'                                               { $$ = new gr8::evaluation_node(LINE, $1);                                        }
       | tASSIGN expr tTO lval ';'                              { $$ = new cdk::assignment_node(LINE, $4, $2);                                    }
       | tPOST expr ';'                                         { $$ = new gr8::post_node(LINE, $2);                                              }
       | tTWEET expr ';'                                        { $$ = new gr8::tweet_node(LINE, $2);                                             }
       | tSWEEPING lval tFROM expr tTO expr tBY expr tDO block  { $$ = new gr8::sweep_node(LINE, $2, $4, $6, $8, $10);                            }
       | tSWEEPING lval tFROM expr tTO expr tDO block           { $$ = new gr8::sweep_node(LINE, $2, $4, $6, new cdk::integer_node(LINE, 1), $8); }
       | tIF if                                                 { $$ = $2;                                                                        }
       | tRETURN ';'                                            { $$ = new gr8::return_node(LINE, nullptr);                                       }
       | tRETURN expr ';'                                       { $$ = new gr8::return_node(LINE, $2);                                            }
       | tAGAIN ';'                                             { $$ = new gr8::again_node(LINE, 1);                                              }
       | tAGAIN tINTEGER ';'                                    { $$ = new gr8::again_node(LINE, $2);                                             }
       | tSTOP ';'                                              { $$ = new gr8::stop_node(LINE, 1);                                               }
       | tSTOP tINTEGER ';'                                     { $$ = new gr8::stop_node(LINE, $2);                                              }
       | block                                                  { $$ = $1;                                                                        }
       ;

if     : expr tTHEN block              { $$ = new gr8::if_node(LINE, $1, $3);          }
       | expr tTHEN block tELSE block  { $$ = new gr8::if_else_node(LINE, $1, $3, $5); }
       | expr tTHEN block tELSIF if    { $$ = new gr8::if_else_node(LINE, $1, $3, $5); }
       ;

exprs  : expr           { $$ = new cdk::sequence_node(LINE, $1);     }
       | exprs ',' expr { $$ = new cdk::sequence_node(LINE, $3, $1); }
       ;

expr   : tINTEGER                       { $$ = new cdk::integer_node(LINE, $1);                                                                                                    }
       | tDOUBLE                        { $$ = new cdk::double_node(LINE, $1);                                                                                                     }
       | string                         { $$ = new cdk::string_node(LINE, $1);                                                                                                     }
       | tNULL                          { $$ = new gr8::null_node(LINE);                                                                                                           }
       | tINPUT                         { $$ = new gr8::read_node(LINE);                                                                                                           }
       | tMINUS expr %prec tUNARY       { $$ = new cdk::neg_node(LINE, $2);                                                                                                        }
       | tPLUS expr %prec tUNARY        { $$ = $2;                                                                                                                                 }
       | expr tPLUS expr                { $$ = new cdk::add_node(LINE, $1, $3);                                                                                                    }
       | expr tMINUS expr               { $$ = new cdk::sub_node(LINE, $1, $3);                                                                                                    }
       | expr tTIMES expr               { $$ = new cdk::mul_node(LINE, $1, $3);                                                                                                    }
       | expr tOVER expr                { $$ = new cdk::div_node(LINE, $1, $3);                                                                                                    }
       | expr tMODULUS expr             { $$ = new cdk::mod_node(LINE, $1, $3);                                                                                                    }
       | expr tBELOW expr               { $$ = new cdk::lt_node(LINE, $1, $3);                                                                                                     }
       | expr tABOVE expr               { $$ = new cdk::gt_node(LINE, $1, $3);                                                                                                     }
       | expr tAND expr                 { $$ = new cdk::and_node(LINE, $1, $3);                                                                                                    }
       | expr tOR expr                  { $$ = new cdk::or_node(LINE, $1, $3);                                                                                                     }
       | tNOT expr                      { $$ = new cdk::not_node(LINE, $2);                                                                                                        }
       | expr tEQ expr                  { $$ = new cdk::eq_node(LINE, $1, $3);                                                                                                     }
       | expr tOBJECTS                  { $$ = new gr8::objects_node(LINE, $1);                                                                                                    }
       | lval %prec tLVALX              { $$ = new cdk::rvalue_node(LINE, $1);                                                                                                     }
       | tCELL expr tAT tIDENTIFIER '?' { $$ = new gr8::address_of_node(LINE, new gr8::indexation_node(LINE, new cdk::rvalue_node(LINE, new cdk::identifier_node(LINE, $4)), $2)); }
       | lval '?'                       { $$ = new gr8::address_of_node(LINE, $1);                                                                                                 }
       | tUSE exprs tFOR tIDENTIFIER    { $$ = new gr8::function_call_node(LINE, *$4, $2); delete $4;                                                                              }
       | tDO tIDENTIFIER                { $$ = new gr8::function_call_node(LINE, *$2, new cdk::sequence_node(LINE)); delete $2;                                                    }
       | '(' expr ')'                   { $$ = $2;                                                                                                                                 }
       ;

string : tSTRING        { $$ = $1;                                               }
       | string tSTRING { $$ = new std::string(*$1 + *$2); delete $1; delete $2; }
       ;

type   : tSMALL       { $$ = new basic_type(4, basic_type::TYPE_INT);                        }
       | smalls tFAKE { $$ = new basic_type(4, basic_type::TYPE_POINTER); $$->_subtype = $1; }
       | tHUGE        { $$ = new basic_type(8, basic_type::TYPE_DOUBLE);                     }
       | huges tFAKE  { $$ = new basic_type(4, basic_type::TYPE_POINTER); $$->_subtype = $1; }
       | tNEWS        { $$ = new basic_type(4, basic_type::TYPE_STRING);                     }
       | fakes tNEWS  { $$ = new basic_type(4, basic_type::TYPE_POINTER); $$->_subtype = $1; }
       ;

smalls : tSMALL         { $$ = new basic_type(4, basic_type::TYPE_INT);                        }
       | smalls tSMALL  { $$ = new basic_type(4, basic_type::TYPE_POINTER); $$->_subtype = $1; }
       ;

huges  : tHUGE        { $$ = new basic_type(8, basic_type::TYPE_DOUBLE);                     }
       | huges tHUGE  { $$ = new basic_type(4, basic_type::TYPE_POINTER); $$->_subtype = $1; }
       ;

fakes  : tFAKE        { $$ = new basic_type(4, basic_type::TYPE_STRING);                     }
       | fakes tFAKE  { $$ = new basic_type(4, basic_type::TYPE_POINTER); $$->_subtype = $1; }
       ;

lval   : tIDENTIFIER %prec tINDEXX { $$ = new cdk::identifier_node(LINE, $1);     }
       | tCELL expr tAT expr       { $$ = new gr8::indexation_node(LINE, $4, $2); }
       ;

%%
