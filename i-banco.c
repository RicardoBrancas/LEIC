
/*  Projeto SO - exercicio 1, version 1
 *  Sistemas Operativos, DEI/IST/ULisboa 2016-17
 */

#include "i-banco.h"
#include "contas.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/wait.h>
#include <pthread.h>
#include <semaphore.h>
#include <fcntl.h>
#include <sys/stat.h>

#define MAX_CHILDREN 20

#define NUM_TRABALHADORAS 3
#define CMD_BUFFER_DIM (NUM_TRABALHADORAS * 2)

#define FICHEIRO_LOG "log.txt"
#define I_BANCO_PIPE "i-banco-pipe"

pthread_t tarefas[NUM_TRABALHADORAS];
pthread_mutex_t mutex_c;
sem_t sem_podeProduzir, sem_podeConsumir;
pthread_cond_t var_cond;

comando_t cmd_buffer[CMD_BUFFER_DIM];
int buff_write_idx = 0, buff_read_idx = 0, buffer_n = 0;

int log_file, file;

int result;
void consumir(comando_t comando) {
	if (comando.operacao == ID_DEBITAR) {

		result = debitar(comando.idConta, comando.valor);
		if (write(comando.terminalID, &result, sizeof(int)) == -1) {
			perror("Error while writing to named pipe (i-banco to terminal)");
			exit(13);
		}

	} else if (comando.operacao == ID_CREDITAR) {

		result = creditar(comando.idConta, comando.valor);
		if (write(comando.terminalID, &result, sizeof(int)) == -1) {
			perror("Error while writing to named pipe (i-banco to terminal)");
			exit(13);
		}

	} else if (comando.operacao == ID_LER_SALDO) {

		result = lerSaldo(comando.idConta);
		if (write(comando.terminalID, &result, sizeof(int)) == -1) {
			perror("Error while writing to named pipe (i-banco to terminal)");
			exit(13);
		}

	} else if (comando.operacao == ID_TRANSFERIR) {

		result = transferir(comando.idConta, comando.idContaDestino, comando.valor);
		if (write(comando.terminalID, &result, sizeof(int)) == -1) {
			perror("Error while writing to named pipe (i-banco to terminal)");
			exit(13);
		}

	}
}

void *consumidor(void *arg) {
	while(1) {
		comando_t comando;

		if (sem_wait(&sem_podeConsumir) != 0) { perror("Erro ao esperar pelo semaforo!"); exit(3); }
		if (pthread_mutex_lock(&mutex_c) != 0) { perror("Erro ao obter trinco!"); exit(4); }
		comando = cmd_buffer[buff_read_idx];
		buff_read_idx = (buff_read_idx + 1) % CMD_BUFFER_DIM;
		pthread_mutex_unlock(&mutex_c); /* Nenhum dos erros do pthread_mutex_unlock e aplicavel. Safe to ignore */
		sem_post(&sem_podeProduzir);  /* Nenhum dos erros do sem_post e aplicavel. Safe to ignore */

		if (comando.operacao != ID_SAIR_TAREFA) {
			consumir(comando);

			if (pthread_mutex_lock(&mutex_c) != 0) {perror("Erro ao obter trinco!"); exit(4); }
			buffer_n--;
			if(buffer_n == 0) pthread_cond_signal(&var_cond); /* Nenhum dos error do pthread_cond_signal e aplicavel. Safe to ignore */
			pthread_mutex_unlock(&mutex_c); /* Nenhum dos erros do pthread_mutex_unlock e aplicavel. Safe to ignore */
		} else
			break;
	}

	pthread_exit(NULL);
}

comando_t temp_c;
void produzir(int terminalID, int op, int idDe, int val, int idPara) {
	temp_c.terminalID = terminalID;
	temp_c.operacao = op;
	temp_c.idConta = idDe;
	temp_c.idContaDestino = idPara;
	temp_c.valor = val;

	if (sem_wait(&sem_podeProduzir) != 0) { perror("Erro ao esperar pelo semaforo!"); exit(3); }
	if (pthread_mutex_lock(&mutex_c) != 0) {perror("Erro ao obter trinco!"); exit(4); }
	cmd_buffer[buff_write_idx] = temp_c;
	buff_write_idx = (buff_write_idx + 1) % CMD_BUFFER_DIM;
	buffer_n++;
	pthread_mutex_unlock(&mutex_c); /* Nenhum dos erros do pthread_mutex_unlock e aplicavel. Safe to ignore */
	sem_post(&sem_podeConsumir); /* Nenhum dos erros do sem_post e aplicavel. Safe to ignore */
}

void handlePipeSignal(int signum) {
	perror("o i-banco tentou escrever/ler numa broken pipe!");
	exit(20);
}

int main(int argc, char **argv) {
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

	if (pthread_cond_init(&var_cond, NULL) != 0) {
		perror("Error while initializing condition variable! Lack of system resources?");
		exit(6);
	}

	if (signal(SIGPIPE, handlePipeSignal) == SIG_ERR)
		perror("Cannot set signal handler");

	if (mkfifo(I_BANCO_PIPE, 0666) == -1) {
		perror("Error while creating named pipe (terminal to i-banco)");
		exit(12);
	}

	log_file = open(FICHEIRO_LOG, O_WRONLY | O_CREAT | O_TRUNC, 0666);
	if(log_file == -1) {
		perror("Error while opening log file");
	}

	for(i = 0; i < NUM_TRABALHADORAS; i++) {
		if(pthread_create(&tarefas[i], NULL, consumidor, NULL) != 0)
			perror("Erro ao criar nova tarefa!");
	}

	inicializarContas(log_file);

	printf("Bem-vinda/o ao i-banco\n");

	printf("A esperar pela ligacao de um terminal...\n");
	file = open(I_BANCO_PIPE, O_RDONLY, 0666);
	if(file == -1) {
		perror("Error while opening named pipe (terminal para i-banco)");
		exit(11);
	}
	printf("Conectado.\n\n");

	while (1) {
		if (read(file, &temp_c, sizeof(comando_t)) == -1) {
			perror("Error while reading comand form named pipe (terminal para i-banco)");
			exit(12);
		}

		/* sair */
		if (temp_c.operacao == ID_SAIR) {
			int wpid, status;

			if (temp_c.valor == ID_SAIR_AGORA) {
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
				produzir(-1, ID_SAIR_TAREFA, 0, 0, 0);
			}

			for(i = 0; i < NUM_TRABALHADORAS; i++) {
				if(pthread_join(tarefas[i], NULL) != 0)
					fprintf(stderr, "Failed to join with thread %ld", tarefas[i]);
			}

			finalizarContas();

			if (close(log_file) == -1)
				perror("Erro ao fechar log file");

			if (close(file) == -1)
				perror("Erro ao fechar named pipe (terminal para i-banco)");

			if (unlink(I_BANCO_PIPE) == -1)
				perror("Error while unliking named pipe (terminal para i-banco)");

			if (sem_destroy(&sem_podeProduzir) != 0) perror("Error while destroying semaphore!");
			if (sem_destroy(&sem_podeProduzir) != 0) perror("Error while destroying semaphore!");

			if (pthread_mutex_destroy(&mutex_c) != 0)
				perror("Error while destoying mutex!");

			if(pthread_cond_destroy(&var_cond) != 0)
				perror("Error while destroying condition variable!");

			printf("--\ni-banco terminou.\n");
			exit(EXIT_SUCCESS);
		}

		else if (temp_c.operacao == ID_SIMULAR) {
			int nAnos, pid, fd;

			if (nChildren < MAX_CHILDREN) {
				nChildren++;
				nAnos = temp_c.valor;

				if (pthread_mutex_lock(&mutex_c) != 0) {perror("Erro ao obter trinco!"); exit(4); }
				while(buffer_n != 0) {
					if(pthread_cond_wait(&var_cond, &mutex_c) != 0) {
						perror("Error while waiting for condition variable!");
						exit(7);
					}
				}
				pid = fork();
				pthread_mutex_unlock(&mutex_c);

				if (pid == -1) {
					perror("Fork failed. Lack of system resources?");
				} else if (pid == 0) {
                    char ficheiro[64];
                    
					log_file = -1;
					
					snprintf(ficheiro, 64, "i-banco-sim-%d.txt", getpid());

					fd = open(ficheiro, O_WRONLY | O_CREAT, 0666);
					dup2(fd, 1);

					simular(nAnos);
					exit(0);
				}
			}
		}

		else if (temp_c.operacao == ID_CRIAR_FIFO) {
			char temp[70];
            int fd;

			snprintf(temp, 70, "i-banco-terminal-pipe-%d", temp_c.valor);

			fd = open(temp, O_WRONLY);
			/* Send the file descriptor back to the terminal, so we can later know
			 * wich pipe (using the file descriptor) to use when sending results
			 * to the terminal.
			 */
			if (write(fd, &fd, sizeof(int)) == -1) {
				perror("Erro ao escrever no pipe (i-banco para terminal)");
				exit(10);
			}
		}

		else if (temp_c.operacao == ID_FINALIZAR_FIFO) {
			if (close(temp_c.terminalID) == -1)
				perror("Erro ao fechar named pipe (i-banco para terminal)");
		}

		else
			produzir(temp_c.terminalID, temp_c.operacao, temp_c.idConta, temp_c.valor, temp_c.idContaDestino);

	}
}
