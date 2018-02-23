# 83533 - Mihail Brinza 83557 - Ricardo Brancas. Grupo 81.

# Este programa pretende simular um jogo de tabuleiro - Picross.

import ast

# =================================================================================================
# TAD coordenada: utilizado para indexar as células do tabuleiro.
#==================================================================================================

def cria_coordenada(l, c):
    """cria_coordenada: construtor que recebe dois argumentos e devolve um tuplo que contem a linha e a coluna"""
    if not (isinstance(l, int) and isinstance(c, int) and l > 0 and c > 0):
        raise ValueError('cria_coordenada: argumentos invalidos')
    return (l, c)


# ---Fim: cria_coordenada---#

def coordenada_linha(coord):
    """coordenada_linha: seletor que recebe uma coordenada e devolve a primeira parcela (linha)"""

    return coord[0]


# ---Fim: coordenada_linha---#

def coordenada_coluna(coord):
    """coordenada_coluna: seletor que recebe uma coordenada e devolve a segunda parcela (coluna)"""

    return coord[1]


# ---Fim: coordenada_coluna---#

def e_coordenada(x):
    """e_coordenada: reconhecedor que recebe um argumento e avalia se e' do tipo coordenada, devolvendo um valor
        booleno"""

    if isinstance(x, tuple):
        if len(x) == 2:
            if isinstance(x[0], int) and isinstance(x[1], int):
                if x[0] > 0 and x[1] > 0:
                    return True
    return False

# ---Fim: e_coordenada---#

def coordenadas_iguais(c1, c2):
    """coordenadas_iguais: teste que recebe duas coordenadas e avalia se são iguais, devolvendo um valor booleano"""

    return coordenada_linha(c1) == coordenada_linha(c2) and coordenada_coluna(c1) == coordenada_coluna(c2)


# ---Fim: coordenadas_iguais---#

def coordenada_para_cadeia(coord):
    """coordenada_para_cadeia: tansformador de saida que recebe uma coordenada e devolve uma string sob a forma
    (linha : coluna)"""

    return '(' + str(coordenada_linha(coord)) + ' : ' + str(coordenada_coluna(coord)) + ')'


# ---Fim: coordenada_para_cadeia---#

# ---Fim: TAD coordenada---#

# =================================================================================================
# TAD Tabuleiro: usado para representar, aceder e modificar o tabuleiro de jogo.
# =================================================================================================

def cria_tabuleiro(tuplo):
    """cria_tabuleiro: construtor que recebe um tuplo que descreve a especificacao das linhas e colunas do tabuleiro e
        devolve um dicionario que contem as informacoes sobre o tabuleiro: dimensao, especificacoes de linhas e colunas
        e valor de cada celula"""

    if isinstance(tuplo, tuple):
        if len(tuplo) == 2:
            if isinstance(tuplo[0], tuple) and isinstance(tuplo[1], tuple):
                verific = True
                for e in tuplo[0]:
                    if isinstance(e, tuple):
                        for e2 in e:
                            if not isinstance(e2, int):
                                verific = False
                    else:
                        verific = False
                for e in tuplo[1]:
                    if isinstance(e, tuple):
                        for e2 in e:
                            if not isinstance(e2, int):
                                verific = False
                    else:
                        verific = False
                if verific:
                    tabuleiro = {}
                    for i in range(1, len(tuplo[0]) + 1):
                        for j in range(1, len(tuplo[1]) + 1):
                            tabuleiro[(i, j)] = 0
                    return {
                        "linhas": tuplo[0],
                        "colunas": tuplo[1],
                        "dimL": len(tuplo[0]),
                        "dimC": len(tuplo[1]),
                        "celulas": tabuleiro
                    }
    raise ValueError("cria_tabuleiro: argumentos invalidos")


# ---Fim: cria_tabuleiro---#

def tabuleiro_dimensoes(tabuleiro):
    """tabuleiro_dimensoes: seletor que recebe um elemento do tipo tabuleiro e devolve um tuplo com a sua dimensão"""

    return tabuleiro['dimL'], tabuleiro['dimC']


# ---Fim: tabuleiro_dimensoes---#

def tabuleiro_especificacoes(tabuleiro):
    """tabuleiro_especificacoes: seletor que recebe um elemento do tipo tabuleiro e devolve um tuplo com a sua
     especificacao de linhas e colunas"""

    return tabuleiro["linhas"], tabuleiro["colunas"]


# ---Fim: tabuleiro_especificacoes---#


def tabuleiro_celula(tabuleiro, coordenada):
    """tabuleiro_celula: seletor que recebe um elemento do tipo tabuleiro e um elemento do tipo coordenada e devolve
        o valor da celula correspondete 'a coordenada"""

    if e_tabuleiro(tabuleiro):
        if e_coordenada(coordenada):
            if (coordenada_linha(coordenada), coordenada_coluna(coordenada)) in tabuleiro['celulas']:
                return tabuleiro['celulas'][(coordenada_linha(coordenada), coordenada_coluna(coordenada))]
    raise ValueError("tabuleiro_celula: argumentos invalidos")


# ---Fim: tabuleiro_celula---#

def tabuleiro_preenche_celula(tabuleiro, coordenada, n):
    """tabuleiro_preenche_celula: modificador que recebe um elemento do tipo tabuleiro, um elemento do tipo coordenada
        e um inteiro alterando o valor correspondente 'a coordenada para o valor recebido"""

    if e_tabuleiro(tabuleiro):
        if e_coordenada(coordenada):
            if (coordenada_linha(coordenada), coordenada_coluna(coordenada)) in tabuleiro["celulas"]:
                if n == 0 or n == 1 or n == 2:
                    tabuleiro["celulas"][(coordenada_linha(coordenada), coordenada_coluna(coordenada))] = n
                    return tabuleiro
    raise ValueError("tabuleiro_preenche_celula: argumentos invalidos")


# ---Fim: tabuleiro_preenche_celula---#

def e_tabuleiro(arg):
    """e_tabuleiro: reconhecedor que recebe um argumento e avalia se e' do tipo tabuleiro devolvendo um valor booleano"""

    if isinstance(arg, dict):
        if len(arg) == 5:
            if ("linhas" in arg) and ("colunas" in arg) and ("dimL" in arg) and ("dimC" in arg) and ("celulas" in arg):
                if isinstance(arg["dimL"], int) and arg["dimL"] > 0 and isinstance(arg["dimC"], int) and arg["dimC"] > 0:
                    for t in arg["linhas"]:
                        if not isinstance(t, tuple):
                            return False
                        for e in t:
                            if not isinstance(e, int):
                                return False

                    for t in arg["colunas"]:
                        if not isinstance(t, tuple):
                            return False
                        for e in t:
                            if not isinstance(e, int):
                                return False

                    for c in arg["celulas"]:
                        coord = cria_coordenada(c[0], c[1])
                        if not (e_coordenada(coord) and (
                                            arg["celulas"][c] == 0 or arg["celulas"][c] == 1
                                or arg["celulas"][c] == 2)):
                            return False

                    return True
    return False


# ---Fim: e_tabuleiro---#

def tabuleiro_completo(tabuleiro):
    """tabuleiro_completo: reconhecedor que recebe um elemento do tipo tabuleiro e avalia se as celulas estao
        corretamente preenchidas de acordo com as especificacoes do tabuleiro devolvendo um valor boleano"""

    for i in range(1, tabuleiro_dimensoes(tabuleiro)[0] + 1): #verifica as especificacoes das linhas
        espf_linha = tabuleiro_especificacoes(tabuleiro)[0][i - 1]
        espf_atual = 0
        contador = 0
        for j in range(1, tabuleiro_dimensoes(tabuleiro)[1] + 1): #verifica se a linha atual esta' de acordo com a especificacao da linha
            celula = tabuleiro_celula(tabuleiro, cria_coordenada(i, j))
            if celula == 2:
                espf_atual += 1
            elif celula == 1:
                if espf_atual != 0:
                    if espf_linha[contador] == espf_atual:
                        espf_atual = 0
                        contador += 1
                    else:
                        return False
            else:
                return False
        if espf_atual != 0: #verificar se no fim da linha sobraram celulas preenchidas
            if espf_linha[contador] != espf_atual:
                return False
    for i in range(1, tabuleiro_dimensoes(tabuleiro)[1] + 1): #verifica as especificacoes das colunas
        espf_coluna = tabuleiro_especificacoes(tabuleiro)[1][i - 1]
        espf_atual = 0
        contador = 0
        for j in range(1, tabuleiro_dimensoes(tabuleiro)[0] + 1): #verifica se a coluna atual esta' de acordo com a especificacao da coluna
            celula = tabuleiro_celula(tabuleiro, cria_coordenada(j, i))
            if celula == 2:
                espf_atual += 1
            elif celula == 1:
                if espf_atual != 0:
                    if espf_coluna[contador] == espf_atual:
                        espf_atual = 0
                        contador += 1
                    else:
                        return False
            else:
                return False
        if espf_atual != 0: #verificar se no fim da coluna sobraram celulas preenchidas
            if espf_coluna[contador] != espf_atual:
                return False
    return True


# ---Fim: tabuleiro_completo---#

def tabuleiro_preenchido(tabuleiro):
    """tabuleiro_preenchido: reconhecedor auxiliar que recebe um elemento do tipo tabuleiro e avalia se as celulas do
        tabuleiro estao todas preenchidas devolvendo um valor booleano"""

    for i in range(1, tabuleiro_dimensoes(tabuleiro)[0] + 1):
        for j in range(1, tabuleiro_dimensoes(tabuleiro)[1] + 1):
            if tabuleiro_celula(tabuleiro, cria_coordenada(i, j)) == 0:
                return False
    return True


# ---Fim: tabuleiro_preenchido---#

def escreve_tabuleiro(tabuleiro):
    """escreve_tabuleiro: esta funcao recebe um elemento do tipo tabuleiro e apresenta-o no ecra"""

    if e_tabuleiro(tabuleiro):
        a = max(len(e) for e in tabuleiro_especificacoes(tabuleiro)[0])
        b = max(len(e) for e in tabuleiro_especificacoes(tabuleiro)[1])

        for i in range(-b, 0, 1):
            for e in tabuleiro_especificacoes(tabuleiro)[1]:
                if i >= -len(e):
                    print("  " + str(e[i]) + "  ", end="")
                else:
                    print("     ", end="")
            print("  ")

        for i in range(1, tabuleiro_dimensoes(tabuleiro)[0] + 1):
            for b in range(1, tabuleiro_dimensoes(tabuleiro)[1] + 1):
                print('[ ' + (
                    'x' if tabuleiro_celula(tabuleiro, cria_coordenada(i, b)) == 2 else '.'
                    if tabuleiro_celula(tabuleiro, cria_coordenada(i, b)) == 1 else '?') + ' ]', end='')
            for j in range(0, a, 1):
                if j < len(tabuleiro_especificacoes(tabuleiro)[0][i - 1]):
                    print(" " + str(tabuleiro_especificacoes(tabuleiro)[0][i - 1][j]), end="")
                else:
                    print("  ", end="")
            print("|")
        print()
    else:
        raise ValueError('escreve_tabuleiro: argumento invalido')


# ---Fim: escreve_tabuleiro---#

def tabuleiros_iguais(tab1, tab2):
    """tabuleiros_iguais: teste que recebe dois elementos do tipo tabuleiro e avalia se sao iguais devolvendo um
        valor booleano"""

    if tabuleiro_dimensoes(tab1)[0] == tabuleiro_dimensoes(tab2)[0] and tabuleiro_dimensoes(tab1)[1] == tabuleiro_dimensoes(tab2)[1]:
        if tabuleiro_especificacoes(tab1) == tabuleiro_especificacoes(tab2):
            for i in range(1, tabuleiro_dimensoes(tab1)[0] + 1):
                for c in range(1, tabuleiro_dimensoes(tab1)[1] + 1):
                    if not (tabuleiro_celula(tab1, cria_coordenada(i, c)) == tabuleiro_celula(tab2, cria_coordenada(i, c))):
                        return False
            return True
    return False


# ---Fim: tabuleiros_iguais---#

# ---Fim: TAD tabuleiro---#

# =======================================================================================================================
# TAD Jogada: utilizado para representar a jogada que sera' efetuada sobre o tabuleiro.

def cria_jogada(coord, nr):
    """cria_jogada: construtor que recebe um elemento do tipo coordenada e um inteiro e devolve um tuplo que contem
        a coordenada onde se deve efetuar a jogada, e o valor a por na celula correspondente"""

    if not (e_coordenada(coord) and isinstance(nr, int) and (nr == 1 or nr == 2)):
        raise ValueError('cria_jogada: argumentos invalidos')
    return coord, nr


# ---Fim: cria_jogada---#

def jogada_coordenada(jog):
    """jogada_coordenada: seletor que recebe um elemento do tipo jogada e devolve a sua coordenada"""

    return jog[0]


# ---Fim: jogada_coordenada---#

def jogada_valor(jog):
    """jogada_valor: seletor que recebe um elemento do tipo jogada e devolve o seu valor"""

    return jog[1]


# ---Fim: jogada_valor---#

def e_jogada(arg):
    """e_jogada: reconhecedor que recebe um argumento e avalia se e' uma jogada devolvendo um valor booleano

    """

    if isinstance(arg, tuple):
        if len(arg) == 2:
            if e_coordenada(arg[0]) and isinstance(arg[1], int):
                if arg[1] == 1 or arg[1] == 2:
                    return True
    return False


# ---Fim: e_jogada---#

def jogadas_iguais(jog1, jog2):
    """jogadas_iguais: teste que recebe dois elementos do tipo jogada e valia se sao iguais devolvendo um valor
        booleano"""
    return jogada_coordenada(jog1) == jogada_coordenada(jog2) and (jogada_valor(jog1) == jogada_valor(jog2))


# ---Fim: jogadas_iguais---#

def jogada_para_cadeia(jog):
    """jogada_para_cadeia: funcao que recebe um elemento do tipo jogada e devolve uma string com a jogada do tipo:
        (coordenada) --> valor"""

    return coordenada_para_cadeia(jogada_coordenada(jog)) + ' --> ' + str(jogada_valor(jog))


# ---Fim: jogada_para_cadeia---#

# ---Fim: TAD jogada---#

# ======================================================================================================================

def le_tabuleiro(cadeia):
    """le_tabuleiro: funcao que recebe uma string nome de um ficheiro de jogo e devolve um tuplo com as especificacoes
        de jogo"""

    f = open(cadeia, 'r')
    t = f.read()
    f.close()

    return ast.literal_eval(t)


# ---Fim: le_tabuleiro---#

def pede_jogada(tabuleiro):
    """pede_jogada: funcao que recebe um elemento do tipo tabuleiro e pede ao utilizador um input da jogada que pretende
        efetuar"""

    print('Introduza a jogada')
    c = input('- coordenada entre (1 : 1) e (' + str(tabuleiro_dimensoes(tabuleiro)[0]) + ' : ' +
              str(tabuleiro_dimensoes(tabuleiro)[1]) + ') >> ')
    valor = int(input('- valor >> '))

    d = c.strip('()').split(' : ') #converte o input para uma lista em que cada numero e' um elemento
    c1 = int(d[0])
    c2 = int(d[1])

    if not ((0 < c1 <= tabuleiro_dimensoes(tabuleiro)[0]) and (0 < c2 <= tabuleiro_dimensoes(tabuleiro)[1])):
        return False

    return cria_jogada(cria_coordenada(c1, c2), valor)


# ---Fim: pede_jogada---#

def jogo_picross(cadeia):
    """jogo_picross: funcao principal do jogo que recebe uma string com o nome do ficheiro do tabuleiro em que vai
        ser jogado o jogo. A funcao apresenta no ecra o tabuleiro todas as jogadas e quando o tabuleiro esta completo
        apresenta no ecra uma mensagem de fim de jogo"""

    t = le_tabuleiro(cadeia)
    tabuleiro = cria_tabuleiro(t)
    print("JOGO PICROSS")
    while not tabuleiro_preenchido(tabuleiro):
        escreve_tabuleiro(tabuleiro)
        j = False
        while not j:
            j = pede_jogada(tabuleiro)
            if not j:
                print("Jogada invalida.")
        tabuleiro_preenche_celula(tabuleiro, jogada_coordenada(j), jogada_valor(j))

    escreve_tabuleiro(tabuleiro)
    if tabuleiro_completo(tabuleiro):
        print("JOGO: Parabens, encontrou a solucao!")
        return True
    else:
        print("JOGO: O tabuleiro nao esta correto!")
        return False

    # ---Fim: jogo_picross---#
