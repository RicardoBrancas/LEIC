
/*
   // Projeto SO - exercicio 1, version 1
   // Sistemas Operativos, DEI/IST/ULisboa 2016-17
 */

#include "commandlinereader.h"
#include "contas.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/wait.h>
#include <pthread.h>

#define COMANDO_DEBITAR "debitar"
#define COMANDO_CREDITAR "creditar"
#define COMANDO_LER_SALDO "lerSaldo"
#define COMANDO_SIMULAR "simular"
#define COMANDO_SAIR "sair"
#define COMANDO_AGORA "agora"

#define MAXARGS 3
#define BUFFER_SIZE 100

#define MAX_CHILDREN 20

#define NUM_TRABALHADORAS 3
#define CMD_BUFFER_DIM (NUM_TRABALHADORAS * 2)

typedef struct {
	int operacao;
	int idConta;
	int valor;
} comando_t;

void *test(void *arg) {
	printf("Tarefa criada!\n");
	sleep(3);
	return NULL;
}

int main(int argc, char **argv) {

	int i;
    char *args[MAXARGS + 1];
    char buffer[BUFFER_SIZE];
    int nChildren = 0;

	pthread_t tarefas[NUM_TRABALHADORAS];

	for(i = 0; i < NUM_TRABALHADORAS; i++) {
		if(pthread_create(&tarefas[i], NULL, test, NULL) != 0) {
				perror("Erro ao criar nova tarefa!");
		}
	}

	comando_t cmd_buffer[CMD_BUFFER_DIM];
	int buff_write_idx = 0, buff_read_idx = 0;

    inicializarContas();

    printf("Bem-vinda/o ao i-banco\n\n");

    while (1) {
        int numargs;

        numargs = readLineArguments(args, MAXARGS + 1, buffer, BUFFER_SIZE);

        /* EOF (end of file) do stdin ou comando "sair" */
        if (numargs < 0 ||
            (numargs > 0 && (strcmp(args[0], COMANDO_SAIR) == 0))) {
            int wpid, status;

            if (numargs > 1 && (strcmp(args[1], COMANDO_AGORA) == 0)) {
                //ignorar o signal no processo-pai
                if (signal(SIGUSR2, SIG_IGN) == SIG_ERR)
                    perror("Cannot set signal handler");
                //Nenhum dos erros do kill() e aplicavel. Safe to ignore
                kill(0, SIGUSR2);
            }

            printf("i-banco vai terminar.\n--\n");

            while (1) { //O ciclo termina quando wait() devolve com o erro ECHILD; no more children
                wpid = wait(&status);
                if (wpid == -1 && errno == ECHILD)
                    break; //Nao existem mais filhos, terminar ciclo
                else if (wpid == -1)
                    continue; //Outro erro, continuar o ciclo ate nao existirem mais filhos

                if (WIFEXITED(status)) {
                    printf("FILHO TERMINADO (PID=%d; terminou normalmente)\n", wpid);
                } else {
                    printf("FILHO TERMINADO (PID=%d; terminou abruptamente)\n", wpid);
                }
            }

            printf("--\ni-banco terminou.\n");

            exit(EXIT_SUCCESS);
        } else if (numargs == 0)
            /* Nenhum argumento; ignora e volta a pedir */
            continue;

            /* Debitar */
        else if (strcmp(args[0], COMANDO_DEBITAR) == 0) {
            int idConta, valor;
            if (numargs < 3) {
                printf("%s: Sintaxe inválida, tente de novo.\n", COMANDO_DEBITAR);
                continue;
            }

            idConta = atoi(args[1]);
            valor = atoi(args[2]);

            if (debitar(idConta, valor) < 0)
                printf("%s(%d, %d): Erro\n\n", COMANDO_DEBITAR, idConta, valor);
            else
                printf("%s(%d, %d): OK\n\n", COMANDO_DEBITAR, idConta, valor);
        }

            /* Creditar */
        else if (strcmp(args[0], COMANDO_CREDITAR) == 0) {
            int idConta, valor;
            if (numargs < 3) {
                printf("%s: Sintaxe inválida, tente de novo.\n", COMANDO_CREDITAR);
                continue;
            }

            idConta = atoi(args[1]);
            valor = atoi(args[2]);

            if (creditar(idConta, valor) < 0)
                printf("%s(%d, %d): Erro\n\n", COMANDO_CREDITAR, idConta, valor);
            else
                printf("%s(%d, %d): OK\n\n", COMANDO_CREDITAR, idConta, valor);
        }

            /* Ler Saldo */
        else if (strcmp(args[0], COMANDO_LER_SALDO) == 0) {
            int idConta, saldo;

            if (numargs < 2) {
                printf("%s: Sintaxe inválida, tente de novo.\n", COMANDO_LER_SALDO);
                continue;
            }
            idConta = atoi(args[1]);
            saldo = lerSaldo(idConta);
            if (saldo < 0)
                printf("%s(%d): Erro.\n\n", COMANDO_LER_SALDO, idConta);
            else
                printf("%s(%d): O saldo da conta é %d.\n\n", COMANDO_LER_SALDO, idConta, saldo);
        }

            /* Simular */
        else if (strcmp(args[0], COMANDO_SIMULAR) == 0) {
            int nAnos, pid;

            if (numargs < 1) {
                printf("%s: Sintaxe inválida, tente de novo.\n", COMANDO_SIMULAR);
                continue;
            }

            //Segundo limitacao do enunciado
            if (nChildren < MAX_CHILDREN) {
                nChildren++;
                nAnos = atoi(args[1]);

                pid = fork();

                if (pid == -1) {
                    perror("Fork failed. Lack of system resources?");
                } else if (pid == 0) {
                    simular(nAnos);
                    exit(0);
                }
            } else {
                perror("No more children available.");
            }


        } else {
            printf("Comando desconhecido. Tente de novo.\n");
        }

    }
}
