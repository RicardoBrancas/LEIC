#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>

#include "commandlinereader.h"
#include "i-banco.h"

#define MAXARGS 4
#define BUFFER_SIZE 100

int file;

comando_t temp_c;
void produzir(int op, int idDe, int val, int idPara) {
	temp_c.operacao = op;
	temp_c.idConta = idDe;
	temp_c.idContaDestino = idPara;
	temp_c.valor = val;

    write(file, &temp_c, sizeof(comando_t));
}

int main(int argc, char **argv) {

	//Inicializar o valor do terminalID do temp_c (nunca muda)
	//temp_c.terminalID = getpid();

	if(argc < 2) {
		perror("Insuficient arguments given.");
		exit(1);
	}

	char *args[MAXARGS + 1];
	char buffer[BUFFER_SIZE];

	char *pathname = argv[1];
	file = open(pathname, O_WRONLY, 0666);

	// produzir(ID_CRIAR_FIFO, 0, getpid(), 0);

	printf("Bem-vinda/o ao terminal do i-banco\n\n");

	while(1) {
		int numargs;

		numargs = readLineArguments(args, MAXARGS + 1, buffer, BUFFER_SIZE);

		if (numargs < 0 || (numargs > 0 && (strcmp(args[0], COMANDO_SAIR_TERMINAL) == 0))) {

			// produzir(ID_FINALIZAR_FIFO, 0, getpid(), 0);

			close(file);

			printf("O terminal do i-banco terminou.\n");
			exit(EXIT_SUCCESS);
		}

		else if (numargs == 0)
			continue;

        /* Sair */
        else if(strcmp(args[0], COMANDO_SAIR) == 0) {
            int tipo = 0;

            if (numargs > 1 && (strcmp(args[1], COMANDO_AGORA) == 0)) {
                tipo = 1;
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
