%:- ['lp-g112'].
% 83557 - Ricardo Miguel Bacala Brancas
% 83533 - Mihail Brinza

%%%%%%%%%%%%%%%%%%%%%%%%%%% Predicados do enunciado %%%%%%%%%%%%%%%%%%%%%%%%%%%

%distancia((L1, C1), (L2, C2), Dist): (L1, C1) e (L2, C2) sao duas coordenadaes
%     e Dist e um numero;
%distancia((L1, C1), (L2, C2), Dist) afirma que Dist e' a distancia entre
%     (L1, C1) e (L2, C2).
distancia((L1, C1), (L2, C2), Dist) :- Dist  is abs(L1 - L2) + abs(C1 - C2).


%movs_possiveis(Lab, Pos, Movs, Poss): Lab e' um labirinto, Pos sao as coordenadas,
%     Movs e' uma lista que contem os movimentos efetuados e Poss e' uma lista com
%     os movimentos possiveis
%movs_possiveis(Lab, Pos, Movs, Poss) afirma que Poss e' a lista de movimentos
%     possiveis a partir da celula indicada pelas coordenadas Pos e tendo em
%     consideracao Movs, a lista de movimentos ja' efetuados.
movs_possiveis(Lab, Pos, Movs, Poss):- celula(Lab, Pos, Cel),
                                       disjuncao(Cel, [c,b,e,d], Dirs),
                                       calcula_movimentos(Dirs, Pos, Poss_D),
                                       removedup(Poss_D, Movs, Poss).

%ordena_poss(Poss, Poss_ord, P_ini, P_fim): Poss e' uma lista de movimentos,
%     possiveis, Poss_ord e' a lista Poss apos ordenada, P_ini e P_fim sao
%     coordenadas.
%ordena_poss(Poss, Poss_ord, P_ini, P_fim) afirma que Poss_ord e' a lista Poss
%     apos ter sido ordenada segundo dois criterios: em primeiro lugar,
%     os movimentos conducentes 'a menor distancia 'a uma posição final P_fim, e,
%     em caso de igualdade, os movimentos conducentes a uma maior distancia a
%     uma posicao inicial, P_ini.
ordena_poss([X], [X], _, _) :- !.
ordena_poss(Poss, Poss_ord, P_ini, P_fim) :- ordena(Poss, [], Poss_ord_1, P_ini),
                                             ordena(Poss_ord_1, [], Poss_ord, P_fim).

%resolve1(Lab, Pos_inicial, Pos_final, Movs): Lab e' um labirinto, Pos_inicial
%     e Pos_final sao coordenadas, e Movs e' uma lista de movimentos.
%resolve1(Lab, Pos_inicial, Pos_final, Movs) significa que a sequencia de
%     movimentos Movs e' uma solucao parao labirinto Lab, desde a posicao
%     inicial Pos_inicial ate' 'a posição final Pos_final. A solucao Movs obedece
%     apenas a restricao de nao passar mais do que uma vez pela mesma celula.
%     Os movimentos possiveis sao testados pela ordem c, b, e, d.
resolve1(Lab, Pos_inicial, Pos_final, Movs) :-
      resolve1_aux(Lab, Pos_inicial, Pos_final, [(i,Pos_inicial)], Movs).
%Predicado auxiliar ao predicado resolve1/4. Pos_atual e' a posicao atual e
%Movs_act sao os movimentos efetuados ate ao momento. O resto das veriaveis tem
%o mesmo significado que no predicado principal
resolve1_aux(_, P, P, M, M) :- !.
resolve1_aux(Lab, Pos_atual, Pos_final, Movs_act, Movs_fim) :-
                    movs_possiveis(Lab, Pos_atual, Movs_act, Poss),
                    membro((D,Pos), Poss),
                    junta(Movs_act, [(D,Pos)], Movs_act1),
                    resolve1_aux(Lab, Pos, Pos_final, Movs_act1, Movs_fim).

%resolve1(Lab, Pos_inicial, Pos_final, Movs): Lab e' um labirinto, Pos_inicial
%     e Pos_final sao coordenadas, e Movs e' uma lista de movimentos.
%resolve1(Lab, Pos_inicial, Pos_final, Movs) significa que a sequencia de
%     movimentos Movs e' uma solucao parao labirinto Lab, desde a posicao
%     inicial Pos_inicial ate' 'a posição final Pos_final. A solucao Movs e' gerada
%     escolhendo entre os varios movimentos possiveis, aquele que se aproximar
%     mais da posicao final. Se existirem varios movimentos conducentes a
%     posicoes com a mesma distancia 'a posicao final, e' escolhido o que mais se
%     distanciar da posicao inicial. No caso de existirem varios movimentos
%     conducentes a posicoes com as mesmas distancias 'as posições inicial e final,
%     e' seguida a ordem c, b, e, d.
resolve2(Lab, Pos_inicial, Pos_final, Movs) :-
  resolve2_aux(Lab, Pos_inicial, Pos_inicial, Pos_final, [(i,Pos_inicial)], Movs).
%Predicado auxiliar ao predicado resolve2/4. Pos_atual e' a posicao atual e
%Movs_act sao os movimentos efetuados ate ao momento. O resto das variaveis tem
%o mesmo significado que no predicado principal
resolve2_aux(_, _, P, P, M, M) :- !.
resolve2_aux(Lab, Pos_inicial, Pos_atual, Pos_final, Movs_act, Movs_fim) :-
          movs_possiveis(Lab, Pos_atual, Movs_act, Poss),
          ordena_poss(Poss, Poss_ord, Pos_inicial, Pos_final),
          membro((D,Pos), Poss_ord),
          junta(Movs_act, [(D,Pos)], Movs_act1),
          resolve2_aux(Lab, Pos_inicial, Pos, Pos_final, Movs_act1, Movs_fim).

%%%%%%%%%%%%%%%%%%%%%%%%%%%% Predicados auxiliares %%%%%%%%%%%%%%%%%%%%%%%%%%%%

%celula(Tab, (Vert,Horiz), Cel): Lab e' um labirinto, (Vert,Horiz) e' uma
%     coordenada e Cel e' uma celula;
%celula(Tab, (Vert,Horiz), Cel) afirma que Cel e a celula que se encontra na
%     posicao (Vert,Horiz) no labirinto Lab.
celula(Lab, (Vert,Horiz), Cel) :- nth1(Vert, Lab, Linha), nth1(Horiz, Linha, Cel).

%disjuncao(L1, L2, L3): L1, L2 e L3 sao listas;
%disjuncao(L1, L2, L3) afirma que L3 resulta da disjuncao de conjuntos entre
%     as listas L1 e L2.
disjuncao([], L, L) :- !.
disjuncao([P | R], L2, [P | S]) :- \+membro(P, L2), !, disjuncao(R, L2, S).
disjuncao([P | R], L2, S) :- escolhe(L2, P, L3), disjuncao(R, L3, S).

%membro(X, L): X e' um qualquer atomo e L e' uma lista;
%membro(X, L) afirma que X e' um membro de L.
membro(X, [X|_]).
membro(X, [_|R]) :- membro(X,R).

%escolhe(L, X, L2): X e' um qualquer atomo, L e L2 sao listas;
%escolhe(L, X, L2) afirma que L2 e' a lista que resulta de retirar 'a lista L
%     o elemento X.
escolhe([P|R], P, R) :- !.
escolhe([P|R], E, [P|S]) :- escolhe(R, E, S).

%calcula_movimentos(Dirs, Pos, Movs): Dirs e' uma lista de direcoes, Pos e' uma
%     coordenada e Movs e' uma lista de movimentos;
%calcula_movimentos(Dirs, Pos, Movs) afirma que Movs e' a lista dos movimentos
%     resultante de fazer um movimento em cada uma das direcoes de Dirs a partir
%     da coordenada Pos.
calcula_movimentos([], _, []) :- !.
calcula_movimentos([Dir | R], PosI, [(Dir,P) | S]) :- calcula_movimento(Dir, PosI, P),
                                                      calcula_movimentos(R, PosI, S).

%calcula_movimento(D, Pos1, Pos2): D e' uma direcao, Pos1 e Pos2 sao coordenadas.
%calcula_movimento(D, Pos1, Pos2) afirma que Pos2 resulta do movimento a partir
%     de Pos1 na direcao D.
calcula_movimento(c, (V, H), (Novo_V, Novo_H)) :- Novo_V is V-1, Novo_H is H.
calcula_movimento(b, (V, H), (Novo_V, Novo_H)) :- Novo_V is V+1, Novo_H is H.
calcula_movimento(e, (V, H), (Novo_V, Novo_H)) :- Novo_H is H-1, Novo_V is V.
calcula_movimento(d, (V, H), (Novo_V, Novo_H)) :- Novo_H is H+1, Novo_V is V.

%removedup(Movs1, Movs2, Movs3): Movs1, Movs2 e Movs3 sao listas de movimentos.
%removedup(Movs1, Movs2, Movs3) afirma que Movs3 e' a lista de movimentos obtida
%       a partir de Movs1, apo's terem sido removidos todos os movimentos contendo
%       posicoes ja contempladas na lista Movs2.
removedup([],_,[]) :- !.
removedup([P|R], Movs, [P|S]) :- \+membro_pos(P, Movs), !, removedup(R, Movs, S).
removedup([P|R], Movs, S) :- membro_pos(P, Movs), removedup(R, Movs, S).

%membro_pos(Mov, L): Mov e' um movimento, L e' uma lista de movimentos;
%membro_pos(Mov, L) afirma que a posicao de Mov existe nalgum movimento da lista
%     L,independentemente das direcoes coincidirem ou nao.
membro_pos((_,Pos), [(_,Pos)|_]) :- !.
membro_pos((_,Pos), [_|R]) :- membro_pos((_,Pos),R).

%ordena(L, Aux, L_ord, Pos): L, Aux e O sao listas de movimentos, Pos e' uma
%     coordenada;
%ordena(L, Aux, L_ord, Pos) afirma que L_ord e' a lista que se obtem da ordenacao
%     da lista L de acordo com a distacia a' coordenada Pos. O algoritmo de
%     ordenacao utilizado e' uma variacao do InsertionSort. O predicado deve ser
%     chamado com Aux == [].
ordena([],O,O,_) :- !.
ordena(L,Aux,O,Pos) :- escolhe_min(L,Min,Resto,Pos),
                       junta(Aux, [Min], Aux1),
                       ordena(Resto, Aux1, O,Pos).

%escolhe_min(L, Min, Resto, Pos): L e Resto sao listas de movimentos, Min e' um
%     movimento e Pos e' uma coordenada.
%escolhe_min(L, Min, Resto, Pos) afirma que Min e' o movimento da lista L cuja
%     posicao esta mais proxima de Pos e que Resto e' a lista que se obtem
%     removendo 'a lista L o elemento Min.
escolhe_min([X], X, [], _) :- !.
escolhe_min(L, Min, Resto, Pos) :- minimo(L,Min,Pos),  escolhe(L, Min, Resto).

%minimo(L, M, Pos): L e' uma lista de movimentos, M e' um movimento e Pos e'
%     uma coordenada.
%minimo(L, M, Pos) afirma que M e' o movimento de lista L cuja posicao esta
%     mais proxima de Pos.
minimo([X], X, _) :- !.
minimo([(D,Pos1)|R], M, Pos2) :- minimo(R, (Dmin,PosMin), Pos2),
                                 distancia(Pos1, Pos2, Dist),
                                 distancia(PosMin, Pos2, DistMin),
                                 !, (
                                   Dist < DistMin, !, M = (D,Pos1);
                                   Dist >= DistMin, M = (Dmin,PosMin)
                                 ).

%junta(L1, L2, L3): L1, L2 e L3 sao listas;
%junta(L1, L2, L3) afirma que L3 resulta de juntar ao fim da lista L1 a lista L2.
junta([], L, L) :- !.
junta(L, [], L) :- !.
junta([P|R], L1, [P|R1]) :- junta(R, L1, R1).
