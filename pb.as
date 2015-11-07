; Passaro Bamboleante

; CONSTANTES
SP_INICIAL	EQU	FDFFh

DISPLAY_O_ADDR	EQU	FFFEh
DISPLAY_C_ADDR	EQU	FFFCh
INT_MASK_ADDR	EQU	FFFAh
CONT_STEP_ADDR	EQU	FFF6h
CONT_CTRL_ADDR	EQU	FFF7h

INT_MASK	EQU	1000000000000001b

COLUNAS		EQU	79
LINHAS		EQU	24

CHAR_TRACO	EQU	2Dh
CHAR_O		EQU	4Fh
CHAR_X		EQU	58h
CHAR_>		EQU	3Eh
PHYSICS_STEP	EQU	5
V_SUBIDA	EQU	1
TEMPO_SUBIDA	EQU	3

; VARIAVEIS
		ORIG	8000h
P_LINHA_ATUAL	STR	11
P_COLUNA_ATUAL	STR	19
N_SUBIDA	STR	0

DEVE_SUBIR	STR	0

; Tabela de interrupcoes
		ORIG	FE00h
INT0		WORD	i0Premido
		ORIG	FE0Fh
INT15		WORD	tempAtivado

		ORIG	0000h
		JMP	Inicio

i0Premido:	MOV	R7, PHYSICS_STEP
		MOV	M[CONT_STEP_ADDR], R7
		MOV	R7, 1
		MOV	M[CONT_CTRL_ADDR], R7
		RTI

tempAtivado:	MOV	R1, M[N_SUBIDA]
		CMP	R1, TEMPO_SUBIDA
		BR.NN	FimTemporizador
		MOV	R7, PHYSICS_STEP
		MOV	M[CONT_STEP_ADDR], R7
		MOV	R7, 1
		MOV	M[CONT_CTRL_ADDR], R7
		MOV	R1, 1
		MOV	M[DEVE_SUBIR], R1
		INC	M[N_SUBIDA]
		BR	FimTempAtivado
FimTemporizador:MOV	M[N_SUBIDA], R0
FimTempAtivado:	RTI

LimpaJanela:	MOV	R1, R0
		MOV	R2, R0
CicloLinhas:	CMP	R1, LINHAS
		BR.NN	FimAJ
CicloColunas:	CMP	R2, COLUNAS
		BR.NN	FimColunas
		MOV	R4, R1
		SHL	R4, 8
		ADD	R4, R2
		MOV	M[DISPLAY_C_ADDR], R4
		MOV	R5, 20h
		MOV	M[DISPLAY_O_ADDR], R5
		INC	R2
		BR	CicloColunas
FimColunas:	MOV	R2, R0
		INC	R1
		BR	CicloLinhas
FimAJ:		RET

AtualizaJanela:	CALL	LimpaJanela
		CALL	DesenhaLimites
		CALL	DesenhaPassaro
		RET

; DesenhaLimites: Esta funcao desenha os limites superior e infeior do ecra de jogo
; Variavies:	R1: linha do limite a ser desenhado
;		R2: coluna atual
;		R3: contem a linha atual, apos um shift left, 8 posicoes
;		R4
;		R5: Contem o CHAR_TRACO
DesenhaLimites:	MOV	R5, CHAR_TRACO
		MOV	R1, 0
		MOV	R2, 0
		MOV	R3, R1
		SHL	R3, 8
DL1:		CMP	R2, COLUNAS
		BR.P	FimDL1
		MOV	R4, R2
		ADD	R4, R3
		MOV	M[DISPLAY_C_ADDR], R4
		MOV	M[DISPLAY_O_ADDR], R5
		INC	R2
		BR	DL1
FimDL1:		NOP
		MOV	R1, 23
		MOV	R2, 0
		MOV	R3, R1
		SHL	R3, 8
DL2:		CMP	R2, COLUNAS
		BR.P	FimDL2
		MOV	R4, R2
		ADD	R4, R3
		MOV	M[DISPLAY_C_ADDR], R4
		MOV	M[DISPLAY_O_ADDR], R5
		INC	R2
		BR	DL2
FimDL2:		NOP
		RET

; DesenhaPassaro: 
DesenhaPassaro:	MOV	R1, M[P_LINHA_ATUAL]
		MOV	R2, M[P_COLUNA_ATUAL]
		MOV	R3, R1
		SHL	R3, 8
		ADD	R3, R2
		MOV	M[DISPLAY_C_ADDR], R3
		MOV	R4, CHAR_O
		MOV	M[DISPLAY_O_ADDR], R4
		INC	R2
		MOV	R3, R1
		SHL	R3, 8
		ADD	R3, R2
		MOV	M[DISPLAY_C_ADDR], R3
		MOV	R4, CHAR_>
		MOV	M[DISPLAY_O_ADDR], R4
		RET

SobePassaro:	DEC	M[P_LINHA_ATUAL]
		MOV	M[DEVE_SUBIR], R0
		CALL	AtualizaJanela
		RET

Inicio:		MOV	R7, SP_INICIAL
		MOV	SP, R7
		MOV	R7, INT_MASK
		MOV	M[INT_MASK_ADDR], R7
		ENI
		MOV	R7, FFFFh
		MOV	M[DISPLAY_C_ADDR], R7
		CALL	AtualizaJanela
Ciclo:		CMP	M[DEVE_SUBIR], R0
		BR.Z	Ciclo
		CALL	SobePassaro
		BR	Ciclo