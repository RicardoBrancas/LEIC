SELECT ean, design
FROM Produto
  NATURAL JOIN (SELECT * FROM tem WHERE nome='Fruta') AS a
  NATURAL JOIN (SELECT * FROM reposicao WHERE data > '10/1/2017') AS b
  NATURAL JOIN (SELECT SUM(unidades), ean
                FROM reposicao
                GROUP BY ean
                HAVING SUM(unidades) > 10) AS c;


SELECT * FROM
  (
    SELECT nif
    FROM Produto
      NATURAL JOIN fornece_prim
    WHERE ean = x
    UNION
    SELECT nif
    FROM Produto
      NATURAL JOIN fornece_sec
    WHERE ean = x
  ) AS a
  NATURAL JOIN Fornecedor;