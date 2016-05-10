#ifndef LIST_H
#define LIST_H

#include "item.h"

typedef struct node {
        Item item;
        struct node *next;
} *listlink;

typedef struct list {
  listlink head;
  int size;
} *List;

List listInit();
listlink newNode(Item);

void listInsert(List, Item);
Item listSearch(List, Key);
void listFree(List, void(*)(Item));

#endif
