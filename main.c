#include <stdio.h>
#include <string.h>

#define MAXAEROPORTO 1000
#define NOMEAEROPORTO 3

typedef struct {
  char nome[NOMEAEROPORTO+1];
  int capacidade;
  int voosPartida;
  int voosChegada;
  short estado;
} Aeroporto;

void func_A(Aeroporto[], int);

void func_F(Aeroporto[], int[][MAXAEROPORTO], int);







void func_L(Aeroporto[], int[][MAXAEROPORTO], int);

int main(){
  Aeroporto aeroportos[MAXAEROPORTO];
  int voos[MAXAEROPORTO][MAXAEROPORTO];
  int posAeroporto = 0;

  int option;
  while((option = getchar()) != 'X') {
    switch(option) {
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
  for(i = 0; i < pos; i++) {
    if(!strcmp(aeroportos[i].nome, nome)) {
      return i;
    }
  }
  return -1;
}

void func_A(Aeroporto aeroportos[], int pos) {
  Aeroporto aero;
  scanf("%s %d", aero.nome, &aero.capacidade);
  aero.estado = 1;
  aero.voosPartida = 0;
  aero.voosChegada = 0;
  aeroportos[pos] = aero;
}

void func_F(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int pos) {
  char nome1[NOMEAEROPORTO+1], nome2[NOMEAEROPORTO+1];
  scanf("%s %s", nome1, nome2);
  int pos1 = procuraAeroporto(aeroportos, pos, nome1);
  int pos2 = procuraAeroporto(aeroportos, pos, nome2);
  if (pos1 >= 0 && pos2 >= 0 &&
     aeroportos[pos1].voosPartida+aeroportos[pos1].voosChegada+1 <= aeroportos[pos1].capacidade &&
      aeroportos[pos2].voosPartida+aeroportos[pos2].voosChegada+1 <= aeroportos[pos2].capacidade) {
        aeroportos[pos1].voosPartida++;
        aeroportos[pos1].voosChegada++;
        aeroportos[pos2].voosPartida++;
        aeroportos[pos2].voosChegada++;
        voos[pos1][pos2]++;
        voos[pos2][pos1]++;
  } else {
    printf("*Impossivel adicionar voo %s %s\n", nome1, nome2);
  }
}

void func_L(Aeroporto aeroportos[], int voos[][MAXAEROPORTO], int pos) {
  int option;
  scanf("%d", &option);
  switch(option) {
    case 0: {
      int i = 0;
      for(i = 0; i < pos; i++) {
        printf("%s:%d:%d:%d\n", aeroportos[i].nome, aeroportos[i].capacidade,
         aeroportos[i].voosPartida, aeroportos[i].voosChegada);
      }
    }

  }
}
