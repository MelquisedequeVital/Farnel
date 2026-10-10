# ESPECIFICAÇÃO DE CASOS DE USO — PROJETO FARNEL

---

## MÓDULO DO CONSUMIDOR (B2C)

### UC01 — Manter Assinatura
* **Ator Principal:** Consumidor B2C
* **Pré-condições:** O cliente precisa estar autenticado no sistema.
* **Gatilho:** Cliente seleciona a opção de criar, alterar ou cancelar um plano de marmitas recorrentes.
* **Fluxo Principal:**
  1. O cliente acessa a tela de gerenciamento de assinaturas.
  2. Seleciona os dias da semana desejados, o plano de refeições e o endereço de entrega.
  3. O sistema calcula o valor total recorrente.
  4. O cliente confirma as seleções.
  5. O sistema salva as informações e direciona o usuário para o pagamento antecipado (UC04).
* **Fluxos de Exceção:**
  * Alteração de Plano: Se o cliente modificar dias ou pratos em plano ativo, o novo valor ajustado vigora a partir do próximo ciclo de cobrança.
  * Cancelamento Tarde Demais: Se o cancelamento for solicitado com menos de 24 horas da próxima entrega, o sistema alerta que a entrega do dia seguinte será mantida e programa o cancelamento para a semana seguinte.
* **Pós-condições:** A assinatura é registrada ou alterada no banco de dados respeitando as regras de cancelamento.

---

### UC02 — Buscar Refeições por Filtros
* **Ator Principal:** Consumidor B2C
* **Pré-condições:** Nenhuma (acesso livre ao catálogo de pratos).
* **Gatilho:** O cliente insere critérios de filtro na busca do cardápio.
* **Fluxo Principal:**
  1. O cliente abre o catálogo geral de refeições.
  2. Aplica os filtros desejados (ex.: sem glúten, sem lactose, faixa de calorias, tipo de dieta).
  3. O sistema pesquisa no banco de dados os pratos correspondentes.
  4. O sistema exibe os resultados com destaque para nutrientes e marcação obrigatória de alérgenos.
* **Fluxos de Exceção:**
  * Nenhum Prato Encontrado: O sistema informa que não localizou refeições com a combinação exata de filtros e sugere opções similares.
* **Pós-condições:** Pratos contendo ingredientes restritos marcados pelo cliente são estritamente excluídos dos resultados exibidos.

---

### UC03 — Personalizar Pedido
* **Ator Principal:** Consumidor B2C
* **Pré-condições:** Cliente autenticado e em processo de escolha/configuração do prato.
* **Gatilho:** O cliente clica em "Personalizar" em uma refeição do catálogo.
* **Fluxo Principal:**
  1. O sistema apresenta os itens editáveis do prato (troca ou remoção de acompanhamento).
  2. O cliente seleciona as modificações desejadas.
  3. O cliente clica em salvar.
  4. O sistema vincula as preferências personalizadas à assinatura.
* **Fluxos de Exceção:**
  * Remoção de Item Obrigatório: Caso o cliente tente remover um ingrediente principal e não editável, o sistema impede a alteração e exibe um alerta.
* **Pós-condições:** As observações e substituições salvas são repassadas para o painel de produção da cozinha do restaurante.

---

### UC04 — Realizar Pagamento Antecipado
* **Ator Principal:** Consumidor B2C
* **Pré-condições:** Existência de uma nova assinatura configurada ou ciclo de renovação pendente.
* **Gatilho:** Finalização do cadastro da assinatura ou disparo do dia de renovação do ciclo.
* **Fluxo Principal:**
  1. O sistema exibe o valor total cobrado pelo plano.
  2. O cliente seleciona o meio de pagamento (Pix, Cartão de Crédito ou Boleto).
  3. O sistema processa a transação com a operadora/banco.
  4. Com o pagamento aprovado, a assinatura é ativada e as entregas são liberadas para agendamento.
* **Fluxos de Exceção:**
  * Pagamento Recusado: O sistema notifica a falha na cobrança e impede o envio do pedido para a fila de produção.
* **Pós-condições:** Nenhuma refeição entra em produção sem confirmação prévia de pagamento.

---

### UC05 — Informar Ausência
* **Ator Principal:** Consumidor B2C
* **Pré-condições:** Assinatura ativa e devidamente paga.
* **Gatilho:** O cliente marca uma data de ausência no calendário de entregas.
* **Fluxo Principal:**
  1. O cliente visualiza o calendário de entregas agendadas.
  2. Seleciona a data em que estará ausente e solicita o bloqueio da entrega.
  3. O sistema valida se o pedido atende ao prazo de antecedência mínima.
  4. O cliente confirma o bloqueio.
  5. O sistema cancela a entrega daquela data e gera crédito/desconto para a cobrança do próximo ciclo.
* **Fluxos de Exceção:**
  * Bloqueio em Cima da Hora: Se a refeição já estiver em processo de produção na cozinha, o sistema bloqueia o cancelamento e alerta o cliente.
* **Pós-condições:** A refeição da data marcada é removida do lote diário de produção do restaurante parceiro.

---

## MÓDULO DO RESTAURANTE PARCEIRO

### UC06 — Cadastrar Pratos e Alérgenos
* **Ator Principal:** Restaurante Parceiro
* **Pré-condições:** Restaurante autenticado no painel parceiro.
* **Gatilho:** Clique no botão "Novo Prato".
* **Fluxo Principal:**
  1. O restaurante insere nome, descrição, foto e preço da refeição.
  2. Preenche a tabela nutricional (calorias, proteínas, carboidratos).
  3. Seleciona obrigatoriamente a presença de alérgenos (glúten, lactose, amendoim, etc.).
  4. Confirma o salvamento.
  5. O prato é disponibilizado no catálogo da plataforma.
* **Fluxos de Exceção:**
  * Campos Obrigatórios Incompletos: O sistema impede o cadastro e destaca os dados nutricionais ou de alérgenos pendentes.
* **Pós-condições:** A declaração de alérgenos fica vinculada à refeição e ativa no banco de dados.

---

### UC07 — Visualizar Lote de Produção
* **Ator Principal:** Restaurante Parceiro
* **Pré-condições:** Login ativo e existência de pedidos confirmados para a data.
* **Gatilho:** Abertura da tela "Produção do Dia".
* **Fluxo Principal:**
  1. O restaurante seleciona a data de consulta.
  2. O sistema consolida todas as marmitas ativas agendadas para aquele dia.
  3. O sistema exibe o total de marmitas por tipo de prato e as observações de personalização dos clientes.
  4. A cozinha utiliza a lista para o preparo em lote.
* **Fluxos de Exceção:**
  * Sem Produção Agendada: O sistema exibe mensagem informando ausência de marmitas no dia selecionado.
* **Pós-condições:** O quantitativo do lote bate com o número total de assinantes pagantes do dia.

---

### UC08 — Despachar Pedidos
* **Ator Principal:** Restaurante Parceiro
* **Pré-condições:** Refeições do dia preparadas, embaladas e etiquetadas.
* **Gatilho:** Clique na opção "Liberar para Entrega".
* **Fluxo Principal:**
  1. O restaurante confere as embalagens lacradas com os rótulos gerados.
  2. Altera o status do lote para "Pronto para Envio".
  3. O sistema notifica a frota/entregadores para a coleta.
* **Fluxos de Exceção:**
  * Inconsistência de Quantidade: Se houver falta de pratos no lote, o restaurante registra a divergência e o sistema aciona o suporte.
* **Pós-condições:** O status do lote altera para "Despachado" apenas quando todas as refeições estiverem prontas.

---

## MÓDULO B2B CORPORATIVO

### UC11 — Gerenciar Contrato e Colaboradores B2B
* **Ator Principal:** Empresa Contratante (B2B)
* **Pré-condições:** Gestor B2B autenticado no painel corporativo.
* **Gatilho:** Acesso ao módulo corporativo para gestão de benefício de alimentação.
* **Fluxo Principal:**
  1. O gestor define ou altera a regra do contrato (subsídio da empresa e coparticipação do funcionário).
  2. Insere, edita ou pausa o cadastro de colaboradores autorizados a receber as marmitas.
  3. O sistema atualiza a lista de beneficiários elegíveis.
* **Fluxos de Exceção:**
  * Limite de Subsídio Excedido: Se o colaborador selecionar pratos com valor acima da política cadastrada, o sistema exige a confirmação do pagamento do valor excedente pelo funcionário.
* **Pós-condições:** A lista de colaboradores ativos e as regras de cobrança compartilhada ficam cadastradas para o faturamento.

---

### UC12 — Acompanhar Pedidos Coletivos
* **Ator Principal:** Empresa Contratante (B2B)
* **Pré-condições:** Existência de pedidos de colaboradores agendados para a data.
* **Gatilho:** Abertura do painel de entregas corporativas.
* **Fluxo Principal:**
  1. O gestor visualiza o resumo consolidado de marmitas enviadas ao endereço da empresa no dia.
  2. Acompanha o horário previsto de chegada do lote único.
* **Fluxos de Exceção:**
  * Alteração do Quadro de Funcionários: Notificações de desligamento ou férias atualizam automaticamente o lote do dia seguinte.
* **Pós-condições:** Confirmação de recebimento coletivo no endereço corporativo.

---

## AUTOMAÇÃO DO SISTEMA E LOGÍSTICA

### UC09 — Consolidar Pedidos Diários
* **Ator Principal:** Sistema (Servidor / Robô Automático)
* **Pré-condições:** Horário agendado atingido no servidor (à meia-noite).
* **Gatilho:** Disparo do temporizador automático (Spring Scheduler).
* **Fluxo Principal:**
  1. O servidor busca todas as assinaturas pagas e ativas para a data.
  2. Remove da lista as refeições de clientes que noticiaram ausência (UC05).
  3. Agrupa as marmitas por restaurante e gera o lote diário de produção.
  4. Atualiza o status dos pedidos para "Em Produção".
* **Fluxos de Exceção:**
  * Erro no Processamento em Lote: Em caso de falha de conexão com banco, o sistema dispara um alerta ao administrador e agenda nova tentativa em 15 minutos.
* **Pós-condições:** Assinaturas não pagas ou pausadas são desconsideradas da produção.

---

### UC10 — Gerar Rotas de Entrega
* **Ator Principal:** Sistema (Servidor / Robô Automático)
* **Pré-condições:** Conclusão bem-sucedida da consolidação diária (UC09).
* **Gatilho:** Finalização da geração dos lotes de produção.
* **Fluxo Principal:**
  1. O sistema obtém a lista de endereços de entrega do dia.
  2. Agrupa as paradas por bairro e proximidade geográfica dos restaurantes parceiros (clusterização/last mile).
  3. Calcula o itinerário otimizado para a frota/entregadores.
  4. Disponibiliza a lista de rotas ordenadas para a equipe de logística.
* **Fluxos de Exceção:**
  * Endereço Inválido/Geolocalização Falha: O pedido é marcado com aviso no mapa e encaminhado para validação manual do suporte.
* **Pós-condições:** Rotas geradas garantindo o cumprimento das janelas de entrega no horário do almoço.
