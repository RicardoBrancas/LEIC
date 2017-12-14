-- a)

CREATE FUNCTION forn_prim_sec()
	RETURNS TRIGGER AS $$
BEGIN

	IF EXISTS(SELECT *
	          FROM fornece_sec
	          WHERE ean = NEW.ean
	                AND nif = NEW.forn_primario)
	THEN
		RAISE EXCEPTION
		'Tried to set product % with forn_prim %, but product already has forn_sec $',
			NEW.ean, NEW.forn_primario, NEW.forn_primario;
	END IF;


END
$$ LANGUAGE plpgsql;

CREATE TRIGGER forn_prim_sec
BEFORE INSERT OR UPDATE ON produto
FOR EACH ROW EXECUTE PROCEDURE forn_prim_sec();

CREATE FUNCTION forn_sec_prim()
	RETURNS TRIGGER AS $$
BEGIN

	IF EXISTS(SELECT *
	          FROM produto
	          WHERE ean = NEW.ean
	                AND forn_primario = NEW.nif)
	THEN
		RAISE EXCEPTION
		'Tried to set forn_sec % for product %, but product already has forn_prim %',
			NEW.nif,NEW.ean, NEW.nif;
	END IF;


END
$$ LANGUAGE plpgsql;

CREATE TRIGGER forn_sec_prim
BEFORE INSERT OR UPDATE ON fornece_sec
FOR EACH ROW EXECUTE PROCEDURE forn_sec_prim();

-- b)

ALTER TABLE reposicao
	ADD CHECK (instante <= CURRENT_TIMESTAMP);