#include <stdlib.h>
#include <stdio.h>

#include "hashtagst.h"
#include "btree.h"
#include "hash.h"

#define PRIMES 9
static int primes[PRIMES] = {1031, 4099, 16127, 34651, 68111, 131071, 260999,
	                     524287, 999983};

struct hashtagstruct {
	BTree tree;
	Htable hashtable;
	Hashtag *hashtags;
	int p, m, n;
	Hashtag max;
	int total;
};

HTST htstInit() {
	HTST htst = (HTST) malloc(sizeof(struct hashtagstruct));
	// htst->tree = btreeInit();
	htst->hashtable = HTableInit();
        htst->m = 0;
        htst->p = primes[htst->m];
	htst->hashtags = (Hashtag*) malloc(htst->p * sizeof(Hashtag));
	htst->n = 0;
	htst->max=NULL;
	htst->total=0;
	return htst;
}

void insertNewHashtag(HTST htst, char *k) {
	Item i = itemInit(k);
	HTableInsert(htst->hashtable, i);
	htst->hashtags[(htst->n)++] = i;
	if (htst->max == NULL) {
		htst->max = i;
	} else if (i->n == htst->max->n) {
        if (itemCmpKey(key(i), key(htst->max)) < 0)
            htst->max = i;
    }
	htst->total++;
	// btreeInsert(htst->tree, i);
}

static void expand(HTST htst) {
        if((htst->m)+1 >= PRIMES)
		exit(-1);
        htst->p = primes[++(htst->m)];
        htst->hashtags = (Hashtag*) realloc(htst->hashtags, htst->p * sizeof(Hashtag));
}

void insertOrIncrementHashtag(HTST htst, char *k) {
	Hashtag h = searchHashtag(htst, k);
	if (h == NULL) insertNewHashtag(htst, k);
	else {
		(h->n)++;
                
		htst->total++;
		// btreeBalance(htst->tree, h);
	}
	if (htst->n == (htst->p)-1) expand(htst);
	if (h != NULL) {
		if (h->n > htst->max->n) {
			htst->max = h;
		} else if (h->n == htst->max->n) {
			if (itemCmpKey(key(h), key(htst->max)) < 0)
				htst->max = h;
        }
    }
}

Item searchHashtag(HTST htst, char *k) {
	return HTableSearch(htst->hashtable, k);
}

Item maxHashtag(HTST htst) {
	return (htst->max);
}

int totalCount(HTST htst) {
	return htst->n;
}

int elementCount(HTST htst) {
	return htst->total;
}

int listCompare(const void* a,const void* b) {
	Hashtag h1 = *((Hashtag*) a);
	Hashtag h2 = *((Hashtag*) b);
	int i = itemCmp(h1, h2);
	if (i != 0) return i;
	return itemCmpKey(key(h1), key(h2));
}

void htstSort(HTST htst) {
	qsort((void*) htst->hashtags, htst->n, sizeof(Hashtag*), listCompare);
}

void htstTraverse(HTST htst, void (*visit)(Hashtag)) {
	int i;
	for(i = 0; i < htst->n; i++) {
		visit(htst->hashtags[i]);
	}
}

void htstFree(HTST htst) {
	HTableFree(htst->hashtable, itemFree);
	// btreeFree(htst->tree, itemFree);
	free(htst->hashtags);
	free(htst);
}
