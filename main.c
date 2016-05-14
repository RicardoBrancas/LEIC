#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "hashtagst.h"

/**
 * Vetor de caracteres delimitam diferentes paralvras.
*/
static const char separators[] = {' ', '\t', ',', ';', '.', '?', '!', '"', '\n', ':', '\0'};

static HTST htst;

void func_A();

void func_S();

void func_M();

void func_L();

int main() {
    htst = htstInit();

    int option;
    while (1) {
        option = getchar();
        switch (option) {
            case 'a':
                func_A();
                break;
            case 's':
                func_S();
                getchar(); //Le o '\n' introduzido pelo utilizador no final de cada linha
                break;
            case 'm':
                func_M();
                getchar(); //Le o '\n' introduzido pelo utilizador no final de cada linha
                break;
            case 'l':
                func_L();
                getchar();
                break;
            case 'x':
                //'X' termina o programa, return do main()
                htstFree(htst);
                return 0;
            default:
                printf("ERRO: Comando desconhecido\n");
        }

    }

    return EXIT_FAILURE;
}

/**
 * @brief Identifica os hashtags no texto inserido e insere-os na hashtable.
 *
 * @param 'line' A string inserida pelo utilizador.
 */
void split(char *line) {
    char *token = strtok(line, separators);
    while (token != NULL) {
        if (strlen(token) > 0) {
            if (token[0] == '#') {
                insertOrIncrementHashtag(htst, token);
            }
        }
        token = strtok(NULL, separators);
    }
}

/**
 * @brief Comando "a", que insere hashtags na hashtable limitando o texto inserido
 *        a 140 caracteres.
 */
void func_A() {
    char str[140];
    fgets(str, 140, stdin);
    split(str);
}

/**
 * @brief Comando "s", apresenta no ecra o total de hashtags diferentes e o total
 *        de ocorrencias.
 */
void func_S() {
    printf("%d %d\n", elementCount(htst), totalCount(htst));
}

/**
 * @brief Comando "m", apresenta no ecra o hashtag mais popular
 */
void func_M() {
    itemPrint(maxHashtag(htst));
}

/**
 * @brief Comando "l", apresenta no ecra os hashtags ordenados pelo numero
 * de ocorrencias e em caso de igualdade, por ordem alfabetica.
 */
void func_L() {
    htstTraverse(htst, itemPrint);
}
