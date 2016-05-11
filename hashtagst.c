#include <stdlib.h>
#include <stdio.h>

#include "hashtagst.h"
#include "hash.h"

#define INITIAL_SIZE 64
#define INCREMENT_SIZE 2048

struct hashtagstruct {
    Htable hashtable;
    Hashtag *hashtags;
    int n, cap, total;
    Hashtag max;
};

HTST htstInit() {
    HTST htst = (HTST) malloc(sizeof(struct hashtagstruct));

    htst->hashtable = HTableInit();
    htst->n = 0;
    htst->cap = INITIAL_SIZE;
    htst->total = 0;
    htst->hashtags = (Hashtag *) malloc(htst->cap * sizeof(Hashtag));
    htst->max = NULL;

    return htst;
}

static void expand(HTST htst) {
    htst->cap += INCREMENT_SIZE;
    htst->hashtags = (Hashtag *) realloc(htst->hashtags, htst->cap * sizeof(Hashtag));
}

void insertNewHashtag(HTST htst, char *k) {
    Item i = itemInit(k);
    htableInsert(htst->hashtable, i);
    htst->hashtags[(htst->n)++] = i;
    if (htst->n >= (htst->cap) - 1) expand(htst);

    if (htst->max == NULL || itemCmp(i, htst->max) < 0)
        htst->max = i;

    htst->total++;
}

void insertOrIncrementHashtag(HTST htst, char *k) {
    Hashtag h = searchHashtag(htst, k);
    if (h == NULL) {
        insertNewHashtag(htst, k);
    } else {
        incrementValue(h);
        htst->total++;

        if (itemCmp(h, htst->max) < 0)
            htst->max = h;
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

int listCompare(const void *a, const void *b) {
    Hashtag h1 = *((Hashtag *) a);
    Hashtag h2 = *((Hashtag *) b);
    return itemCmp(h1, h2);
}

void htstSort(HTST htst) {
    qsort((void *) htst->hashtags, (size_t) htst->n, sizeof(Hashtag *), listCompare);
}

void htstTraverse(HTST htst, void (*visit)(Hashtag)) {
    int i;
    for (i = 0; i < htst->n; i++) {
        visit(htst->hashtags[i]);
    }
}

void htstFree(HTST htst) {
    HTableFree(htst->hashtable, itemFree);
    free(htst->hashtags);
    free(htst);
}
