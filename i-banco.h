#ifndef IBANCO_H
#define IBANCO_H

#define COMANDO_DEBITAR       "debitar"
#define COMANDO_CREDITAR      "creditar"
#define COMANDO_LER_SALDO     "lerSaldo"
#define COMANDO_SIMULAR       "simular"
#define COMANDO_SAIR          "sair"
#define COMANDO_AGORA         "agora"
#define COMANDO_TRANSFERIR    "transferir"
#define COMANDO_SAIR_TERMINAL "sair-terminal"

#define ID_CRIAR_FIFO     -4
#define ID_FINALIZAR_FIFO -3
#define ID_SAIR_TAREFA    -2
#define ID_SAIR           -1

#define ID_DEBITAR         1
#define ID_CREDITAR        2
#define ID_LER_SALDO       3
#define ID_TRANSFERIR      4
#define ID_SIMULAR         5

typedef struct {
	//int terminalID;
	int operacao;
	int idConta;
	int idContaDestino;
	int valor;
} comando_t;

typedef struct {
	comando_t comando;
	int result;
} result_t;

#endif
