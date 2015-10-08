# 83533 - Mihail Brinza 83557 - Ricardo Brancas. Grupo 9.

# Este programa server para calcular o numero de mandatos atribuidos a cada partido com base no metodo D'Hondt
# utilizado em Portugal.

# Numero de deputadodos por cada circulo eleitoral
deputados = (16, 3, 19, 3, 4, 9, 3, 9, 4, 10, 47, 2, 39, 9, 18, 6, 5, 9, 5, 6, 2, 2)

partidos_siglas = ('PDR', 'PCP-PEV', 'PPD/PSD-CDS/PP', 'MPT', 'L/TDA', 'PAN', 'PTP-MAS', 'JPP',
                   'PNR', 'PPM', 'NC', 'PCTP/MRPP', 'PS', 'B.E.', 'PURP')

partidos_nomes = ('Partido Democratico Republicano', 'CDU - Coligacao Democratica Unitaria', 'Portugal a Frente',
                  'Partido da Terra', 'LIVRE/Tempo de Avancar', 'Pessoas-Animais-Natureza', 'Agir', 'Juntos pelo Povo',
                  'Partido Nacional Renovador', 'Partido Popular Monarquico', 'Nos, Cidadaos!',
                  'Partido Comunista dos Trabalhadores Portugueses', 'Partido Socialista', 'Bloco de Esquerda',
                  'Partido Unido dos Reformados e Pensionistas')

# Esta funcao recebe o numero de votos de cada partido num determinado circulo eleitoral e devolve o numero de mandatos
# aplicando o metodo D'Hondt
def mandatos(nr_mandatos, nr_votacoes):
    lista_votacoes = list(nr_votacoes)
    #Cria uma lista com o mesmo numero de elementos que a lista de votos onde sera guardado o numero de mandatos por candidatura
    mandatos_candidatura = [0] * len(lista_votacoes)

    while nr_mandatos != 0:
        i = lista_votacoes.index(max(lista_votacoes)) # Devolve o indice do elemento com valor de votacoes maximo
        mandatos_candidatura[i] = mandatos_candidatura[i] + 1
        lista_votacoes[i] = nr_votacoes[i] / (mandatos_candidatura[i] + 1)
        nr_mandatos = nr_mandatos - 1

    return mandatos_candidatura

# Esta funcao recebe um tuplo de tuplos com o numero de votos por partido por circulo eleitoral e devolve um tuplo
# contendo o numero total de mandatos atribuidos por partido.
def assembleia(votacoes):
    total_mandatos = [0] * len(partidos_nomes) # Cria uma lista com o mesmo numero de elementos que o numero de partidos

    for i in range(len(votacoes)): # Percorre os indices do tuplo votacoes
        mandatos_temp = mandatos(deputados[i], votacoes[i]) # Calcula o numero de mandatos por cada circulo eleitoral

        for i in range(len(mandatos_temp)):
            total_mandatos[i] = total_mandatos[i] + mandatos_temp[i] # Calcula o total de mandatos por partido

    return tuple(total_mandatos)


# Esta funcao recebe um tuplo de tuplos com o numero de votos por partido por circulo eleitoral e devolve o nome do
# partido com mais mandatos atribuidos ou "empate tecnico" caso haja dois ou mais partidos com o mesmo numero de mandatos.
def max_mandatos(votacoes):
    total_mandatos = assembleia(votacoes) # Vai buscar o valor da funcao assembleia
    mais_mandatos = max(total_mandatos) # Encontra o numero maximo de mandatos atribuidos.

    # Conta o numero de vezes que o numero maximo de mandatos atribuidos existe no tuplo e caso seja maior que um devolve
    # "Empate Tecnico"
    if total_mandatos.count(mais_mandatos) > 1:
        return "Empate Tecnico"
    else:
        # Encontra o indice do elemento maximo do tuplo que corresponde ao partido com mais mandatos.
        indice_mais_mandatos = total_mandatos.index(mais_mandatos)
        # Consulta os tuplos partidos_siglas e partidos_nomes e devolve a sigla e o nome do partido com mais mandatos.
        return partidos_siglas[indice_mais_mandatos] + "\t" + partidos_nomes[indice_mais_mandatos]
