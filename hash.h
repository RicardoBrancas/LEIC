/**
 * @file hash.h
 * @brief Implementacao de tabela de dispersao com encadeamento externo
 */
#ifndef HASH_H
#define HASH_H

#include "list.h"
#include "item.h"

typedef struct hashtable *Htable;

Htable HTableInit();

Item HTableSearch(Htable, Key);
void HTableDelete(Htable, Item);
void HTableInsert(Htable, Item);

void HTableFree(Htable);

#endif
