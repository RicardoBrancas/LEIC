#ifndef ITEM_H
#define ITEM_H

typedef struct hashtag {
    char *nome;
    int n;
} *Hashtag;

typedef Hashtag Item;
typedef char *Key;

Item itemInit(Key);
/* int itemCmp(Item, Item); */
void itemPrint(Item);
Key key(Item);
void itemFree(Item);

#endif
