
# ESPECIFICAÇÃO DE REQUISITOS — PROJETO FARNEL

---

## REQUISITOS FUNCIONAIS (RF)

### 1. Módulo do Consumidor (B2C)

* **RF-CON-01 (Cadastro e Perfil):** Como Consumidor, eu gostaria de criar e gerenciar minha conta com dados pessoais, endereço de entrega e formas de pagamento, para que eu possa contratar o serviço e receber minhas marmitas no local correto.

* **RF-CON-02 (Gestão de Assinatura Recorrente — UC01):** Como Consumidor, eu gostaria de criar, alterar ou cancelar meu plano de refeições escolhendo os dias da semana, para que eu tenha entregas recorrentes planejadas nos dias em que preciso.

* **RF-CON-03 (Busca e Filtros com Restrições — UC02):** Como Consumidor, eu gostaria de filtrar o catálogo de pratos por restrições alimentares (sem glúten, sem lactose), calorias e tipo de dieta, para que eu encontre refeições seguras e alinhadas à minha saúde.

* **RF-CON-04 (Personalização de Prato — UC03):** Como Consumidor, eu gostaria de remover ou trocar acompanhamentos e ingredientes editáveis do prato, para que a refeição seja preparada de acordo com meu gosto pessoal.

* **RF-CON-05 (Checkout e Pagamento Antecipado — UC04):** Como Consumidor, eu gostaria de realizar o pagamento recorrente prévio (via Pix ou Cartão), para que minha assinatura seja ativada e a produção das marmitas seja liberada.

* **RF-CON-06 (Gestão de Ausências e Pausas — UC05):** Como Consumidor, eu gostaria de sinalizar datas de ausência no aplicativo com antecedência, para que a entrega seja pausada e o valor seja convertido em desconto no próximo ciclo.

---

### 2. Módulo do Restaurante Parceiro

* **RF-RES-01 (Cadastro de Pratos e Alérgenos — UC06):** Como Restaurante Parceiro, eu gostaria de cadastrar pratos com foto, preço, tabela nutricional e marcação obrigatória de alérgenos, para que os clientes visualizem o cardápio com total transparência e segurança alimentar.

* **RF-RES-02 (Painel do Lote de Produção Diário — UC07):** Como Restaurante Parceiro, eu gostaria de visualizar o total consolidado de marmitas do dia agrupadas por prato e com as observações dos clientes, para que a equipe da cozinha prepare as refeições em lote com eficiência e sem erros.

* **RF-RES-03 (Despacho de Pedidos — UC08):** Como Restaurante Parceiro, eu gostaria de marcar o lote como "Pronto para Envio", para que a equipe de entregadores seja notificada para realizar a coleta e distribuição.

---

### 3. Módulo B2B Corporativo

* **RF-B2B-01 (Gestão de Contratos Empresariais):** Como Empresa Contratante (B2B), eu gostaria de credenciar a empresa e gerenciar um contrato coletivo de refeições, para que meus colaboradores tenham acesso ao benefício de alimentação diária.

* **RF-B2B-02 (Subvenção de Refeições):** Como Empresa Contratante (B2B), eu gostaria de configurar a porcentagem de subsídio paga pela empresa e o valor pago pelo funcionário, para que o faturamento das refeições corporativas seja dividido de forma simples e automatizada.

* **RF-B2B-03 (Painel de Pedidos Coletivos):** Como Empresa Contratante (B2B), eu gostaria de acompanhar o lote consolidado de marmitas dos colaboradores, para que todas as refeições cheguem organizadas em uma única entrega no endereço corporativo.

* **RF-B2B-04 (Gestão do Quadro de Colaboradores):** Como Empresa Contratante (B2B), eu gostaria de incluir, remover ou pausar funcionários vinculados ao benefício, para que apenas colaboradores ativos utilizem a subvenção sem alterar o contrato principal.

---

### 4. Módulo de Logística e Expedição

* **RF-LOG-01 (Consolidação Diária de Pedidos — UC09):** Como Operador de Logística, eu gostaria de acessar a lista de todas as assinaturas pagas e ativas do dia por restaurante, para que a operação de coleta e embalagem seja planejada sem falhas.

* **RF-LOG-02 (Clusterização Geográfica / Last Mile — UC10):** Como Operador de Logística, eu gostaria de agrupar os endereços de entrega por bairro e regiões próximas, para que o custo do frete seja reduzido e o gargalo no horário de pico seja mitigado.

* **RF-LOG-03 (Roteamento e Ordenação de Entregas):** Como Entregador, eu gostaria de receber um itinerário ordenado com os pontos de parada do meu setor, para que eu consiga realizar todas as entregas dentro do horário do almoço.

* **RF-LOG-04 (Rastreamento em Tempo Real):** Como Consumidor, eu gostaria de acompanhar o status da minha refeição (Agendada ➔ Em Produção ➔ Em Rota ➔ Entregue), para que eu saiba exatamente quando receberei minha marmita.

* **RF-LOG-05 (Gestão de Embalagens Térmicas):** Como Operador de Logística, eu gostaria de registrar a conferência das embalagens térmicas e seladas antes da saída, para que a refeição chegue na temperatura adequada e sem avarias no transporte.

---

### 5. Automação e Processamento em Background

* **RF-AUT-01 (Rotina Agendada de Lotes — UC09):** Como Sistema, eu gostaria de executar automaticamente uma rotina diária à meia-noite, para que as assinaturas pagas sejam varridas e os lotes de produção do dia seguinte sejam gerados.

* **RF-AUT-02 (Processamento Automático de Ausências — UC05/UC09):** Como Sistema, eu gostaria de descontar do lote diário as marmitas dos clientes que informaram ausência dentro do prazo, para que os restaurantes não preparem comida em excesso e não haja desperdício de insumos.

---

## REQUISITOS NÃO FUNCIONAIS (RNF)

* **RNF-01 (Arquitetura e Stack Tecnológica Java Web):** O backend deve ser desenvolvido em Java Web utilizando o ecossistema Spring (Spring Boot, Spring Security e Spring Data JPA), organizado no modelo em camadas (Controller ➔ Service ➔ Repository ➔ Model).

* **RNF-02 (Segurança, Autenticação e LGPD):** A plataforma deve controlar o acesso por perfis (Consumidor, Restaurante, Empresa B2B e Admin) com autenticação via Spring Security/JWT e criptografia de dados sensíveis, garantindo conformidade estrita com a Lei Geral de Proteção de Dados (LGPD).

* **RNF-03 (Automação em Background):** O sistema deve utilizar tarefas agendadas automáticas (Spring Scheduler / Batch) para consolidar pedidos, atualizar ausências, disparar rotas e gerar cobranças recorrentes sem intervenção manual.

* **RNF-04 (Trava Lógica Absoluta de Alérgenos):** O algoritmo de consulta no banco de dados deve implementar uma trava de segurança no backend, garantindo que pratos que contenham alérgenos marcados pelo usuário jamais apareçam na busca ou entrem em sua assinatura.

* **RNF-05 (Interface Web Desktop Estilo Editorial):** As telas devem ser projetadas exclusivamente para navegadores Web Desktop, priorizando layouts limpos com carrosséis horizontais e fotos grandes, sendo estritamente proibido o uso de cards sombreados e badges coloridos.

* **RNF-06 (Desempenho e Suporte a Picos de Carga):** A API deve responder às requisições em menos de 500ms, garantindo estabilidade durante os picos de tráfego que ocorrem antes do horário do almoço e no fechamento dos lotes diários.

* **RNF-07 (Modularidade e Evolução em Sprints):** A arquitetura do sistema deve ser modular para permitir a entrega progressiva de software dividida em lançamentos (Farnel 1.0 MVP, Farnel 2.0 e Farnel 3.0).
