#ifndef BTREE_H
#define BTREE_H

#include "item.h"

typedef struct binarytree *BTree;
typedef struct btreenode *btreelink;

BTree btreeInit();
btreelink newBTreeNode(Item, btreelink, btreelink);

int btreeHeight(BTree);
Item btreeMax(BTree);

void btreeInsert(BTree, Item);

void btreeTraverse(BTree, void (*)(Item));

void btreeBalance(BTree, Item);

void btreeFree(BTree, void (*)(Item));


#endif
