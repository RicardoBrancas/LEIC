-- 1
SELECT nome
FROM (
		 SELECT forn_primario AS nif, categoria
		 FROM produto
		 UNION ALL
		 SELECT nif, categoria
		 FROM fornece_sec NATURAL JOIN produto
	 ) AS c
	NATURAL JOIN fornecedor
GROUP BY nif, nome
HAVING COUNT(categoria) >= ALL (
	SELECT MAX(count)
	FROM (SELECT
			  nif,
			  COUNT(categoria) AS count
		  FROM (SELECT forn_primario AS nif, categoria
				FROM produto
				UNION ALL
				SELECT nif, categoria
				FROM fornece_sec NATURAL JOIN produto) AS a
		  GROUP BY nif) AS b
);

-- 2
-- 3

SELECT ean
FROM
