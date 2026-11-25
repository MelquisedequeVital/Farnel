-- Dados iniciais do catálogo. Roda a cada inicialização (spring.sql.init.mode=always),
-- mas só insere o que ainda não existe, então dados cadastrados pela aplicação são preservados.
-- Restaurantes e CNPJs são fictícios. Imagens: Unsplash (https://unsplash.com/license).

INSERT INTO restaurante (nome_fantasia, cnpj, categoria, descricao, imagem) VALUES
	('Cozinha Bella Massa', '11111111000101', 'Massas & Trattoria',
		'Cozinha artesanal inspirada nas tradições italianas, massas frescas feitas à mão e molhos de cozimento lento.',
		'/img/pratos/lasanha-bolonhesa.jpg'),
	('Sabor do Mar', '22222222000102', 'Pescados & Frutos do Mar',
		'Pescados frescos da costa paraibana, ensopados aromáticos e preparos grelhados com ingredientes regionais.',
		'/img/pratos/salmao-grelhado.jpg'),
	('Terra & Fogo', '33333333000103', 'Grelhados & Saudável',
		'Gastronomia contemporânea com cortes nobres grelhados, bowls nutritivos, grãos orgânicos e opções veganas.',
		'/img/pratos/curry-de-frango.jpg')
ON CONFLICT (cnpj) DO UPDATE SET
	categoria = EXCLUDED.categoria,
	descricao = EXCLUDED.descricao,
	imagem = EXCLUDED.imagem;

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

-- O Hibernate cria um CHECK com os valores do enum TipoUsuario apenas na criação da tabela;
-- removemos para que novos perfis (ex.: EMPRESA) possam ser gravados em bancos já existentes.
ALTER TABLE usuario DROP CONSTRAINT IF EXISTS usuario_tipo_check;

-- Usuários padrão do sistema (Senha para todos: 123456)
-- Hash BCrypt gerado pelo Spring Security: $2a$10$.39kV.D/Et3eHQ/akbckVewJg/wVfP3vwqxj/YqtZwINvuzHSTD2C
INSERT INTO usuario (nome, email, senha, tipo, restaurante_id)
SELECT 'Ana Souza', 'ana.souza@farnel.com.br', '$2a$10$.39kV.D/Et3eHQ/akbckVewJg/wVfP3vwqxj/YqtZwINvuzHSTD2C', 'CONSUMIDOR', NULL
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'ana.souza@farnel.com.br');

INSERT INTO usuario (nome, email, senha, tipo, restaurante_id)
SELECT 'Chef Carlos - Bella Massa', 'parceiro@farnel.com.br', '$2a$10$.39kV.D/Et3eHQ/akbckVewJg/wVfP3vwqxj/YqtZwINvuzHSTD2C', 'PARCEIRO', r.id
FROM restaurante r
WHERE r.cnpj = '11111111000101'
  AND NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'parceiro@farnel.com.br');

INSERT INTO usuario (nome, email, senha, tipo, restaurante_id)
SELECT 'Carlos Entregador', 'entregador@farnel.com.br', '$2a$10$.39kV.D/Et3eHQ/akbckVewJg/wVfP3vwqxj/YqtZwINvuzHSTD2C', 'ENTREGADOR', NULL
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'entregador@farnel.com.br');

-- Consumidor com endereço e forma de pagamento simulada vinculado ao usuário
INSERT INTO consumidor (nome, email, telefone, endereco, tipo_pagamento, titular_cartao, numero_cartao_mascarado, validade_cartao, usuario_id)
SELECT 'Ana Souza', 'ana.souza@farnel.com.br', '(83) 98765-4321', 'Av. Epitácio Pessoa, 1000 - Tambauzinho', 'Cartão de Crédito', 'Ana Souza', '•••• •••• •••• 4242', '10/29', u.id
FROM usuario u
WHERE u.email = 'ana.souza@farnel.com.br'
ON CONFLICT (email) DO UPDATE SET
	usuario_id = EXCLUDED.usuario_id,
	telefone = EXCLUDED.telefone,
	tipo_pagamento = EXCLUDED.tipo_pagamento,
	titular_cartao = EXCLUDED.titular_cartao,
	numero_cartao_mascarado = EXCLUDED.numero_cartao_mascarado,
	validade_cartao = EXCLUDED.validade_cartao;

-- Entregador cadastrado e disponível
INSERT INTO entregador (usuario_id, telefone, veiculo, disponivel)
SELECT u.id, '(83) 99123-9876', 'Honda CG 160 Fan (Placa QFA-1234)', true
FROM usuario u
WHERE u.email = 'entregador@farnel.com.br'
  AND NOT EXISTS (SELECT 1 FROM entregador e WHERE e.usuario_id = u.id);

-- Assinaturas ativas de exemplo (recorrência da consumidora Ana Souza)
INSERT INTO assinatura (status, dias_semana, horario_entrega, valor, consumidor_id, prato_id)
SELECT 'ATIVA', 'Terça-feira', '12:30', p.preco, c.id, p.id
FROM prato p, consumidor c
WHERE p.nome = 'Frango Grelhado com Arroz e Legumes' AND c.email = 'ana.souza@farnel.com.br'
  AND NOT EXISTS (SELECT 1 FROM assinatura a WHERE a.prato_id = p.id AND a.consumidor_id = c.id);

INSERT INTO assinatura (status, dias_semana, horario_entrega, valor, consumidor_id, prato_id)
SELECT 'ATIVA', 'Quarta-feira', '13:00', p.preco, c.id, p.id
FROM prato p, consumidor c
WHERE p.nome = 'Salmão Grelhado com Arroz e Legumes' AND c.email = 'ana.souza@farnel.com.br'
  AND NOT EXISTS (SELECT 1 FROM assinatura a WHERE a.prato_id = p.id AND a.consumidor_id = c.id);

INSERT INTO assinatura (status, dias_semana, horario_entrega, valor, consumidor_id, prato_id)
SELECT 'ATIVA', 'Sexta-feira', '12:15', p.preco, c.id, p.id
FROM prato p, consumidor c
WHERE p.nome = 'Carne de Panela com Batatas' AND c.email = 'ana.souza@farnel.com.br'
  AND NOT EXISTS (SELECT 1 FROM assinatura a WHERE a.prato_id = p.id AND a.consumidor_id = c.id);

-- Módulo B2B: empresa contratante de exemplo, gestor e contrato coletivo (CNPJ fictício)
INSERT INTO empresa (razao_social, cnpj, responsavel, telefone, endereco_entrega)
SELECT 'Tech Nordeste Soluções Ltda.', '44444444000104', 'Mariana Costa', '(83) 3222-1000',
	'Av. Epitácio Pessoa, 2500, 5º andar - Tambauzinho, João Pessoa/PB'
WHERE NOT EXISTS (SELECT 1 FROM empresa WHERE cnpj = '44444444000104');

INSERT INTO usuario (nome, email, senha, tipo, empresa_id)
SELECT 'Mariana Costa - Tech Nordeste', 'empresa@farnel.com.br', '$2a$10$.39kV.D/Et3eHQ/akbckVewJg/wVfP3vwqxj/YqtZwINvuzHSTD2C', 'EMPRESA', e.id
FROM empresa e
WHERE e.cnpj = '44444444000104'
  AND NOT EXISTS (SELECT 1 FROM usuario WHERE email = 'empresa@farnel.com.br');

INSERT INTO contrato_empresarial (empresa_id, data_inicio, data_fim, limite_colaboradores, horario_entrega, status)
SELECT e.id, DATE '2026-01-01', NULL, 30, '12:00', 'ATIVO'
FROM empresa e
WHERE e.cnpj = '44444444000104'
  AND NOT EXISTS (SELECT 1 FROM contrato_empresarial c WHERE c.empresa_id = e.id);
