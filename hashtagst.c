#include <stdlib.h>
#include <stdio.h>

#include "hashtagst.h"
#include "btree.h"
#include "hash.h"

struct hashtagstruct {
    BTree tree;
    Htable hashtable;
    Hashtag *hashtags;
    int n;
    Hashtag max;
};

HTST htstInit() {
    HTST htst = (HTST) malloc(sizeof(struct hashtagstruct));
    // htst->tree = btreeInit();
    htst->hashtable = HTableInit();
    htst->hashtags = (Hashtag*) malloc(4099 * sizeof(Hashtag));
    htst->n = 0;
    return htst;
}

void insertNewHashtag(HTST htst, char *k) {
    Item i = itemInit(k);
    HTableInsert(htst->hashtable, i);
    htst->hashtags[(htst->n)++] = i;
    // btreeInsert(htst->tree, i);
}

void insertOrIncrementHashtag(HTST htst, char *k) {
    Hashtag h = searchHashtag(htst, k);
    if (h == NULL) insertNewHashtag(htst, k);
    else {
        h->n = h->n+1;
        // btreeBalance(htst->tree, h);
    }
}

Item searchHashtag(HTST htst, char *k) {
    return HTableSearch(htst->hashtable, k);
}

Item maxHashtag(HTST htst) {
    // return btreeMax(htst->tree);
    return NULL;
}

int listCompare(const void* a,const void* b) {
    return itemCmp(a, b);
}

void htstSort(HTST htst) {
    qsort(htst->hashtags, htst->n, sizeof(Hashtag), listCompare);
}

void htstTraverse(HTST htst, void (*visit)(Hashtag)) {
    Hashtag* h = htst->hashtags;
    while(h != NULL) {
        visit(*(h++));
    }
}

void htstFree(HTST htst) {
    HTableFree(htst->hashtable, itemFree);
    // btreeFree(htst->tree, itemFree);
    free(htst->hashtags);
    free(htst);
}
