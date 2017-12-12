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
	dia SMALLINT,
	mes SMALLINT,
	ano INT,
	PRIMARY KEY (dia, mes, ano),
	CHECK (dia > 0 AND dia <= 31),
	CHECK (mes > 0 AND mes <= 12),
	CHECK (ano > 0)
);

CREATE TABLE reposition_facts (
	cean NUMERIC(13),
	dia SMALLINT,
	mes SMALLINT,
	ano INT,
	PRIMARY KEY (cean, dia, mes, ano),
	FOREIGN KEY (cean) REFERENCES d_produto (cean),
	FOREIGN KEY (dia, mes, ano) REFERENCES d_tempo (dia, mes, ano)
);

INSERT INTO d_tempo
	SELECT DISTINCT date_part('day', instante),
	                date_part('month', instante),
	                date_part('year', instante)
	FROM evento_reposicao;

INSERT INTO d_produto
	SELECT DISTINCT ean, categoria, forn_primario
	FROM reposicao NATURAL JOIN produto;

INSERT INTO reposition_facts
	SELECT ean,
	       date_part('day', instante),
	       date_part('month', instante),
	       date_part('year', instante)
	FROM reposicao;