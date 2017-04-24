#include "expr.h"

static TOKEN *tokenPtr;

static SymbolTable symbolTable[MAX_LEN_SYMBOL];
static int symbolTableLength;
static int data[MAX_LEN_DATA];
static int dataLength;

//util
static void initExpr();
static int findInSymbolTable(TOKEN *tokenPtr);
static int insertSymbolTable(TOKEN *tokenPtr);
static intptr_t mallocForTokenInData();

//declaration
static void globalDeclarationOrExpression();

//expression
static void global_expr();
static void single_expr();
static void binaryOperExpr();
static void functionExpr();
static void functionBodyExpr();
static void functionParaExpr();
static void statement();


void initExpr()
{
    int i = 0;

    tokenPtr = NULL;

    for (i = 0; i < MAX_LEN_SYMBOL; i++)
    {
        symbolTable[i].type = Tunknown;
        symbolTable[i].address = NULL;
        symbolTable[i].name = NULL;
        symbolTable[i].range = Runknown;
        symbolTable[i].state = Sunknown;
    }
    symbolTableLength = 0;

    for (i = 0; i < MAX_LEN_DATA; i++)
    {
        data[i] = 0;
    }
    dataLength = 0;
}

int findInSymbolTable(TOKEN *tokenPtr)
{
    if (Identifier != tokenPtr->token_type)
    {
        return -1;
    }
    for (int i = 0; i < symbolTableLength; i++)
    {
        if (!symbolTable[i].name || !strcmp(tokenPtr, symbolTable[i].name))
        {
            return i;
        }
    }
    return -1;
}

int insertSymbolTable(TOKEN *tokenPtr)
{
    if (-1 == findInSymbolTable(tokenPtr))
    {
        return -1;
    }
    if (symbolTableLength >= MAX_LEN_SYMBOL)
    {
        printf("insertSymbolTable symbolTableLength >= MAX_LEN_SYMBOL\n");
        exit(0);
    }
}

intptr_t mallocForTokenInData()
{
    intptr_t res = NULL;

    if (Num == tokenPtr->token_type)
    {
        res = &data[dataLength];

        dataLength++;
        data[dataLength - 1] = tokenPtr->value.number;
    }
    else if (String == tokenPtr->token_type)
    {
        int i = 0;
        int len = strlen(tokenPtr->value.str);
        res = &data[dataLength];

        for (i = 0; i < len; i++)
        {
            dataLength++;
            data[dataLength - 1] = tokenPtr->value.str[i];
        }
        dataLength++;
        data[dataLength - 1] = '\0';
    }
    else
    {
        printf("error ");
        exit(0);
    }
    return res;
}

void globalDeclarationOrExpression()
{
    TOKEN typeToken = (*tokenPtr);
    getNextToken();

    //inReservedWordTable or inSymbolTable
    TOKEN idenToken = (*tokenPtr);
    int isInTableFlag = isInReservedWordTable(*tokenPtr);
    if(-1 == isInTableFlag)
    {
        printf("error at line: %d\n", getCurLine());
        exit(0);
    }

    isInTableFlag = findInSymbolTable(tokenPtr);
    if (-1 == isInTableFlag)
    {
        printf("error at line: %d\n", getCurLine());
        exit(0);
    }

    if (symbolTableLength + 1 >= MAX_LEN_SYMBOL)
    {
        printf("error at symbolTableLenght + 1 >= MAX_LEN_SYMBO\n");
        exit(0);
    }

    symbolTableLength++;
    if (RW_Int == typeToken.token_type)
    {
        symbolTable[symbolTableLength - 1].type = Tint;
    }
    else if (RW_String == typeToken.token_type)
    {
        symbolTable[symbolTableLength - 1].type = Tstring;
    }
    else
    {
        printf("error at Tunkonw \n");
        exit(0);
    }

    symbolTable[symbolTableLength - 1].address = NULL;

    int len = strlen(idenToken.value.str);
    symbolTable[symbolTableLength - 1].name = malloc(len + 1);
    MAL_FAILED(symbolTable[symbolTableLength - 1].name);
    strcpy(symbolTable[symbolTableLength - 1].name, idenToken.value.str);

    symbolTable[symbolTableLength - 1].range = Rglobal;
    symbolTable[symbolTableLength - 1].state = Sdeclaration;
}

void global_expr()
{
    tokenPtr = getCurTokenVariable();
    //    while (1)
    //    {
    //        if (Eof == token.token_type)
    //        {
    //            break;
    //        }
    //        single_expr();
    //    }
}

// 1. unit_unary ::= unit | unit unary_op | unary_op unit
// 2. expr ::= unit_unary (bin_op unit_unary ...)
void single_expr()
{
    while (Sem != tokenPtr->token_type && RBrace != tokenPtr->token_type)
    {
        int index = -1;
        if (RW_Int == tokenPtr->token_type || RW_String == tokenPtr->token_type)
        {
            globalDeclarationOrExpression();
            index = symbolTableLength - 1;
        }
        else
        {
            index = findInSymbolTable(tokenPtr);
            if (-1 == index)
            {
                printf("error at line: %d\n", getCurLine());
                exit(0);
            }
        }

        getNextToken();
        if(Sem == tokenPtr->token_type)
        {
            break;
        }

        if(Equ == (*tokenPtr).token_type)
        {
        }
        else if(Add == (*tokenPtr).token_type)
        {
        }
        else if (Sub == (*tokenPtr).token_type)
        {
        }
        else if(Mul == (*tokenPtr).token_type)
        {
        }
        else if(Div == (*tokenPtr).token_type)
        {
        }
        else
        {
            printf("error at line: %d\n", getCurLine());
            exit(0);
        }

        // expression
        if (LParen == (*tokenPtr).token_type)
        {
            functionExpr();
        }
        else if (Sem == (*tokenPtr).token_type || Equ == (*tokenPtr).token_type)
        {

            if (Equ == (*tokenPtr).token_type)
            {
                getNextToken();
                /*if (typeToken.token_type != tokenPtr->token_type)
                {
                    printf("error at the type of identier(%s) is not same\n", idenToken.value.str);
                    exit(0);
                }
                symbolTable[symbolTableLength - 1].address = mallocForTokenInData();
                symbolTable[symbolTableLength - 1].state = Sdefined;*/
            }
            continue;
        }
        else
        {
            printf("error at ");
            exit(0);
        }
    }
}
void binaryOperExpr()
{
}
void functionExpr()
{
}
void functionBodyExpr()
{
}
void functionParaExpr()
{
}
void statement()
{
}
