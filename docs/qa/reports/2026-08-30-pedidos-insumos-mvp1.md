# QA Run Report — 2026-08-30 — pedidos-insumos-mvp1

- **Scope:** ciclo MVP 1 — topo da loja, carrossel fechado, Entrar/cadastro próprios, checkout com identificação tardia, retaguarda sem login inline
- **Cadence tier:** targeted
- **Build:** d87389f (+ worktree de qa-report / E2E-019) · **Environment:** `http://127.0.0.1:5173` + Spring `local` `:8080` + PostgreSQL Compose; `gatewayErp=simulado`; walk Playwright contra o stack real (sem mock e2e)
- **Started:** 2026-08-30T22:35:42Z · **Status:** closed

## Personas

| Persona | Base | Device / Network / Locale | Sessions |
|---|---|---|---|
| Produtor rural | seed | phone-large 390×844 / 4g / pt-BR | CH-pedido-rapido-money, CH-descoberta-carrossel, CH-entrar-e-topo-loja |
| Operador da revenda | seed | desktop 1280×800 / wifi-fast / pt-BR | CH-operador-lista-retaguarda |
| Produtora no campo | seed | phone-small 375×667 / flaky / pt-BR | CH-canario-retoma-carrinho |
| Produtor após falha | seed | phone-large 390×844 / 4g / pt-BR | CH-checkout-lixo-entrada |

## Flows in Scope

- `J-produtor-pedido-rapido` — descobrir, montar, identificar-se e confirmar um pedido (`../journeys/J-produtor-pedido-rapido.md`)
- `J-operador-retaguarda` — listar e inspecionar pedidos da revenda (`../journeys/J-operador-retaguarda.md`)
- `J-convidado-retoma-carrinho` — interromper e retomar o carrinho de convidado (`../journeys/J-convidado-retoma-carrinho.md`)

## Session Matrix & Results

| # | Charter | Journey / Scenario | Persona | Tour | Status | Issue | Fix commit |
|---|---|---|---|---|---|---|---|
| 1 | CH-pedido-rapido-money | J-produtor-pedido-rapido / CAT-produtor-descobre-produto | Produtor rural | Money Tour | Pass | | |
| 2 | CH-pedido-rapido-money | J-produtor-pedido-rapido / CAT-produto-indisponivel-ou-regulado | Produtor rural | Money Tour | Pass | | |
| 3 | CH-pedido-rapido-money | J-produtor-pedido-rapido / CART-monta-e-ajusta | Produtor rural | Money Tour | Pass | | |
| 4 | CH-pedido-rapido-money | J-produtor-pedido-rapido / CHK-identificacao-tardia | Produtor rural | Money Tour | Pass | | |
| 5 | CH-pedido-rapido-money | J-produtor-pedido-rapido / ORD-confirma-e-revisita | Produtor rural | Money Tour | Pass | | |
| 6 | CH-pedido-rapido-money | J-produtor-pedido-rapido / INT-mock-erp-aceita | Produtor rural | Money Tour | Pass | | |
| 7 | CH-descoberta-carrossel | J-produtor-pedido-rapido / CAT-carrossel-e-carregar-mais | Produtor rural | Landmark Tour | Pass | | |
| 8 | CH-descoberta-carrossel | J-produtor-pedido-rapido / CAT-produtor-descobre-produto | Produtor rural | Landmark Tour | Pass | | |
| 9 | CH-descoberta-carrossel | J-produtor-pedido-rapido / CAT-paginacao-catalogo | Produtor rural | Landmark Tour | Pass | | |
| 10 | CH-descoberta-carrossel | J-produtor-pedido-rapido / CAT-produto-indisponivel-ou-regulado | Produtor rural | Landmark Tour | Pass | | |
| 11 | CH-entrar-e-topo-loja | J-produtor-pedido-rapido / NAV-topo-loja | Produtor rural | Feature Tour | Pass | | |
| 12 | CH-entrar-e-topo-loja | J-produtor-pedido-rapido / AUTH-entrar-tela-propria | Produtor rural | Feature Tour | Pass | | |
| 13 | CH-entrar-e-topo-loja | J-produtor-pedido-rapido / AUTH-sessao-atual | Produtor rural | Feature Tour | Pass | | |
| 14 | CH-entrar-e-topo-loja | J-produtor-pedido-rapido / AUTH-cadastro-e-sessao | Produtor rural | Feature Tour | Pass | | |
| 15 | CH-entrar-e-topo-loja | J-produtor-pedido-rapido / RET-visual-loja | Produtor rural | Feature Tour | Pass | | |
| 16 | CH-operador-lista-retaguarda | J-operador-retaguarda / ORD-backoffice-lista | Operador da revenda | Feature Tour | Pass | | |
| 17 | CH-operador-lista-retaguarda | J-operador-retaguarda / ORD-backoffice-vazio-ou-negado | Operador da revenda | Feature Tour | Pass | | |
| 18 | CH-operador-lista-retaguarda | J-operador-retaguarda / ORD-acesso-negado | Operador da revenda | Feature Tour | Pass | | |
| 19 | CH-operador-lista-retaguarda | J-operador-retaguarda / INT-mock-erp-aceita | Operador da revenda | Feature Tour | Pass | | |
| 20 | CH-canario-retoma-carrinho | J-convidado-retoma-carrinho / CART-convidado-persiste | Produtora no campo | Interrupt Tour | Pass | | |
| 21 | CH-canario-retoma-carrinho | J-convidado-retoma-carrinho / CART-monta-e-ajusta | Produtora no campo | Interrupt Tour | Pass | | |
| 22 | CH-checkout-lixo-entrada | J-produtor-pedido-rapido / AUTH-cadastro-e-sessao | Produtor após falha | Garbage Tour | Pass | | |
| 23 | CH-checkout-lixo-entrada | J-produtor-pedido-rapido / CHK-erros-validacao | Produtor após falha | Garbage Tour | Pass | | |
| 24 | CH-checkout-lixo-entrada | J-produtor-pedido-rapido / CHK-identificacao-tardia | Produtor após falha | Garbage Tour | Pass | | |
| 25 | CH-checkout-lixo-entrada | J-produtor-pedido-rapido / CART-convidado-persiste | Produtor após falha | Garbage Tour | Pass | | |

Status legend: `Pending | Pass | Fixed | Skipped | Blocked (needs human verify) | Blocked (human decision)`

## Session Debriefs

### CH-pedido-rapido-money — Produtor rural

- **Ran:** 2026-08-30T22:38Z → 22:40Z (box respected: yes)
- **Findings:** nenhum bloqueio. Pedido UI `36b95df8-3e2c-485a-b952-3e38975a09ef` (2× Aurora, Fazenda Norte, Aceita). Reload e Meus pedidos iguais. Replay HTTP da mesma `Idempotency-Key` devolveu `722e3c3c-e962-442d-a496-c62217683d90` nas duas chamadas. UUID regulamentado: UI “Acesso negado”; API 404 `PRODUTO_NAO_ELEGIVEL`.
- **Bugs filed/updated:** `BUG-20260830-sem-sair-na-interface` retestado → verified (Sair no topo, dois toques).
- **Scenarios settled:** CAT-produtor-descobre-produto → pass; CAT-produto-indisponivel-ou-regulado → pass; CART-monta-e-ajusta → pass; CHK-identificacao-tardia → pass; ORD-confirma-e-revisita → pass; INT-mock-erp-aceita → pass
- **Paper cuts:** Ureia 45% N permanece indisponível (esperado no seed).
- **Surprises:** Aurora não cabe no primeiro bloco de 10 do recorte Todos (ordem alfabética); o produtor precisa buscar ou abrir o detalhe.
- **Suggested next charter:** n/a neste ciclo

### CH-descoberta-carrossel — Produtor rural

- **Ran:** 2026-08-30T22:38Z → 22:39Z (box respected: yes)
- **Findings:** carrossel fechado Todos / Sementes / Fertilizantes / Correção; sem Defensivos; bloco 10; `tamanhoPagina=15` lista 15; Carregar mais 10→20; query sem `pagina`; Limpar filtros volta a `/catalogo`; ureia em Fertilizantes mostra só Ureia 45% N.
- **Bugs filed/updated:** nenhum
- **Scenarios settled:** CAT-carrossel-e-carregar-mais → pass; CAT-produtor-descobre-produto → pass; CAT-paginacao-catalogo → pass; CAT-produto-indisponivel-ou-regulado → pass
- **Paper cuts:** nenhum afiado
- **Surprises:** nenhum
- **Suggested next charter:** n/a

### CH-entrar-e-topo-loja — Produtor rural

- **Ran:** 2026-08-30T22:39Z → 22:40Z (box respected: yes)
- **Findings:** TopoLoja com Catálogo / Carrinho / Checkout / Pedido e Entrar; `/entrar?origem=/catalogo`; Voltar sem sessão; GET `/sessao` convidado `autenticado=false`, após Alfa `email`+`PRODUTOR`; Sair em dois toques; operador em `/entrar?origem=/checkout` cai em Pedidos da revenda com TopoOperador.
- **Bugs filed/updated:** Sair retestado (ver money)
- **Scenarios settled:** NAV-topo-loja → pass; AUTH-entrar-tela-propria → pass; AUTH-sessao-atual → pass; AUTH-cadastro-e-sessao → pass; RET-visual-loja → pass
- **Paper cuts:** nenhum afiado
- **Surprises:** nenhum
- **Suggested next charter:** n/a

### CH-operador-lista-retaguarda — Operador da revenda

- **Ran:** 2026-08-30T22:40Z → 22:40Z (box respected: yes)
- **Findings:** lista contém o pedido Alfa; detalhe readonly. Produtor Alfa em `/retaguarda/pedidos` vê “Acesso negado” + `GET /retaguarda/pedidos` 403 `ACESSO_NEGADO`. Alfa em `/pedidos/{idBeta}` (`9963e113-fd40-49ca-a891-972770ab45d0`) vê “Você não pode visualizar este pedido.”
- **Bugs filed/updated:** nenhum
- **Scenarios settled:** ORD-backoffice-lista → pass; ORD-backoffice-vazio-ou-negado → pass; ORD-acesso-negado → pass; INT-mock-erp-aceita → pass
- **Paper cuts:** título “Pedidos da revenda” no h1 e no link do topo (já conhecido no e2e)
- **Surprises:** nenhum
- **Suggested next charter:** n/a

### CH-canario-retoma-carrinho — Produtora no campo

- **Ran:** 2026-08-30T22:40Z → 22:40Z (box respected: yes)
- **Findings:** segunda aba restaurou Aurora; cookie `chaveCarrinhoConvidado` após Sair; em 375×667 quantidade 3 e total visíveis sem hover.
- **Bugs filed/updated:** nenhum
- **Scenarios settled:** CART-convidado-persiste → pass; CART-monta-e-ajusta → pass
- **Paper cuts:** rede flaky não foi cortada (Playwright headless sem throttle) — ver parity
- **Surprises:** nenhum
- **Suggested next charter:** Network Tour se o canário voltar

### CH-checkout-lixo-entrada — Produtor após falha

- **Ran:** 2026-08-30T22:39Z → 22:40Z (box respected: yes)
- **Findings:** confirmar vazio foca e-mail `aria-invalid`; senha errada “E-mail ou senha inválidos.”; cadastro duplicado “Este e-mail já está cadastrado….”; URL sem senha; checkout vazio mantém atalho no topo.
- **Bugs filed/updated:** nenhum novo
- **Scenarios settled:** AUTH-cadastro-e-sessao → pass; CHK-erros-validacao → pass; CHK-identificacao-tardia → pass; CART-convidado-persiste → pass
- **Paper cuts:** nenhum afiado
- **Surprises:** nenhum
- **Suggested next charter:** n/a

## What Was Fixed

Nenhum auto-fix nesta rodada (zero fails no walk).

## Paper Cuts

| Persona | Where (journey/step) | Felt | Sharpness | Outcome |
|---|---|---|---|---|
| Produtor rural | J-produtor-pedido-rapido descoberta | Aurora some do primeiro bloco de 10 | dull | watching |
| Operador da revenda | J-operador-retaguarda lista | “Pedidos da revenda” no h1 e no nav | dull | watching |

## Runtime Errors Observed

- Nenhum `pageerror` relevante no Chromium do walk.

## Human Verifications Needed

Nenhuma perna exige humano (sem pagamento real, e-mail externo ou OAuth).

## Decisions for a Human

Nenhuma.

## Experiential lenses (J-produtor-pedido-rapido e J-operador-retaguarda)

| Lens | J-produtor | J-operador |
|---|---|---|
| Usability | pass — Pedido recebido / Aceita; Sair pede confirmação | pass — lista e detalhe readonly |
| Accessibility (quick) | pass — labels, `aria-invalid`, um h1 | pass — heading “Pedidos da revenda” |
| Perceived performance | pass — catálogo e checkout responderam no wifi local | pass |
| Compatibility | friction — só Chromium 390/375/1280; Safari/Firefox/dark mode não andaram | friction — só Chromium desktop |
| Error recoverability | pass — credenciais, vazio, regulado com próximo passo | pass — acesso negado com voltar |
| Production parity | friction — stack local real, mas `gatewayErp=simulado` e sem throttle 4g | igual |

## Learnings

- `browser-use:browser` e `agent-browser` não estão nesta sessão; o fluxo de maior risco andou via Playwright no stack real.
- `make test-e2e-runtime` é harness de daemon Compozy e não se aplica; HTTP de `_dx.md` exercitado (`GET /sessao`, `tamanhoPagina=24` → `TAMANHO_PAGINA_INVALIDO`, idempotência de `POST /pedidos`).
- `waitForURL(/catalogo/)` casa `origem=/catalogo` em `/entrar` — esperar `btn-sair`, não a URL.
- Aurora não está na primeira página do recorte Todos.

## Final Status

- **Exit gate (full automated suite):** `make gate` exit 0 (46.9s, 2026-08-30T22:50:06Z). `check-spec` ainda aponta `SLUG=pedidos-insumos-mvp0`. `make test` unidade verde. `make test-integration` exit 0. `make test-e2e-web` 54 passed após liberar :5173 (primeira corrida contra o Vite do walk falhou E2E-045 no `operator-top-bar` — interferência do servidor reutilizado; rerun isolado verde). `make test-e2e-runtime` não se aplica (harness Compozy).
- **Issues by user impact:** Blocks-Completion 0 · Data-Loss 0 · Trust-Damage 0 · Friction 0 · Cosmetic 0
- **Coverage:** 3/3 jornadas; 6/6 charters (5 targeted + lixo); Chromium 390/375/1280; Safari/Firefox/dark mode não andaram (parity friction, não bloqueia)
- **Verdict:** ready — o pedido rápido, o topo/Entrar e a retaguarda fecham no stack local sem Blocks-Completion nem Data-Loss.
