-- a
SELECT nome
FROM (
	     SELECT forn_primario AS nif, categoria
	     FROM produto
	     UNION ALL
	     SELECT nif, categoria
	     FROM fornece_sec
		     NATURAL JOIN produto
     ) AS c
	NATURAL JOIN fornecedor
GROUP BY nif, nome
HAVING COUNT(categoria) >= ALL (
	SELECT MAX(count)
	FROM (
		     SELECT nif, COUNT(categoria) AS count
		     FROM (SELECT forn_primario AS nif, categoria
		           FROM produto
		           UNION ALL
		           SELECT nif, categoria
		           FROM fornece_sec
			           NATURAL JOIN produto) AS a
		     GROUP BY nif) AS b
);

-- b
SELECT DISTINCT nif, nome
FROM produto AS outter
	JOIN fornecedor ON forn_primario = nif
WHERE NOT EXISTS(
	SELECT nome
	FROM categoria_simples
	EXCEPT
	SELECT categoria AS nome
	FROM fornece_sec
		NATURAL JOIN produto
	WHERE nif = outter.forn_primario
	      OR forn_primario = outter.forn_primario
);

-- c
SELECT ean
FROM produto
EXCEPT
SELECT ean
FROM reposicao;

-- d
SELECT ean
FROM fornece_sec
GROUP BY ean
HAVING COUNT(nif) > 10;

-- e
SELECT ean
FROM reposicao
GROUP BY ean
HAVING COUNT(DISTINCT operador) = 1;
