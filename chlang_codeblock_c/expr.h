#ifndef EXPR_H
#define EXPR_H

#include <string.h>
#include <stdint.h>
#include <stdlib.h>
#include <stdio.h>
#include "token.h"

#define MAX_LEN_SYMBOL 2000
#define MAX_LEN_NAME 200
#define MAX_LEN_DATA 3000
#define MAL_FAILED(x) do { \
    if(!x) {\
        printf("malloc failed"); \
        exit(0); \
    } \
} while(0);

extern enum TYPE {
    Tint = 0,
    Tstring,
    Tunknown
};

extern enum RANGE {
    Rglobal = 0,
    Rlocal,
    Runknown
};

extern enum STATE {
    Sdeclaration = 0,
    Sdefined,
    Sunknown
};

//extern enum SYMBOLTYPE{
//    Type = 0,
//    Address,
//    Range,
//    Name,
//    State,
//    SymbolTableMaxLen
//};

typedef struct SymbolTable_tag {
    enum TYPE type;
    intptr_t address;
    char *name;
    enum RANGE range;
    enum STATE state;
}SymbolTable;

//extern void statement();
//extern void gloabl_expr();
//extern void single_expr();
//extern void binaryOperExpr();
//extern void functionBodyExpr();
//extern void functionParaExpr();

#endif
