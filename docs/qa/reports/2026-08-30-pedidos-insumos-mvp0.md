# QA Run Report — 2026-08-30 — pedidos-insumos-mvp0

- **Scope:** ciclo targeted MVP0 (pedidos de insumos) — charters planejados na task_06; jornadas J-produtor-pedido-rapido, J-operador-retaguarda e canário J-convidado-retoma-carrinho
- **Cadence tier:** targeted
- **Build:** 5ee1b55 · **Environment:** http://127.0.0.1:5173 (Vite + proxy `/api` e `/actuator` → Spring Boot 8080, perfil `local`, Postgres via docker compose); `agriplataforma.gatewayErp=simulado`; walk sem mocks de Playwright
- **Started:** 2026-08-30T13:10:00-03:00 · **Status:** closed

## Personas

| Persona | Base | Device / Network / Locale | Sessions |
|---|---|---|---|
| Produtor rural | New User | phone-large 390×844 / 4g declarado (wifi local real) / pt-BR | CH-pedido-rapido-money |
| Produtor após falha | Recovering User | phone-large 390×844 / 4g declarado (wifi local real) / pt-BR | CH-checkout-lixo-entrada |
| Operador da revenda | Power User | desktop 1280×800 / wifi-fast / pt-BR | CH-operador-lista-retaguarda |
| Produtora no campo | Mobile User | phone-small 375×812 / flaky simulado (offline no ajuste) / pt-BR | CH-canario-retoma-carrinho |

## Flows in Scope

- `J-produtor-pedido-rapido` — O produtor recebe um pedido local com identificador e confirmação ACEITA sem depender de ERP real. (`../journeys/J-produtor-pedido-rapido.md`)
- `J-operador-retaguarda` — O operador confirma que o pedido do produtor chegou com o mesmo snapshot e confirmação ACEITA. (`../journeys/J-operador-retaguarda.md`)
- `J-convidado-retoma-carrinho` — O produtor reencontra o carrinho que montou sem conta, mesmo depois de sair ou recarregar. (`../journeys/J-convidado-retoma-carrinho.md`)

## Session Matrix & Results

| # | Charter | Journey / Scenario | Persona | Tour | Status | Issue | Fix commit |
|---|---|---|---|---|---|---|---|
| 1 | CH-pedido-rapido-money | J-produtor-pedido-rapido / CAT-produtor-descobre-produto | Produtor rural | Money | Pass | | |
| 2 | CH-pedido-rapido-money | J-produtor-pedido-rapido / CAT-produto-indisponivel-ou-regulado | Produtor rural | Money | Pass | | |
| 3 | CH-pedido-rapido-money | J-produtor-pedido-rapido / CART-monta-e-ajusta | Produtor rural | Money | Pass | | |
| 4 | CH-pedido-rapido-money | J-produtor-pedido-rapido / CHK-identificacao-tardia | Produtor rural | Money | Pass | | |
| 5 | CH-pedido-rapido-money | J-produtor-pedido-rapido / ORD-confirma-e-revisita | Produtor rural | Money | Pass | | |
| 6 | CH-pedido-rapido-money | J-produtor-pedido-rapido / INT-mock-erp-aceita | Produtor rural | Money | Pass | | |
| 7 | CH-checkout-lixo-entrada | J-produtor-pedido-rapido / AUTH-cadastro-e-sessao | Produtor após falha | Garbage | Pass | [BUG-20260830-sem-sair-na-interface](../bugs/BUG-20260830-sem-sair-na-interface.md) | |
| 8 | CH-checkout-lixo-entrada | J-produtor-pedido-rapido / CHK-erros-validacao | Produtor após falha | Garbage | Pass | | |
| 9 | CH-checkout-lixo-entrada | J-produtor-pedido-rapido / CHK-identificacao-tardia | Produtor após falha | Garbage | Pass | | |
| 10 | CH-checkout-lixo-entrada | J-convidado-retoma-carrinho / CART-convidado-persiste | Produtor após falha | Garbage | Pass | | |
| 11 | CH-operador-lista-retaguarda | J-operador-retaguarda / ORD-backoffice-lista | Operador da revenda | Feature | Pass | | |
| 12 | CH-operador-lista-retaguarda | J-operador-retaguarda / ORD-backoffice-vazio-ou-negado | Operador da revenda | Feature | Pass | vazio não alcançável após Money; recusa do produtor evidenciada | |
| 13 | CH-operador-lista-retaguarda | J-produtor-pedido-rapido / ORD-acesso-negado | Operador da revenda | Feature | Pass | | |
| 14 | CH-operador-lista-retaguarda | J-produtor-pedido-rapido / INT-mock-erp-aceita | Operador da revenda | Feature | Pass | | |
| 15 | CH-canario-retoma-carrinho | J-convidado-retoma-carrinho / CART-convidado-persiste | Produtora no campo | Interrupt | Pass | | |
| 16 | CH-canario-retoma-carrinho | J-produtor-pedido-rapido / CART-monta-e-ajusta | Produtora no campo | Interrupt | Pass | | |

Status legend: `Pending | Pass | Fixed | Skipped | Blocked (needs human verify) | Blocked (human decision)`

## Preconditions (automated)

- `make test-integration`: Failsafe 15 completed, 0 errors, 0 failures, 0 skipped (`backend/target/failsafe-reports/failsafe-summary.xml`). Exit 0.
- `make test-e2e-web`: Playwright 10 passed (3.3s). Esses E2E usam `apiMock` e **não** substituem o walk.
- `make test-e2e-runtime`: **não se aplica**. É harness de daemon Compozy, não deste produto. No lugar, o walk exercitou as rotas HTTP de `_dx.md` (catálogo, carrinho, csrf, entrada, saida, propriedades, `POST/GET /pedidos`, `GET /retaguarda/pedidos`) e `GET /actuator/health`.
- Stack real: `docker compose up -d` (Postgres 16 aceitando conexões); `./mvnw spring-boot:run -Dspring-boot.run.profiles=local` (cookie Secure=false); `npm run dev` em 5173. `GET /actuator/health` → `{"status":"UP"}`.
- Driver do walk: Playwright contra o stack vivo. O MCP `cursor-ide-browser` criou aba e a perdeu em seguida (`Browser view not found`); não houve troca silenciosa por E2E mockado.

## Session Debriefs

### CH-pedido-rapido-money — Produtor rural

- **Ran:** 2026-08-30T16:17:28Z → 16:17:31Z (box 60 min respeitado: sim; o relógio de 2,8 s é automação, não polegar humano)
- **Findings:** nenhum bloqueio. Pedido `528175d5-86a1-44b9-b638-3431bb1baee9`, total R$ 1.240,00 (2× Aurora), propriedade Fazenda Norte, confirmação Aceita. Reload e Meus pedidos iguais. `GET /api/v1/pedidos` e detalhe: `ACEITA` / `1240.00`. Replay HTTP da mesma `Idempotency-Key` devolveu o mesmo `idPedido`. UUID regulamentado: UI “Acesso negado” sem conteúdo; API 404 `PRODUTO_NAO_ELEGIVEL`. Listagem sem Defensivos (30 itens). Ureia 45% N com botão Indisponível.
- **Bugs filed/updated:** nenhum neste charter
- **Scenarios settled:** CAT-produtor-descobre-produto → pass; CAT-produto-indisponivel-ou-regulado → pass; CART-monta-e-ajusta → pass; CHK-identificacao-tardia → pass; ORD-confirma-e-revisita → pass; INT-mock-erp-aceita → pass
- **Paper cuts:** copy da URL regulada não usa o texto canônico de `_dx.md` (dull)
- **Surprises:** o primeiro toque em Confirmar pedido autentica e só então libera as propriedades — dois toques no mesmo botão (identificação tardia)
- **Suggested next charter:** Garbage no checkout
- **Tour + edges:** Money (duplo clique, replay de chave, ausência de pagamento). Edges: qtd 0; busca sem resultado; URL inexistente; URL regulada; cookie `sessao` HttpOnly

### CH-checkout-lixo-entrada — Produtor após falha

- **Ran:** 2026-08-30T16:17:31Z → 16:17:36Z (box 60 min: sim)
- **Findings:** Friction — sem Sair na interface ([BUG-20260830-sem-sair-na-interface](../bugs/BUG-20260830-sem-sair-na-interface.md)). Funcionalmente: vazios com `aria-invalid`; e-mail duplicado em foco; senha errada sem eco na URL; lixo longo não derruba a tela; propriedade/retirada obrigatórias; `POST /autenticacao/saida` 204 e carrinho R$ 620,00 intacto (`GET /api/v1/carrinhos/convidado`)
- **Bugs filed/updated:** BUG-20260830-sem-sair-na-interface
- **Scenarios settled:** AUTH-cadastro-e-sessao → pass; CHK-erros-validacao → pass; CHK-identificacao-tardia → pass; CART-convidado-persiste → pass
- **Paper cuts:** “Não achei um botão para sair da conta” (sharp → bug)
- **Surprises:** o cookie `sessao` permanece no jar após saida, mas `GET /api/v1/pedidos` passa a 401
- **Suggested next charter:** Feature da retaguarda
- **Tour + edges:** Garbage (vazio, duplicado, senha errada, lixo, duplo clique, saida). Senha nunca na URL/mensagem

### CH-operador-lista-retaguarda — Operador da revenda

- **Ran:** 2026-08-30T16:17:36Z → 16:18:11Z (box 30 min: sim)
- **Findings:** nenhum. Lista mostrou o pedido Alfa (R$ 1.240,00, Aceita) e os de Beta. Detalhe somente leitura bate com o snapshot do produtor. Refresh intacto. `GET /api/v1/retaguarda/pedidos/{id}` → `ACEITA`, `nomeProdutor` Produtor Alfa. Produtor em `/retaguarda/pedidos`: “Acesso negado” + API 403. Alfa no pedido de Beta: “Você não pode visualizar este pedido.” / `ACESSO_PEDIDO_NEGADO`. Sem atalho Backoffice na nav do produtor.
- **Bugs filed/updated:** nenhum
- **Scenarios settled:** ORD-backoffice-lista → pass; ORD-backoffice-vazio-ou-negado → pass (recusa evidenciada; vazio não alcançável neste tenant após Money); ORD-acesso-negado → pass; INT-mock-erp-aceita → pass
- **Paper cuts:** nenhum
- **Surprises:** a lista da retaguarda já tinha o pedido HTTP de Beta da sessão anterior
- **Suggested next charter:** canário de carrinho
- **Tour + edges:** Feature. Edges: refresh do detalhe; URL direta; papel errado; pedido alheio

### CH-canario-retoma-carrinho — Produtora no campo

- **Ran:** 2026-08-30T16:18:11Z → 16:18:13Z (box 30 min: sim)
- **Findings:** nenhum. Em 375×812, item sobreviveu a fechar a aba, PATCH 3 (R$ 1.860,00) + reload, corte de rede (total não inventou linha), DELETE + reload (vazio), e login+logout HTTP (cookie e total R$ 620,00). Qtd e total visíveis sem hover.
- **Bugs filed/updated:** nenhum
- **Scenarios settled:** CART-convidado-persiste → pass; CART-monta-e-ajusta → pass
- **Paper cuts:** nenhum
- **Surprises:** nenhum
- **Suggested next charter:** interrupt com 3G real / troca de app
- **Tour + edges:** Interrupt. Edges: fechar aba; reload; offline mid-PATCH; DELETE; logout; viewport 375

## Experiential lenses

Reandada das duas jornadas mais largas (pedido rápido e retaguarda), ~caixa de 45 min.

| Journey | Usability | Accessibility | Perceived performance | Compatibility | Error recoverability | Production parity |
|---|---|---|---|---|---|---|
| J-produtor-pedido-rapido | friction | friction | pass | friction | pass | friction |
| J-operador-retaguarda | pass | friction | pass | friction | pass | friction |

Notas: dois toques em Confirmar (login depois propriedade); busca com `aria-label` e foco visível; sem Sair; walk só Chromium; ERP simulado; Secure=false no perfil `local`; 3G real não aplicado (gap). Sem charter dedicado de leitor de tela neste targeted.

## What Was Fixed

Nenhum auto-fix. O único achado (Sair ausente) falha o bound de trade-off de produto do governor.

## Paper Cuts

| Persona | Where (journey/step) | Felt | Sharpness | Outcome |
|---|---|---|---|---|
| Produtor após falha | AUTH / identificação | "Não achei um botão para sair da conta." | sharp | filed BUG-20260830-sem-sair-na-interface; Decisions for a Human |
| Produtor rural | CAT / URL regulada | "A mensagem fala em acesso negado, não no texto do produto." | dull | watching |
| Produtor rural | CHK / confirmar | "Apertei Confirmar e ainda pediu fazenda." | dull | watching (identificação tardia) |

## Runtime Errors Observed

- 401 em `GET /api/v1/produtor/propriedades` antes da entrada — esperado; a UI trata e segue.
- 409 em cadastro duplicado — esperado; copy `EMAIL_DUPLICADO` em foco.
- 404 em produto regulado / inexistente — esperado; UI de acesso negado.
- 400 `CARRINHO_VAZIO` num replay HTTP com carrinho já consumido — esperado; não criou pedido fantasma.
- Nenhum erro de console não explicado.

## Human Verifications Needed

- [ ] Safari iOS / Chrome Android reais e Slow 3G (o walk foi Chromium headless em wifi local). Não bloqueia o targeted deste ciclo; qualifica compatibilidade.

## Decisions for a Human

### Produtor não encontra Sair (BUG-20260830-sem-sair-na-interface)
- What's broken: depois de entrar, não há controle de encerrar sessão na nav nem no checkout. Evidência: `docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-checkout-lixo-entrada-07-logout-carrinho.png`
- Why not auto-fixed: trade-off de produto (onde vive o Sair, se o MVP0 precisa dele, copy e impacto no checkout tardio)
- Options:
  1. Acrescentar Sair na nav ou no checkout — custo baixo, deixa a saida descoberta
  2. Deixar só a expiração de 30 min — menos superfície, pior em dispositivo compartilhado
- Recommendation: opção 1 no checkout, depois da identificação, sem remover o cookie do carrinho

## Learnings

- `make test-e2e-runtime` não se aplica a este produto; HTTP de `_dx.md` é o substituto.
- cursor-ide-browser MCP não manteve a aba; Playwright no stack vivo foi o driver do walk de maior risco.
- Native `<option>` de propriedade carrega após o primeiro Confirmar; waiter de “visible” no option falha — o controle existe, só não está “visível” para o driver.
- Estado vazio da retaguarda compete com o charter Money no mesmo tenant: caminhar Money primeiro esgota o vazio.

## Final Status

- **Exit gate (full automated suite):** `make gate` exit 0. `check-spec-part1-leak.py` OK; `check-spec-markers.py` OK; Surefire 34 tests, Failures 0, Errors 0, Skipped 0; Failsafe `completed=15 errors=0 failures=0 skipped=0`. Mensagem final do Makefile: “Specification and application gates passed for pedidos-insumos-mvp0.” Fechamento extra: `make test-e2e-web` — 10 passed (2.6s).
- **Issues by user impact:** Blocks-Completion 0 · Data-Loss 0 · Trust-Damage 0 · Friction 1 · Cosmetic 0
- **Coverage:** 3/3 jornadas caminhadas (produtor, operador, canário). Vazio da retaguarda não observável após o pedido Money — divulgado. Sem Safari/Firefox/3G real.
- **Verdict:** ready with blocked items — o pedido rápido, a retaguarda e o canário do carrinho fecharam com evidência independente; a Friction de Sair ausente fica para decisão humana e não bloqueia o MVP0.
