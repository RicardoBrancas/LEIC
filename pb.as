;=====================================================================================

;                                PÁSSARO BAMBOLEANTE

;=====================================================================================
; Projeto - Introdução à Arquitetura de Computadores
; Grupo 40
;    Mihail Brinza - 83533
;    Ricardo Brancas - 83557
;    Vasco Pais - 83573

;=====================================================================================
;                                    CONSTANTES
;=====================================================================================

SP_INICIAL	EQU	FDFFh

;Constantes do display de 7 segmentos
DISPLAY_7S_1	EQU	FFF0h
DISPLAY_7S_2	EQU	FFF1h
DISPLAY_7S_3	EQU	FFF2h
DISPLAY_7S_4	EQU	FFF3h

;Constantes do LCD
LCD_C_ADDR	EQU	FFF4h
LCD_O_ADDR	EQU	FFF5h

;Constantes do Temporizador
CONT_STEP_ADDR	EQU	FFF6h
CONT_CTRL_ADDR	EQU	FFF7h

;Constante dos LED's
LEDS_ADDR	EQU	FFF8h

;Constante da mascara de interrupções
INT_MASK_ADDR	EQU	FFFAh

;Constantes da Janela de Texto
DISPLAY_C_ADDR	EQU	FFFCh
DISPLAY_O_ADDR	EQU	FFFEh

;Mascara
INT_MASK	EQU	1000000000000111b

;Cursores LCD
LCD_CURSOR_INIC	EQU	1000000000000101b
LCD_CURSOR_TXT	EQU	1000000000000111b

;Dimensões da Janela de texto
COLUNAS		EQU	79
LINHAS		EQU	24

; Carateres
FIM_TEXTO	EQU	'@'
CHAR_TRACO	EQU	2Dh
CHAR_O		EQU	4Fh
CHAR_X		EQU	58h
CHAR_>		EQU	3Eh
ESPACO		EQU	20h
CHAR_0		EQU	30h

; Número de interrupções de relógio por ciclo de simulação
PHYSICS_STEP	EQU	1
; 0.0625m/100ms^2 Gravidade em virgula fixa - 8 casas decimais
GRAV_POR_STEP	EQU	0000000000010000b
; 0.5625m/100ms Impulso de subida em virgula fixa - 8 casas decimais
IMPULSO_SUBIDA	EQU	0000000010010000b

;Altura máxima da abertura dos obstáculos
POS_OBST_MAX	EQU	15

;Intervalo entre cada obstáculo
INTERV_OBSTAC	EQU	5

ESPACO_OBST	EQU	6

NIVEL_MAXIMO	EQU	8

;Máscara incial utilizada para a geração aleatória
MASK_SEQUENCIA	EQU	1000000000010110b

;=====================================================================================
;                                     VARIÁVEIS
;=====================================================================================

		ORIG	8000h
;Strings
Vartext1	STR	'Prepare-se', FIM_TEXTO
Vartext2	STR	'Prima I1 para iniciar o jogo', FIM_TEXTO
Vartext3	STR	'COLUNAS', FIM_TEXTO
Vartext4	STR	'Fim do jogo', FIM_TEXTO
VarText5	STR	'Prima I1 para reiniciar o jogo', FIM_TEXTO
COMECAR_JOGO	STR	1

N_SEQUENCIA	STR	0

P_COLUNA_ATUAL	STR	19
;Linha atual do pássaro (número inteiro utilizado para atualizar o ecrã)
P_LINHA_ATUAL	STR	11
;Linha atual do pássaro em vírgula flutuante
LINHA_ATUAL	STR	0000101100000000b ; 11d em virgula fixa - 8 casas decimais
FALTA_SUBIR	STR	0

;Velocidades de movimentos de obstáculos para cada nível
VELOCIDADES	STR	10, 9, 8, 7, 6, 5, 4, 2, 1

VELOCIDADE	STR	0

NIVEL		STR	0

V_OBSTACULOS	STR	0
CONT_MOV_OBST	STR	0
CONTADOR_OBST	STR	0
DistanciaPerc	STR	0
ObstaculosUltr	STR	0

OBSTACULOS	TAB	COLUNAS

NIVEL_MUDOU	STR	0
DEVE_SUBIR	STR	0
DEVE_CRIAR_OSBT	STR	0
DEVE_MOVER_OBST	STR	0
DEVE_PYSC_STEP	STR	0

;=====================================================================================
;                             TABELA DE INTERRUPÇÕES
;=====================================================================================

		ORIG	FE00h
INT0		WORD	i0Premido
		ORIG	FE01h
INT1		WORD	i1Premido
		ORIG	FE02h
INT2		WORD	i2Premido
		ORIG	FE0Fh
INT15		WORD	tempAtivado

;=====================================================================================
;                                      CÓDIGO
;=====================================================================================

		ORIG	0000h
		JMP	Inicio

;-------------------------------------------------------------------------------------
;i0Premido: Interrupção 0
;	    Altera a flag DEVE_SUBIR cujo estado vai ser verificado no ciclo principal
;-------------------------------------------------------------------------------------
i0Premido:	INC	M[DEVE_SUBIR]
		RTI

;-------------------------------------------------------------------------------------
;i1Premido: Interrupção 1
;	    Dá inicio ao jogo (através da alteração da flag COMECAR_JOGO)
;	    Diminui o nível (dificuldade) caso o jogo ainda não esteja na dificuldade
;	    mínima
;-------------------------------------------------------------------------------------
i1Premido:	MOV	M[COMECAR_JOGO], R0
		CMP	M[NIVEL], R0
		BR.NP	FimI1Primido
		DEC	M[NIVEL]
		INC	M[NIVEL_MUDOU]
FimI1Primido:	RTI

;-------------------------------------------------------------------------------------
;i2Premido: Interrupção 2
;	    Aumenta o nível (dificuldade) caso o jogo ainda não esteja na dificuldade
;	    máxima
;-------------------------------------------------------------------------------------
i2Premido:	PUSH	R1
		MOV	R1, M[NIVEL]
		CMP	R1, NIVEL_MAXIMO
		BR.NN	FimI2Primido
		INC	M[NIVEL]
		INC	M[NIVEL_MUDOU]
FimI2Primido:	POP	R1
		RTI

;-------------------------------------------------------------------------------------
;tempAtivado: Interrupção 15 (temporizador)
;	      Atualiza várias flags cujo estado é verificado no ciclo principal
;-------------------------------------------------------------------------------------
tempAtivado:	INC	M[DEVE_PYSC_STEP] ;Altera o estado da flag DEVE_PYSC_STEP
		CMP	M[CONT_MOV_OBST], R0
		BR.P	temporAtiv2
		INC	M[DEVE_MOVER_OBST] ;Altera o estado da flag DEVE_MOVER_OBST
temporAtiv2:	DEC	M[CONT_MOV_OBST]
		MOV	R7, PHYSICS_STEP
		MOV	M[CONT_STEP_ADDR], R7
		MOV	R7, 1
		MOV	M[CONT_CTRL_ADDR], R7
FimTempAtivado:	RTI

;-------------------------------------------------------------------------------------
;Random: Subrotina que gera um numero aleatório para a altura de cada obstáculo
;	 com base na função apresentada no enunciado do projeto.
;	Entradas: Recebe um numero aleatorio que é gerado no início do jogo
;	Saidas: STACK (M[SP+4]) - devolve um número aleatório
;-------------------------------------------------------------------------------------
Random:		PUSH	R1
		PUSH	R2
		MOV	R1, M[N_SEQUENCIA]
		SHR	R1, 1
		BR.C	Random2
		MOV	R1, M[N_SEQUENCIA]
		ROR	R1, 1
		MOV	M[N_SEQUENCIA], R1
		BR	RandomFim
Random2:	MOV	R1, M[N_SEQUENCIA]
		XOR	R1, MASK_SEQUENCIA
		ROR	R1, 1
		MOV	M[N_SEQUENCIA], R1
RandomFim:	MOV	R2, POS_OBST_MAX
		DIV	R1, R2
		INC	R2
		MOV	M[SP+4], R2
		POP	R2
		POP	R1
		RET

;-------------------------------------------------------------------------------------
;LimpaJanela: Subrotina que apaga todos os carateres da Janela de texto (escreve um 
;	      espaço por cima de todos os carcteres)
;	Entradas: O numero total de linhas e de colunas e o carater "espaço" em ASCII
;-------------------------------------------------------------------------------------
LimpaJanela:	MOV	R1, R0
		MOV	R2, R0
CicloLinhas:	CMP	R1, LINHAS
		BR.NN	FimLimpaJanela
CicloColunas:	CMP	R2, COLUNAS
		BR.NN	FimColunas
		PUSH	R1
		PUSH	R2
		PUSH	ESPACO
		CALL	EscreveChar
		INC	R2
		BR	CicloColunas
FimColunas:	MOV	R2, R0
		INC	R1
		BR	CicloLinhas
FimLimpaJanela:	RET

;-------------------------------------------------------------------------------------
;LimpaLCD: Subrotina que apaga a distância percorrida do ecrã. Da coluna 0 à 7
;	Entradas: O cursor que indica a posição em que se deve escrever e o carácter
;		  espaço
;-------------------------------------------------------------------------------------
LimpaLCD:	MOV	R1, LCD_CURSOR_INIC
		MOV	R2, ESPACO
CicloLimpaLCD:	MOV	M[LCD_C_ADDR], R1
		MOV	M[LCD_O_ADDR], R2
		DEC	R1
		CMP	R1, 1000000000000000b
		BR.Z	FimLimpaLCD
		BR	CicloLimpaLCD
FimLimpaLCD:	RET

;-------------------------------------------------------------------------------------
;LimpaDisplay7S: Rotina que limpa o display de 7 segmentos
;-------------------------------------------------------------------------------------
LimpaDisplay7S:	MOV	M[DISPLAY_7S_1], R0
		MOV	M[DISPLAY_7S_2], R0
		MOV	M[DISPLAY_7S_3], R0
		MOV	M[DISPLAY_7S_4], R0
		RET

;-------------------------------------------------------------------------------------
;EscreveChar: Subrotina que efetua a escrita de um carácter na janela de texto.
;	Entradas: STACK (M[SP+7]) - Linha onde a string vai ser escrita
;		  STACK (M[SP+6]) - Coluna inicial
;		  STACK (M[SP+5]) - Carácter a ser escrito
;	(Preserva os valores dos Registos utilizados)
;-------------------------------------------------------------------------------------
EscreveChar:	PUSH	R1
		PUSH	R2
		PUSH	R3
		MOV	R1, M[SP+7]
		MOV	R2, M[SP+6]
		MOV	R3, M[SP+5]
		SHL	R1, 8
		ADD	R1, R2
		MOV	M[DISPLAY_C_ADDR], R1
		MOV	M[DISPLAY_O_ADDR], R3
		POP	R3
		POP	R2
		POP	R1
		RETN	3

;-------------------------------------------------------------------------------------
;EscreveStr: Subrotina que efetua a escrita duma string na janela de texto.
;	Entradas: STACK (M[SP+8]) - Endereço do primeiro carácter da string
;		  STACK (M[SP+7]) - Linha onde a string vai ser escrita
;		  STACK (M[SP+6]) - Coluna inicial
;	(Preserva os valores dos Registos utilizados)
;-------------------------------------------------------------------------------------
EscreveStr:	PUSH	R1
		PUSH	R2
		PUSH	R3
		PUSH	R4
		MOV	R1, M[SP+8]
		MOV	R2, M[SP+7]
		MOV	R3, M[SP+6]
CicloEscreveStr:MOV	R4, M[R1]
		CMP	R4, FIM_TEXTO
		BR.Z	FimEscreveStr
		PUSH	R2
		PUSH	R3
		PUSH	R4
		CALL	EscreveChar
		INC	R1
		INC	R3
		BR	CicloEscreveStr
FimEscreveStr:	POP	R4
		POP	R3
		POP	R2
		POP	R1
		RETN	3

;-------------------------------------------------------------------------------------
;AtualDisplay7S: Subrotina que apresenta a pontuação no display led de 7 segmentos.
;	Entradas: Endereço de memoria do digito do display menos significativo e o 
;		  numero de obstaculos ultrapassados.
;-------------------------------------------------------------------------------------
AtualDisplay7S:	PUSH	R1
		PUSH	R2
		PUSH	R3
		MOV	R1, DISPLAY_7S_1
		MOV	R2, M[ObstaculosUltr]
CicloAtualD7S:	MOV	R3, 10
		DIV	R2, R3
		MOV	M[R1], R3
		INC	R1
		CMP	R2, R0
		BR.NZ	CicloAtualD7S
		POP	R3
		POP	R2
		POP	R1
		RET

;-------------------------------------------------------------------------------------
;AtualizaLeds: Subrotina que apresenta o nivel de dificuldade do jogo nos LEDs.
;	Entradas: O Nivel atual de jogo e o endereço de escrita dos LEDs.
;-------------------------------------------------------------------------------------
AtualizaLeds:	MOV	R1, M[NIVEL]
		MOV	R2, R0
		MOV	R3, R0
CicloAtualLeds:	CMP	R1, R2
		BR.N	FimAtualizaLeds
		STC
		RORC	R3, 1
		INC	R2
		BR	CicloAtualLeds
FimAtualizaLeds:MOV	M[LEDS_ADDR], R3
		RET

;-------------------------------------------------------------------------------------
;LCD: Subrotina que apresenta a distância percorrida no LCD
;	Entradas: O carácter e o cursor, que indica onde se deve escrever.
;-------------------------------------------------------------------------------------
LCD:		MOV	R1, LCD_CURSOR_INIC
		MOV	R2, M[DistanciaPerc]
CicloLCD:	MOV	R3, 10
		DIV	R2, R3
		MOV	R4, R3
		ADD	R4, CHAR_0 ;Para obter o caracter ASCII de um número entre 0 e 9
		MOV	M[LCD_C_ADDR], R1
		MOV	M[LCD_O_ADDR], R4
		DEC	R1
		CMP	R2, R0
		BR.NZ	CicloLCD
		RET

;-------------------------------------------------------------------------------------
;EscreveStrLCD: Subrotina que escreve uma string no LCD.
;	Entradas: O cursor que indica a posição em que se deve escrever e
;		  o endereço de memória do primeiro caracter.
;-------------------------------------------------------------------------------------
EscreveStrLCD:	MOV	R1, LCD_CURSOR_TXT
		MOV	R2, Vartext3
CiclEscrvStrLCD:MOV	R3, M[R2]
		CMP	R3, FIM_TEXTO
		BR.Z	FimEscrvStrLCD
		MOV	M[LCD_C_ADDR], R1
		MOV	M[LCD_O_ADDR], R3
		INC	R2
		INC	R1
		BR	CiclEscrvStrLCD
FimEscrvStrLCD:	RET

;-------------------------------------------------------------------------------------
; DesenhaLimites: Esta funcao desenha os limites superior e infeior do ecra de jogo
; 	Entradas: O numero de colunas
;-------------------------------------------------------------------------------------
DesenhaLimites:	MOV	R2, R0
CicloDLim2:	MOV	R1, COLUNAS
		DEC	R1
CicloDLim1:	PUSH	R2
		PUSH	R1
		PUSH	CHAR_TRACO
		CALL	EscreveChar
		DEC	R1
		CMP	R0, R1
		BR.NP	CicloDLim1
		CMP	R2, R0 ;Se a linha no qual acabamos de desenhar limites for 0, significa que falta desenhar o limite inferior
		BR.NZ	FimDesenhaLim
		MOV	R2, LINHAS
		DEC	R2
		BR	CicloDLim2
FimDesenhaLim:	RET

;-------------------------------------------------------------------------------------
;AtualizaPassaro: Mostra ou apaga o pássaro da Janela Texto, dependendo do argumento
;		  passado: caso zero o pássaro é apagado, caso contrário o pássaro é
;		  desenhado
;	Entradas: STACK (M[SP+2]) - Variável que controla se o passáro é desenhado ou 
;				    apagado
;-------------------------------------------------------------------------------------
AtualizaPassaro:MOV	R1, M[P_LINHA_ATUAL]
		MOV	R2, M[P_COLUNA_ATUAL]
		CMP	M[SP+2], R0
		BR.NZ	DesenhaPassaro
		MOV	R3, ESPACO ;Como vamos apagar o pássaro, o corpo é um espaço
		MOV	R4, ESPACO ;Como vamos apagar o pássaro, o bico é um espaço
		BR	InicioDPassaro
DesenhaPassaro:	MOV	R3, CHAR_O ;Como vamos desenhar o pássaro, o corpo é um "O"
		MOV	R4, CHAR_> ;Como vamos apdesenharagar o pássaro, o bico é um ">"
InicioDPassaro:	PUSH	R1
		PUSH	R2
		PUSH	R3
		CALL	EscreveChar
		INC	R2
		PUSH	R1
		PUSH	R2
		PUSH	R4
		CALL	EscreveChar
		RETN	1

;-------------------------------------------------------------------------------------
;AtualizaObst: Mostra ou apaga os obstáculos da Janela Texto, dependendo do argumento
;	       passado: caso zero os obstáculos são apagados, caso contrário os 
;	       obstáculos são desenhados
;	Entradas: STACK (M[SP+2]) - Variável que controla se os obstáculos são desenhados ou 
;			  apagados
;-------------------------------------------------------------------------------------
AtualizaObst:	CMP	M[SP+2], R0
		BR.NZ	DesenhaObst
		MOV	R5, ESPACO ;Caso o argumento seja igual a zero vamos apagar (escrever um espaço) onde existir um obstáculo
		BR	InicioAO
DesenhaObst:	MOV	R5, CHAR_X ;Caso o argumento seja diferente de zero vamos escrever um X onde existir um obstáculo
InicioAO:	MOV	R1, R0
		MOV	R2, OBSTACULOS
CicloColAO:	CMP	R1, COLUNAS
		JMP.NN	FimAtualzObst
		CMP	M[R2], R0
		JMP.Z	FimCicloAO
		MOV	R4, LINHAS
		SUB	R4, 2
		SUB	R4, M[R2]
		MOV	R3, LINHAS
		SUB	R3, 2
CicloLinhaAO:	CMP	R3, R4
		BR.NZ	SaltaCicloAO
		SUB	R3, ESPACO_OBST
SaltaCicloAO:	PUSH	R3
		PUSH	R1
		PUSH	R5
		CALL	EscreveChar
		DEC	R3
		CMP	R3, R0
		BR.NZ	CicloLinhaAO
FimCicloAO:	INC	R1
		INC	R2
		JMP	CicloColAO
FimAtualzObst:	RETN	1

;-------------------------------------------------------------------------------------
;VerificaColisao: Verifica se o passaro pode ser movido para a nova posição (se vai ou 
;		  não haver colisão)
;	Entradas: STACK (M[SP+3]) - nova linha
;		  STACK (M[SP+2]) - nova coluna
;	Saida: STACK - 0 se não houve colisão, 1 caso contrário
;	(Possui dois pontos de saída)
;-------------------------------------------------------------------------------------
VerificaColisao:MOV	R1, M[SP+3]
		CMP	R1, R0 ;Verifica colisao com o limite superior
		BR.NP	HaColisao
		CMP	R1, LINHAS ;Verifica colisao com o limite inferior
		BR.NN	HaColisao
		MOV	R5, R0 ;Usado para verificar se estamos a testar a colisão do corpo ou do bico
		MOV	R2, OBSTACULOS
		ADD	R2, M[SP+2] ;endereço do obstaculo da coluna na qual o passaro se encontra
VC2:		CMP	M[R2], R0
		BR.Z	VerfcColsBico
		MOV	R4, LINHAS
		SUB	R4, 2
		SUB	R4, M[R2] ;Parte inferior do obstáculo
		MOV	R5, R4
		SUB	R5, ESPACO_OBST ;Parte superior do obstáculo
		CMP	R1, R4
		BR.NN	HaColisao
		CMP	R1, R5
		BR.NP	HaColisao
VerfcColsBico:	CMP	R5, R0
		BR.NZ	NaoHaColisao ;Já testamos o corpo e o bico. Não há colisão
		INC	R2
		MOV	R5, 1
		BR	VC2 ;Já testamos o corpo, vamos testar o bico
HaColisao:	MOV	R1, 1
		MOV	M[SP+4], R1
		RETN	2
NaoHaColisao:	MOV	M[SP+4], R0
		RETN	2

;-------------------------------------------------------------------------------------
; MudaPassaro: Caso seja possível, apaga o pásaro e desenha-o na nova posição
;	Entradas: STACK (M[SP+3]) - nova linha
;		  STACK (M[SP+2]) - nova coluna
;-------------------------------------------------------------------------------------
MudaPassaro:	MOV	R1, M[SP+3]
		MOV	R2, M[SP+2]
		PUSH	R0
		PUSH	R1
		PUSH	R2
		CALL	VerificaColisao
		POP	R3
		CMP	R3, R0
		CALL.NZ	FimJogo ;Caso haja colisão, o jogo terminou
		PUSH	R0
		CALL	AtualizaPassaro ;Apaga o pássaro
		MOV	R1, M[SP+3]
		MOV	R2, M[SP+2]
		MOV	M[P_LINHA_ATUAL], R1
		MOV	M[P_COLUNA_ATUAL], R2
		PUSH	1
		CALL	AtualizaPassaro ;Volta a desenhá-lo na nova posição
		RETN	2

;-------------------------------------------------------------------------------------
;SobePassaro: Altera a velocidade do pássaro para IMPULSO_SUBIDA
;-------------------------------------------------------------------------------------
SobePassaro:	MOV	R1, IMPULSO_SUBIDA
		MOV	M[VELOCIDADE], R1
		MOV	M[DEVE_SUBIR], R0
		RET

;-------------------------------------------------------------------------------------
;CriaObstaculo: Cria um novo obstáculo com uma altura aletória na coluna mais à 
;		direita
;-------------------------------------------------------------------------------------
CriaObstaculo:	MOV	R1, OBSTACULOS
		ADD	R1, COLUNAS
		DEC	R1 ;Endereço do obstáculo correspondenta à coluna mais à direita
		PUSH	R0
		CALL	Random
		POP	R2
		MOV	M[R1], R2
		MOV	R1, INTERV_OBSTAC
		MOV	M[CONTADOR_OBST], R1
		RET

;-------------------------------------------------------------------------------------
;MoveObstaculos: Apaga os obstáculos da Janela de Texto, faz uma rotação para a 
;		 esquerda do vetor OBSTACULOS (move cada obstáculo para a posição à
;		 sua esquerda), verifica se é necessário criar um novo obstáculo e 
;		 volta a desenhar os obstáculos na sua nova posição
;	Variáveis: R1 - coluna atual
;		   R2 - endereço do obstáculo correspondente à coluna atual
;		   R3 - usado como "carry", para transportar o obstáculo para a coluna
;			à sua esquerda
;-------------------------------------------------------------------------------------
MoveObstaculos:	PUSH	R0
		CALL	AtualizaObst ;Apaga os obtáculos da Janela de Texto
		MOV	R1, COLUNAS
		DEC	R1
		MOV	R2, OBSTACULOS
		ADD	R2, R1
		MOV	R3, R0
CicloMoveObst:	CMP	R1, R0
		BR.N	FimCicloMO
		CMP	M[R2], R0
		BR.Z	SaltaMoveObst
		CMP	R1, M[P_COLUNA_ATUAL]
		BR.NZ	SaltaMoveObst
		INC	M[ObstaculosUltr]
		CALL	AtualDisplay7S
SaltaMoveObst:	MOV	R4, M[R2]
		MOV	M[R2], R3
		MOV	R3, R4
		DEC	R1
		DEC	R2
		BR	CicloMoveObst
FimCicloMO:	CMP	M[CONTADOR_OBST], R0
		CALL.N	CriaObstaculo
		DEC	M[CONTADOR_OBST] ;Decrementa a variável usada para saber se é necessário criar um novo obstáculo
		MOV	M[DEVE_MOVER_OBST], R0 ;Faz reset à flag DEVE_MOVER_OBST
		MOV	R4, M[V_OBSTACULOS]
		MOV	M[CONT_MOV_OBST], R4 ;Coloca no contador que representa quantos ciclos faltam para voltar a mover os obstáculos a velocidade atual dos obstáculos
		PUSH	1
		CALL	AtualizaObst ;Volta a desenhar os obstáculos nas novas posições
		INC	M[DistanciaPerc]
		CALL	LCD ;Atualiza o LCD da placa com a nova distância percorrida
FimMoveObst:	RET

;-------------------------------------------------------------------------------------
;PhysicsStep: Faz um ciclo de simulação:
;		- Atualiza a velocidade dos obstáculos caso o nível tenha mudado
;		- Atualiza a velocidade do pássaro (v(t) = v(t-1) + g)
;		- Atualiza a posição (p(t) = p(t-1) + v(t))
;		- Calcula o valor inteiro da posição e chama MudaPassaro
;-------------------------------------------------------------------------------------
PhysicsStep:	CMP	M[NIVEL_MUDOU], R0
		BR.Z	SaltaPhyscsStep ;Caso o nível não tenha sido alterado, salta para a segunda parte da função
		MOV	R1, VELOCIDADES
		ADD	R1, M[NIVEL]
		MOV	R2, M[R1]
		MOV	M[V_OBSTACULOS], R2
		CALL	AtualizaLeds
		MOV	M[NIVEL_MUDOU], R0
SaltaPhyscsStep:MOV	R1, M[VELOCIDADE]
		SUB	R1, GRAV_POR_STEP ;Subtrai (de modo a que o pássaro desça) GRAV_POR_STEP à velocidade atual
		MOV	M[VELOCIDADE], R1
		MOV	R2, M[LINHA_ATUAL]
		SUB	R2, R1 ;Subtrai a velocidade atual à posição atual
		MOV	M[LINHA_ATUAL], R2
		SHR	R2, 8 ;Converte a linha atual para um inteiro
		PUSH	R2
		PUSH	M[P_COLUNA_ATUAL]
		CALL	MudaPassaro
		MOV	M[DEVE_PYSC_STEP], R0 ;Volta a colocar a flag DEVE_PYSC_STEP a zero
		RET

;-------------------------------------------------------------------------------------
;ReiniciaJogo: Coloca todas as variáveis no seu estado inicial e passa a execução do
;	       programa para EsperaInicio
;-------------------------------------------------------------------------------------
ReiniciaJogo:	MOV	R1, 1
		MOV	M[COMECAR_JOGO], R1
		MOV	R1, 19
		MOV	M[P_COLUNA_ATUAL], R1
		MOV	R1, 11
		MOV	M[P_LINHA_ATUAL], R1
		MOV	R1, 0000101100000000b
		MOV	M[LINHA_ATUAL], R1
		MOV	M[FALTA_SUBIR], R0
		MOV	M[VELOCIDADE], R0
		MOV	M[NIVEL], R0
		MOV	M[V_OBSTACULOS], R0
		MOV	M[CONT_MOV_OBST], R0
		MOV	M[CONTADOR_OBST], R0
		MOV	M[DistanciaPerc], R0
		MOV	M[ObstaculosUltr], R0
		MOV	M[NIVEL_MUDOU], R0
		MOV	M[DEVE_SUBIR], R0
		MOV	M[DEVE_CRIAR_OSBT], R0
		MOV	M[DEVE_MOVER_OBST], R0
		MOV	M[DEVE_PYSC_STEP], R0
		MOV	R1, R0
		MOV	R2, OBSTACULOS
CicloLimpaObst:	CMP	R1, COLUNAS ;Apaga todos os obstáculos da memória
		BR.NN	FimReinicio
		MOV	M[R2], R0
		INC	R1
		INC	R2
		BR	CicloLimpaObst
FimReinicio:	JMP	EsperaInicio

;-------------------------------------------------------------------------------------
;Inicio: Ativa as interrupções, incia a Janela de Texto, escreve a mensagem de início
;	 e espera que o utilizador prima o botão I1
;-------------------------------------------------------------------------------------
Inicio:		MOV	R7, SP_INICIAL
		MOV	SP, R7
		MOV	R7, INT_MASK
		MOV	M[INT_MASK_ADDR], R7
		MOV	R7, FFFFh
		MOV	M[DISPLAY_C_ADDR], R7
		PUSH	Vartext1
		PUSH	11
		PUSH	38
		CALL	EscreveStr
		PUSH	Vartext2
		PUSH	13
		PUSH	29
		CALL	EscreveStr
		ENI
EsperaInicio:	CMP	M[COMECAR_JOGO], R0
		BR.Z	ComecaJogo
		INC	M[N_SEQUENCIA] ;Incrementa o número utilizado como raiz da geração aleatória
		BR	EsperaInicio

;-------------------------------------------------------------------------------------
;ComecaJogo: Inicializa os diferentes periféricos e começa o ciclo de jogo
;-------------------------------------------------------------------------------------
ComecaJogo:	CALL	LimpaJanela
		CALL	LimpaLCD
		CALL	LimpaDisplay7S
		CALL	DesenhaLimites
		PUSH	1
		CALL	AtualizaPassaro
		CALL	AtualizaLeds
		CALL	EscreveStrLCD
		CALL	AtualDisplay7S
		MOV	R1, M[VELOCIDADES]
		MOV	M[V_OBSTACULOS], R1
		MOV	R7, PHYSICS_STEP
		MOV	M[CONT_STEP_ADDR], R7
		MOV	R7, 1
		MOV	M[CONT_CTRL_ADDR], R7
Ciclo1:		CMP	M[DEVE_PYSC_STEP], R0
		BR.Z	Ciclo2
		CALL	PhysicsStep
Ciclo2:		CMP	M[DEVE_SUBIR], R0
		BR.Z	Ciclo3
		CALL	SobePassaro
Ciclo3:		CMP	M[DEVE_MOVER_OBST], R0
		BR.Z	Ciclo1
		CALL	MoveObstaculos
		BR	Ciclo1

;-------------------------------------------------------------------------------------
; FimJogo: Subrotina chamada quando o passaro colide com algum obstaculo.
;	   Apresenta a pontuação e uma mensagem de fim de jogo no ecrã
;	   O programa termina nesta subrotina
;	Entradas: Endereço de memoria da string 4 e o numero de obstáculos 
;		  ultrapassados
;-------------------------------------------------------------------------------------
FimJogo:	MOV	M[CONT_CTRL_ADDR], R0
		CALL	LimpaJanela
		PUSH	Vartext4
		PUSH	11
		PUSH	34
		CALL	EscreveStr
		MOV	R1, 38
		MOV	R2, M[ObstaculosUltr]
CicloFimJogo:	MOV	R3, 10
		DIV	R2, R3
		MOV	R4, R3
		ADD	R4, CHAR_0
		PUSH	13
		PUSH	R1
		PUSH	R4
		CALL	EscreveChar
		DEC	R1
		CMP	R2, R0
		BR.NZ	CicloFimJogo
		PUSH	VarText5
		PUSH	15
		PUSH	24
		CALL	EscreveStr
		CALL	ReiniciaJogo