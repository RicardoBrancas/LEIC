SELECT COUNT(DISTINCT cean) as nrOcorrencias, categoria, ano, NULL
FROM d_produto P INNER JOIN reposition_facts R
      ON P.cean = R.cean INNER JOIN d_tempo T ON T.tid=R.tid
WHERE nif_fornecedor_principal = 123455678
GROUP BY categoria, ano
UNION
SELECT COUNT(DISTINCT cean) as nrOcorrencias, categoria, NULL, mes
FROM d_produto P INNER JOIN reposition_facts R
      ON P.cean = R.cean INNER JOIN d_tempo T ON T.tid=R.tid
WHERE nif_fornecedor_principal = 123455678
GROUP BY categoria, mes;
