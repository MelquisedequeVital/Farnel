-- Dados iniciais do catálogo. Roda a cada inicialização (spring.sql.init.mode=always),
-- mas só insere o que ainda não existe, então dados cadastrados pela aplicação são preservados.
-- Restaurantes e CNPJs são fictícios. Imagens: Unsplash (https://unsplash.com/license).

INSERT INTO restaurante (nome_fantasia, cnpj) VALUES
	('Cozinha Bella Massa', '11111111000101'),
	('Sabor do Mar', '22222222000102'),
	('Terra & Fogo', '33333333000103')
ON CONFLICT (cnpj) DO NOTHING;

INSERT INTO prato (nome, descricao, preco, alergenos, imagem, destaque, restaurante_id)
SELECT v.nome, v.descricao, v.preco, v.alergenos, v.imagem, v.destaque, r.id
FROM (VALUES
	('Lasanha à Bolonhesa',
		'Camadas de massa fresca, molho bolonhesa de carne bovina, bechamel e queijo gratinado.',
		38.90, 'Glúten, Leite, Ovo', '/img/pratos/lasanha-bolonhesa.jpg', true, '11111111000101'),
	('Espaguete à Bolonhesa',
		'Espaguete al dente com molho de tomate e carne moída cozido lentamente.',
		32.90, 'Glúten', '/img/pratos/espaguete-bolonhesa.jpg', false, '11111111000101'),
	('Almôndegas ao Molho de Tomate',
		'Almôndegas de carne bovina temperadas com ervas, servidas com molho de tomate caseiro.',
		34.90, 'Glúten, Ovo', '/img/pratos/almondegas.jpg', false, '11111111000101'),
	('Macarrão com Camarão',
		'Massa salteada no azeite e alho com camarões, tomate e salsinha.',
		44.90, 'Glúten, Crustáceos', '/img/pratos/macarrao-camarao.jpg', false, '22222222000102'),
	('Salmão Grelhado com Arroz e Legumes',
		'Filé de salmão grelhado acompanhado de arroz amarelo, couve-de-bruxelas assada e folhas verdes.',
		52.90, 'Peixe', '/img/pratos/salmao-grelhado.jpg', true, '22222222000102'),
	('Ensopado de Peixe com Legumes',
		'Peixe branco cozido em caldo de tomate, pimentões e ervas frescas.',
		42.90, 'Peixe', '/img/pratos/ensopado-de-peixe.jpg', false, '22222222000102'),
	('Curry de Frango com Arroz',
		'Pedaços de frango em molho de curry com leite de coco, servidos com arroz branco.',
		36.90, NULL, '/img/pratos/curry-de-frango.jpg', true, '33333333000103'),
	('Frango Grelhado com Arroz e Legumes',
		'Coxa de frango grelhada com arroz temperado e legumes refogados.',
		29.90, NULL, '/img/pratos/frango-grelhado-arroz.jpg', false, '33333333000103'),
	('Carne de Panela com Batatas',
		'Carne bovina cozida lentamente com batatas, cebola e temperos da casa.',
		39.90, NULL, '/img/pratos/carne-de-panela.jpg', false, '33333333000103'),
	('Bowl de Frango Desfiado com Arroz e Grãos',
		'Frango desfiado, arroz branco, grão-de-bico e verduras frescas.',
		31.90, NULL, '/img/pratos/bowl-frango-graos.jpg', false, '33333333000103'),
	('Macarrão Oriental com Legumes',
		'Macarrão salteado com legumes e molho shoyu.',
		33.90, 'Glúten, Soja', '/img/pratos/macarrao-oriental.jpg', false, '33333333000103'),
	('Grão-de-Bico com Brócolis',
		'Grão-de-bico refogado com brócolis, alho e azeite.',
		27.90, NULL, '/img/pratos/grao-de-bico-brocolis.jpg', false, '33333333000103'),
	('Bowl Vegano de Legumes',
		'Mix de folhas, abacate, grão-de-bico, legumes frescos e molho de amendoim.',
		30.90, 'Amendoim', '/img/pratos/bowl-vegano.jpg', false, '33333333000103')
) AS v (nome, descricao, preco, alergenos, imagem, destaque, cnpj)
JOIN restaurante r ON r.cnpj = v.cnpj
WHERE NOT EXISTS (SELECT 1 FROM prato p WHERE p.nome = v.nome);
