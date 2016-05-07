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
	h->nome = (char*) malloc(strlen(k));
	strcpy(k, h->nome);
	h->n = 1;
	return h;
}

Key key(Item i) {
	return i->nome;
}

int hashfunc(Key k, int M) {
	int h = 0, a = 127;

	for (; *k != '\0'; k++)
		h = (a*h + *k) % M;
	return h;
}

void itemPrint(Item i) {
	printf("%s %d", i->nome, i->n);
}

void itemFree(Item i) {
	free(i->nome);
	free(i);
}
