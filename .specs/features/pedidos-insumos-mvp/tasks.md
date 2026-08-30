# Plano de execução — MVP de pedidos digitais de insumos agrícolas

**Referências:** [PRD](prd.md) · [Tech spec](tech-spec.md)

## Fase 0 — Fundamentos verificáveis

- [ ] T01 Criar repositório Maven multi-módulo lógico, Spring Boot, Spring Modulith,
  React/Vite/TypeScript e ambiente local PostgreSQL.
  - Verificação: API, SPA e banco iniciam com um único comando documentado.
- [ ] T02 Configurar Flyway e schemas proprietários `identity`, `producer`, `catalog`,
  `pricing`, `cart`, `orders`, `fulfillment`, `compliance` e `notification`.
  - Verificação: banco vazio sobe até a versão atual e `flyway validate` passa.
- [ ] T03 Criar esqueleto dos módulos, interfaces públicas e teste
  `ApplicationModules`.
  - Verificação: teste arquitetural passa e um acesso proibido de exemplo falha.
- [ ] T04 Criar convenções de erros REST, OpenAPI, logs estruturados, health checks e
  correlação de requisição.
  - Verificação: endpoint de health e exemplo de erro obedecem ao contrato.

## Fase 1 — Identidade e produtor

- [ ] T05 Implementar usuário, papéis, senha protegida, login/logout e sessão via
  cookie HttpOnly com proteção CSRF.
  - Verificação: produtor não acessa rota administrativa; sessão não é lida pelo JS.
- [ ] T06 Implementar cadastro e edição do perfil do produtor e suas propriedades.
  - Verificação: produtor cria conta no checkout e visualiza apenas seus dados.
- [ ] T07 Implementar gestão de papéis para produtor, vendedor/RTV e administrador.
  - Verificação: matriz de autorização coberta por testes de integração.

## Fase 2 — Catálogo regulatório e comercial

- [ ] T08 Realizar spike do CSV Agrofit: obter layout vigente, licença, URL operacional,
  colunas obrigatórias e regra de atualização.
  - Verificação: amostra versionada/mascarada é analisada e o mapeamento é aprovado.
- [ ] T09 Implementar modelo de categoria, produto, dados regulatórios e dados
  comerciais complementares.
  - Verificação: administrador cria produto de semente/fertilizante e complementa
    defensivo importado sem alterar a fonte Agrofit.
- [ ] T10 Implementar carga inicial e agendamento diário idempotente do Agrofit,
  registrando versão, hash, contagens e erros.
  - Verificação: duas cargas do mesmo arquivo não duplicam registro MAPA.
- [ ] T11 Implementar publicação segura da última versão válida e painel de
  monitoramento de importações.
  - Verificação: arquivo inválido preserva catálogo publicado e aparece no painel.
- [ ] T12 Implementar gestão de imagens/vídeos no Cloudinary por porta de mídia,
  inclusive limites e URL assinada de upload.
  - Verificação: administrador associa mídia válida; arquivo inválido é recusado.
- [ ] T13 Implementar catálogo público com categorias, busca e filtros regulatórios.
  - Verificação: buscas de nome, categoria, cultura, alvo e ingrediente ativo retornam
    apenas produtos visíveis.

## Fase 3 — Comercial e carrinho

- [ ] T14 Implementar condições de pagamento, tabelas de preço e regra de
  desconto/acréscimo administráveis.
  - Verificação: condições distintas geram totais e parcelas esperados por testes.
- [ ] T15 Implementar API e interface de simulação de preço de referência.
  - Verificação: mudança de quantidade/condição atualiza total sem recarregar a página.
- [ ] T16 Implementar carrinho com adição, edição, remoção e persistência para visitante
  e usuário autenticado.
  - Verificação: carrinho com 100 itens mantém quantidades corretas após login.

## Fase 4 — Checkout, revisão e reserva

- [ ] T17 Implementar checkout: identificação tardia, cadastro, propriedade,
  retirada/entrega e data/janela pretendida.
  - Verificação: visitante finaliza a identificação e envia solicitação.
- [ ] T18 Implementar solicitação e snapshots imutáveis de itens, cotação e logística.
  - Verificação: alteração posterior do preço/catálogo não muda solicitação existente.
- [ ] T19 Implementar painel comercial para aprovar, solicitar ajuste ou recusar, com
  motivo e auditoria.
  - Verificação: toda transição exibe ator, data/hora e motivo.
- [ ] T20 Implementar depósitos, disponibilidade manual e reserva após aprovação, com
  expiração configurável.
  - Verificação: reserva só existe após aprovação e expira sem excluir histórico.

## Fase 5 — Conformidade e notificações

- [ ] T21 Implementar classificação de exigência de receita e bloqueio de confirmação
  até validação humana registrada.
  - Verificação: tentativa de aprovar item bloqueado falha com erro de domínio claro.
- [ ] T22 Implementar registro de validação de receita e transições
  `AGUARDANDO_RECEITA`/`APROVADA`.
  - Verificação: trilha de validação vincula responsável, data e decisão.
- [ ] T23 Implementar adaptador de e-mail, fila persistente de notificações e
  retentativa limitada.
  - Verificação: mudança de pedido gera registro entregue ou falho auditável.
- [ ] T24 Integrar WhatsApp Cloud API direta com opt-in, templates e registro de custo
  estimado por mensagem.
  - Verificação: sandbox da Meta recebe template de utilidade e falha é retentada.
- [ ] T25 Disparar alertas de falha Agrofit por painel, e-mail e WhatsApp.
  - Verificação: importação simulada como falha notifica administrador sem retirar o
    catálogo vigente.

## Fase 6 — Qualidade e piloto

- [ ] T26 Cobrir regras de domínio e cálculos com testes unitários.
  - Verificação: relatório de testes cobre preço, estados, receita e reserva.
- [ ] T27 Criar testes de integração com PostgreSQL para migrações, segurança, módulos e
  endpoints principais.
  - Verificação: pipeline local executa testes contra PostgreSQL isolado.
- [ ] T28 Criar E2E dos fluxos críticos: compra comum, item com receita e falha Agrofit.
  - Verificação: cenários rodam contra ambiente local sem mocks de regras internas.
- [ ] T29 Executar teste de usabilidade com produtor/vendedor: pedido de 20 itens em até
  cinco minutos e coleta de conversão.
  - Verificação: resultado documenta tempo, conclusão, erros e melhorias priorizadas.
- [ ] T30 Configurar ambiente piloto, segredos, conta Meta, templates, opt-ins,
  Cloudinary e observabilidade.
  - Verificação: checklist operacional aprovado antes de liberar usuários externos.

## Ordem de entrega

Primeiro disponibilizar T01–T16 para validar descoberta, carrinho e simulação. Em
seguida, T17–T25 para validação comercial e conformidade. T26–T30 são obrigatórios
para abertura do piloto.
