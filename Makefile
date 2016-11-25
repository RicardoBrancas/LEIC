CFLAGS = -g -Wall -pedantic

i-banco: commandlinereader.o contas.o i-banco.o
	gcc -pthread -o i-banco commandlinereader.o contas.o i-banco.o

commandlinereader.o: commandlinereader.c commandlinereader.h
	gcc $(CFLAGS) -c commandlinereader.c

contas.o: contas.c contas.h
	gcc $(CFLAGS) -c contas.c

i-banco.o: i-banco.c contas.h commandlinereader.h
	gcc $(CFLAGS) -c i-banco.c

test:
	(i-banco )
	(ls i-banco-pipe)
	("simular 4" | i-banco-terminal i-banco-pipe)
	
	
clean:
	rm -f *.o i-banco
