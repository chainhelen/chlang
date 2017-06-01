#include "token.h"
#include "./link/LinkedList.h"

static TOKEN *tokenPtr;

int main()
{
    char src[MAX_LEN_OF_STRING + 1] = {0};

    //read the source code
    FILE *fp = fopen("test.ch", "r");
    if (NULL == fp)
    {
        printf("open test.ch failed\n");
        exit(0);
    }
    char tmpchar = ' ';
    int srcLen = 0;
    while (EOF != (tmpchar = fgetc(fp)))
    {
        if (srcLen + 1 > MAX_LEN_OF_STRING)
        {
            printf("the len of source code exceed MAX_LEN_OF_STRING");
            exit(0);
        }
        src[srcLen++] = tmpchar;
    }
    fclose(fp);

    //init Token
    initReservedWordTable(src);
    tokenPtr = getCurTokenVariable();

    while (1)
    {
        getNextToken();
        priToken(*tokenPtr);
        if (Eof == (*tokenPtr).token_type)
        {
            break;
        }
    }

    return 0;
}
