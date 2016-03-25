#include <stdio.h>
#include <string.h>

#define MAXAEROPORTO 1000
#define NOMEAEROPORTO 3

#define AEROPORTO_ABERTO 1
#define AEROPORTO_FECHADO 0

typedef struct {
	char nome[NOMEAEROPORTO + 1];
	int capacidade;
	short estado;
} Aeroporto;

int func_A(Aeroporto[], int);
void func_I(Aeroporto[],int[][MAXAEROPORTO], int);
void func_F(Aeroporto[], int[][MAXAEROPORTO], int);
void func_G(Aeroporto[], int[][MAXAEROPORTO], int);
void func_R(Aeroporto[], int[][MAXAEROPORTO], int);
void func_S(Aeroporto[], int[][MAXAEROPORTO], int);
void func_N(Aeroporto[], int[][MAXAEROPORTO], int);
void func_P(Aeroporto[], int[][MAXAEROPORTO], int);
void func_Q(Aeroporto[], int[][MAXAEROPORTO], int);
void func_V(Aeroporto[], int[][MAXAEROPORTO], int);
void func_C(Aeroporto[], int[][MAXAEROPORTO], int);
void func_O(Aeroporto[], int[][MAXAEROPORTO], int);
void func_L(Aeroporto[], int[][MAXAEROPORTO], int);
void func_X(Aeroporto[], int[][MAXAEROPORTO], int);

int main() {
	Aeroporto aeroportos[MAXAEROPORTO];
	int voos[MAXAEROPORTO][MAXAEROPORTO];
	int posAeroporto = 0;

	int i, j;
	for (int i = 0; i < MAXAEROPORTO; i++) {
		for (int j = 0; j < MAXAEROPORTO; j++) {
			voos[i][j] = 0;
		}
	}

	int option;
	short continua = 1;
	while (continua) {
		option = getchar();
		switch (option) {
		case 'A':
			//utiliza o valor de retorno de func_A para incrementar (ou não) a posicao do ultimo aeroporto adicionado
			posAeroporto += func_A(aeroportos, posAeroporto);
			break;
		case 'I':
			func_I(aeroportos, voos, posAeroporto);
			break;
		case 'F':
			func_F(aeroportos, voos, posAeroporto);
			break;
		case 'G':
			func_G(aeroportos, voos, posAeroporto);
			break;
		case 'R':
			func_R(aeroportos, voos, posAeroporto);
			break;
		case 'S':
			func_S(aeroportos, voos, posAeroporto);
			break;
		case 'N':
			func_N(aeroportos, voos, posAeroporto);
			break;
		case 'P':
			func_P(aeroportos, voos, posAeroporto);
			break;
		case 'Q':
			func_Q(aeroportos, voos, posAeroporto);
			break;
		case 'V':
			func_V(aeroportos, voos, posAeroporto);
			break;
		case 'C':
			func_C(aeroportos, voos, posAeroporto);
			break;
		case 'O':
			func_O(aeroportos, voos, posAeroporto);
			break;
		case 'L':
			func_L(aeroportos, voos, posAeroporto);
			break;
		case 'X':
			func_X(aeroportos, voos, posAeroporto);
			continua = 0;
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

int conexoes(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int aeroporto, int posMax) {
	int i = 0, conexoes = 0;
	for(i = 0; i < posMax; i++) {
		if (voos[aeroporto][i]  > 0 || voos[i][aeroporto]  > 0)
			conexoes++;
	}
	return conexoes;
}

/* Le do stdin o NOME e a CAPACIDADE do novo aeroporto.
 * Caso haja espaco no vetor aeroportos e a capacidade seja valida, o novo
 * aeroporto e adicionado e o valor 1 e devolvido. Caso contrario, nada
 * acontece e o valor 0 e devolvido.
 *
 * INPUT: Aeroporto[] - vetor de Aeroporto ao qual o novo aeroporto vai ser adicionado
 *        int         - indice onde o novo aeroporto vai ser colocado
 * OUTPUT: 1 se o aeroporto foi adicionado com sucesso
 *         0 caso contrario
 */
int func_A(Aeroporto aeroportos[], int pos) {
	Aeroporto aero;
	scanf("%s %d", aero.nome, &aero.capacidade);
	if(pos < MAXAEROPORTO && aero.capacidade > 0) {
		aero.estado = 1;
		aeroportos[pos] = aero;
		return 1;
	}
	return 0;
}

void func_I(Aeroporto aeroportos[],int voos[][MAXAEROPORTO], int posMax){
	int capacidade, pos;
	char nome[NOMEAEROPORTO + 1];
	scanf("%s %d", nome, &capacidade);
	pos = procuraAeroporto(aeroportos, posMax, nome);
	if ((pos==-1) || (aeroportos[pos].estado==0) || (voosTotais(aeroportos, voos, pos, posMax)>(aeroportos[pos].capacidade+capacidade)))
		printf("*Capacidade de %s inalterada\n",nome);
	else {(aeroportos[pos].capacidade=aeroportos[pos].capacidade+capacidade); }
}

void func_F(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax) {
	char nome1[NOMEAEROPORTO + 1], nome2[NOMEAEROPORTO + 1];
	scanf("%s %s", nome1, nome2);
	int pos1 = procuraAeroporto(aeroportos, posMax, nome1);
	int pos2 = procuraAeroporto(aeroportos, posMax, nome2);
	if (pos1 >= 0 && pos2 >= 0 &&
	    voosTotais(aeroportos, voos, pos1, posMax) + 2 <= aeroportos[pos1].capacidade &&
	    voosTotais(aeroportos, voos, pos2, posMax) + 2 <= aeroportos[pos2].capacidade &&
	    aeroportos[pos1].estado == AEROPORTO_ABERTO && aeroportos[pos2].estado == AEROPORTO_ABERTO) {
		voos[pos1][pos2]++;
		voos[pos2][pos1]++;
	} else {
		printf("*Impossivel adicionar voo RT %s %s\n", nome1, nome2);
	}
}

void func_G(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax){
	char nome1[NOMEAEROPORTO + 1], nome2[NOMEAEROPORTO + 1];
	scanf("%s %s", nome1, nome2);
	int pos1 = procuraAeroporto(aeroportos, posMax, nome1);
	int pos2 = procuraAeroporto(aeroportos, posMax, nome2);
	if (pos1 >= 0 && pos2 >= 0 &&
	    voosTotais(aeroportos, voos, pos1, posMax) + 1 <= aeroportos[pos1].capacidade &&
	    voosTotais(aeroportos, voos, pos2, posMax) + 1 <= aeroportos[pos2].capacidade &&
	    aeroportos[pos1].estado == AEROPORTO_ABERTO && aeroportos[pos2].estado == AEROPORTO_ABERTO)
		voos[pos1][pos2]++;
	else {
		printf("*Impossivel adicionar voo %s %s\n", nome1, nome2);
	}
}


void func_R(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax) {
	char nome1[NOMEAEROPORTO + 1], nome2[NOMEAEROPORTO + 1];
	scanf("%s %s", nome1, nome2);
	int pos1 = procuraAeroporto(aeroportos, posMax, nome1);
	int pos2 = procuraAeroporto(aeroportos, posMax, nome2);
	if (pos1 >= 0 && pos2 >= 0 && voos[pos1][pos2] > 0) {
		voos[pos1][pos2]--;
	} else {
		printf("*Impossivel remover voo %s %s\n", nome1, nome2);
	}
}

void func_S(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax){
	char nome1[NOMEAEROPORTO + 1], nome2[NOMEAEROPORTO + 1];
	scanf("%s %s", nome1, nome2);
	int pos1 = procuraAeroporto(aeroportos, posMax, nome1);
	int pos2 = procuraAeroporto(aeroportos, posMax, nome2);
	if (pos1 >= 0 && pos2 >= 0 && voos[pos1][pos2] > 0 && voos[pos2][pos1]>0) {
		voos[pos1][pos2]--;
		voos[pos2][pos1]--;
	} else {
		printf("*Impossivel remover voo RT %s %s\n", nome1, nome2);
	}
}

void func_N(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax) {
	char nome1[NOMEAEROPORTO + 1], nome2[NOMEAEROPORTO + 1];
	scanf("%s %s", nome1, nome2);
	int pos1 = procuraAeroporto(aeroportos, posMax, nome1);
	int pos2 = procuraAeroporto(aeroportos, posMax, nome2);
	if (pos1 >= 0 && pos2 >= 0) {
		printf("Voos entre cidades %s:%s:%d:%d\n",
		       nome1, nome2, voos[pos1][pos2], voos[pos2][pos1]);
	} else {
		if (pos1 < 0) {
			printf("*Aeroporto %s inexistente\n", nome1);
		}
		if (pos2 < 0) {
			printf("*Aeroporto %s inexistente\n", nome2);
		}
	}
}

void func_P(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax){
	int aeroMaxRotas = 0;
	int i;
	for (i = 0; i < posMax; i++) {
		if ((voosTotais(aeroportos, voos, i, posMax)) > (voosTotais(aeroportos, voos, aeroMaxRotas, posMax)))
			aeroMaxRotas = i;
	}
	printf("Aeroporto com mais rotas %s:%d:%d\n", aeroportos[aeroMaxRotas].nome,
	       voosPartida(aeroportos, voos, aeroMaxRotas, posMax),
	       voosChegada(aeroportos, voos, aeroMaxRotas, posMax));
}

void func_Q(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax) {
	int i = 0, aeroporto = 0, conex = 0;
	for (i = 0; i < posMax; i++) {
		int con = conexoes(aeroportos, voos, i, posMax);
		if(con > conex) {
			aeroporto = i;
			conex = con;
		}
	}
	printf("Aeroporto com mais ligacoes %s:%d\n", aeroportos[aeroporto].nome, conex);
}

void func_V(Aeroporto aeroportos[], int voos[][MAXAEROPORTO],int posMax){
	int i,j,nrmaxvoos=voos[0][0];
	Aeroporto partida, chegada;

	for (i = 0; i < posMax; i++) {
		for (j = 0; j < posMax; j++) {
			if (voos[i][j]>nrmaxvoos) {
				nrmaxvoos=voos[i][j];
				partida=aeroportos[i];
				chegada=aeroportos[j];
			}
		}
	}
	printf("Voo mais popular %s:%s:%d\n", partida.nome, chegada.nome, nrmaxvoos);
}

void func_C(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax) {
	char nome[NOMEAEROPORTO + 1];
	scanf("%s", nome);
	int pos = procuraAeroporto(aeroportos, posMax, nome);
	if(pos >= 0) {
		aeroportos[pos].estado = AEROPORTO_FECHADO;
		int i;
		for(i = 0; i < posMax; i++) {
			voos[pos][i] = 0;
			voos[i][pos] = 0;
		}
	} else {
		printf("*Aeroporto %s inexistente\n", nome);
	}
}

void func_O(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax) {
	char nome[NOMEAEROPORTO+1];
	scanf("%s", nome);
	int pos = procuraAeroporto(aeroportos, posMax, nome);
	if (pos>=0)
		aeroportos[pos].estado = AEROPORTO_ABERTO;
	else
		printf("*Aeroporto %s inexistente\n",nome);

}

void func_L(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax) {
	int option;
	scanf("%d", &option);
	switch (option) {
	case 0: {
		int i = 0;
		for (i = 0; i < posMax; i++) {
			printf("%s:%d:%d:%d\n", aeroportos[i].nome, aeroportos[i].capacidade,
			       voosPartida(aeroportos, voos, i, posMax), voosChegada(aeroportos, voos, i, posMax));
		}
		break;
	}
	case 1: {
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
	case 2: {
		int hist[MAXAEROPORTO] = {0}, i; //Nota do enunciado
		for(i = 0; i < posMax; i++) {
			hist[voosTotais(aeroportos, voos, i, posMax)] += 1;
		}
		for(i = 0; i < MAXAEROPORTO; i++) {
			if(hist[i] != 0) {
				printf("%d:%d\n", i, hist[i]);
			}
		}
		break;
	}
	}
}

void func_X(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int posMax) {
	int i, j, totalvoos = 0;
	for (i = 0; i < posMax; i++) {
		for (j = 0; j < posMax; j++) {
			totalvoos += voos[i][j];
		}
	}
	printf("%d:%d\n", totalvoos, posMax);
}
