#include <stdlib.h>

#include "item.h"
#include "list.h"

List listInit(){
	List l = (List) malloc(sizeof(struct list));
	l->size = 0;
	l->head=NULL;
	return l;
}

listlink newListNode(Item m){
	listlink newnode;
	newnode = (listlink)malloc(sizeof(struct node));
	newnode->item = m;
	newnode->next = NULL;
	return newnode;
}

void listInsert(List l, Item m){
	listlink new_node;
	new_node = newListNode(m);
	l->size++;
	l->head = new_node;
}

Item listSearch(List l, Key k){
	listlink node;
	node = l->head;
	while(node != NULL)
		if(key(node->item) == k)
			return node->item;
		else
			node = node->next;
    return NULL;
}


void listFree(List l, void (*elemFree)(Item)) {
	listlink curr, head=l->head;
	while ((curr = head) != NULL) {
		head = head->next;
		if(elemFree != NULL)
			elemFree(curr->item);
		free (curr);
	}
	free(l);
}
