DROP TABLE IF EXISTS reposition_facts;
DROP TABLE IF EXISTS d_produto;
DROP TABLE IF EXISTS d_tempo;

CREATE TABLE d_produto (
	cean                     NUMERIC(13),
	categoria                VARCHAR(80),
	nif_fornecedor_principal NUMERIC(9),
	PRIMARY KEY (cean)
);

CREATE TABLE d_tempo (
	tid NUMERIC(8),
	dia NUMERIC(2),
	mes NUMERIC(2),
	ano NUMERIC(4),
	PRIMARY KEY (tid),
	CHECK (CAST(tid AS INT) / 10000 = ano),
	CHECK (CAST(tid AS INT) % 10000 / 100 = mes),
	CHECK (CAST(tid AS INT) % 100 = dia)
);

CREATE TABLE reposition_facts (
	cean NUMERIC(13),
	tid  NUMERIC(8),
	PRIMARY KEY (cean, tid),
	FOREIGN KEY (cean) REFERENCES d_produto (cean),
	FOREIGN KEY (tid) REFERENCES d_tempo (tid)
);

INSERT INTO d_tempo
	SELECT DISTINCT
		to_number(to_char(instante, 'YYYYMMDD'), '00000000'),
		date_part('day', instante),
		date_part('month', instante),
		date_part('year', instante)
	FROM evento_reposicao;

INSERT INTO d_produto
	SELECT DISTINCT
		ean,
		categoria,
		forn_primario
	FROM reposicao NATURAL JOIN produto;

INSERT INTO reposition_facts
	SELECT
		ean,
		to_number(to_char(instante, 'YYYYMMDD'), '00000000')
	FROM reposicao;