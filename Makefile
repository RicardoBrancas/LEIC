CFLAGS = -g -Wall -pedantic

all: i-banco i-banco-terminal

i-banco: contas.o i-banco.o
	gcc -pthread -o i-banco contas.o i-banco.o

i-banco-terminal: commandlinereader.o i-banco-terminal.o
	gcc -o i-banco-terminal commandlinereader.o i-banco-terminal.o

commandlinereader.o: commandlinereader.c commandlinereader.h
	gcc $(CFLAGS) -c commandlinereader.c

contas.o: contas.c contas.h
	gcc $(CFLAGS) -c contas.c

i-banco.o: i-banco.c i-banco.h contas.h
	gcc $(CFLAGS) -c i-banco.c

i-banco-terminal.o: i-banco-terminal.c i-banco.h commandlinereader.h
	gcc $(CFLAGS) -c i-banco-terminal.c

clean:
	rm -f *.o i-banco i-banco-terminal
