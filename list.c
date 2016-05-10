#include <stdlib.h>
#include "list.h"
#include "item.h"

List listInit() {
  List l = (List) malloc(sizeof(struct list));
  l->head = (l->tail = NULL);
  return l;
}

listlink newNode(Item i) {
  listlink l = (listlink) malloc(sizeof(struct node));
  l->item = i;
  return l;
}

void listInsert(List l, Item i) {
    listlink x = newNode(i); 
    x->next = l->head; 
    l->head = x; 
}

static Item listSearchAux(listlink h, Key k) {
    if (h == NULL) return NULL;
    if (itemCmpKey(key(h->item), k)) return h->item;
    return listSearchAux(h->next, k);
}

Item listSearch(List l, Key k) {
   return listSearchAux(l->head, k);
}

void show(List l) {
  listlink c;
  for(c = l->head; c != NULL; c = c->next) {
    itemPrint(c->item);
  }
}

static void listFreeAux(listlink h, void (*freeFunc)(Item)) {
  if(h == NULL) return;
  listFreeAux(h->next, freeFunc);
  freeFunc(h->item);
  free(h);
}

void listFree(List l, void (*freeFunc)(Item)) {
  listFreeAux(l->head, freeFunc);
  free(l);
}