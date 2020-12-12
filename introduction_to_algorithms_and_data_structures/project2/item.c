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

/**
 * @brief Cria um novo Item (Hashtag) (e aloca memoria para o guardar) com o
 *        nome 'k' e o contador inicializado a 1.
 *
 * @param 'k' o nome da hashtag.
 */
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

/**
 * @brief Esta funcao devolve o nome da hashtag.
 *
 * @param 'i' o item.
 *
 * @return O nome de 'i'.
 */
Key key(Item i) {
    return i->nome;
}

/**
 * @brief Esta funcao incrementa o contador da hashtag.
 *
 * @param 'i' a hashtag 'a qual incementar o contador.
 */
void incrementValue(Item i) {
    (i->n)++;
}

/**
 * @brief Compara os contadores de duas hashtags.
 *
 * @param 'a' e' uma hashtag.
 * @param 'b' e' uma hashtag.
 *
 * @return a diferenca entre os contadores de 'b' e 'a'.
 */
int itemCmpValue(Item a, Item b) {
    return (b->n) - (a->n);
}

/**
 * @brief Compara os nomes de duas hashtags.
 *
 * @param 'a' e' uma hashtag.
 * @param 'b' e' uma hashtag.
 *
 * @return a diferenca entre os primeiros dois caracteres diferentes dos nomes de 'b' e 'a'.
 */
int itemCmpKey(Key a, Key b) {
    while (tolower(*a) == tolower(*b)) {
        if (*a == '\0')
            return (0);
        a++;
        b++;
    }
    return (tolower(*a) - tolower(*b));
}

/**
 * @brief Compara duas hashtags segundo a ordem dos contadores em primeiro lugar. E segundo a ordem alfanumerica
 *        dos nomes em caso de empate.
 *
 * @param 'a' e' uma hashtag.
 * @param 'b' e' uma hashtag.
 *
 * @return um numero que representa a relacao de ordem entre 'b' e 'a'.
 */
int itemCmp(Item a, Item b) {
    if (itemCmpValue(a,b) != 0) return itemCmpValue(a,b);
    return itemCmpKey(key(a), key(b));
}

/**
 * @brief Esta funcao devolve um hash do nome 'k' com mo'dulo 'M'.
 *
 * @param 'k' o nome do qual calcular o hash.
 * @param 'M' o modulo.
 */
int hashfunc(Key k, int M) {
    int h = 0, a = 127;

    for (; *k != '\0'; k++)
        h = (a * h + tolower(*k)) % M;
    return h;
}

/**
 * @brief Esta funcao mostra um Item (= Hashtag) no stdout.
 *
 * @param 'i' o item a ser mostrado.
 */
void itemPrint(Item i) {
    if (i != NULL)
        printf("%s %d\n", i->nome, i->n);
}

/**
 * @brief Esta liberta a memoria associada a um item.
 *
 * @param 'i' o item a ser libertado.
 */
void itemFree(Item i) {
    free(i->nome);
    free(i);
}
