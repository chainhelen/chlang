#include "LinkedList.h"

typedef struct LNode_tag LNode;

static struct LNode_tag {
    //childrent
    LNode *first;
    LNode *last;

    //sibling
    LNode *prev;
    LNode *next;

    //parent
    LNode *parent;
    char isRoot;

    int type;
    void *value;
};

static void assignZero(LNode *nodeP)
{
    nodeP->last = NULL;
    nodeP->first = NULL;
    nodeP->prev = NULL;
    nodeP->next = NULL;
    nodeP->parent = NULL;
    nodeP->isRoot = 0;

    nodeP->type = 0;
    nodeP->value = NULL;
}

static int isRoot(LNode **nodePP)
{
    return (*nodePP)->type;
}


int initRoot(Node **rootPP)
{
    (*rootPP) = (Node *)malloc(sizeof(LNode));
    if(NULL == *rootPP)
        return MALLOC_ERROR;

    assignZero((LNode *)(*rootPP));
    ((LNode *)(*rootPP))->isRoot = 1;

    return OK;
}

int mallocForNode(Node **nodePP, int type, void *value)
{
    (*nodePP) = (Node *)malloc(sizeof(LNode));
    if(NULL == *nodePP)
        return MALLOC_ERROR;

    assignZero((LNode *)(*nodePP));
    ((LNode *)(*nodePP))->type = type;
    ((LNode *)(*nodePP))->value = value;

    return OK;
}

int insertFirstChidren(Node **parentPP, Node **childrenPP)
{
    if(NULL == parentPP || NULL == childrenPP) {
        return INSERT_PARAISNULL;
    }
    if(NULL == *parentPP) {
        return INSERT_PARENTISNULL;
    }
    if(NULL == *childrenPP){
        return INSERT_CHILDRENISNULL;
    }

    if(*childrentPP->isRoot) {
        return INSERT_SONISROOT;
    }
    if(*parentPP == *childrentPP) {
        return INSERT_FATHERSONISSAME;
    }

    LNode* parentP = (LNode *)(*parentPP);
    LNode* childrenP = (LNode *)(*childrenPP);

    if(!(childrenP->first || childrenP->last \
            childrenP->prev || childrenP->next \
            childrenP->parent))
    {
        return INSERT_NODEISNOTINDEPENENT;
    }

    if(NULL == parentP->first) {
        parentP->first = childrenP;
        parentP->last = childrenP;
    } else {
        (LNode *) oldFirstP = parentP->first;

        //fix new node
        childrenP->next = oldFirstP;
        childrenP->parent = parentP;

        //fix old first
        oldFirst->prev = chidrentP;

        //fix parent
        parentP->first = childrentP;
    }
    return OK;
}

int insertLastChidren(Node **parentPP, Node **childrentPP)
{
    if(NULL == parentPP || NULL == childrenPP) {
        return INSERT_PARAISNULL;
    }
    if(NULL == *parentPP) {
        return INSERT_PARENTISNULL;
    }
    if(NULL == *childrenPP){
        return INSERT_CHILDRENISNULL;
    }

    if(*childrentPP->isRoot) {
        return INSERT_SONISROOT;
    }
    if(*parentPP == *childrentPP) {
        return INSERT_FATHERSONISSAME;
    }

    LNode* parentP = (LNode *)(*parentPP);
    LNode* childrenP = (LNode *)(*childrenPP);

    if(!(childrenP->first || childrenP->last \
            childrenP->prev || childrenP->next \
            childrenP->parent))
    {
        return INSERT_NODEISNOTINDEPENENT;
    }

    if(NULL == parentP->first) {
        parentP->first = childrenP;
        parentP->last = childrenP;
    } else {
        (LNode *) oldLastP = parentP->last;

        //fix new node
        childrenP->prev = oldLastP;
        childrenP->parent = parentP;

        //fix old first
        oldFirst->next = chidrenP;

        //fix parent
        parentP->last = childrenP;
    }
    return OK;
}

int freeTree(Node **nodePP) {
    LNode* nodeP = (LNode *)(*nodePP);

    LNode* tmpNodeP = *nodeP->first;
    while(tmpNodeP) {
        LNode *s = tmpNode->next;
        freeTree(&tmpNodeP);
        tmpNode = s;
    }
    tmpNodeP = NULL;

    if(nodeP->value) {
        free(nodeP->value)
    };
    nodeP->first = NULL;
    nodeP->last = NULL;
    nodeP->prev = NULL;
    nodeP->next = NULL;
    nodeP->parent = NULL;
    node->value = NULL;
    free(nodeP);
    nodeP = NULL;

    return OK;
}

int destory(Node **rootPP)
{
    if(NULL == rootPP) {
        return DESTORY_PARAISNULL;
    }
    LNode *rootP = (LNode *)(*rootPP);
    if(NULL == *rootPP) {
        return DESOTRY_NOTINITROOT;
    }
    if(!rootP->isRoot) {
        return FREELIST_ISNOTROOT;
    }
    freeTree(rootPP);
}