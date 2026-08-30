# Quality assurance workspace

Documentação viva de QA do MVP0 (pedidos de insumos). Um único tronco
(`docs/qa/`). Rodadas de `qa-execution` só acrescentam vereditos, bugs e
relatórios datados — nunca uma árvore `qa/` por rodada, ids `TC-*` ou
`verification-report.md`.

## Códigos de área

`AUTH` (identidade), `CAT` (catálogo), `CART` (carrinho), `CHK` (checkout),
`ORD` (pedidos) e `INT` (integração / Mock ERP).

## Personas, jornadas e charters deste ciclo

- Personas: [personas.md](personas.md)
- Jornadas: [J-produtor-pedido-rapido](journeys/J-produtor-pedido-rapido.md),
  [J-operador-retaguarda](journeys/J-operador-retaguarda.md)
- Canário adjacente: [J-convidado-retoma-carrinho](journeys/J-convidado-retoma-carrinho.md)
- Charters targeted (ordem de risco):
  [CH-pedido-rapido-money](charters/CH-pedido-rapido-money.md),
  [CH-checkout-lixo-entrada](charters/CH-checkout-lixo-entrada.md),
  [CH-operador-lista-retaguarda](charters/CH-operador-lista-retaguarda.md),
  [CH-canario-retoma-carrinho](charters/CH-canario-retoma-carrinho.md)

## Superfícies de entrada

Usar estas rotas e chaves como `entry_points` dos cenários — não como casos
soltos.

### Web (produtor)

`/`, `/catalogo`, `/catalogo/{idProduto}`, `/carrinho`, `/checkout`,
`/pedidos/{idPedido}`, `/meus-pedidos`

### Web (operador)

`/retaguarda/pedidos`, `/retaguarda/pedidos/{idPedido}` — URL direta; sem
atalho Backoffice na nav do produtor.

### HTTP (`_dx.md`)

`GET /api/v1/catalogo/produtos`,
`POST /api/v1/carrinhos/convidado/itens`,
`GET /api/v1/autenticacao/csrf`,
`POST /api/v1/autenticacao/cadastro`,
`POST /api/v1/autenticacao/entrada`,
`POST /api/v1/autenticacao/saida`,
`GET /api/v1/produtor/propriedades`,
`POST /api/v1/pedidos`,
`GET /api/v1/pedidos`,
`GET /api/v1/pedidos/{idPedido}`,
`GET /api/v1/retaguarda/pedidos`

### HTTP extra das tarefas 01–05

`GET /api/v1/catalogo/produtos/{idProduto}`,
`GET /api/v1/carrinhos/convidado`,
`PATCH /api/v1/carrinhos/convidado/itens/{idProduto}`,
`DELETE /api/v1/carrinhos/convidado/itens/{idProduto}`,
`GET /api/v1/retaguarda/pedidos/{idPedido}` (não está em `_dx.md`; US-005 AC-2),
`GET /actuator/health`

### Sessão, cookies e configuração

Não versionar segredos. Chaves de entrada dos cenários:

- `server.servlet.session.cookie.name` / cookie `sessao` (HttpOnly; Secure fora de `local`)
- `server.servlet.session.timeout`
- cookie `XSRF-TOKEN` + header `X-XSRF-TOKEN`
- cookie `chaveCarrinhoConvidado`
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- `AGRIPLATAFORMA_ID_TENANT_SEMEADO` / `agriplataforma.idTenantSemeado`
- `agriplataforma.gatewayErp` (`simulado`)

## Como subir o ambiente

1. `docker compose up -d` (PostgreSQL local; valores de `.env.example`).
2. Backend: `cd backend && ./mvnw spring-boot:run` (porta 8080).
3. Frontend: `cd frontend && npm run dev` (proxy `/api` e `/actuator`).
4. Health: `GET /actuator/health` deve responder UP antes do walk.

Fixtures fictícias (senha só no código de teste): `produtor.alfa@example.com`,
`produtor.beta@example.com`, `operador.revenda@example.com`. Produto Aurora
`10000000-0000-4000-8000-000000000001`. Regulamentado oculto
`10000000-0000-4000-8000-000000000099`.

## Comandos de verificação automática

Estes alvos não substituem o walk de persona. Encaminhar suítes técnicas
(integração, Modulith, carga) para o gate — não absorvê-las neste tronco.

- `make test` — unidade Maven
- `make test-integration` — Failsafe / Testcontainers
- `make test-e2e-web` — Playwright em `frontend/`
- `make gate` — spec + unidade + integração

## Hot spots de regressão

Invariantes da Parte II e do ADR-001 que os charters deste ciclo devem tocar:

| Hot spot | Invariante | Onde o walk prova |
| --- | --- | --- |
| Idempotência | Uma `Idempotency-Key` mapeia no máximo um pedido local | CH-pedido-rapido-money; ORD-confirma-e-revisita |
| Produto regulamentado | Não entra no catálogo nem no pedido | CH-pedido-rapido-money; CAT-produto-indisponivel-ou-regulado |
| Isolamento de pedido | Produtor só lê o próprio; operador só com `OPERADOR_REVENDA` | CH-operador-lista-retaguarda; ORD-acesso-negado; ORD-backoffice-vazio-ou-negado |
| Mock não muta pedido | `GatewayErpSimulado` aceita depois de persistir; não cria/altera/apaga | CH-pedido-rapido-money; INT-mock-erp-aceita |

## Taxonomia deste ciclo

Considerada por jornada; dimensão pulada tem motivo.

| Dimensão | J-produtor-pedido-rapido | J-operador-retaguarda | J-convidado-retoma-carrinho |
| --- | --- | --- | --- |
| Jornadas | ORD-confirma-e-revisita; CHK-identificacao-tardia | ORD-backoffice-lista | CART-convidado-persiste |
| Funcional | AUTH, CAT, CART, CHK-erros | ACESSO_NEGADO e detalhe | PATCH/DELETE + cookie |
| Experiencial | Paper cuts nos charters Money/Garbage | Paper cuts no Feature | Touch/375px no Interrupt |
| Erro / vazio / abandono | CAT regulado; CHK-erros; ORD-acesso-negado | Lista vazia; papel errado | Rede caída; logout |
| Transversal | Canário de continuidade; isolamento | Isolamento de papel | Responsividade no canário |

Pulos conscientes: sem charter dedicado de Accessibility-Reliant (tier targeted;
paper cuts no debrief). Sem charter Locale (copy só pt-BR). Sem item em
`automation-backlog/` — primeira rodada ainda não estabilizou E2E de jornada.
`INT-mock-erp-aceita` anda como efeito colateral do pedido, não como suíte de
carga.

## Política de evidência

`docs/qa/state.csv` e `docs/qa/evidence/` estão no `.gitignore`. Prints só em
checkpoint e falha. Relatórios datados em `reports/`. Bugs em `bugs/`.

## Templates

`templates/scenario.md`, `templates/charter.md`, `templates/bug.md`,
`templates/report.md`.
