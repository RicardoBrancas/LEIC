# Projeto 2 - Picross
Projeto 2 da cadeira de FP 2015/2016

## Tipos Abstratos de Dados
### TAD coordenada
O TAD coordenada será utilizado para indexar as várias células do tabuleiro. Cada célula do tabuleiro é indexada através da linha (um inteiro entre 1 e o número de linhas do tabuleiro) e da coluna respetiva (um inteiro entre 1 e o número de colunas do tabuleiro), em que a célula (1 : 1) corresponde ao canto superior esquerdo do tabuleiro. O TAD coordenada deverá pois ser um tipo imutável que armazena dois inteiros correspondentes a uma linha e uma coluna do tabuleiro.
As operações básicas associadas a este TAD são:
* `cria_coordenada`: int × int → coordenada

    Esta função corresponde ao construtor do tipo coordenada. Recebe dois argumentos do tipo inteiro, o primeiro dos quais corresponde a uma linha l (um inteiro positivo) e o segundo a uma coluna c (um inteiro positivo), e deve devolver um elemento do tipo coordenada correspondente à célula (l : c). A função deve verificar a validade dos seus argumentos, gerando um ValueError com a mensagem 'cria_coordenada: argumentos invalidos' caso algum dos argumentos introduzidos seja inválido.
* `coordenada_linha`: coordenada → inteiro

    Este seletor recebe como argumento um elemento do tipo coordenada e devolve a linha respetiva.
* `coordenada_coluna`: coordenada → inteiro

    Este seletor recebe como argumento um elemento do tipo coordenada e devolve a coluna respetiva.
* `e_coordenada` : universal → lógico

    Este reconhecedor recebe um único argumento e devolve `True` caso esse argumento seja do tipo coordenada, e False em caso contrário.
* `coordenadas_iguais`: coordenada × coordenada → lógico

    Este teste recebe como argumentos dois elementos do tipo coordenada e devolve `True` caso esses argumentos correspondam à mesma célula do tabuleiro, e `False` em caso contrário.
* `coordenada_para_cadeia`: coordenada → cad. caracteres

    Esta função recebe como argumento um elemento do tipo coordenada e devolve uma cadeia de caracteres que a represente de acordo com o exemplo em baixo.

### TAD tabuleiro
O TAD tabuleiro será utilizado para representar o tabuleiro. Este TAD deverá permitir:
1. representar um tabuleiro de Picross (uma grelha de n × n células);
2. aceder a cada uma das células do tabuleiro;
3. modificar o conteúdo de cada uma das células.
#### Tabuleiro inicial.
A Fig. 2 apresenta um exemplo de um tabuleiro 5 × 5, em que 2 na primeira linha estabelece o número de células que devem estar preenchidas consecutivamente nessa linha, e em que 3 na última coluna estabelece o número de células que devem estar preenchidas consecutivamente nessa coluna. A especificação 2 2 na 4ª linha, estabelece que a 4ª linha tem duas caixas, seguidas de pelo menos uma célula em branco, seguidas de mais duas caixas.
As operações básicas associadas a este TAD são:
* `cria_tabuleiro`: tuplo → tabuleiro

    Esta função corresponde ao construtor do tipo tabuleiro. Recebe como argumento um elemento t do tipo tuplo descrevendo a especificação das linhas e das colunas do tabuleiro, e deverá devolver um elemento do tipo tabuleiro de acordo com a representação interna escolhida. O tuplo t deve ser composto por dois tuplos de tuplos de inteiros, em que o primeiro corresponde à especificação das linhas e o segundo à especificação das colunas. Por exemplo, o tuplo (((2,),(3,),(2,),(2, 2),(2,)),((2,),(1, 2),(2,),(3,),(3,))) contém a especificação para o jogo representado na Fig. 2. O tabuleiro criado deverá estar vazio, isto é, todas as células deverão conter o valor 0, representando as células do quadro vazias (como desconhecidas). A função deve verificar a validade dos seus argumentos, gerando um ValueError com a mensagem 'cria_tabuleiro: argumentos invalidos' caso o argumento introduzido seja inválido.
* `tabuleiro_dimensoes` : tabuleiro → tuplo

    Este seletor recebe como argumento um elemento t do tipo tabuleiro e devolve um tuplo com dois elementos, cujo primeiro elemento é o número de linhas do tabuleiro e o segundo o número de colunas do mesmo.
* `tabuleiro_especificacoes`: tabuleiro → tuplo

    Este seletor recebe como argumento um elemento t do tipo tabuleiro e devolve um tuplo composto por dois tuplos de tuplos de inteiros, cujo primeiro elemento corresponde à especificação das linhas e o segundo à especificação das colunas.
* `tabuleiro_celula`: tabuleiro × coordenada → {0, 1, 2}

    Este seletor recebe como argumentos um elemento t do tipo tabuleiro e um elemento c do tipo coordenada e devolve um elemento do tipo inteiro entre 0 e 2, que corresponde ao valor contido na célula do tabuleiro referente à coordenada c. Caso a célula correspondente a c esteja vazia, deverá devolver o valor 0, caso corresponda a uma célula em branco deve devolver o valor 1 e caso esteja preenchida deve devolver o valor 2. A função deve verificar a validade dos argumentos para o tabuleiro em causa, e gerar um ValueError com a mensagem 'tabuleiro_celula: argumentos invalidos' caso não se verifique.
* `tabuleiro_preenche_celula`: tabuleiro × coordenada × {0, 1, 2} → tabuleiro

    Este modificador recebe como argumentos um elemento t do tipo tabuleiro, um elemento c do tipo coordenada e um inteiro e entre 0 e 2, e modifica o tabuleiro t, preenchendo a célula referente à coordenada c com o elemento e, que pode ser 0, 1 ou 2, para representar o vazio, uma célula em branco ou uma caixa, respetivamente. A função deve devolver o tabuleiro modificado. Deve ainda verificar a validade dos argumentos e gerar um ValueError com a mensagem 'tabuleiro_preenche_celula: argumentos invalidos' caso algum dos argumentos introduzidos não seja válido.
* `e_tabuleiro`: universal → lógico

    Este reconhecedor recebe um único argumento, devendo devolver `True` se o seu argumento for do tipo tabuleiro, e `False` em caso contrário.
* `tabuleiro_completo`: tabuleiro → lógico

    Este reconhecedor recebe como argumento um elemento t do tipo tabuleiro e devolve True caso o tabuleiro t esteja totalmente preenchido corretamente de acordo com as suas especificações, e False em caso contrário.
* `tabuleiros_iguais`: tabuleiro × tabuleiro → lógico

    Este teste recebe como argumentos dois elementos t1 e t2 do tipo tabuleiro e devolve `True` caso t1 e t2 correspondam a dois tabuleiros com as mesmas especificações e quadros com o mesmo conteúdo, e `False` em caso contrário. Deve ainda implementar o seguinte transformador de saída:
* `escreve_tabuleiro`: tabuleiro → {}

    A função escreve_tabuleiro recebe como argumento um elemento t do tipo tabuleiro e escreve para o ecrã a representação externa de um tabuleiro de Picross, apresentada no exemplo abaixo. Deve ainda verificar se t é um tabuleiro válido e, caso o argumento introduzido seja inválido, deve gerar um ValueError com a mensagem 'escreve_tabuleiro: argumento invalido'.

### TAD jogada
O TAD jogada será utilizado para representar a jogada a efetuar sobre um tabuleiro. Cada jogada é composta por uma coordenada e um valor igual a 1 ou 2, que representa o conteúdo de uma célula de um tabuleiro de picross (uma célula em branco ou uma caixa, respetivamente). As operações básicas associadas a este TAD são:
* `cria_jogada`: coordenada × {1, 2} → jogada

    Esta função corresponde ao construtor do tipo jogada. Recebe como argumento um elemento do tipo coordenada e um inteiro com valor 1 ou 2. A função deve verificar a validade dos seus argumentos, gerando um ValueError com a mensagem 'cria_jogada: argumentos invalidos' caso algum dos argumentos introduzidos seja inválido.
* `jogada_coordenada`: jogada → coordenada

    Este seletor recebe como argumento um elemento do tipo jogada e devolve a coordenada respetiva.
* `jogada_valor`: jogada → {1, 2}

    Este seletor recebe como argumento um elemento do tipo jogada e devolve o valor respetivo.
* `e_jogada`: universal → lógico

    Este reconhecedor recebe um único argumento e devolve True caso esse argumento seja do tipo jogada, e False em caso contrário.
* `jogadas_iguais`: jogada × jogada → lógico

    Este teste recebe como argumentos dois elementos do tipo jogada e devolve True caso esses argumentos correspondam à mesma jogada, e False caso contrário.
* `jogada_para_cadeia`: jogada → cad. caracteres

    Esta função recebe como argumento um elemento do tipo jogada e devolve uma cadeia de caracteres que a represente, de acordo com o exemplo em baixo.

## Funções Adicionais

* `le_tabuleiro`: cad. caracteres → tuplo

    Esta função recebe uma cadeia de caracteres que corresponde ao nome do ficheiro com os dados de especificação do jogo, e devolve um tuplo de dois tuplos com a especificação das linhas e colunas, respetivamente. Este tuplo pode ser usado para criar um tabuleiro, como no exemplo que se segue. A função deve conseguir ler ficheiros que contenham as especificações referidas, semelhantes aos ficheiros de exemplo fornecidos.

* `pede_jogada`: tabuleiro → jogada

    Esta função recebe o tabuleiro do jogo como argumento e devolve a jogada que o jogador pretende executar. A função deve começar por pedir a coordenada da célula a preencher c, seguida do valor v, de acordo com o exemplo de interação a seguir. A cadeia de caracteres introduzida para representar a coordenada c deve seguir o mesmo formato que a função coordenada_para_cadeia. A função devolve o resultado de invocar a função cria_jogada(c, v) sempre que possível, e o valor F alse caso o utilizador não introduza uma coordenada válida para o tabuleiro em causa. No caso em que o utilizador não introduz uma cadeia de caracteres possível de ser transformada numa coordenada e num inteiro entre 1 e 2, o comportamento da função não está definido.

* `jogo_picross`: cad. caracteres → lógico

    Esta função corresponde à função principal do jogo e permite a um utilizador jogar um jogo completo de Picross. Recebe como argumento uma cadeia de caracteres representando o nome do ficheiro com a especificação do tabuleiro, e devolve True caso o tabuleiro resultante do jogo esteja completo (quadro completo e de acordo com as especificações) e False caso contrário. Após cada jogada, a função deve desenhar o tabuleiro resultante no ecrã e pedir ao utilizador uma nova jogada. Caso a jogada seja válida, deverá atualizar o tabuleiro e repetir este processo até o jogo terminar. Caso contrário, deverá escrever para o ecrã a indicação “Jogada invalida.” e solicitar nova jogada ao utilizador.

    O jogo termina quando o tabuleiro já não tiver células vazias. Note que o jogador pode alterar o conteúdo de uma célula mais do que uma vez, mas só o pode fazer enquanto existirem células vazias no tabuleiro. Quando todas as células têm valor conhecido (branco ou caixa), ou seja já não existem células vazias, o jogo termina.

    No Anexo encontra dois exemplos de execução completa da função jogo_picross