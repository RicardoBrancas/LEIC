#include "contas.h"
#include "i-banco.h"
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <errno.h>
#include <pthread.h>
#include <string.h>

#define atrasar() sleep(ATRASO)

#define CONTINUE 1
#define STOP 0

#define BUFFER_SIZE 70

int contasSaldos[NUM_CONTAS];

char buf[BUFFER_SIZE];
int log_file;

pthread_mutexattr_t attr;
pthread_mutex_t mutex_contas[NUM_CONTAS];

int contaExiste(int idConta) {
	return (idConta > 0 && idConta <= NUM_CONTAS);
}

void inicializarContas(int logFile) {
	int i;

	log_file = logFile;

	pthread_mutexattr_init(&attr);
	pthread_mutexattr_settype(&attr, PTHREAD_MUTEX_RECURSIVE);

	for (i = 0; i < NUM_CONTAS; i++) {
		contasSaldos[i] = 0;
		if (pthread_mutex_init(&mutex_contas[i], &attr) != 0) {
			perror("Error while creating mutex!");
			exit(1);
		}
	}
}

void finalizarContas() {
	int i;
	for (i = 0; i < NUM_CONTAS; i++) {
		if (pthread_mutex_destroy(&mutex_contas[i]) != 0)
			perror("Error while destroying mutex!");
	}
}

int min(int a, int b) {
	return (a <= b) ? a : b;
}

int max(int a, int b) {
	return (a >= b) ? a : b;
}

int transferir(int idContaDe, int idContaPara, int valor) {
    int tempLogFile;
    
	if (!contaExiste(idContaDe) || !contaExiste(idContaPara))
		return -1;

	if (pthread_mutex_lock(&mutex_contas[min(idContaDe, idContaPara)-1]) != 0) {perror("Erro ao obter trinco!"); exit(4); }
	if (pthread_mutex_lock(&mutex_contas[max(idContaDe, idContaPara)-1]) != 0) {perror("Erro ao obter trinco!"); exit(4); }

	tempLogFile = log_file; /* Os comandos usados internamente no transferir nao devem aparecer no log */
	log_file = -1;

	if(debitar(idContaDe, valor) < 0) {
		log_file = tempLogFile;
		pthread_mutex_unlock(&mutex_contas[min(idContaDe, idContaPara)-1]); /* Nenhum dos erros do pthread_mutex_unlock e aplicavel. Safe to ignore */
		pthread_mutex_unlock(&mutex_contas[max(idContaDe, idContaPara)-1]); /* Nenhum dos erros do pthread_mutex_unlock e aplicavel. Safe to ignore */
		return -1;
	}

	creditar(idContaPara, valor);

	log_file = tempLogFile;

	if(log_file != -1) {
		snprintf(buf, BUFFER_SIZE, "%lu : %s(%d, %d, %d)\n", pthread_self(), COMANDO_TRANSFERIR, valor, idContaDe, idContaPara);
		if (write(log_file, buf, strlen(buf)) == -1)
			perror("Erro ao escrever no log file!");
	}

	pthread_mutex_unlock(&mutex_contas[min(idContaDe, idContaPara)-1]); /* Nenhum dos erros do pthread_mutex_unlock e aplicavel. Safe to ignore */
	pthread_mutex_unlock(&mutex_contas[max(idContaDe, idContaPara)-1]); /* Nenhum dos erros do pthread_mutex_unlock e aplicavel. Safe to ignore */

	return 0;
}

int debitar(int idConta, int valor) {
	atrasar();
	if (!contaExiste(idConta))
		return -1;
	if (pthread_mutex_lock(&mutex_contas[idConta-1]) != 0) {perror("Erro ao obter trinco!"); exit(4); }
	if (contasSaldos[idConta - 1] < valor) {
		pthread_mutex_unlock(&mutex_contas[idConta-1]); /* Nenhum dos erros do pthread_mutex_unlock e aplicavel. Safe to ignore */
		return -1;
	}
	atrasar();
	contasSaldos[idConta - 1] -= valor;

	if(log_file != -1) {
		snprintf(buf, BUFFER_SIZE, "%ld : %s(%d, %d)\n", (long) pthread_self(), COMANDO_DEBITAR, idConta, valor);
		if (write(log_file, buf, strlen(buf)) == -1)
			perror("Erro ao escrever no log file!");
	}

	pthread_mutex_unlock(&mutex_contas[idConta-1]); /* Nenhum dos erros do pthread_mutex_unlock e aplicavel. Safe to ignore */
	return 0;
}

int creditar(int idConta, int valor) {
	atrasar();
	if (!contaExiste(idConta))
		return -1;
	if (pthread_mutex_lock(&mutex_contas[idConta-1]) != 0) {perror("Erro ao obter trinco!"); exit(4); }
	contasSaldos[idConta - 1] += valor;

    if(log_file != -1) {
        snprintf(buf, BUFFER_SIZE, "%ld : %s(%d, %d)\n", (long) pthread_self(), COMANDO_CREDITAR, idConta, valor);
		if (write(log_file, buf, strlen(buf)) == -1)
			perror("Erro ao escrever no log file!");
    }

	pthread_mutex_unlock(&mutex_contas[idConta-1]); /* Nenhum dos erros do pthread_mutex_unlock e aplicavel. Safe to ignore */
	return 0;
}

int lerSaldo(int idConta) {
	int s;
	atrasar();
	if (!contaExiste(idConta))
		return -1;
	if (pthread_mutex_lock(&mutex_contas[idConta-1]) != 0) {perror("Erro ao obter trinco!"); exit(4); }
	s = contasSaldos[idConta - 1];

    if(log_file != -1) {
        snprintf(buf, BUFFER_SIZE, "%ld : %s(%d)\n", (long) pthread_self(), COMANDO_LER_SALDO, idConta);
		if (write(log_file, buf, strlen(buf)) == -1)
			perror("Erro ao escrever no log file!");
    }

	pthread_mutex_unlock(&mutex_contas[idConta-1]); /* Nenhum dos erros do pthread_mutex_unlock e aplicavel. Safe to ignore */
	return s;
}

int interrupted = CONTINUE;

void handleSignal(int signum) {
	interrupted = STOP;
}

void simular(int numAnos) {
	int n, i, saldo;
    
    log_file = -1;

	if (signal(SIGUSR2, handleSignal) == SIG_ERR)
		perror("Cannot set signal handler");

	for (n = 0; n <= numAnos; n++) {
		printf("SIMULACAO: Ano %d\n=================\n", n);
		for (i = 1; i <= NUM_CONTAS; i++) {

			if (n > 0) {
				saldo = lerSaldo(i);
				creditar(i, saldo * TAXAJURO);
				saldo = lerSaldo(i);
				debitar(i, (CUSTOMANUTENCAO > saldo) ? saldo : CUSTOMANUTENCAO);
			}
			saldo = lerSaldo(i);

			/* A funcao printf pode ser interrompida por um signal que ocorra durante a sua execucao */
			while (printf("Conta %d, Saldo %d\n", i, saldo) < 0) {
				if (errno == EINTR)
					continue;
				else
					break;
			}
		}
		while (printf("\n") < 0) {
			if (errno == EINTR)
				continue;
			else
				break;
		}

		if (interrupted == STOP) {
			printf("Simulacao terminada por signal;\n");
			exit(1);
		}
	}
}
