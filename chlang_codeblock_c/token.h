#ifndef TOKEN_H
#define TOKEN_H

#include <string.h>
#include <ctype.h>
#include <stdlib.h>
#include <stdio.h>

#define MAX_LEN_OF_STRING 200
#define MAX_LEN_OF_RESERVEDWORDTABLE 200

extern enum TOKEN_TYPE {
    Identifier = 0,
    RW_Int,
    RW_String,
    RW_While,
    RW_If,

    Mul,
    Add,
    Sub,
    Div,
    Ent,
    Sem,
    Equ,

    LParen, // "("
    RParen, // ")"
    LBrace, // "{"
    RBrace, // "}"
    Num,
    String,
    Eof,
    Function,
    UnkonwnToken
};

typedef struct TOKEN_TAG
{
    enum TOKEN_TYPE token_type;
    union {
        char str[MAX_LEN_OF_STRING + 1];
        int number;
    } value;
} TOKEN;

extern void initReservedWordTable(char *src);
extern TOKEN *getCurTokenVariable();
extern void getNextToken();
extern void priToken(TOKEN token);
extern int getCurLine();
extern int isInReservedWordTable(TOKEN pToken);

#endif
