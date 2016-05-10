/**
 * @file item.c
 * @brief Implementacao de um item generico
 */
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#include "item.h"

Item itemInit(Key k) {
	Hashtag h = (Hashtag) malloc(sizeof(struct hashtag));
	h->nome = strdup(k);
	h->n = 1;
	return h;
}

Key key(Item i) {
	return i->nome;
}

int itemCmp(Item a, Item b) {
  return a->n - b->n;
}

int itemCmpKey(Key a, Key b) {
  return strcmp(a, b);
}

int hashfunc(Key k, int M) {
	int h = 0, a = 127;

	for (; *k != '\0'; k++)
		h = (a*h + *k) % M;
	return h;
}

void itemPrint(Item i) {
	printf("%s %d\n", i->nome, i->n);
}

void itemFree(Item i) {
	free(i->nome);
	free(i);
}
