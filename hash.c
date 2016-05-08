/**
 * @file hash.c
 * @brief Implementacao de tabelas de dispersao com resolucao por encadeamento
 *        externo.
 */
#include <stdlib.h>
#include <string.h>

#include "hash.h"
/* Lista de primos auxiliar, deve ser aumentada se precisarmos de
 * guardar mais elementos. */
#define PRIMES 9
static int primes[PRIMES] = {1031, 4099, 16127, 34651, 68111, 131071, 260999,
	                     524287, 999983};

#define MAX_LOAD 0.8

/**
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
Htable HTableInit() {
	Htable h = (Htable) malloc(sizeof(struct hashtable));
	h->n = h->m = 0;
	h->p = primes[h->m];
	h->lists = (List*) malloc(h->p * sizeof(List));
	int i = 0;
	for(i = 0; i < h->p; i++) {
		h->lists[i] = listInit();
	}
	return h;
}

static float loadfactor(Htable h) {
	return h->n / h->p;
}

/**
 * @brief devolve o Item com a chave Key se ele existir na tabela. Caso
 *		  contrario devolve NULL.
 */
Item HTableSearch(Htable h, Key key) {
	int index = hashfunc(key, h->p);
	return listSearch(h->lists[index], key);
}


void HTableDelete(Htable h, Item i) {
	int index = hashfunc(key(i), h->p);
	listDelete(h->lists[index], i);
}

static void HTableExpand(Htable h) {
	int i, oldP;
	List *oldlists;
	listlink l;

	if((h->p)+1 >= PRIMES)
		return;

	oldlists = h->lists;
	oldP = h->p;

	h->n = 0;
	(h->m)++;
	h->p = primes[++(h->m)];
	h->lists = (List*) malloc((h->p) * sizeof(List));

	for(i = 0; i < oldP; i++) {
		if((l = (oldlists[i]->cabeca)) != NULL) {
			HTableInsert(h, l->item);
			while ((l = l->next) != NULL) {
				HTableInsert(h, l->item);
			}
		}
		listFree(oldlists[i], NULL);
	}
	free(oldlists);
}

/**
 * Esta funcao nao duplica o Item passado passado como argumento. E' da
 * responsabilidade do utilizador garantir que a memoria se mantem disponivel
 * ate ao fim de vida da tabela de dispersao
 */
void HTableInsert(Htable h, Item i) {
	int index = hashfunc(key(i), h->p);
	listInsert(h->lists[index], i);

	if(loadfactor(h) > MAX_LOAD) {
		HTableExpand(h);
	}
}

/**
 * Esta funcao nao liberta a memoria dos Item contidos!
 */
void HTableFree(Htable h, void (*elemFree)(Item)) {
	int i;
	for(i = 0; i < h->p; i++) {
		listFree(h->lists[i], elemFree);
	}
	free(h->lists);
	free(h);
}
