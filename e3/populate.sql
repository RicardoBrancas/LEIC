INSERT INTO categoria VALUES ('Charcutaria'), ('Laticinios'), ('Talho'), ('Legumes'), ('Queijaria'), ('Padaria'), ('Queijaria Gourmet'), ('Produtos de Beleza'), ('Bebidas'), ('Banho'), ('Sumos'), ('Alcool'), ('Refrigerantes'), ('Perfumes');

INSERT INTO categoria_simples VALUES ('Talho'), ('Legumes'), ('Padaria'), ('Queijaria Gourmet'), ('Sumos'), ('Alcool'), ('Perfumes');

INSERT INTO super_categoria VALUES ('Charcutaria'), ('Laticinios'), ('Queijaria'), ('Produtos de Beleza'), ('Bebidas'), ('Refrigerantes');

INSERT INTO constituida VALUES ('Charcutaria', 'Talho'), ('Laticinios', 'Queijaria'), ('Queijaria', 'Queijaria Gourmet'), ('Produtos de Beleza', 'Perfumes'), ('Produtos de Beleza', 'Banho'), ('Bebidas', 'Refrigerantes'), ('Refrigerantes', 'Sumos'), ('Bebidas', 'Alcool');

INSERT INTO fornecedor VALUES (111111111, 'Ibramugi'), (111111112, 'Ramos e Pereira'), (111111113, 'Amanhecer'), (111111114, 'Espiga da Planicie'), (111111115, 'Nestle'), (111111116, 'Dia'), (111111117, 'Continente');

INSERT INTO produto VALUES (1111111111111, 'Desc1', 'Talho', 111111111, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111112, 'Desc2', 'Queijaria', 111111117, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111113, 'Desc3', 'Laticinios', 111111112, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111114, 'Desc4', 'Legumes', 111111113, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111115, 'Desc5', 'Charcutaria', 111111111, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111116, 'Desc6', 'Padaria', 111111117, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111117, 'Desc7', 'Legumes', 111111114, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111118, 'Desc8', 'Padaria', 111111113, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111119, 'Desc9', 'Queijaria', 111111112, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111120, 'Desc10', 'Talho', 111111112, 'Fri 24 Nov 17:03:30 WET 2017')
	(1111111111121, 'Desc11', 'Alcool', 111111117, 'Fri 1 Dec 17:03:30 WET 2017'),
	(1111111111122, 'Desc12', 'Perfumes', 111111117, 'Fri 1 Dec 17:03:30 WET 2017'),
	(1111111111123, 'Desc13', 'Banho', 111111115, 'Fri 1 Dec 17:03:30 WET 2017'),
	(1111111111124, 'Desc14', 'Sumos', 111111116, 'Fri 1 Dec 17:03:30 WET 2017'),
	(1111111111125, 'Desc15', 'Charcutaria', 111111115, 'Fri 1 Dec 17:03:30 WET 2017'),
	(1111111111126, 'Desc16', 'Padaria', 111111114, 'Fri 1 Dec 17:03:30 WET 2017'),
	(1111111111127, 'Desc17', 'Alcool', 111111114, 'Fri 1 Dec 17:03:30 WET 2017'),
	(1111111111128, 'Desc18', 'Padaria', 111111115, 'Fri 1 Dec 17:03:30 WET 2017'),
	(1111111111129, 'Desc19', 'Sumos', 111111116, 'Fri 1 Dec 17:03:30 WET 2017'),
	(1111111111130, 'Desc10', 'Talho', 111111112, 'Fri 1 Dec 17:03:30 WET 2017');

INSERT INTO fornece_sec VALUES (111111113, 1111111111112),
	(111111114, 1111111111112),
	(111111114, 1111111111111),
	(111111111, 1111111111113),
	(111111111, 1111111111114),
	(111111112, 1111111111115),
	(111111112, 1111111111116),
	(111111112, 1111111111117),
	(111111112, 1111111111118),
	(111111114, 1111111111119),
	(111111114, 1111111111120)
	(111111113, 1111111111122),
	(111111114, 1111111111121),
	(111111111, 1111111111123),
	(111111113, 1111111111124),
	(111111112, 1111111111125),
	(111111117, 1111111111126),
	(111111111, 1111111111127),
	(111111112, 1111111111128),
	(111111114, 1111111111129),
	(111111115, 1111111111130);

INSERT INTO corredor VALUES (1, 100), (2, 150), (3, 100), (4, 120);

INSERT INTO prateleira VALUES (1, 'esquerda', 'superior'), (1, 'direita', 'chao'),
	(2, 'esquerda', 'medio'), (2, 'direita', 'superior');

INSERT INTO planograma VALUES (1111111111111, 1, 'esquerda', 'superior', 2, 10, 1),
	(1111111111112, 1, 'direita', 'chao', 2, 10, 1),
	(1111111111113, 2, 'esquerda', 'medio', 2, 10, 1),
	(1111111111114, 2, 'direita', 'superior', 2, 10, 1),
	(1111111111115, 1, 'esquerda', 'superior', 2, 10, 1),
	(1111111111116, 1, 'direita', 'chao', 2, 10, 1),
	(1111111111117, 2, 'esquerda', 'medio', 2, 10, 1),
	(1111111111118, 2, 'direita', 'superior', 2, 10, 1),
	(1111111111119, 1, 'esquerda', 'superior', 2, 10, 1),
	(1111111111120, 1, 'direita', 'chao', 2, 10, 1)
	(1111111111121, 1, 'esquerda', 'superior', 2, 10, 1),
	(1111111111122, 1, 'direita', 'chao', 2, 10, 1),
	(1111111111123, 2, 'esquerda', 'medio', 2, 10, 1),
	(1111111111124, 2, 'direita', 'superior', 2, 10, 1),
	(1111111111125, 1, 'esquerda', 'superior', 2, 10, 1),
	(1111111111126, 1, 'direita', 'chao', 2, 10, 1),
	(1111111111127, 2, 'esquerda', 'medio', 2, 10, 1),
	(1111111111128, 2, 'direita', 'superior', 2, 10, 1),
	(1111111111129, 1, 'esquerda', 'superior', 2, 10, 1),
	(1111111111130, 1, 'direita', 'chao', 2, 10, 1);

INSERT INTO evento_reposicao VALUES ('Jorge', 'Fri 24 Nov 17:03:30 WET 2017'),
('Ricardo','24 Nov 18:03:30 WET 2017'),
('Manuel', 'Sat 25 Nov 17:03:30 WET 2017'),
('Tozé', 'Mon 27 Nov 17:03:30 WET 2017'),
('Mihail', 'Thu 30 Nov 17:03:30 WET 2017'),
('David', 'Fri 1 Dec 17:03:30 WET 2017');


INSERT INTO reposicao VALUES (1111111111114, 2, 'direita', 'superior', 'Jorge', 'Fri 24 Nov 17:03:30 WET 2017', 3),
(1111111111120, 1, 'direita', 'chao', 'Ricardo', '24 Nov 18:03:30 WET 2017', 10),
(1111111111111, 1, 'esquerda', 'superior', 'Manuel', 'Sat 25 Nov 17:03:30 WET 2017', 10),
(1111111111112, 1, 'direita', 'chao', 'Tozé', 'Mon 27 Nov 17:03:30 WET 2017', 3),
(1111111111114, 2, 'direita', 'superior', 'Mihail', 'Thu 30 Nov 17:03:30 WET 2017', 3),
(1111111111123, 1, 'esquerda', 'medio', 'David', 'Fri 1 Dec 17:03:30 WET 2017', 10);
