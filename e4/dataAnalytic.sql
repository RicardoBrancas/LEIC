SELECT COUNT(cean) as n, categoria, NULL AS ano, NULL AS mes
FROM reposition_facts
	NATURAL JOIN d_tempo
	NATURAL JOIN d_produto
WHERE nif_fornecedor_principal = 123455678
GROUP BY categoria
UNION
SELECT COUNT(cean) as n, categoria, ano, mes
FROM reposition_facts
	NATURAL JOIN d_tempo
	NATURAL JOIN d_produto
WHERE nif_fornecedor_principal = 123455678
GROUP BY categoria, ano, mes;
