#include <stdlib.h>

#include "btreehashtable.h"
#include "btree.h"
#include "hash.h"

struct btreehashtable {
    BTree tree;
    Htable hashtable;
};

BTHT bthtInit() {
    BTHT btht = (BTHT) malloc(sizeof(struct btreehashtable));
    btht->tree = btreeInit();
    btht->hashtable = HTableInit();
    return btht;
}

void bthtInsertNew(BTHT btht, Key k) {
    Item i = itemInit(k);
    HTableInsert(btht->hashtable, i);
    btreeInsert(btht->tree, i);
}

Item bthtSearch(BTHT btht, Key k) {
    return HTableSearch(btht->hashtable, k);
}

Item bthtMax(BTHT btht) {
    return btreeMax(btht->tree);
}

void bthtTraverse(BTHT btht, void (*visit)(Item)) {
    btreeTraverse(btht->tree, visit);
}

void bthtFree(BTHT btht) {
    HTableFree(btht->hashtable, NULL);
    btreeFree(btht->tree, itemFree);
    free(btht);
}
