/**
 * @file hashtagst.h
 * @brief Implementacao da estrutura utilizada para guardar hashtags.
 */
#ifndef HASHTAGST_H
#define HASHTAGST_H

#include "item.h"

typedef struct hashtagstruct *HTST;

HTST htstInit();

void insertOrIncrementHashtag(HTST, char *);
Hashtag searchHashtag(HTST, char *);
Hashtag maxHashtag(HTST);
int totalCount(HTST);
int elementCount(HTST);
void htstSort(HTST);
void htstTraverse(HTST, void (*)(Hashtag));

void htstFree(HTST);

#endif
