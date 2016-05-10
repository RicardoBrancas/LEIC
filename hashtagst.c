#include <stdlib.h>
#include <stdio.h>

#include "hashtagst.h"
#include "btree.h"
#include "hash.h"

struct hashtagstruct {
    BTree tree;
    Htable hashtable;
};

HTST htstInit() {
    HTST htst = (HTST) malloc(sizeof(struct hashtagstruct));
    htst->tree = btreeInit();
    htst->hashtable = HTableInit();
    return htst;
}

void insertNewHashtag(HTST htst, char *k) {
    Item i = itemInit(k);
    HTableInsert(htst->hashtable, i);
    btreeInsert(htst->tree, i);
}

Item searchHashtag(HTST htst, char *k) {
    return HTableSearch(htst->hashtable, k);
}

Item maxHashtag(HTST htst) {
    return btreeMax(htst->tree);
}

void htstShow(HTST htst) {
    btreeTraverse(htst->tree, itemPrint);
}

void htstTraverse(HTST htst, void (*visit)(Hashtag)) {
  btreeTraverse(htst->tree, visit);
}

void incrementHashtagCounter(HTST htst, char *k) {
    Hashtag h = searchHashtag(htst, k);
    h->n = h->n+1;
    btreeBalance(htst->tree, h);
}

void htstFree(HTST htst) {
    HTableFree(htst->hashtable, NULL);
    btreeFree(htst->tree, itemFree);
    free(htst);
}
