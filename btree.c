/**
 * @file btree.c
 * @brief Implemnetacao de arvores binarias de procura AVL. //TODO is this AVL?
 */
#include <stdlib.h>

#include "btree.h"
#include "item.h"

struct btreenode {
  Item item;
  struct btreenode *left;
  struct btreenode *right;
};

struct binarytree {
  struct btreenode *root;
  int n;
};

BTree btreeInit() {
  BTree tree = (BTree) malloc(sizeof(struct binarytree));
  tree->root = NULL;
  tree->n = 0;
  return tree;
}

btreelink newBTreeNode(Item i, btreelink l, btreelink r) {
  btreelink link = (btreelink) malloc(sizeof(struct btreenode));
  link->item = i;
  link->left = l;
  link->right = r;
  return link;
}

static int btreeHeightAux(btreelink link) {
    int a, b;
    if (link == NULL) {
        return 0;
    } else {
        a = btreeHeightAux(link->left);
        b = btreeHeightAux(link->right);
        if (a > b)
            return a;
        else
            return b;
    }
}

int btreeHeight(BTree tree) {
    return btreeHeightAux(tree->root);
}

static Item btreeMaxAux(btreelink link) {
  if(link == NULL)
    return NULL;
  if(link->right != NULL) {
    return btreeMaxAux(link->right);
  } else {
    return link->item;
  }
}

Item btreeMax(BTree tree) {
  return btreeMaxAux(tree->root);
}

static btreelink btreeInsertAux(btreelink link, Item i) {
  if (link == NULL)
    return newBTreeNode(i, NULL, NULL);
  if(itemCmp(i, link->item) < 0) {
    link->left = btreeInsertAux(link->left, i);
  } else {
    link->right = btreeInsertAux(link->right, i);
  }
  return link;
}

void btreeInsert(BTree tree, Item i) {
  tree->root = btreeInsertAux(tree->root, i);
  (tree->n)++;
}

static void btreeTraverseAux(btreelink link, void (*visit)(Item)) {
    if (link == NULL)
        return;
    btreeTraverseAux(link->left, visit);
    visit(link->item);
    btreeTraverseAux(link->right, visit);
}

void btreeTraverse(BTree tree, void (*visit)(Item)) {
    btreeTraverseAux(tree->root, visit);
}

static void btreeFreeAux(btreelink link, void (*elemFree)(Item)) {
    if(link == NULL)
        return;
    btreeFreeAux(link->left, elemFree);
    btreeFreeAux(link->right, elemFree);
    if(elemFree != NULL)
        elemFree(link->item);
    free(link);
}

void btreeFree(BTree tree, void (*elemFree)(Item)) {
    btreeFreeAux(tree->root, elemFree);
    free(tree);
}
