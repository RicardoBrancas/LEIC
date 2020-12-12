
:- [testes_projeto], ['lp-g112'].
:- discontiguous corre_testes/1.

%%%%%%%%%%%%%%%%%%%%%%%%%%%% Execucao dos Testes %%%%%%%%%%%%%%%%%%%%%%%%%%%

  %%%%%%%%%%%%%%%%%%% Para testar so um predicado
testes_pred(Nome, Testes_pred) :-
  testes(Testes), testes_pred(Testes, Nome, Testes_pred).

testes_pred([[Nome, Testes] | _], Nome, [Nome, Testes]) :- !.
testes_pred([_ | R], Nome, Teste) :-
  testes_pred(R, Nome, Teste).

corre_testes(Nome_pred) :-
  atom(Nome_pred), !,
  testes_pred(Nome_pred, Testes_pred),
  corre_testes([Testes_pred]).

   %%%%%%%%%%%%%%%%%%% Para testar todos os predicados
corre_testes :-
  testes(Testes), corre_testes(Testes).

corre_testes([]) :- !.
corre_testes([T | R_Testes]) :-
	       corre_testes_pred(T),
	       corre_testes(R_Testes).

corre_testes_pred([Nome_pred , Testes]) :-
	       corre_testes_pred(Nome_pred, Testes, 1).

corre_testes_pred(_, [],_) :- !.
corre_testes_pred(Nome_pred, [Goals | R_testes],N) :-
	     N_N is N + 1,
	     write('Teste'), write(N), write(' '), writeln(Nome_pred),
	     ignore(avalia_lst(Goals)),
	     corre_testes_pred(Nome_pred, R_testes,N_N).

 %%%%%%%%%%%%%%%%%%% Para correr o teste numero Num_teste do predicado Nome_pred
corre_teste(Nome_pred, Num_teste) :-
	testes_pred(Nome_pred, Testes_pred),
	nth1(2,Testes_pred,Testes),
	nth1(Num_teste, Testes, Teste),
	corre_testes_pred(Nome_pred, [Teste],Num_teste).


%%%%%%%%%%%%%%%%%%%%%%%%%%%% Avaliacao de uma lista de objetivos
avalia_lst([]) :- !.
avalia_lst([Goal | R]) :-
  call(Goal), avalia_lst(R).

%%%%%%%%%%%%%%%%%%%%%%%%%%%% Escrita do resultado de um teste
escreve_resultado(Res, Sol) :-
 (Res == Sol -> writeln('\t OK') ;
                writeln('\t FALHOU'),
	        write('\t Resultado correto: '),  writeln(Sol),
	        write('\t Resultado obtido: '),   writeln(Res)).
