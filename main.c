#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAXAEROPORTO 1000
#define MAXVOOS 1001
#define NOMEAEROPORTO 3

#define AEROPORTO_ABERTO 1
#define AEROPORTO_FECHADO 0

typedef struct {
    char nome[NOMEAEROPORTO + 1]; 
    int capacidade;
    short estado;
} Aeroporto;

Aeroporto aeroportos[MAXAEROPORTO];
int voos[MAXAEROPORTO][MAXAEROPORTO];
int nAeroportos;

void func_A();
void func_I();
void func_F();
void func_G();
void func_R();
void func_S();
void func_N();
void func_P();
void func_Q();
void func_V();
void func_C();
void func_O();
void func_L();
void func_X();

/* Esta funcao inicia um ciclo infinito. Em cada iteracao do ciclo e lido
 * um caracter do STDIN; caso esse caracter corresponda a uma funcao do programa,
 * essa funcao e chamada e o ciclo recomeca. No caso de um comando desconhecido
 * e' mostrada uma mensagem de erro.
 * No fim de cada ciclo e ainda lido outro caracter que corresponde ao '\n' no
 * fim de cada linha.
 * No caso co comando 'X', apos a execucao da funcao correspondente e feito um
 * return e a funcao main termina.
 */
int main() {
    int option;
    while (1) {
        option = getchar();
        switch (option) {
            case 'A':
                func_A();
                break;
            case 'I':
                func_I();
                break;
            case 'F':
                func_F();
                break;
            case 'G':
                func_G();
                break;
            case 'R':
                func_R();
                break;
            case 'S':
                func_S();
                break;
            case 'N':
                func_N();
                break;
            case 'P':
                func_P();
                break;
            case 'Q':
                func_Q();
                break;
            case 'V':
                func_V();
                break;
            case 'C':
                func_C();
                break;
            case 'O':
                func_O();
                break;
            case 'L':
                func_L();
                break;
            case 'X':
                func_X();
                //'X' termina o programa, return do main()
                return EXIT_SUCCESS;
            default:
                printf("ERRO: Comando desconhecido\n");
        }
        getchar(); //Le o '\n' introduzido pelo utilizador no final de cada linha
    }
    //Em funcionamento normal esta linha nunca e' executada.
    return EXIT_FAILURE;
}

/* Procura no vetor aeroportos um aeroporto com o nome igual ao passado como
 * argumento e debvolve a posicao desse aeroporto. Caso exista mais que 1,
 * a posicao do primeiro e devolvida. Caso nenhum seja encontrado,
 * e devolvido -1.
 *
 * @param nome Nome do aeroporto a procurar
 * @return a posicao no vetor caso o aeroporto exista, -1 caso contrario
 */
int procuraAeroporto(char nome[]) {
    int i;
    for (i = 0; i < nAeroportos; i++) {
        if (!strcmp(aeroportos[i].nome, nome)) {
            return i;
        }
    }
    return -1;
}

/* Conta o numero de voos com partida do aeroporto
 * cuja posicao for passada como argumento.
 *
 * @param aeroporto Posicao do aeroporto no vetor aeroportos
 * @return numero de voos de partida
 */
int voosPartida(int aeroporto) {
    int i, partidas = 0;
    for (i = 0; i < nAeroportos; i++) {
        partidas += voos[aeroporto][i];
    }
    return partidas;
}

/* Conta o numero de voos com chegada ao aeroporto
 * cuja posicao for passada como argumento.
 *
 * @param aeroporto Posicao do aeroporto no vetor aeroportos
 * @return numero de voos de chegada
 */
int voosChegada(int aeroporto) {
    int i, chegadas = 0;
    for (i = 0; i < nAeroportos; i++) {
        chegadas += voos[i][aeroporto];
    }
    return chegadas;
}

/* Conta o numero de voos de e para o aeroporto
 * cuja posicao for passada como argumento.
 *
 * @param aeroporto Posicao do aeroporto no vetor aeroportos
 * @return numero de voos total
 */
int voosTotais(int aeroporto) {
    int i, voosTotais = 0;
    for (i = 0; i < nAeroportos; i++) {
        voosTotais += voos[aeroporto][i];
        voosTotais += voos[i][aeroporto];
    }
    return voosTotais;
}

/* Conta o numero de conexoes (aeroportos de tem voos de/para) do aeroporto
 * cuja posicao for passada como argumento.
 *
 * @param aeroporto Posicao do aeroporto no vetor aeroportos
 * @return numero de conexoes
 */
int conexoes(int aeroporto) {
    int i = 0, conexoes = 0;
    for (i = 0; i < nAeroportos; i++) {
        if (voos[aeroporto][i] > 0 || voos[i][aeroporto] > 0)
            conexoes++;
    }
    return conexoes;
}

/* Le do stdin o NOME e a CAPACIDADE do novo aeroporto.
 * Caso haja espaco no vetor aeroportos e a capacidade seja valida, o novo
 * aeroporto e adicionado. Caso contrario, nada acontece.
 * Quando e adicionado um aeroporto, o valor de nAeroportos e incrementado.
 */
void func_A() {
    Aeroporto aero;
    scanf("%s %d", aero.nome, &aero.capacidade);
    if (nAeroportos < MAXAEROPORTO && aero.capacidade > 0) {
        aero.estado = 1;
        aeroportos[nAeroportos++] = aero;
    }
}

/* Le do stdin o NOME e a CAPACIDADE do aeroporto e caso o aeroporto esteja
 * definido e a capacidade lida + a capacidade do aeroporto seja menor do
 * os voos totais do aeroporto, altera a sua capacidade.
 * Caso contra'rio uma messagem de erro e' mostrada.
 */
void func_I() {
    int capacidade, pos;
    char nome[NOMEAEROPORTO + 1];
    scanf("%s %d", nome, &capacidade);
    pos = procuraAeroporto(nome);
    if (pos >= 0 && aeroportos[pos].estado == AEROPORTO_ABERTO &&
        voosTotais(pos) <= (aeroportos[pos].capacidade + capacidade)) {
        aeroportos[pos].capacidade += capacidade;
    } else {
        printf("*Capacidade de %s inalterada\n", nome);
    }
}

/* Le do STDIN o nome dos dois aeroportos. Caso ambos os aeroportos estejam
 * definidos, abertos e tenham 2 voos livres (ida e volta para cada um dos
 * aeroportos), os voos são adicionados na matriz voos.
 * Caso contra'rio uma messagem de erro e' mostrada.
 */
void func_F() {
    char nome1[NOMEAEROPORTO + 1], nome2[NOMEAEROPORTO + 1];
    scanf("%s %s", nome1, nome2);
    int pos1 = procuraAeroporto(nome1);
    int pos2 = procuraAeroporto(nome2);
    if (pos1 >= 0 && pos2 >= 0 &&
        voosTotais(pos1) + 2 <= aeroportos[pos1].capacidade &&
        voosTotais(pos2) + 2 <= aeroportos[pos2].capacidade &&
        aeroportos[pos1].estado == AEROPORTO_ABERTO && aeroportos[pos2].estado == AEROPORTO_ABERTO) {
        voos[pos1][pos2]++;
        voos[pos2][pos1]++;
    } else {
        printf("*Impossivel adicionar voo RT %s %s\n", nome1, nome2);
    }
}

/* Le do STDIN o nome do aeroporto de partida e de chegada. Caso ambos os
 * aeroportos estejam definidos, abertos e tenham 1 voo livre, o voo é adicionado
 * na matriz voos. Caso contra'rio uma messagem de erro e' mostrada.
 */
void func_G() {
    char nome1[NOMEAEROPORTO + 1], nome2[NOMEAEROPORTO + 1];
    scanf("%s %s", nome1, nome2);
    int pos1 = procuraAeroporto(nome1);
    int pos2 = procuraAeroporto(nome2);
    if (pos1 >= 0 && pos2 >= 0 &&
        voosTotais(pos1) + 1 <= aeroportos[pos1].capacidade &&
        voosTotais(pos2) + 1 <= aeroportos[pos2].capacidade &&
        aeroportos[pos1].estado == AEROPORTO_ABERTO && aeroportos[pos2].estado == AEROPORTO_ABERTO) {
        voos[pos1][pos2]++;
    } else {
        printf("*Impossivel adicionar voo %s %s\n", nome1, nome2);
    }
}

/* Le do STDIN o nome dos dois aeroportos. Caso ambos os aeroportos estejam
 * definidos e exista pelo menos um voo do 1o para o 2o aeroporto, e removido
 * um voo da matriz voos. (Nao e necessario verificar se o aeroporto esta'
 * aberto, porque um aeroporto fechado nunca tem nenhum voo associado)
 * Caso contra'rio uma messagem de erro e' mostrada.
 */
void func_R() {
    char nome1[NOMEAEROPORTO + 1], nome2[NOMEAEROPORTO + 1];
    scanf("%s %s", nome1, nome2);
    int pos1 = procuraAeroporto(nome1);
    int pos2 = procuraAeroporto(nome2);
    if (pos1 >= 0 && pos2 >= 0 && voos[pos1][pos2] > 0) {
        voos[pos1][pos2]--;
    } else {
        printf("*Impossivel remover voo %s %s\n", nome1, nome2);
    }
}

/* Le do STDIN o nome dos dois aeroportos. Caso ambos os aeroportos estejam
 * definidos e exista pelo menos um voo do 1o para o 2o aeroporto e do 2o para
 * o 1o sao removidos os respetivos voos. (Nao e' necessario verificar
 * se o aeroporto esta' aberto, porque um aeroporto fechado nunca tem nenhum
 * voo associado). Caso contra'rio uma messagem de erro e' mostrada.
 */
void func_S() {
    char nome1[NOMEAEROPORTO + 1], nome2[NOMEAEROPORTO + 1];
    scanf("%s %s", nome1, nome2);
    int pos1 = procuraAeroporto(nome1);
    int pos2 = procuraAeroporto(nome2);
    if (pos1 >= 0 && pos2 >= 0 && voos[pos1][pos2] > 0 && voos[pos2][pos1] > 0) {
        voos[pos1][pos2]--;
        voos[pos2][pos1]--;
    } else {
        printf("*Impossivel remover voo RT %s %s\n", nome1, nome2);
    }
}

/* Le do STDIN o nome dos dois aeroportos. Caso ambos os aeroportos estejam
 * definidos e' mostrado o numero de voos de/para entre os dois aeroportos.
 * Caso contra'rio uma messagem de erro e' mostrada.
 */
void func_N() {
    char nome1[NOMEAEROPORTO + 1], nome2[NOMEAEROPORTO + 1];
    scanf("%s %s", nome1, nome2);
    int pos1 = procuraAeroporto(nome1);
    int pos2 = procuraAeroporto(nome2);
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

/* Percorre a matriz voos e com a ajuda da função auxiliar voostotais() mostra
 * o aeroporto com o maior numero de voos totais (soma de partidas e chegadas).
 */
void func_P() {
    int aeroMaxRotas = 0;
    int i;
    for (i = 0; i < nAeroportos; i++) {
        if (voosTotais(i) > voosTotais(aeroMaxRotas))
            aeroMaxRotas = i;
    }
    printf("Aeroporto com mais rotas %s:%d:%d\n", aeroportos[aeroMaxRotas].nome,
           voosPartida(aeroMaxRotas),
           voosChegada(aeroMaxRotas));
}

/* Percorre o vetor aeroportos e identifica o aeroporto com mais conexoes
 * (utilizando a funcao auxiliar conexoes()) e mostra o nome e o numero de
 * conexoes desse aeroporto.
 */
void func_Q() {
    int i = 0, aeroporto = 0, conex = 0;
    for (i = 0; i < nAeroportos; i++) {
        int con = conexoes(i);
        if (con > conex) {
            aeroporto = i;
            conex = con;
        }
    }
    printf("Aeroporto com mais ligacoes %s:%d\n", aeroportos[aeroporto].nome, conex);
}

/* Percorre a matriz voos e mostra o voo mais popular (os aeroportos A
 * e B com mais voos de A para B).
 */
void func_V() {
    int i, j, nMaxVoos = voos[0][0];
    Aeroporto partida, chegada;

    for (i = 0; i < nAeroportos; i++) {
        for (j = 0; j < nAeroportos; j++) {
            if (voos[i][j] > nMaxVoos) {
                nMaxVoos = voos[i][j];
                partida = aeroportos[i];
                chegada = aeroportos[j];
            }
        }
    }
    printf("Voo mais popular %s:%s:%d\n", partida.nome, chegada.nome, nMaxVoos);
}

/* Le do STDIN o nome do aeroporto. Caso o aeroporto esteja definido todos
 * os voos de e para esse aeroporto sao removidos e o estado e colocado a
 * AEROPORTO_FECHADO.
 * Caso contra'rio uma messagem de erro e' mostrada.
 */
void func_C() {
    char nome[NOMEAEROPORTO + 1];
    scanf("%s", nome);
    int pos = procuraAeroporto(nome);
    if (pos >= 0) {
        aeroportos[pos].estado = AEROPORTO_FECHADO;
        int i;
        for (i = 0; i < nAeroportos; i++) {
            voos[pos][i] = 0;
            voos[i][pos] = 0;
        }
    } else {
        printf("*Aeroporto %s inexistente\n", nome);
    }
}

/* Le do STDIN o nome do aeroporto. Caso o aeroporto esteja definido o estado
 * e' colocado a AEROPORTO_ABERTO.
 * Caso contra'rio uma messagem de erro e' mostrada.
 */
void func_O() {
    char nome[NOMEAEROPORTO + 1];
    scanf("%s", nome);
    int pos = procuraAeroporto(nome);
    if (pos >= 0)
        aeroportos[pos].estado = AEROPORTO_ABERTO;
    else
        printf("*Aeroporto %s inexistente\n", nome);

}

/* Le do STDIN o tipo de listagem.
 * Caso 0, percorre o vetor aeroportos e mostra
 * o nome, a capacidade e os voos de chegada e partida para cada um.
 * Caso 1, utiliza um vetor de indices temporario para "ordenar" os aeroportos
 * por ordem alfabetica (o vetor de indices e utilizado para que a ordem de insercao
 * dos aeroportos nao seja perdida).
 * Caso 2, percorre os aeroportos e para cada um deles incrementa no histograma a
 * posicao correspondente (numero total de voos). Por fim, percorre o histograma
 * e mostra no ecra o numero de aeroportos com 'n' voos totais (para n != 0).
 */
void func_L() {
    int option;
    scanf("%d", &option);
    switch (option) {
        case 0: {
            int i = 0;
            for (i = 0; i < nAeroportos; i++) {
                printf("%s:%d:%d:%d\n", aeroportos[i].nome, aeroportos[i].capacidade,
                       voosPartida(i), voosChegada(i));
            }
            break;
        }
        case 1: {
            /* E utilizado um vetor temporario para que a ordem original dos aeroportos
             * nao seja perdida
             */
            int i = 0, j = 0, aux = 0;
            int indices[nAeroportos]; //O vetor apenas precisa dos indices ate' nAeroportos
            for (i = 0; i < nAeroportos; i++) {
                indices[i] = i; //Inicializacao dos indices
            }
            for (i = 0; i < nAeroportos; i++) {
                int min = i;
                for (j = i + 1; j < nAeroportos; j++)
                    if (strcmp(aeroportos[indices[j]].nome, aeroportos[indices[min]].nome) < 0)
                        min = j;
                aux = indices[i];
                indices[i] = indices[min];
                indices[min] = aux;
                printf("%s:%d:%d:%d\n", aeroportos[indices[i]].nome, aeroportos[indices[i]].capacidade,
                       voosPartida(indices[i]), voosChegada(indices[i]));
            }
            break;
        }
        case 2: {
            int hist[MAXVOOS] = {0}, i; //Nota do enunciado
            for (i = 0; i < nAeroportos; i++) {
                hist[voosTotais(i)] += 1;
            }
            for (i = 0; i < MAXVOOS; i++) {
                if (hist[i] != 0) {
                    printf("%d:%d\n", i, hist[i]);
                }
            }
            break;
        }
    }
}

/* Mostra o numero total de voos e o nu'mero de aeroportos inseridos.
 */
void func_X() {
    int i, j, totalvoos = 0;
    for (i = 0; i < nAeroportos; i++) {
        for (j = 0; j < nAeroportos; j++) {
            totalvoos += voos[i][j];
        }
    }
    printf("%d:%d\n", totalvoos, nAeroportos);
}
