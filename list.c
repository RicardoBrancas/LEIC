#include <stdlib.h>

#include "item.h"
#include "list.h"

struct node {
    Item item;
    struct node *next;
};

struct list {
    listlink head;
    int size;
};

List listInit() {
    List l = (List) malloc(sizeof(struct list));
    l->size = 0;
    l->head = NULL;
    return l;
}

static listlink newListNode(Item m) {
    listlink newnode = (listlink) malloc(sizeof(struct node));
    newnode->item = m;
    newnode->next = NULL;
    return newnode;
}

void listInsert(List l, Item m) {
    listlink node = newListNode(m);
    node->next = l->head;
    l->head = node;
    l->size++;
}

Item listSearch(List l, Key k) {
    listlink node = l->head;
    while (node != NULL)
        if (itemCmpKey(key(node->item), k) == 0)
            return node->item;
        else
            node = node->next;
    return NULL;
}

Item listPop(List l) {
    if (l->head == NULL) return NULL;
    Item i = l->head->item;
    l->head = l->head->next;
    return i;
}

void listFree(List l, void (*elemFree)(Item)) {
    listlink curr, head = l->head;
    while ((curr = head) != NULL) {
        head = head->next;
        if (elemFree != NULL)
            elemFree(curr->item);
        free(curr);
    }
    free(l);
}
