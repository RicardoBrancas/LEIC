#include <stdio.h>

#define MAXAEROPORTO 1000
#define NOMEAEROPORTO 3

typedef struct {
  char nome[NOMEAEROPORTO+1];
  int capacidade;
  short estado;
} Aeroporto;

void func_A();

int main(){
  Aeroporto aeroportos[MAXAEROPORTO];
  int voos[MAXAEROPORTO][MAXAEROPORTO];
  int posAeroporto;

  int option;
  while((option = getchar()) != 'X') {
    switch(option) {
      case 'A':
        func_A();
        break;
    }
  }


  return 0;
}

void func_A() {
  scanf("%s %d");
}
