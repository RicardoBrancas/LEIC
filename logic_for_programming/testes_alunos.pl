%:- ['lp-g0'].
:- discontiguous corre_testes/1.
%%%%%%%%%%%%%%%%%%%%%%% Labirintos para testes  %%%%%%%%%%%%%%%%%%%%%%%
% lab(N, Lab, Pos_inicial, Pos_final): N e' o numero do labirinto (1,2, ou 3);
% Lab e' o labirinto numero N;
% Pos_inicial, Pos_final sao as posicoes incial e final, respetivamente.

        %%%%%%%%%%%%%%%%%%%%%%%% Labirinto 1 %%%%%%%%%%%%%%%%
lab(1, [[[c,e],[c],[c,d]], 
        [[e],[],[d]], 
	[[b,e],[b],[b,d]]], 
       (1,1), (3,3)).
        %%%%%%%%%%%%%%%%%%%%%%%% Labirinto 2 %%%%%%%%%%%%%%%%

lab(2, [[[d,e,c],[e,b,c],[b,c],[c],[d,c],[d,e,c]],
        [[e,b],[b,c],[b,c],[],[b],[d,b]],
	[[e,c],[b,c],[b,c],[b],[b,c],[d,b,c]],
	[[d,e],[e,c],[c],[c],[c],[d,c]],
	[[e,b],[d,b],[e,b],[b],[b],[d,b]]],
       (1,6), (5, 6)).
        %%%%%%%%%%%%%%%%%%%%%%%% Labirinto 3 %%%%%%%%%%%%%%%%

lab(3, [[[d,e,c],[b,c,e],[b,c],[b,c],[c],[c],[b,c],[b,c],[d,c],[b,c,e],[b,c],[c],[c],[c],[c,d]],
        [[e],[b,c],[b,c],[d,b,c],[d,e],[d,e],[b,c,e],[b,c],[d,b],[c,e],[d,c],[d,e],[b,e],[b],[b,d]],
	[[d,e],[c,e],[b,c],[b,c],[d,b],[b,e],[b,c],[b,c],[d,c],[d,e],[d,e],[b,e],[b,c],[b,c],[c,d]],
	[[d,e],[d,e],[e,c],[c],[c],[c],[d,c],[e,c],[d],[d,e],[b,e],[b,c],[b,c],[d,c],[d,e]],
	[[d,e],[d,e],[e],[b],[b],[b],[d],[e],[d,b],[d,e],[e,c],[b,c],[b,c],[d,b],[d,e]],
	[[d,e],[d,e],[e],[b,c],[b,c],[d,c],[d,e],[d,e],[e,c],[d,b],[b,e],[b,c],[b,c],[d,c],[d,e]],
	[[d,e],[d,e],[e],[c],[d,c],[d,e],[d,e],[d,e],[d,e],[d,e,c],[e,c],[c],[d,c],[d,e],[d,e]],
	[[b,e],[d,b],[b,e],[b],[d],[d,e],[e],[d],[d,e],[b,e],[b],[d,b],[d,e],[d,e],[d,e]],
	[[e,c],[c],[c],[c],[d],[d,e],[b,e],[d,b],[d,e],[e,c],[c],[c],[d],[d,e],[d,e]],
	[[e],[],[b],[b],[d,b],[b,e],[b,c],[b,c],[d,b],[e],[b],[b],[d,b],[d,e],[e,d]],
	[[e,b],[b],[b,c],[b,c],[b,c],[b,c],[b,c],[b,c],[b,c],[d,b],[b,e,c],[b,c],[b,c],[b],[b,d]]],
       (11,1), (1, 15)).


%%%%%%%%%%%%%%%%%%%%%%% Solucoes  %%%%%%%%%%%%%%%%%%%%%%%
% sol(Pred, N, Sol): Pred e' resolve1 ou resolve2; N e' 1,2, ou 3; 
% sol(Pred, N, Sol) significa que Sol
% e' a solucao do predicado Pred, para o labirinto N.
        %%%%%%%%%%%%%%%%%%%%%%%% Labirinto 1 %%%%%%%%%%%%%%%%

sol(resolve1, 1, 
    [(i,1,1), (b,2,1), (b,3,1), (d,3,2), (c,2,2), (c,1,2), 
     (d,1,3), (b,2,3), (b,3,3)]).

sol(resolve2, 1, [(i,1,1), (b,2,1), (b,3,1), (d,3,2), (d,3,3)]).


        %%%%%%%%%%%%%%%%%%%%%%%% Labirinto 2 %%%%%%%%%%%%%%%%
sol(resolve1, 2, 
    [(i,1,6), (b,2,6), (e,2,5), (c,1,5), (e,1,4), (b,2,4), 
     (b,3,4), (e,3,3), (e,3,2), (e,3,1), (b,4,1), (b,5,1), 
     (d,5,2), (c,4,2), (d,4,3), (b,5,3), (d,5,4), (c,4,4), 
     (d,4,5), (b,5,5), (d,5,6)]).

sol(resolve2, 2, 
    [(i,1,6), (b,2,6), (e,2,5), (e,2,4), (b,3,4), (e,3,3), 
     (e,3,2), (e,3,1), (b,4,1), (b,5,1), (d,5,2), (c,4,2), 
     (d,4,3), (b,5,3), (d,5,4), (d,5,5), (d,5,6)]).

        %%%%%%%%%%%%%%%%%%%%%%%% Labirinto 3 %%%%%%%%%%%%%%%%
sol(resolve1, 3, 
    [(i,11,1), (c,10,1), (c,9,1), (d,9,2), (b,10,2), (d,10,3), (c,9,3), (d,9,4), (b,10,4), (d,10,5), 
     (c,9,5), (c,8,5), (c,7,5), (e,7,4), (b,8,4), (e,8,3), (c,7,3), (c,6,3), (d,6,4), (d,6,5), (d,6,6), 
     (b,7,6), (b,8,6), (b,9,6), (b,10,6), (d,10,7), (d,10,8), (d,10,9), (c,9,9), (c,8,9), (c,7,9), (c,6,9), 
     (d,6,10), (c,5,10), (c,4,10), (c,3,10), (c,2,10), (d,2,11), (b,3,11), (b,4,11), (d,4,12), (d,4,13), 
     (d,4,14), (b,5,14), (e,5,13), (e,5,12), (e,5,11), (b,6,11), (d,6,12), (d,6,13), (d,6,14), (b,7,14), 
     (b,8,14), (b,9,14), (b,10,14), (b,11,14), (d,11,15), (c,10,15), (c,9,15), (c,8,15), (c,7,15), (c,6,15), 
     (c,5,15), (c,4,15), (c,3,15), (e,3,14), (e,3,13), (e,3,12), (c,2,12), (c,1,12), (d,1,13), (b,2,13), 
     (d,2,14), (c,1,14), (d,1,15)]).

sol(resolve2, 3, 
    [(i,11,1), (c,10,1), (c,9,1), (d,9,2), (d,9,3), (d,9,4), (d,9,5), (c,8,5), (c,7,5), (e,7,4), (b,8,4), 
     (e,8,3), (c,7,3), (c,6,3), (d,6,4), (d,6,5), (d,6,6), (b,7,6), (b,8,6), (b,9,6), (b,10,6), (d,10,7), 
     (d,10,8), (d,10,9), (c,9,9), (c,8,9), (c,7,9), (c,6,9), (d,6,10), (c,5,10), (c,4,10), (c,3,10), (c,2,10), 
     (d,2,11), (b,3,11), (b,4,11), (d,4,12), (d,4,13), (d,4,14), (b,5,14), (e,5,13), (e,5,12), (e,5,11), 
     (b,6,11), (d,6,12), (d,6,13), (d,6,14), (b,7,14), (b,8,14), (b,9,14), (b,10,14), (b,11,14), (d,11,15), 
     (c,10,15), (c,9,15), (c,8,15), (c,7,15), (c,6,15), (c,5,15), (c,4,15), (c,3,15), (e,3,14), (e,3,13), 
     (e,3,12), (c,2,12), (c,1,12), (d,1,13), (d,1,14), (d,1,15)]).

%%%%%%%%%%%%%%%%%%%%%%%%%%%% Testes %%%%%%%%%%%%%%%%%%%%%%%%%%%
testes( [[movs_possiveis,
	  [[Sol1 = [],
	    lab(3,Lab1,_,_), movs_possiveis(Lab1,(2,7), [(b,2,9), (e,2,8)], Res1),
	    escreve_resultado(Res1, Sol1)],
	   [Sol2 = [(b, 8, 4), (e, 7, 3)],
	    lab(3,Lab2,_,_),movs_possiveis(Lab2,(7,4), [(c,7,5), (c,8,5)], Res2),
	    escreve_resultado(Res2, Sol2)],
	   [Sol3 =  [ (c,9,2), (e,10,1), (d,10,3)],
	    lab(3,Lab3,_,_),movs_possiveis(Lab3,(10,2), [(d,11,2), (c,10,2)], Res3), 
	    escreve_resultado(Res3, Sol3)]]],
	 [distancia,
	  [[Sol4 = 12, 
	    distancia((1,3),(6,10), Res4), 
	    escreve_resultado(Res4, Sol4)]]],
	[ordena_poss,
	  [[Sol5 = [(d, 5, 5), (c, 4, 4)], 
	    ordena_poss([ (c, 4, 4), (d, 5, 5)], Res5, (1, 6), (5, 6)), 
	    escreve_resultado(Res5, Sol5)],
	  [Sol6 = [ (e, 2, 4), (c, 1, 5)], 
	    ordena_poss([(c, 1, 5), (e, 2, 4)], Res6, (1, 6), (5, 6)), 
	    escreve_resultado(Res6, Sol6)],
	  [Sol7 = [ (b, 8, 4), (e, 7, 3)], 
	    ordena_poss([(b, 8, 4), (e, 7, 3)], Res7, (11,1), (1,15)), 
	    escreve_resultado(Res7, Sol7)],
	  [Sol8 = [ (c, 9, 2), (d, 10, 3), (e, 10, 1)], 
	    ordena_poss([(c, 9, 2), (e, 10, 1), (d, 10, 3)], Res8,(11,1), (1,15)), 
	    escreve_resultado(Res8, Sol8)]]],
	[resolve1,
	 [[sol(resolve1,1,Sol9),
	   lab(1, Lab9, PI9, PF9),
	   resolve1(Lab9, PI9, PF9, Res9), 
	   escreve_resultado(Res9, Sol9)],
	 [sol(resolve1,2,Sol10),
	   lab(2, Lab10, PI10, PF10),
	   resolve1(Lab10, PI10, PF10, Res10), 
	   escreve_resultado(Res10, Sol10)],
	 [sol(resolve1,3,Sol11),
	   lab(3, Lab11, PI11, PF11),
	   resolve1(Lab11, PI11, PF11, Res11), 
	   escreve_resultado(Res11, Sol11)]]],
	 [resolve2,
	  [[sol(resolve2,1,Sol12),
	   lab(1, Lab12, PI12, PF12),
	   resolve2(Lab12, PI12, PF12, Res12), 
	   escreve_resultado(Res12, Sol12)],
	 [sol(resolve2,2,Sol13),
	   lab(2, Lab13, PI13, PF13),
	   resolve2(Lab13, PI13, PF13, Res13), 
	   escreve_resultado(Res13, Sol13)],
	 [sol(resolve2,3,Sol14),
	   lab(3, Lab14, PI14, PF14),
	   resolve2(Lab14, PI14, PF14, Res14), 
	   escreve_resultado(Res14, Sol14)]]]]).

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









