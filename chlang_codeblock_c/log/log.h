#ifndef LOG_H
#define LOG_H

#include <stdio.h>
#include <time.h>

#ifndef LOG_LEVEL
#define LOG_LEVEL 0
#endif

#define logPF(moduleName, ctx)                                               \
do                                                                    \
{                                                                        \
        time_t seconds = time(NULL);                                         \
        struct tm *timeinfo = localtime(&seconds);                           \
        \
printf("%d-%d-%d: ", ); \
        printf("[Time %2d-%2d-%d][Module : %7s][Moudle %7s] : %s\n", \
        timeinfo->tm_hour, timeinfo->tm_min, timeinfo->tm_sec, moduleName, ctx); \
    \
}while (0);

enum
{
    debug = 0,
    info,
    error,
    unkown
};

int getLevel(char *levelStr)
{
    if (0 == strcmp(levelStr, "debug"))
    {
        return debug;
    }
    if (0 == strcmp(levelStr, "info"))
    {
        return info;
    }
    if (0 == strcmp(levelStr, "error"))
    {
        return error;
    }
    return unkown;
}

void log(char *levelStr, char *moduleName, char *cxt)
{
    int le = getLevel(levelStr);
    if (le >= unkown)
    {
        logPf(moduleName, "error the log get the");
    }
}

#endif