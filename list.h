#ifndef LIST_H
#define LIST_H

#include "item.h"

typedef struct node *listlink;

typedef struct list *List;

List listInit();

void listInsert(List, Item);
Item listSearch(List, Key);
Item listPop(List);
void listFree(List, void(*)(Item));

#endif
