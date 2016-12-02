#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <time.h>
#include <errno.h>

#include "commandlinereader.h"
#include "i-banco.h"

#define MAXARGS 4
#define BUFFER_SIZE 100

int pipeTerminalToIbanco;
int pipeIbancoToTerminal;
int idTerminalNoIbanco;

comando_t temp_c;
time_t start, stop;
void produzir(int op, int idDe, int val, int idPara) {
	temp_c.operacao = op;
	temp_c.idConta = idDe;
	temp_c.idContaDestino = idPara;
	temp_c.valor = val;

	time(&start);
	if (write(pipeTerminalToIbanco, &temp_c, sizeof(comando_t)) == -1 && errno != EPIPE) {
		perror("Erro ao escrever no pipe (terminal para i-banco)");
		exit(10);
	}
}

void handlePipeSignal(int signum) {
	perror("o terminal do i-banco tentou escrever/ler numa broken pipe!");
	exit(20);
}

int main(int argc, char **argv) {
    char *args[MAXARGS + 1];
	char buffer[BUFFER_SIZE];
    char temp[70];
    

	if(argc < 2) {
		perror("Insuficient arguments given.");
		exit(1);
	}

	if (signal(SIGPIPE, handlePipeSignal) == SIG_ERR)
		perror("Cannot set signal handler");

	pipeTerminalToIbanco = open(argv[1], O_WRONLY, 0666);
	if(pipeTerminalToIbanco == -1) {
		perror("Error while opening the specified file! Is i-banco running?");
		exit(11);
	}
	
	snprintf(temp, 70, "i-banco-terminal-pipe-%d", getpid());
	if (mkfifo(temp, 0666) == -1) {
		perror("Error while creating named pipe (i-banco to terminal)");
		exit(12);
	}

	produzir(ID_CRIAR_FIFO, 0, getpid(), 0);

	pipeIbancoToTerminal = open(temp, O_RDONLY);
	if(pipeIbancoToTerminal == -1) {
		perror("Error while opening named pipe (i-banco para terminal)");
		exit(11);
	}
	if (read(pipeIbancoToTerminal, &idTerminalNoIbanco, sizeof(int)) == -1) {
		perror("Erro ao ler do pipe (i-banco para terminal)");
		exit(12);
	}

	/* Inicializar o valor do terminalID do temp_c (nunca muda) */
	temp_c.terminalID = idTerminalNoIbanco;

	printf("Bem-vinda/o ao terminal do i-banco\n\n");

	while(1) {
		int numargs, result;

		numargs = readLineArguments(args, MAXARGS + 1, buffer, BUFFER_SIZE);

		if (numargs < 0 || (numargs > 0 && (strcmp(args[0], COMANDO_SAIR_TERMINAL) == 0))) {

			if (signal(SIGPIPE, SIG_IGN) == SIG_ERR) /* O terminal vai terminar. Nao interessa */
				perror("Cannot set signal handler");

			produzir(ID_FINALIZAR_FIFO, 0, 0, 0);

			if (close(pipeTerminalToIbanco) == -1)
				perror("Erro ao fechar named pipe (terminal para i-banco)");
			if (close(pipeIbancoToTerminal) == -1)
				perror("Erro ao fechar named pipe (i-banco para terminal)");

			snprintf(temp, 70, "i-banco-terminal-pipe-%d", getpid());
			if (unlink(temp) == -1)
				perror("Error while unliking named pipe (i-banco para terminal)");

			printf("O terminal do i-banco terminou.\n");
			exit(EXIT_SUCCESS);
		}

		else if (numargs == 0)
			continue;

		/* Sair */
		else if(strcmp(args[0], COMANDO_SAIR) == 0) {
			int tipo = ID_SAIR_NAO_AGORA;

			if (numargs > 1 && (strcmp(args[1], COMANDO_AGORA) == 0)) {
				tipo = ID_SAIR_AGORA;
			}

			produzir(ID_SAIR, 0, tipo, 0);
		}

		/* Debitar */
		else if (strcmp(args[0], COMANDO_DEBITAR) == 0) {
			int idConta, valor;
			if (numargs < 3) {
				printf("%s: Sintaxe inválida, tente de novo.\n", COMANDO_DEBITAR);
				continue;
			}

			idConta = atoi(args[1]);
			valor = atoi(args[2]);

			produzir(ID_DEBITAR, idConta, valor, 0);
			if (read(pipeIbancoToTerminal, &result, sizeof(int)) == -1) {
				perror("Erro ao ler do pipe (i-banco para terminal)");
				exit(12);
			}
			time(&stop);
			if(result < 0)
				printf("%s(%d, %d): Erro\n", COMANDO_DEBITAR, idConta, valor);
			else
				printf("%s(%d, %d): OK\n", COMANDO_DEBITAR, idConta, valor);
			printf("Tempo: %f\n\n", difftime(stop, start));
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

			produzir(ID_CREDITAR, idConta, valor, 0);
			if (read(pipeIbancoToTerminal, &result, sizeof(int)) == -1) {
				perror("Erro ao ler do pipe (i-banco para terminal)");
				exit(12);
			}
			time(&stop);
			if(result < 0)
				printf("%s(%d, %d): Erro\n", COMANDO_CREDITAR, idConta, valor);
			else
				printf("%s(%d, %d): OK\n", COMANDO_CREDITAR, idConta, valor);
			printf("Tempo: %f\n\n", difftime(stop, start));
		}

		/* Ler Saldo */
		else if (strcmp(args[0], COMANDO_LER_SALDO) == 0) {
			int idConta;

			if (numargs < 2) {
				printf("%s: Sintaxe inválida, tente de novo.\n", COMANDO_LER_SALDO);
				continue;
			}

			idConta = atoi(args[1]);

			produzir(ID_LER_SALDO, idConta, 0, 0);
			if (read(pipeIbancoToTerminal, &result, sizeof(int)) == -1) {
				perror("Erro ao ler do pipe (i-banco para terminal)");
				exit(12);
			}
			time(&stop);
			if (result < 0)
				printf("%s(%d): Erro.\n", COMANDO_LER_SALDO, idConta);
			else
				printf("%s(%d): O saldo da conta é %d.\n", COMANDO_LER_SALDO, idConta, result);
			printf("Tempo: %f\n\n", difftime(stop, start));
		}

		/* Transferir */
		else if (strcmp(args[0], COMANDO_TRANSFERIR) == 0) {
			int idDe, idPara, valor;

			if (numargs < 4) {
				printf("%s: Sintaxe inválida, tente de novo.\n", COMANDO_TRANSFERIR);
				continue;
			}

			idDe = atoi(args[1]);
			idPara = atoi(args[2]);
			valor = atoi(args[3]);

			produzir(ID_TRANSFERIR, idDe, valor, idPara);
			if (read(pipeIbancoToTerminal, &result, sizeof(int)) == -1) {
				perror("Erro ao ler do pipe (i-banco para terminal)");
				exit(12);
			}
			time(&stop);
			if(result < 0)
				printf("Erro ao transferir %d da conta %d para a conta %d.\n", valor, idDe, idPara);
			else
				printf("%s(%d, %d, %d): OK\n", COMANDO_TRANSFERIR, idDe, idPara, valor);
			printf("Tempo: %f\n\n", difftime(stop, start));
		}

		/* Simular */
		else if (strcmp(args[0], COMANDO_SIMULAR) == 0) {
			int nAnos;

			if (numargs < 2) {
				printf("%s: Sintaxe inválida, tente de novo.\n", COMANDO_SIMULAR);
				continue;
			}

			nAnos = atoi(args[1]);

			produzir(ID_SIMULAR, 0, nAnos, 0);
		}

		else {
			printf("Comando desconhecido. Tente de novo.\n");
		}

	}
}
