; Passaro Bamboleante

; CONSTANTES
SP_INICIAL	EQU	FDFFh

DISPLAY_O_ADDR	EQU	FFFEh
DISPLAY_C_ADDR	EQU	FFFCh

COLUNAS		EQU	79
LINHAS		EQU	24

CHAR_TRACO	EQU	2Dh
CHAR_O		EQU	4Fh
CHAR_X		EQU	58h
CHAR_>		EQU	3Eh

; VARIAVEIS
		ORIG	8000h
P_LINHA_ATUAL	STR	11
P_COLUNA_ATUAL	STR	19



		ORIG	0000h
		JMP	Inicio

AtualizaJanela:	CALL	DesenhaLimites
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

Inicio:		MOV	R7, SP_INICIAL
		MOV	SP, R7
		MOV	R1, FFFFh
		MOV	M[DISPLAY_C_ADDR], R1
		CALL	AtualizaJanela
Fim:		BR	Fim