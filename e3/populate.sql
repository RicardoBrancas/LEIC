INSERT INTO categoria VALUES ('Charcutaria'), ('Laticineos'), ('Talho'), ('Legumes'), ('Queijaria'), ('Padaria'), ('Queijaria Gourmet');

INSERT INTO categoria_simples VALUES ('Talho'), ('Legumes'), ('Padaria'), ('Queijaria Gourmet');

INSERT INTO super_categoria VALUES ('Charcutaria'), ('Laticineos'), ('Queijaria');

INSERT INTO constituida VALUES ('Charcutaria', 'Talho'), ('Laticineos', 'Queijaria'), ('Queijaria', 'Queijaria Gourmet');

INSERT INTO fornecedor VALUES (111111111, 'Ibramugi'), (111111112, 'Ramos e Pereira'), (111111113, 'Amanhecer'), (111111114, 'Espiga da Planicie');

INSERT INTO produto VALUES (1111111111111, 'Desc1', 'Talho', 111111111, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111112, 'Desc2', 'Queijaria', 111111111, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111113, 'Desc3', 'Laticineos', 111111112, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111114, 'Desc4', 'Legumes', 111111113, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111115, 'Desc5', 'Charcutaria', 111111111, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111116, 'Desc6', 'Padaria', 111111114, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111117, 'Desc7', 'Legumes', 111111114, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111118, 'Desc8', 'Padaria', 111111113, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111119, 'Desc9', 'Queijaria', 111111112, 'Fri 24 Nov 17:03:30 WET 2017'),
	(1111111111120, 'Desc10', 'Talho', 111111112, 'Fri 24 Nov 17:03:30 WET 2017');

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
	(111111114, 1111111111120);

INSERT INTO corredor VALUES (1, 100), (2, 150), (3, 100), (4, 120);

INSERT INTO prateleira VALUES (1, 'esquerda', 'superior'), (1, 'direita', 'chao'),
	(2, 'esquerda', 'medio'), (2, 'direita', 'superior');

INSERT INTO planograma VALUES (1111111111111, 1, 'esquerda', 'superior', 2, 10),
	(1111111111112, 1, 'direita', 'chao', 2, 10),
	(1111111111113, 2, 'esquerda', 'medio', 2, 10),
	(1111111111114, 2, 'direita', 'superior', 2, 10),
	(1111111111115, 1, 'esquerda', 'superior', 2, 10),
	(1111111111116, 1, 'direita', 'chao', 2, 10),
	(1111111111117, 2, 'esquerda', 'medio', 2, 10),
	(1111111111118, 2, 'direita', 'superior', 2, 10),
	(1111111111119, 1, 'esquerda', 'superior', 2, 10),
	(1111111111120, 1, 'direita', 'chao', 2, 10);

INSERT INTO evento_reposicao VALUES ('Jorge', 'Fri 24 Nov 17:03:30 WET 2017'),
('Manuel', 'Sat 25 Nov 17:03:30 WET 2017');

INSERT INTO reposicao VALUES (1111111111111, 1, 'esquerda', 'superior', 'Manuel', 'Sat 25 Nov 17:03:30 WET 2017', 10),
(1111111111112, 1, 'direita', 'chao', 'Jorge', 'Fri 24 Nov 17:03:30 WET 2017', 3);