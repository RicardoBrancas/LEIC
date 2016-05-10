#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "hashtagst.h"


#define NUMSEP 11
static const char separators[] = {' ','\t',',',';','.','?','!','"','\n',':','\0'};

static HTST htst;

void func_A();
void func_S();
void func_M();
void func_L();
void func_X();

int main() {
	htst = htstInit();

	int option;
	while (1) {
		option = getchar();
		switch (option) {
		case 'a':
			getchar(); //space
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
			// func_X();
			//'X' termina o programa, return do main()
			return 0;
			break;
		default:
			printf("ERRO: Comando desconhecido\n");
		}

	}

	htstFree(htst);
	return 0;

}

void split(char *line) {
	char * token = strtok(line, separators);
	while (token !=NULL) {
		if(strlen(token)>0) {
			if(token[0] == '#') {
                insertOrIncrementHashtag(htst, token);
			}
		}
		token = strtok(NULL, separators);
	}
}

void func_A() {
	char str[140];
	fgets(str, 140, stdin);
	split(str);
}

void func_S(){
	printf("%d %d\n", totalCount(htst), elementCount(htst));
}

void func_M() {
	itemPrint(maxHashtag(htst));
}

void func_L() {
	htstSort(htst);
	htstTraverse(htst, itemPrint);
}
