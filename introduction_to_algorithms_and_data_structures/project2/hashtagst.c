/**
 * @file hashtagst.c
 * @brief Implementacao da estrutura utilizada para guardar hashtags.
 */
#include <stdlib.h>

#include "hashtagst.h"
#include "hash.h"

/**
 * Tamanho inicial e tamanho do incremento da lista.
 */
#define INITIAL_SIZE 64
#define INCREMENT_SIZE 2048

/**
 * @brief estrutura que representa uma HashtagStructure.
 *
 * hashtable e' uma hashtable usada para indexar as hashtags.
 * hashtags e' um vetor com todas as hashtags usado para ordenar.
 * n e' o numero de hashtags atual.
 * cap e a capacidade atual do vetor 'hashtags'.
 * total e' o numero total de hashtags (incluindo repeticoes).
 * max e' a hashtag com maior numero de ocorrencias.
 */
struct hashtagstruct {
    Htable hashtable;
    Item *hashtags;
    int n, cap, total;
    Item max;
};

/**
 * @brief Inicializa uma nova HashtagStructure.
 *
 * @return A nova HTST.
 */
HTST htstInit() {
    HTST htst = (HTST) malloc(sizeof(struct hashtagstruct));

    htst->hashtable = htableInit();
    htst->n = 0;
    htst->cap = INITIAL_SIZE;
    htst->total = 0;
    htst->hashtags = (Item *) malloc(htst->cap * sizeof(Item));
    htst->max = NULL;

    return htst;
}

/**
 * Funcao interna que aumenta o tamanho do vetor de uma HTST.
 *
 * @param 'htst' uma HTST.
 */
static void expand(HTST htst) {
    htst->cap += INCREMENT_SIZE;
    htst->hashtags = (Item *) realloc(htst->hashtags, htst->cap * sizeof(Item));
}

/**
 * @brief Esta funcao insere uma nova hashtag. Essa hashtag e' inserida na hashtable e no vetor. E' verificado se o
 *        vetor precisa de ser expandido e e' verificado se o maximo se alterou.
 *
 * NOTA: quando e' criada uma hashtag o seu contador e' inicializado a 1!
 *
 * @param 'htst' a HTST na qual inserir o elemento.
 * @param 'k' o nome da nova hashtag a inserir.
 */
void insertNewHashtag(HTST htst, Key k) {
    Item i = itemInit(k);
    htableInsert(htst->hashtable, i);
    htst->hashtags[(htst->n)++] = i;
    if (htst->n >= (htst->cap) - 1) expand(htst);

    if (htst->max == NULL || itemCmp(i, htst->max) < 0)
        htst->max = i;

    htst->total++;
}

/**
 * @brief Caso o elementos ja' exista na htst, o hashtag e' incrementada. Caso contrario a hashtag e' inserida na
 *        htst.
 *
 * @param 'htst' a HTST na qual inserir o elemento.
 * @param 'k' o nome da nova hashtag a inserir.
 */
void insertOrIncrementHashtag(HTST htst, Key k) {
    Item h = searchHashtag(htst, k);
    if (h == NULL) {
        insertNewHashtag(htst, k);
    } else {
        incrementValue(h);
        htst->total++;

        if (itemCmp(h, htst->max) < 0)
            htst->max = h;
    }
}

/**
 * @brief Procura uma hashtag com o nome 'k' na htst e devolve-o caso exista; ou NULL caso contrario.
 *
 * @param 'htst' a HTST onde procurar.
 * @param 'k' o nome a procurar.
 *
 * @return O Item caso exista. NULL caso contrario.
 */
Item searchHashtag(HTST htst, Key k) {
    return htableSearch(htst->hashtable, k);
}

/**
 * @brief devolve a hashtag com mais ocurrencias.
 *
 * @param 'htst' a HTST.
 */
Item maxHashtag(HTST htst) {
    return (htst->max);
}

/**
 * @brief devolve o numero total de hashtags (incluindo duplicados).
 *
 * @param 'htst' a HTST.
 */
int totalCount(HTST htst) {
    return htst->total;
}

/**
 * @brief devolve o numero de hashtags (excluindo duplicados).
 *
 * @param 'htst' a HTST.
 */
int elementCount(HTST htst) {
    return htst->n;
}

/**
 * Funcao interna que compara dois pointeiros para Items (= Hashtags*). Usado no qsort.
 */
static int listCompare(const void *a, const void *b) {
    Item h1 = *((Item *) a);
    Item h2 = *((Item *) b);
    return itemCmp(h1, h2);
}

/**
 * @brief Esta funcao ordena o vetor de uma htst, usando a funcao qsort.
 *
 * @param 'htst' a HTST a ordenar.
 */
void htstSort(HTST htst) {
    qsort((void *) htst->hashtags, (unsigned) htst->n, sizeof(Item *), listCompare);
}

/**
 * @brief Executa uma determinada funcao para todos os elementos da HTST, por ordem.
 *
 * @param 'htst' a HTST.
 * @param 'visit' a funcao a executar para cada elemento.
 */
void htstTraverse(HTST htst, void (*visit)(Item)) {
    int i;
    htstSort(htst);
    for (i = 0; i < htst->n; i++) {
        visit(htst->hashtags[i]);
    }
}

/**
 * @brief Liberta toda a memoria associada a uma htst.
 *
 * @param 'htst' a HTST.
 */
void htstFree(HTST htst) {
    htableFree(htst->hashtable, itemFree);
    free(htst->hashtags);
    free(htst);
}
