#ifndef LINKEDLIST_H
#define LINKEDLIST_H

#include <stdio.h>
typedef struct LNode Node;

enum LINKEDLISTERROR {
    OK = 0,
    MALLOC_ERROR,
    INSERT_PARENTCHILDRENISSAME,
    INSERT_CHILDRENISROOT,
    INSERT_NODEISNOTINDEPENDENT,

    INSERT_PARENTISNULL,
    INSERT_CHILDRENISNULL,
    INSERT_PARAISNULL,

    FREELIST_ISNOTROOT,
    DESTORY_PARAISNULL,
    DESTORY_ISNOTROOT,
    DESTORY_NOTINITROOT
};

extern int initRoot(Node **rootPP);
extern int mallocForNode(Node **nodePP,int type, void *value);
extern int insertFirstChildren(Node **parentPP, Node **childrentPP);
extern int insertLastChildren(Node **parentPP, Node **childrentPP);
extern int destory(Node **rootPP);

#endif
