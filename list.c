/**
 * @file item.c
 * @brief Implementacao de uma lista generica.
 */
#include <stdlib.h>

#include "item.h"
#include "list.h"

/**
 * @brief estrutura que representa um no'.
 *
 * item e' um Item.
 * next e' um ponteiro que aponta para o proximo no'.
 */
struct node {
    Item item;
    struct node *next;
};

/**
 * @brief estrutura que representa uma lista.
 * head e' um ponteiro que aponta para o primeiro no' da lista.
 * next e' um ponteiro que aponta para o proximo no'.
 * size e' um inteiro que contem o comprimento da lista.
 */
struct list {
    listlink head;
    int size;
};

/**
 * @brief Inicializa uma nova lista ligada, com o tamanho a 0, uma vez que e'
 * iniciada sem qualquer item.
 *
 * @return A nova lista.
 */
List listInit() {
    List l = (List) malloc(sizeof(struct list));
    l->size = 0;
    l->head = NULL;
    return l;
}

/**
 * @brief Cria um novo no'.
 *
 * @param 'm' um Item.
 *
 * @return O novo no'.
 */
static listlink newListNode(Item m) {
    listlink newnode = (listlink) malloc(sizeof(struct node));
    newnode->item = m;
    newnode->next = NULL;
    return newnode;
}

/**
 * @brief Insere um novo item na lista.
 *
 * @param 'l' a lista onde vai ser inserido.
 * @param 'm' o item a inserir.
 */
void listInsert(List l, Item m) {
    listlink node = newListNode(m);
    node->next = l->head;
    l->head = node;
    l->size++;
}

/**
 * @brief Procura um item na lista.
 *
 * @param 'l' a lista onde procurar.
 * @param 'k' a Key (nome do item) a ser procurada.
 *
 * @return O item caso encontre, se nao devolve NULL.
 */
Item listSearch(List l, Key k) {
    listlink node = l->head;
    while (node != NULL)
        if (itemCmpKey(key(node->item), k) == 0)
            return node->item;
        else
            node = node->next;
    return NULL;
}

/**
 * @brief Remove o primeiro item da lista.
 *
 * @param 'l' uma lista.
 *
 * @return O item removido.
 */
Item listPop(List l) {
    if (l->head == NULL) return NULL;
    listlink t = l->head;
    Item i = t->item;
    l->head = t->next;
    free(t);
    return i;
}

/**
 * @brief Liberta toda a memoria alocada duma lista.
 *
 * @param 'l' a lista a ser libertada
 * @param 'elemFree' a funcao a ser chamada para cada elemento.
 */
void listFree(List l, void (*elemFree)(Item)) {
    listlink curr, head = l->head;
    while ((curr = head) != NULL) {
        head = head->next;
        if (elemFree != NULL)
            elemFree(curr->item);
        free(curr);
    }
    free(l);
}
