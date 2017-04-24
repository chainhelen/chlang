#include "token.h"

static int line = 0;
static const char *oriSrcP = NULL;
static const char *curSrcP = NULL;
static TOKEN token;

//
const char *reservedWords = "int string while if\0";
TOKEN reservedWordTable[MAX_LEN_OF_RESERVEDWORDTABLE];
int reservedWordTableLen = 0;

void initReservedWordTable(char *src)
{
    int i = 0;
    for (i = 0; i < MAX_LEN_OF_RESERVEDWORDTABLE; i++)
    {
        reservedWordTable[i].token_type = UnkonwnToken;
    }
    reservedWordTableLen = 0;
    curSrcP = reservedWords;

    for (i = RW_Int; i <= RW_If; i++)
    {
        getNextToken();
        if (Eof == token.token_type)
            break;
        token.token_type = (enum TOKEN_TYPE)(i);
        reservedWordTable[reservedWordTableLen++] = token;
    }
    curSrcP = oriSrcP = src;
}

int findInReservedWordTableByTkStr()
{
    int i = 0;
    for (i = 0; i < reservedWordTableLen; i++)
    {
        if (!strcmp(token.value.str, reservedWordTable[i].value.str))
        {
            return reservedWordTable[i].token_type;
        }
    }
    return -1;
}

int isInReservedWordTable(TOKEN pToken)
{
    int i = 0;
    for (i = 0; i < reservedWordTableLen; i++)
    {
        if (!strcmp(pToken.value.str, reservedWordTable[i].value.str))
        {
            return 1;
        }
    }
    return 0;
}

TOKEN *getCurTokenVariable()
{
    return &token;
}

void getNextToken()
{
    int len = strlen(curSrcP);
    char str[MAX_LEN_OF_STRING];

    if (len <= 0)
    {
        token.token_type = Eof;
        strcpy(token.value.str, "<Eof>\0");
        return;
    }

    while (isblank(*curSrcP))
    {
        curSrcP++;
    }

    //a-z A-Z
    if (isalpha(*curSrcP))
    {
        int strLen = 0;
        while (*curSrcP && (isalpha(*curSrcP) || isdigit(*curSrcP)))
        {
            str[strLen++] = *curSrcP;
            curSrcP++;
        }
        str[strLen++] = '\0';
        strcpy(token.value.str, str);

        int result = findInReservedWordTableByTkStr();
        if (-1 == result)
        {
            token.token_type = Identifier;
        }
        else
        {
            token.token_type = (enum TOKEN_TYPE)(result);
        }
    }
    else if (isdigit(*curSrcP))
    { // 0-9
        int number = 0;
        while (*curSrcP && isdigit(*curSrcP))
        {
            number = number * 10 + (int)(*curSrcP - '0');
            curSrcP++;
        }
        token.token_type = Num;
        token.value.number = number;
        if (isalpha(*curSrcP))
        {
            printf("the number has something problem\n");
            exit(0);
        }
    }
    else if ('\"' == *curSrcP)
    { // "abc"
        int strLen = 0;
        str[strLen++] = *curSrcP;
        curSrcP++;

        while (*curSrcP)
        {
            str[strLen++] = *curSrcP;
            if ('\n' == *curSrcP)
            {
                line++;
            }
            curSrcP++;
            if ('\"' == str[strLen - 1])
            {
                break;
            }
        }
        str[strLen++] = '\0';
        token.token_type = String;
        strcpy(token.value.str, str);
    }
    else if ('\n' == *curSrcP)
    {
        token.token_type = Ent;
        strcpy(token.value.str, "\\n\0");
        line++;
        curSrcP++;
    }
    else if ('+' == *curSrcP)
    {
        token.token_type = Add;
        strcpy(token.value.str, "+\0");
        curSrcP++;
    }
    else if ('*' == *curSrcP)
    {
        token.token_type = Mul;
        strcpy(token.value.str, "*\0");
        curSrcP++;
    }
    else if ('/' == *curSrcP)
    {
        token.token_type = Div;
        strcpy(token.value.str, "/\0");
        curSrcP++;
    }
    else if ('-' == *curSrcP)
    {
        token.token_type = Sub;
        strcpy(token.value.str, "-\0");
        curSrcP++;
    }
    else if (';' == *curSrcP)
    {
        token.token_type = Sem;
        strcpy(token.value.str, ";\0");
        curSrcP++;
    }
    else if ('=' == *curSrcP)
    {
        token.token_type = Equ;
        strcpy(token.value.str, "=\0");
        curSrcP++;
    }
    else if ('(' == *curSrcP)
    {
        token.token_type = LParen;
        strcpy(token.value.str, "(\0");
        curSrcP++;
    }
    else if (')' == *curSrcP)
    {
        token.token_type = RParen;
        strcpy(token.value.str, ")\0");
        curSrcP++;
    }
    else if ('{' == *curSrcP)
    {
        token.token_type = LBrace;
        strcpy(token.value.str, "{\0");
        curSrcP++;
    }
    else if ('}' == *curSrcP)
    {
        token.token_type = RBrace;
        strcpy(token.value.str, "}\0");
        curSrcP++;
    }
    else
    {
        printf("the unkonwn token = %s\n", str);
        exit(0);
    }
}

void priToken(TOKEN token)
{
    if (Identifier == token.token_type ||

        RW_Int == token.token_type ||
        RW_String == token.token_type ||
        RW_While == token.token_type ||
        RW_While == token.token_type ||
        RW_If == token.token_type ||

        Mul == token.token_type ||
        Add == token.token_type ||
        Sub == token.token_type ||
        Div == token.token_type ||
        Ent == token.token_type ||
        Sem == token.token_type ||
        Equ == token.token_type ||

        LParen == token.token_type ||
        RParen == token.token_type ||
        LBrace == token.token_type ||
        RBrace == token.token_type ||

        String == token.token_type ||
        Eof == token.token_type ||
        Function == token.token_type)
    {
        printf("The token : type = \"%d\", value = \"%s\"\n", token.token_type, token.value.str);
    }
    else if (Num == token.token_type)
    {
        printf("The token : type = \"%d\", value = \"%d\"\n", token.token_type, token.value.number);
    }
    else
    {
        printf("The token : type = ???, value = ???\n");
    }
}

void addLine()
{
    line++;
}

int getCurLine()
{
    return line;
}
