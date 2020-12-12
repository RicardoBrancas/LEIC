/**
 * @file hash.h
 * @brief Implementacao de tabelas de dispersao com resolucao por encadeamento
 *        externo.
 */
#ifndef HASH_H
#define HASH_H

#include "list.h"
#include "item.h"

typedef struct hashtable *Htable;

Htable htableInit();

Item htableSearch(Htable, Key);

void htableInsert(Htable, Item);

void htableFree(Htable, void (*)(Item));

#endif
