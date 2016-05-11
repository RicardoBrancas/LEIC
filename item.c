/**
 * @file item.c
 * @brief Implementacao de um item generico
 */
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <ctype.h>

#include "item.h"

struct hashtag {
    char *nome;
    int n;
};

Item itemInit(Key k) {
    Hashtag h = (Hashtag) malloc(sizeof(struct hashtag));
    h->nome = (char *) malloc(strlen(k) + 1 * sizeof(char));
    int i = 0;
    while ((h->nome[i] = (char) tolower(k[i])) != '\0') {
        i++;
    }
    h->n = 1;
    return h;
}

Key key(Item i) {
    return i->nome;
}

void incrementValue(Item i) {
    (i->n)++;
}

int itemCmpValue(Item a, Item b) {
    return (b->n) - (a->n);
}

int itemCmpKey(Key a, Key b) {
    while (tolower(*a) == tolower(*b)) {
        if (*a == '\0')
            return (0);
        a++;
        b++;
    }
    return (tolower(*a) - tolower(*b));
}

int itemCmp(Item a, Item b) {
    if (itemCmpValue(a,b) != 0) return itemCmpValue(a,b);
    return itemCmpKey(key(a), key(b));
}

int hashfunc(Key k, int M) {
    int h = 0, a = 127;

    for (; *k != '\0'; k++)
        h = (a * h + tolower(*k)) % M;
    return h;
}

void itemPrint(Item i) {
    if (i != NULL)
        printf("%s %d\n", i->nome, i->n);
}

void itemFree(Item i) {
    free(i->nome);
    free(i);
}
