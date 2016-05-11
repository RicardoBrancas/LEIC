#ifndef ITEM_H
#define ITEM_H

typedef struct hashtag *Hashtag;

typedef Hashtag Item;
typedef char *Key;

Item itemInit(Key);
Key key(Item);
void incrementValue(Item);
int itemCmp(Item, Item);
int itemCmpValue(Item, Item);
int itemCmpKey(Key, Key);
int hashfunc(Key, int);
void itemPrint(Item);
void itemFree(Item);


#endif
