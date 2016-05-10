/**
 * @file btree.c
 * @brief Implemnetacao de arvores binarias de procura AVL. //TODO is this AVL?
 */
#include <stdlib.h>

#include "btree.h"
#include "item.h"

struct btreenode {
  Item item;
  int height;
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
  link->height = 1;
  return link;
}

static int height(btreelink link) {
    if (link == NULL) return 0;
    return link->height;
}

int btreeHeight(BTree tree) {
    return height(tree->root);
}

static int balanceFactor(btreelink h) {
    return height(h->left) - height(h->right);
}

static btreelink rotL(btreelink h) {
    int height_l, height_r;
    btreelink x = h->right;
    h->right = x->left;
    x->left = h;

    height_l = height(h->left);
    height_r = height(h->right);
    h->height = height_l > height_r ? height_l + 1 : height_r + 1;

    height_l = height(x->left);
    height_r = height(x->right);
    x->height = height_l > height_r ? height_l + 1 : height_r + 1;

    return x;
}

static btreelink rotR(btreelink h) {
    int height_l, height_r;
    btreelink x = h->left;
    h->left = x->right;
    x->right = h;

    height_l = height(h->left);
    height_r = height(h->right);
    h->height = height_l > height_r ? height_l + 1 : height_r + 1;

    height_l = height(x->left);
    height_r = height(x->right);
    x->height = height_l > height_r ? height_l + 1 : height_r + 1;

    return x;
}

/* Rotacao dupla esquerda direita */
static btreelink rotLR(btreelink h) {
     if (h == NULL) return h;
     h->left = rotL(h->left);
     return rotR(h);
}

/* Rotacao dupla direita esquerda */
static btreelink rotRL(btreelink h) {
    if (h == NULL) return h;
    h->right = rotR(h->right);
    return rotL(h);
}

static btreelink AVLbalance(btreelink h) {
    int bF;
    if (h == NULL) return h;

    bF = balanceFactor(h);
    if(bF > 1) {
        if (balanceFactor(h->left) >= 0) h = rotR(h);
        else h = rotLR(h);
    } else if(bF < -1) {
        if (balanceFactor(h->right) <= 0) h = rotL(h);
        else h = rotRL(h);
    }
    else {
        int height_l = height(h->left);
        int height_r = height(h->right);
        h->height = height_l > height_r ? height_l + 1 : height_r + 1;
    }
    return h;
}

static void btreeBalanceAux(btreelink h, Item i) {
    if (h == NULL) return;
    if (itemCmp(h->item, i) > 0) {
        return btreeBalanceAux(h->left, i);
    } else if (itemCmp(h->item, i) < 0) {
        return btreeBalanceAux(h->right, i);
    } else if (itemCmpKey(key(h->item), key(i)) != 0) {
        return btreeBalanceAux(h->left, i);
    } else {
        AVLbalance(h);
    }
}

void btreeBalance(BTree tree, Item i) {
    btreeBalanceAux(tree->root, i);
}

static btreelink btreeInsertAux(btreelink link, Item i) {
  if (link == NULL)
    return newBTreeNode(i, NULL, NULL);
  if(itemCmp(i, link->item) <= 0) {
    link->left = btreeInsertAux(link->left, i);
  } else {
    link->right = btreeInsertAux(link->right, i);
  }
  link = AVLbalance(link);
  return link;
}

void btreeInsert(BTree tree, Item i) {
  tree->root = btreeInsertAux(tree->root, i);
  (tree->n)++;
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
