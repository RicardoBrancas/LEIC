/**
 * @file hash.c
 * @brief Implementacao de tabelas de dispersao com resolucao por encadeamento
 *        externo.
 */
#include <stdlib.h>

#include "hash.h"

/**
 * Lista de primos auxiliar, deve ser aumentada se precisarmos de
 * guardar mais elementos.
 */
#define PRIMES 9
static int primes[PRIMES] = {1031, 4099, 16127, 34651, 68111, 131071, 260999,
                             524287, 999983};

/**
 * Fator de carga a partir do qual a hastable e' expandida.
 */
#define MAX_LOAD 1.3

/**
 * @brief estrutura que representa uma hashtable.
 *
 * n e o numero atual de elementos na tabela.
 * m e um indice do vetor primes.
 * p e o primo correspondente a primes[m] e o numero de listas da tabela
 */
struct hashtable {
    List *lists;
    int n, p, m;
};

/**
 * @brief Inicializa uma nova tabela de dispersao com primes[0] listas.
 *
 * @return A nova tabela de dispersao.
 */
Htable htableInit() {
    Htable h = (Htable) malloc(sizeof(struct hashtable));
    h->n = h->m = 0;
    h->p = primes[h->m];
    h->lists = (List *) malloc(h->p * sizeof(List));
    int i = 0;
    for (i = 0; i < h->p; i++) {
        h->lists[i] = listInit();
    }
    return h;
}

/**
 * @brief devolve o fator de carga atual da hashtable.
 */
static float loadfactor(Htable h) {
    return h->n / h->p;
}

/**
 * @brief Esta funcao aloca um novo vetor com primes[m+1] listas e reinsere
 *        todos os elementos nas novas listas.
 *        No fim o vetor antigo e' libertado.
 *
 * @param 'h' a hastable a expandir.
 */
static void htableExpand(Htable h) {
    int i, oldP;
    List *oldlists;

    if ((h->m) + 1 >= PRIMES)
        return;

    oldlists = h->lists;
    oldP = h->p;

    h->n = 0;
    (h->m)++;
    h->p = primes[++(h->m)];
    h->lists = (List *) malloc((h->p) * sizeof(List));
    for (i = 0; i < h->p; i++) {
        h->lists[i] = listInit();
    }

    for (i = 0; i < oldP; i++) {
        Item item;
        while((item = listPop(oldlists[i])) != NULL) {
            htableInsert(h, item);
        }
        listFree(oldlists[i], NULL);
    }
    free(oldlists);
}

/**
 * @brief Insere um Item na hashtable.
 *
 * Esta funcao nao duplica o Item passado passado como argumento! E' da
 * responsabilidade do utilizador garantir que a memoria se mantem disponivel
 * ate ao fim de vida da tabela de dispersao.
 *
 * @param 'h' a hashtable na qual inserir.
 * @param 'i' o Item a inserir.
 */
void htableInsert(Htable h, Item i) {
    int index = hashfunc(key(i), h->p);
    listInsert(h->lists[index], i);
    (h->n)++;

    if (loadfactor(h) > MAX_LOAD)
        htableExpand(h);
}

/**
 * @brief devolve o Item com a chave Key se ele existir na tabela. Caso
 *		  contrario devolve NULL.
 *
 * @param 'h' a hastable na qual procurar.
 * @param 'key' a chave a procurar.
 * @return O item caso exista; NULL caso contrario.
 */
Item htableSearch(Htable h, Key key) {
    int index = hashfunc(key, h->p);
    return listSearch(h->lists[index], key);
}


/**
 * @brief liberta a memoria associada a uma hashtable
 *
 * @param 'h' hastable a libertar.
 * @param 'elemFree' a funcao utilizada para libertar os elementos da tabela.
 *                   Pode ser NULL.
 */
void htableFree(Htable h, void (*elemFree)(Item)) {
    int i;
    for (i = 0; i < h->p; i++) {
        listFree(h->lists[i], elemFree);
    }
    free(h->lists);
    free(h);
}
