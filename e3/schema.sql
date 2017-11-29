-- Drop tables in case they already exist. Will delete all data

DROP TABLE IF EXISTS reposicao;
DROP TABLE IF EXISTS evento_reposicao;
DROP TABLE IF EXISTS planograma;
DROP TABLE IF EXISTS prateleira;
DROP TABLE IF EXISTS corredor;
DROP TABLE IF EXISTS fornece_sec;
DROP TABLE IF EXISTS produto;
DROP TABLE IF EXISTS fornecedor;
DROP TABLE IF EXISTS constituida;
DROP TABLE IF EXISTS super_categoria;
DROP TABLE IF EXISTS categoria_simples;
DROP TABLE IF EXISTS categoria;

DROP TYPE IF EXISTS SIDE;
DROP TYPE IF EXISTS HEIGHT;

-- Create tables

CREATE TYPE SIDE AS ENUM ('esquerda', 'direita');
CREATE TYPE HEIGHT AS ENUM ('chao', 'medio', 'superior');

CREATE TABLE categoria (
	nome VARCHAR(80) NOT NULL,
	PRIMARY KEY (nome)
);

CREATE TABLE categoria_simples (
	nome VARCHAR(80) NOT NULL,
	PRIMARY KEY (nome),
	FOREIGN KEY (nome) REFERENCES categoria (nome)
);

CREATE TABLE super_categoria (
	nome VARCHAR(80) NOT NULL,
	PRIMARY KEY (nome),
	FOREIGN KEY (nome) REFERENCES categoria (nome)
);

CREATE TABLE constituida (
	super_categoria VARCHAR(80) NOT NULL,
	categoria       VARCHAR(80) NOT NULL,
	PRIMARY KEY (super_categoria, categoria),
	FOREIGN KEY (super_categoria) REFERENCES super_categoria (nome),
	FOREIGN KEY (categoria) REFERENCES categoria (nome),
	CHECK (super_categoria <> categoria)
);

CREATE TABLE fornecedor (
	nif  NUMERIC(9)  NOT NULL,
	nome VARCHAR(80) NOT NULL,
	PRIMARY KEY (nif)
);

CREATE TABLE produto (
	ean           NUMERIC(13) NOT NULL,
	design        VARCHAR(80) NOT NULL,
	categoria     VARCHAR(80) NOT NULL,
	forn_primario NUMERIC(9)  NOT NULL,
	data          TIMESTAMP   NOT NULL,
	PRIMARY KEY (ean),
	FOREIGN KEY (categoria) REFERENCES categoria (nome),
	FOREIGN KEY (forn_primario) REFERENCES fornecedor (nif)
);

CREATE TABLE fornece_sec (
	nif NUMERIC(9)  NOT NULL,
	ean NUMERIC(13) NOT NULL,
	FOREIGN KEY (nif) REFERENCES fornecedor (nif),
	FOREIGN KEY (ean) REFERENCES produto (ean)
);

CREATE TABLE corredor (
	nro     SMALLINT NOT NULL,
	largura SMALLINT NOT NULL,
	PRIMARY KEY (nro)
);

CREATE TABLE prateleira (
	nro    SMALLINT NOT NULL,
	lado   SIDE     NOT NULL,
	altura HEIGHT   NOT NULL,
	PRIMARY KEY (nro, lado, altura),
	FOREIGN KEY (nro) REFERENCES corredor (nro)
);

CREATE TABLE planograma (
	ean      NUMERIC(13) NOT NULL,
	nro      SMALLINT    NOT NULL,
	lado     SIDE        NOT NULL,
	altura   HEIGHT      NOT NULL,
	face     SMALLINT    NOT NULL,
	unidades SMALLINT    NOT NULL,
	PRIMARY KEY (ean, nro, lado, altura),
	FOREIGN KEY (ean) REFERENCES produto (ean),
	FOREIGN KEY (nro, lado, altura) REFERENCES prateleira (nro, lado, altura)
);

CREATE TABLE evento_reposicao (
	operador VARCHAR(80) NOT NULL,
	instante TIMESTAMP   NOT NULL,
	PRIMARY KEY (operador),
	UNIQUE (instante),
	UNIQUE (operador, instante),
	CHECK (instante < CURRENT_TIMESTAMP)
);

CREATE TABLE reposicao (
	ean      NUMERIC(13) NOT NULL,
	nro      SMALLINT    NOT NULL,
	lado     SIDE        NOT NULL,
	altura   HEIGHT      NOT NULL,
	operador VARCHAR(80) NOT NULL,
	instante TIMESTAMP   NOT NULL,
	unidades SMALLINT    NOT NULL,
	PRIMARY KEY (ean, nro, lado, altura),
	UNIQUE (operador),
	UNIQUE (instante),
	FOREIGN KEY (ean, nro, lado, altura)
		REFERENCES planograma (ean, nro, lado, altura),
	FOREIGN KEY (operador, instante)
		REFERENCES evento_reposicao (operador, instante)
);



CREATE OR REPLACE FUNCTION remove_cat(name VARCHAR(80))
	RETURNS VOID AS $$
DECLARE temp_name VARCHAR(80);
BEGIN
	SET CONSTRAINTS ALL DEFERRED;

	DELETE FROM constituida WHERE categoria = name;
	DELETE FROM categoria_simples WHERE nome = name;
	DELETE FROM categoria WHERE nome = name;

	FOR temp_name IN SELECT *
	                 FROM super_categoria AS outter
	                 WHERE NOT EXISTS(SELECT *
	                                  FROM constituida
	                                  WHERE super_categoria = outter.nome)
	LOOP
		INSERT INTO categoria_simples VALUES (temp_name);
		DELETE FROM super_categoria WHERE nome = temp_name;
	END LOOP;

	SET CONSTRAINTS ALL IMMEDIATE;
END
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION sub_cat_insert(super_name VARCHAR(80), sub_name VARCHAR(80))
	RETURNS VOID AS $$
BEGIN
	SET CONSTRAINTS ALL DEFERRED;

	IF NOT EXISTS(SELECT * FROM categoria WHERE nome = super_name) THEN
		RAISE EXCEPTION 'A super categoria escolhida nao existe.'; -- TODO
	END IF;

	IF EXISTS(SELECT nome FROM categoria_simples WHERE nome = super_name) THEN
		DELETE FROM categoria_simples WHERE nome = super_name;
		INSERT INTO super_categoria VALUES (super_name);
	END IF;

	INSERT INTO categoria VALUES (sub_name);
	INSERT INTO categoria_simples VALUES (sub_name);
	INSERT INTO constituida VALUES (super_name, sub_name);

	SET CONSTRAINTS ALL IMMEDIATE;
END;
$$ LANGUAGE plpgsql;



CREATE OR REPLACE FUNCTION all_subcategories(nome VARCHAR(80))
	RETURNS SETOF VARCHAR(80) AS $$
DECLARE temp_name VARCHAR(80);
BEGIN

	DROP TABLE IF EXISTS result;
	DROP TABLE IF EXISTS temp;

	CREATE TEMP TABLE result (nome VARCHAR(80));

	CREATE TEMP TABLE temp_names AS
		SELECT categoria
		FROM constituida
		WHERE super_categoria = nome;

	WHILE EXISTS(SELECT * FROM temp_names)
	LOOP
		FOR temp_name IN SELECT * FROM temp_names
		LOOP
			INSERT INTO temp_names SELECT categoria FROM constituida
				WHERE super_categoria = temp_name;

			DELETE FROM temp_names WHERE categoria = temp_name;

			INSERT INTO result VALUES (temp_name);
		END LOOP;
	END LOOP;

	RETURN QUERY SELECT * FROM result;
END;
$$ LANGUAGE plpgsql;