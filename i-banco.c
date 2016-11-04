
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
#include <semaphore.h>

#define COMANDO_DEBITAR   "debitar"
#define COMANDO_CREDITAR  "creditar"
#define COMANDO_LER_SALDO "lerSaldo"
#define COMANDO_SIMULAR   "simular"
#define COMANDO_SAIR      "sair"
#define COMANDO_AGORA     "agora"

#define ID_SAIR      0
#define ID_DEBITAR   1
#define ID_CREDITAR  2
#define ID_LER_SALDO 3

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

pthread_t tarefas[NUM_TRABALHADORAS];
pthread_mutex_t mutex_c;
sem_t sem_podeProduzir, sem_podeConsumir;

comando_t cmd_buffer[CMD_BUFFER_DIM];
int buff_write_idx = 0, buff_read_idx = 0;

void consumir(comando_t comando) {
	if (comando.operacao == ID_DEBITAR) {

		if (debitar(comando.idConta, comando.valor) < 0)
			printf("%s(%d, %d): Erro\n\n", COMANDO_DEBITAR, comando.idConta, comando.valor);
		else
			printf("%s(%d, %d): OK\n\n", COMANDO_DEBITAR, comando.idConta, comando.valor);

	} else if (comando.operacao == ID_CREDITAR) {

		if (creditar(comando.idConta, comando.valor) < 0)
			printf("%s(%d, %d): Erro\n\n", COMANDO_CREDITAR, comando.idConta, comando.valor);
 		else
			printf("%s(%d, %d): OK\n\n", COMANDO_CREDITAR, comando.idConta, comando.valor);

	} else if (comando.operacao == ID_LER_SALDO) {
		int saldo = lerSaldo(comando.idConta);
		if (saldo < 0)
			printf("%s(%d): Erro.\n\n", COMANDO_LER_SALDO, comando.idConta);
		else
			printf("%s(%d): O saldo da conta é %d.\n\n", COMANDO_LER_SALDO, comando.idConta, saldo);
	}
}

void *consumidor(void *arg) {
	while(1) {
		comando_t comando;

		if (sem_wait(&sem_podeConsumir) != 0) { perror("Erro ao esperar pelo semaforo!"); exit(3); }
		if (pthread_mutex_lock(&mutex_c) != 0) {perror("Erro ao obter trinco!"); exit(4); }
		comando = cmd_buffer[buff_read_idx];
		buff_read_idx = (buff_read_idx + 1) % CMD_BUFFER_DIM;
		pthread_mutex_unlock(&mutex_c); /* Nenhum dos erros do pthread_mutex_unlock e aplicavel. Safe to ignore */
		sem_post(&sem_podeProduzir);  /* Nenhum dos erros do sem_post e aplicavel. Safe to ignore */

		if (comando.operacao != ID_SAIR)
			consumir(comando);
		else
			break;
	}

	pthread_exit(NULL);
}

comando_t temp_c;
void produzir(int op, int id, int val) {
	temp_c.operacao = op;
	temp_c.idConta = id;
	temp_c.valor = val;

	if (sem_wait(&sem_podeProduzir) != 0) { perror("Erro ao esperar pelo semaforo!"); exit(3); }
	cmd_buffer[buff_write_idx] = temp_c;
	buff_write_idx = (buff_write_idx + 1) % CMD_BUFFER_DIM;
	sem_post(&sem_podeConsumir); /* Nenhum dos erros do sem_post e aplicavel. Safe to ignore */
}

int main(int argc, char **argv) {

    char *args[MAXARGS + 1];
    char buffer[BUFFER_SIZE];
    int nChildren = 0;
	int i;

	if (pthread_mutex_init(&mutex_c, NULL) != 0) {
		perror("Error while creating mutex!");
		exit(1);
	}

	if ((sem_init(&sem_podeProduzir, 0, CMD_BUFFER_DIM) != 0) ||
		(sem_init(&sem_podeConsumir, 0, 0) != 0)) {
		if (errno == ENOSYS)
			perror("Semaphores not supported!");
		else
			perror("Error while creating semaphore");
		exit(2);
	}

	for(i = 0; i < NUM_TRABALHADORAS; i++) {
		if(pthread_create(&tarefas[i], NULL, consumidor, NULL) != 0)
				perror("Erro ao criar nova tarefa!");
	}

    inicializarContas();

    printf("Bem-vinda/o ao i-banco\n\n");

    while (1) {
        int numargs;

        numargs = readLineArguments(args, MAXARGS + 1, buffer, BUFFER_SIZE);

        /* EOF (end of file) do stdin ou comando "sair" */
        if (numargs < 0 || (numargs > 0 && (strcmp(args[0], COMANDO_SAIR) == 0))) {
            int wpid, status;

        	if (numargs > 1 && (strcmp(args[1], COMANDO_AGORA) == 0)) {
            	/* ignorar o signal no processo-pai */
            	if (signal(SIGUSR2, SIG_IGN) == SIG_ERR)
                	perror("Cannot set signal handler");
            	/* Nenhum dos erros do kill() e aplicavel. Safe to ignore */
            	kill(0, SIGUSR2);
        	}

        	printf("i-banco vai terminar.\n--\n");

        	while (1) { /* O ciclo termina quando wait() devolve com o erro ECHILD; no more children */
            	wpid = wait(&status);
            	if (wpid == -1 && errno == ECHILD)
                	break; /* Nao existem mais filhos, terminar ciclo */
            	else if (wpid == -1)
                	continue; /* Outro erro, continuar o ciclo ate nao existirem mais filhos */

            	if (WIFEXITED(status)) {
                	printf("FILHO TERMINADO (PID=%d; terminou normalmente)\n", wpid);
            	} else {
                	printf("FILHO TERMINADO (PID=%d; terminou abruptamente)\n", wpid);
            	}
        	}

			for (i = 0; i < NUM_TRABALHADORAS; i++) {
				produzir(ID_SAIR, 0, 0);
			}

			for(i = 0; i < NUM_TRABALHADORAS; i++) {
				if(pthread_join(tarefas[i], NULL) != 0)
					fprintf(stderr, "Failed to join with thread %ld", tarefas[i]);
			}
			
			finalizarContas();
            
            if (sem_destroy(&sem_podeProduzir) != 0) {perror("Error while destroying semaphore!");}
            if (sem_destroy(&sem_podeProduzir) != 0) {perror("Error while destroying semaphore!");}
            
            if (pthread_mutex_destroy(&mutex_c) != 0) {
                perror("Error while destoying mutex!");
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

			produzir(ID_DEBITAR, idConta, valor);
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

			produzir(ID_CREDITAR, idConta, valor);
		}
            /* Ler Saldo */
        else if (strcmp(args[0], COMANDO_LER_SALDO) == 0) {
            int idConta;

            if (numargs < 2) {
        		printf("%s: Sintaxe inválida, tente de novo.\n", COMANDO_LER_SALDO);
                continue;
            }

            idConta = atoi(args[1]);

			produzir(ID_LER_SALDO, idConta, 0);
        }

            /* Simular */
        else if (strcmp(args[0], COMANDO_SIMULAR) == 0) {
            int nAnos, pid;

            if (numargs < 2) {
                printf("%s: Sintaxe inválida, tente de novo.\n", COMANDO_SIMULAR);
                continue;
            }

            /* Segundo limitacao do enunciado */
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
