/*
// Operações sobre contas, versao 1
// Sistemas Operativos, DEI/IST/ULisboa 2016-17
*/

#ifndef CONTAS_H
#define CONTAS_H

#define NUM_CONTAS 10
#define TAXAJURO 0.1
#define CUSTOMANUTENCAO 1

#define ATRASO 0

void inicializarContas();

int contaExiste(int idConta);

int debitar(int idConta, int valor);

int creditar(int idConta, int valor);

int lerSaldo(int idConta);

void simular(int numAnos);

#endif
