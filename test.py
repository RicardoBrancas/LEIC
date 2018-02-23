import ast


# TAD coordenada
def cria_coordenada(l, c):
    if not (isinstance(l, int) and isinstance(c, int) and l > 0 and c > 0):
        raise ValueError('cria_coordenada: argumentos invalidos')

    return (l, c)


def coordenada_linha(coord):
    return coord[0]


def coordenada_coluna(coord):
    return coord[1]


def e_coordenada(x):
    if isinstance(x, tuple):
        if len(x) == 2:
            if isinstance(x[0], int) and isinstance(x[1], int):
                if x[0] > 0 and x[1] > 0:
                    return True
    return False


def coordenadas_iguais(c1, c2):
    if coordenada_linha(c1) == coordenada_linha(c2) and \
                    coordenada_coluna(c1) == coordenada_coluna(c2):
        return True
    return False


def coordenada_para_cadeia(coord):
    return '(' + str(coordenada_linha(coord)) + " : " + str(coordenada_coluna(coord)) + ")"


# TAD Tabuleiro
def cria_tabuleiro(tuplo):
    if isinstance(tuplo, tuple):
        if len(tuplo) == 2:
            if isinstance(tuplo[0], tuple) and isinstance(tuplo[1], tuple):
                verific = True
                for e in tuplo[0]:
                    for e2 in e:
                        if not isinstance(e2, int):
                            verific = False
                for e in tuplo[1]:
                    for e2 in e:
                        if not isinstance(e2, int):
                            verific = False
                if verific:
                    tabuleiro = {}
                    for i in range(1, len(tuplo[0]) + 1):
                        for j in range(1, len(tuplo[1]) + 1):
                            tabuleiro[cria_coordenada(i, j)] = 0
                    return {
                        "linhas": tuplo[0],
                        "colunas": tuplo[1],
                        "dimL": len(tuplo[0]),
                        "dimC": len(tuplo[1]),
                        "tabuleiro": tabuleiro
                    }
    raise ValueError("cria_tabuleiro: argumentos invalidos")


def tabuleiro_dimensoes(tabuleiro):
    return tabuleiro['dimL'], tabuleiro['dimC']


def tabuleiro_especificacoes(tabuleiro):
    return tabuleiro["linhas"], tabuleiro["colunas"]


def tabuleiro_celula(tabuleiro, coordenada):
    if e_tabuleiro(tabuleiro):
        if e_coordenada(coordenada):
            if coordenada in tabuleiro['tabuleiro']:
                return tabuleiro['tabuleiro'][coordenada]
    raise ValueError("tabuleiro_celula: argumentos invalidos")


def tabuleiro_preenche_celula(tabuleiro, coordenada, n):
    if e_tabuleiro(tabuleiro):
        if e_coordenada(coordenada):
            if coordenada in tabuleiro["tabuleiro"]:
                if n == 0 or n == 1 or n == 2:
                    tabuleiro["tabuleiro"][coordenada] = n
                    return tabuleiro
    raise ValueError("tabuleiro_preenche_celula: argumentos invalidos")


def e_tabuleiro(arg):
    if isinstance(arg, dict):
        if len(arg) == 5:
            if ("linhas" in arg) and ("colunas" in arg) and ("dimL" in arg) and ("dimC" in arg) and (
                        "tabuleiro" in arg):
                if isinstance(arg["dimL"], int) and arg["dimL"] > 0 and isinstance(arg["dimC"], int) \
                        and arg["dimC"] > 0:
                    for t in arg["linhas"]:
                        for e in t:
                            if not isinstance(e, int):
                                return False

                    for t in arg["colunas"]:
                        for e in t:
                            if not isinstance(e, int):
                                return False

                    for c in arg["tabuleiro"]:
                        if not (e_coordenada(c) and (
                                            arg["tabuleiro"][c] == 0 or arg["tabuleiro"][c] == 1
                                or arg["tabuleiro"][c] == 2)):
                            return False

                    return True
    return False


def tabuleiro_completo(tabuleiro):
    for i in range(1, tabuleiro_dimensoes(tabuleiro)[0] + 1):
        a = tabuleiro_especificacoes(tabuleiro)[0][i - 1]
        b = 0
        c = 0
        for j in range(1, tabuleiro_dimensoes(tabuleiro)[1] + 1):
            d = tabuleiro_celula(tabuleiro, cria_coordenada(i, j))
            if d == 2:
                b += 1
            elif d == 1:
                if b == 0:
                    pass
                elif a[c] == b:
                    b = 0
                    c += 1
                else:
                    return False
            else:
                return False
    for i in range(1, tabuleiro_dimensoes(tabuleiro)[1] + 1):
        a = tabuleiro_especificacoes(tabuleiro)[1][i - 1]
        b = 0
        c = 0
        for j in range(1, tabuleiro_dimensoes(tabuleiro)[0] + 1):
            d = tabuleiro_celula(tabuleiro, cria_coordenada(j, i))
            if d == 2:
                b += 1
            elif d == 1:
                if b == 0:
                    pass
                elif a[c] == b:
                    b = 0
                    c += 1
                else:
                    return False
            else:
                return False
    return True


def tabuleiro_preenchido(tabuleiro):
    for i in range(1, tabuleiro_dimensoes(tabuleiro)[0] + 1):
        for j in range(1, tabuleiro_dimensoes(tabuleiro)[1] + 1):
            if tabuleiro_celula(tabuleiro, cria_coordenada(i, j)) == 0:
                return False


def escreve_tabuleiro(tabuleiro):
    if e_tabuleiro(tabuleiro):
        a = max(len(e) for e in tabuleiro_especificacoes(tabuleiro)[1])
        b = max(len(e) for e in tabuleiro_especificacoes(tabuleiro)[0])

        for i in range(-a, 0, 1):
            for e in tabuleiro_especificacoes(tabuleiro)[1]:
                if i >= -len(e):
                    print("  " + str(e[i]) + "  ", end="")
                else:
                    print("     ", end="")
            print("  ")

        for i in range(1, tabuleiro_dimensoes(tabuleiro)[0] + 1):
            for c in range(1, tabuleiro_dimensoes(tabuleiro)[1] + 1):
                print('[ ' + (
                    'X' if tabuleiro_celula(tabuleiro, cria_coordenada(i, c)) == 2 else '.' if tabuleiro_celula(tabuleiro,
                                                                                                                cria_coordenada(i,
                                                                                                                                c)) == 1 else '?') + ' ]',
                      end='')
            for j in range(0, b, 1):
                if j < len(tabuleiro_especificacoes(tabuleiro)[0][i - 1]):
                    print(" " + str(tabuleiro_especificacoes(tabuleiro)[0][i - 1][j]), end="")
                else:
                    print("  ", end="")
            print("|")
        print()


def tabuleiros_iguais(tab1, tab2):
    if e_tabuleiro(tab1) and e_tabuleiro(tab2):
        if tab1['dimL'] == tab2['dimL'] and tab1['dimC'] == tab2['dimC']:
            if tab1['colunas'] == tab2['colunas']:
                if tab1['linhas'] == tab2['linhas']:
                    for i in range(1, tabuleiro_dimensoes(tab1)[0] + 1):
                        for c in range(1, tabuleiro_dimensoes(tab1)[1] + 1):
                            if not (tab1['tabuleiro'][cria_coordenada(i, c)] == tab2['tabuleiro'][cria_coordenada(i, c)]):
                                return False
                    return True
    return False


# TAD Jogada
def cria_jogada(coord, nr):
    if not (e_coordenada(coord) and isinstance(nr, int) and (nr == 1 or nr == 2)):
        raise ValueError('cria_jogada: argumentos invalidos')
    return (coord, nr)


def jogada_coordenada(jog):
    return jog[0]


def jogada_valor(jog):
    return jog[1]


def e_jogada(arg):
    if isinstance(arg, tuple):
        if len(arg) == 2 and e_coordenada(arg[0]):
            return (arg[1] == 1 or arg[1] == 2)
    return False


def jogadas_iguais(jog1, jog2):
    return coordenadas_iguais(jogada_coordenada(jog1), jogada_coordenada(jog2)) and jogada_valor(jog1) == jogada_valor(jog2)


def jogada_para_cadeia(jog):
    return coordenada_para_cadeia(jogada_coordenada(jog)) + ' --> ' + str(jogada_valor(jog))


def le_tabuleiro(str):
    f = open(str)
    t = f.read()
    return ast.literal_eval(t)


def pede_jogada(tabuleiro):
    print('Introduza a jogada')
    c = input('- coordenada entre (1 : 1) e (' + str(tabuleiro_dimensoes(tabuleiro)[0]) + ' : ' +
              str(tabuleiro_dimensoes(tabuleiro)[1]) + ') >> ')
    v = int(input('- valor >> '))
    d = c.strip('()').split(':')
    c1 = int(d[0])
    c2 = int(d[1])

    if not ((0 < c1 <= tabuleiro_dimensoes(tabuleiro)[0]) and
                (0 < c2 <= tabuleiro_dimensoes(tabuleiro)[1])):
        return False

    return cria_jogada(cria_coordenada(int(d[0]), int(d[1])), v)


def jogo_picross(str):
    t = le_tabuleiro(str)
    tabuleiro = cria_tabuleiro(t)
    print("JOGO PICROSS")
    while not tabuleiro_preenchido(tabuleiro):
        escreve_tabuleiro(tabuleiro)
        j = False
        while j == False:
            j = pede_jogada(tabuleiro)
            if j == False:
                print("Jogada invalida.")
        tabuleiro_preenche_celula(tabuleiro, jogada_coordenada(j), jogada_valor(j))

    escreve_tabuleiro(tabuleiro)
    if tabuleiro_completo(tabuleiro):
        print("JOGO: Parabens, encontrou a solucao!")
        return True
    else:
        print("JOGO: O tabuleiro nao esta correto!")
        return False


e=(((2,), (3,), (2,), (2, 3), (2,), (2,)), ((2,), (1, 2), (2,), (3,), (3,)))
t = cria_tabuleiro(e)

escreve_tabuleiro(t)
print(tabuleiro_completo(t))

# tabuleiro_preenche_celula(t, cria_coordenada(4, 2), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(4, 3), 1)
# tabuleiro_preenche_celula(t, cria_coordenada(1, 1), 1)
# tabuleiro_preenche_celula(t, cria_coordenada(1, 2), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(1, 3), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(1, 4), 1)
# tabuleiro_preenche_celula(t, cria_coordenada(1, 5), 1)
# tabuleiro_preenche_celula(t, cria_coordenada(2, 1), 1)
# tabuleiro_preenche_celula(t, cria_coordenada(2, 2), 1)
# tabuleiro_preenche_celula(t, cria_coordenada(2, 3), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(2, 4), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(2, 5), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(3, 1), 1)
# tabuleiro_preenche_celula(t, cria_coordenada(3, 2), 1)
# tabuleiro_preenche_celula(t, cria_coordenada(3, 3), 1)
# tabuleiro_preenche_celula(t, cria_coordenada(3, 4), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(3, 5), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(4, 1), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(4, 4), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(4, 5), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(5, 1), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(5, 2), 2)
# tabuleiro_preenche_celula(t, cria_coordenada(5, 3), 1)
# tabuleiro_preenche_celula(t, cria_coordenada(5, 4), 1)
# tabuleiro_preenche_celula(t, cria_coordenada(5, 5), 1)
#
# escreve_tabuleiro(t)
# print(tabuleiro_completo(t))