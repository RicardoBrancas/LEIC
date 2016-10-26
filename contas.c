#include "contas.h"
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <errno.h>
#include <pthread.h>

#define atrasar() sleep(ATRASO)

#define CONTINUE 1
#define STOP 0

int contasSaldos[NUM_CONTAS];

pthread_mutex_t mutex_contas[NUM_CONTAS];

int contaExiste(int idConta) {
    return (idConta > 0 && idConta <= NUM_CONTAS);
}

void inicializarContas() {
    int i;
    for (i = 0; i < NUM_CONTAS; i++)
        contasSaldos[i] = 0;
}

int debitar(int idConta, int valor) {
    atrasar();
    if (!contaExiste(idConta))
        return -1;
    pthread_mutex_lock(&mutex_contas[idConta-1]);
    if (contasSaldos[idConta - 1] < valor) {
        pthread_mutex_unlock(&mutex_contas[idConta-1]);
        return -1;
    }
    atrasar(); //FIXME
    contasSaldos[idConta - 1] -= valor;
    pthread_mutex_unlock(&mutex_contas[idConta-1]);
    return 0;
}

int creditar(int idConta, int valor) {
    atrasar();
    if (!contaExiste(idConta))
        return -1;
    pthread_mutex_lock(&mutex_contas[idConta-1]);
    contasSaldos[idConta - 1] += valor;
    pthread_mutex_unlock(&mutex_contas[idConta-1]);
    return 0;
}

int lerSaldo(int idConta) {
    int s;
    atrasar();
    if (!contaExiste(idConta))
        return -1;
    pthread_mutex_lock(&mutex_contas[idConta-1]);
    s = contasSaldos[idConta - 1];
    pthread_mutex_unlock(&mutex_contas[idConta-1]);
    return s;
}

int interrupted = CONTINUE;

void handleSignal(int signum) {
    interrupted = STOP;
}

void simular(int numAnos) {
    int n, i, saldo;

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

            //A funcao printf pode ser interrompida por um signal que ocorra durante a sua execucao
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
