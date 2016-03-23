#include <stdio.h>
#include <string.h>

#define MAXAEROPORTO 1000
#define NOMEAEROPORTO 3

typedef struct {
		char nome[NOMEAEROPORTO + 1];
		int capacidade;
		short estado;
} Aeroporto;

void func_A(Aeroporto[], int);

void func_F(Aeroporto[], int[][MAXAEROPORTO], int);


void func_L(Aeroporto[], int[][MAXAEROPORTO], int);

int main() {
		Aeroporto aeroportos[MAXAEROPORTO];
		int voos[MAXAEROPORTO][MAXAEROPORTO];
		int posAeroporto = 0;

		int option;
		while ((option = getchar()) != 'X') {
				switch (option) {
				case 'A':
						func_A(aeroportos, posAeroporto);
						posAeroporto++;
						break;
				case 'F':
						func_F(aeroportos, voos, posAeroporto);
						break;
				case 'L':
						func_L(aeroportos, voos, posAeroporto);
						break;
				}
		}


		return 0;
}

int procuraAeroporto(Aeroporto aeroportos[], int pos, char nome[]) {
		int i;
		for (i = 0; i < pos; i++) {
				if (!strcmp(aeroportos[i].nome, nome)) {
						return i;
				}
		}
		return -1;
}

int voosPartida(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int aeroporto, int posMax) {
		int i, partidas = 0;
		for (i = 0; i < posMax; i++) {
				partidas += voos[aeroporto][i];
		}
		return partidas;
}

int voosChegada(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int aeroporto, int posMax) {
		int i, chegadas = 0;
		for (i = 0; i < posMax; i++) {
				chegadas += voos[i][aeroporto];
		}
		return chegadas;
}

int voosTotais(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int aeroporto, int posMax) {
		int i, voosTotais = 0;
		for (i = 0; i < posMax; i++) {
				voosTotais += voos[aeroporto][i];
				voosTotais += voos[i][aeroporto];
		}
		return voosTotais;
}

void func_A(Aeroporto aeroportos[], int pos) {
		Aeroporto aero;
		scanf("%s %d", aero.nome, &aero.capacidade);
		aero.estado = 1;
		aeroportos[pos] = aero;
}

void func_F(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax) {
		char nome1[NOMEAEROPORTO + 1], nome2[NOMEAEROPORTO + 1];
		scanf("%s %s", nome1, nome2);
		int pos1 = procuraAeroporto(aeroportos, posMax, nome1);
		int pos2 = procuraAeroporto(aeroportos, posMax, nome2);
		if (pos1 >= 0 && pos2 >= 0 &&
		    voosTotais(aeroportos, voos, pos1, posMax) + 2 <= aeroportos[pos1].capacidade &&
		    voosTotais(aeroportos, voos, pos2, posMax) + 2 <= aeroportos[pos2].capacidade) {
				voos[pos1][pos2]++;
				voos[pos2][pos1]++;
		} else {
				printf("*Impossivel adicionar voo %s %s\n", nome1, nome2);
		}
}

void func_L(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax) {
		char option;
		scanf("%c", &option);
		switch (option) {
		case '0': {
				int i = 0;
				for (i = 0; i < posMax; i++) {
						printf("%s:%d:%d:%d\n", aeroportos[i].nome, aeroportos[i].capacidade,
						       voosPartida(aeroportos, voos, i, posMax), voosChegada(aeroportos, voos, i, posMax));
				}
				break;
		}
		case '1': {
				int i = 0, j = 0, k = 0;
				int indexes[posMax];
				for(k = 0; k < posMax; k++) {
						indexes[k] = k;
				}
				for (i = 0; i < posMax; i++) {
						int min = i;
						for (j = i+1; j < posMax; j++)
								if (strcmp(aeroportos[indexes[j]].nome, aeroportos[indexes[min]].nome) < 0)
										min = j;
						k = indexes[i]; indexes[i] = indexes[min]; indexes[min] = k;
						printf("%s:%d:%d:%d\n", aeroportos[indexes[i]].nome, aeroportos[indexes[i]].capacidade,
						       voosPartida(aeroportos, voos, indexes[i], posMax), voosChegada(aeroportos, voos, indexes[i], posMax));
				}
				break;
		}
		case '2': {

				break;
		}
		}
}
