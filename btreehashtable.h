#ifndef BTREEHASHTABLE_H
#define BTREEHASHTABLE_H

#include "item.h"

typedef struct btreehashtable *BTHT;

BTHT bthtInit();

void bthtInsertNew(BTHT, Key);
Item bthtSearch(BTHT, Key);
Item bthtMax(BTHT);
void bthtTraverse(BTHT, void (*)(Item));

void bthtFree(BTHT);

#endif
