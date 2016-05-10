#ifndef HASHTAGST_H
#define HASHTAGST_H

#include "item.h"

typedef struct hashtagstruct *HTST;

HTST htstInit();

void insertNewHashtag(HTST, char*);
void insertOrIncrementHashtag(HTST, char*);
Hashtag searchHashtag(HTST, char*);
Hashtag maxHashtag(HTST);
void htstShow(HTST);
void htstTraverse(HTST, void (*)(Hashtag));

void htstFree(HTST);

#endif
