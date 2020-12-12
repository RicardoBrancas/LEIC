DROP INDEX IF EXISTS fornece_sec_nif_idx;

EXPLAIN SELECT P.ean, COUNT(nif)
FROM produto P, fornece_sec F
WHERE P.ean = F.ean
GROUP BY P.ean
HAVING COUNT(nif) > 1