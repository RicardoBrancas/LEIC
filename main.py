
#TAD coordenada

def cria_coordenada(l, c):
    if not(isinstance(l, int) and isinstance(c, int) and l>0 and c>0):
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
    if coordenada_linha(c1) == coordenada_linha(c2) and\
        coordenada_coluna(c1) == coordenada_coluna(c2):
        return True
    return False

def coordenada_para_cadeia(coord):
    return '(' + str(coordenada_linha(coord)) + " : " + str(coordenada_coluna(coord)) + ")"

# TAD Tabuleiro


#TAD Jogada

def cria_jogada(coord, nr):
    if not (e_coordenada(coord) and isinstance(nr,int) and (nr==1 or nr==2)):
            raise ValueError('Argumentos Invalidos')
    return (coord, nr)

def jogada_coordenada(jog):
    return jog[0]

def jogada_valor(jog):
    return jog[1]

def e_jogada(arg):
    if isinstance(arg, tuple):
        if len(arg)==2 and e_coordenada(arg[0]):
            return (arg[1]==1 or arg[1]==2)
    return False

def jogadas_iguais(jog1, jog2):
    return jog1[0]==jog2[0] and jog1[1]==jog2[1]

def jogada_para_cadeia(jog):
    return coordenada_para_cadeia(jog[0]) + ' --> ' + str(jog[1])











        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        


