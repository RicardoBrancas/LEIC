deputados = (16, 3, 19, 3, 4, 9, 3, 9, 4, 10, 47, 2, 39, 9, 18, 6, 5, 9, 5, 6, 2, 2)

partidos_siglas = ('PDR', 'PCP-PEV', 'PPD/PSD-CDS/PP', 'MPT', 'L/TDA', 'PAN', 'PTP-MAS', 'JPP',
                   'PNR', 'PPM', 'NC', 'PCTP/MRPP', 'PS', 'B.E.', 'PURP')

partidos_nomes = ('Partido Democratico Republicano', 'CDU - Coligacao Democratica Unitaria', 'Portugal a Frente',
                  'Partido da Terra', 'LIVRE/Tempo de Avancar', 'Pessoas-Animais-Natureza', 'Agir', 'Juntos pelo Povo',
                  'Partido Nacional Renovador', 'Partido Popular Monarquico', 'Nos, Cidadaos!',
                  'Partido Comunista dos Trabalhadores Portugueses', 'Partido Socialista', 'Bloco de Esquerda',
                  'Partido Unido dos Reformados e Pensionistas')

def mandatos(nr_mandatos, nr_votacoes):
    lista_votacoes = list(nr_votacoes)
    mandatos_candidatura = [0] * len(lista_votacoes)

    while nr_mandatos != 0:
        i = lista_votacoes.index(max(lista_votacoes))
        mandatos_candidatura[i] += 1
        lista_votacoes[i] = nr_votacoes[i] / (mandatos_candidatura[i] + 1)
        nr_mandatos -= 1

    return mandatos_candidatura

def assembleia(votacoes):
    total_mandatos = [0] * len(partidos_nomes)

    for i in range(len(votacoes)):
        mandatos_temp = mandatos(deputados[i], votacoes[i])

        for i in range(len(mandatos_temp)):
            total_mandatos[i] = total_mandatos[i] + mandatos_temp[i]

    return tuple(total_mandatos)

def max_mandatos(votacoes):
    total_mandatos = list(assembleia(votacoes))
    mais_mandatos = max(total_mandatos)
    if total_mandatos.count(mais_mandatos) > 1:
        return "Empate Tecnico"
    else:
        indice_mais_mandatos = total_mandatos.index(mais_mandatos)
        return partidos_siglas[indice_mais_mandatos] + "\t" + partidos_nomes[indice_mais_mandatos]
