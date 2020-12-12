DROP INDEX IF EXISTS fornecedor_nif_idx;
DROP INDEX IF EXISTS produto_forn_primario_idx;
DROP INDEX IF EXISTS produto_categoria_idx;

CREATE INDEX fornecedor_nif_idx ON fornecedor USING BTREE (nif);
CREATE INDEX produto_forn_primario_idx ON produto USING BTREE (forn_primario);
CREATE INDEX produto_categoria_idx ON produto USING HASH (categoria);

EXPLAIN SELECT DISTINCT F.nif, F.nome
        FROM fornecedor F JOIN produto P
		        ON F.nif = P.forn_primario
        WHERE P.categoria = 'Frutos';