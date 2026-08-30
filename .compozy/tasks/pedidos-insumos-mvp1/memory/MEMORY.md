# Workflow Memory: pedidos-insumos-mvp1

## Current State

- Phase E: workflow `gate` adicionado para o check nomeado que `--ci-pass` exige. Draft PR ainda depende de `gh auth` / `GH_TOKEN`.
- Branch de trabalho: `mvp-1` (tracking `origin/mvp-1`). Sem `--frontend` e sem `--stacked`.

## Shared Decisions

- Pacote Java-base herdado do MVP 0: `br.agriplataforma`.
- Spring Boot 4.1.1 + Spring Modulith 2.1.1; módulos não-OPEN; pacote `application` é named interface.
- Tenant semeado: `11111111-1111-1111-1111-111111111111`.
- Desenvolvimento nesta branch: `mvp-1`.
- Contrato visual já existe: `docs/design/opendesign/pedidos-insumos-mvp1/loja-insumos-agricolas.html`. Não é tarefa numerada.
- Tarefas 01–04 implementam a loja; 05–06 são o par QA. Sem worker frontend; execução local.
- Seed de catálogo não foi alterado para casar `_dx.md`: ureia semeada é `Ureia 45% N` @ 178.00 (UUID `...0013`), indisponível. ITs usam a semente; unitários mockam o exemplo 198.00.
- `GET /error` é `permitAll` para erros de validação não virarem 401.
- Destino pós-Entrar: `resolverDestinoAposEntrada`; operador ignora `origem` da loja.
- Sair no topo é dois toques e não apaga o carrinho de convidado.
- `eng-ui-screenshot` não está instalado neste repo; não inventar pacote visual substituto.
- Query do catálogo S1: `categoria`, `consulta`, `tamanhoPagina`. `pagina` não entra na URL.
- Mock e2e de catálogo lista o recorte quando `consulta` está em branco e pagina os 30 SKUs semeados.

## Shared Learnings

- `detect-phase.py` emite `phase=0 action=bootstrap` enquanto `state.yaml` não existe.
- `init-state.py` detectou `mode=tasks` pelo grafo `_tasks.md` + `task_*.md`.
- Jackson databind não entra no compile de teste; ITs HTTP devem parsear o corpo sem `ObjectMapper`.
- `Paginacao` da loja recusa 24; retaguarda continua com 25.
- E2E-019 no `make gate` paralelo: dois `click()` Playwright no botão que some após o 1º bloco; usar `dispatchEvent("click")`.
- Playwright `waitForURL(/catalogo/)` casa `?origem=/catalogo` em `/entrar`.
- Aurora não cabe no primeiro bloco de 10 do recorte Todos.
- `make gate` sem `SLUG=` precisa defaultar o slug do loop ativo (MVP1).
- Select vazio “Propriedade” no checkout colide com “Nome da propriedade”.
- Phase E sem URL de PR: não usar `--verify-fail` — isso anula SHIP e o detect volta para a Phase D.

## Open Risks

- Phase E: sem credencial da API GitHub. `origin/mvp-1` tinha zero check-runs; `.github/workflows/gate.yml` passa a rodar `make gate` em `pull_request` e `push` de `main`/`mvp-1`. Sem URL de PR, `--ci-pending`/`--ci-pass` ainda não grava. Compare: https://github.com/vicentepf78/pedido-produtor-rural/compare/main...mvp-1

## Open Questions

- Worker Fable 5 (`claude --permission-mode auto --model claude-fable-5` via herdr) não está instalado neste host. O `qa-report` foi produzido na sessão do orquestrador com a skill canônica, depois do `make gate` verde. Confirmar se rodadas futuras devem exigir o worker.
- Phase D rodada 1: herdr/`claude` ausentes; lane `codex` via Task (`gpt-5.6-sol-medium`) + síntese local. Confirmar se rodadas seguintes exigem o worker.
- Phase D rodada 2: `gpt-5.6-sol-medium` recusou por limite de uso; lanes `composer-2.5-fast` + `inherit`. Confirmar se o `codex` canônico continua obrigatório.

## Handoffs

- `task_01` concluída: catálogo paginado, `GET /sessao`, cadastro/entrada com `email`+`papeis`.
- `task_02` concluída: TopoLoja, `/entrar` `/cadastro`, destino por papel, TopoOperador mínimo.
- QA flag-only task_02: reset `AUTH-cadastro-e-sessao`; novos `AUTH-entrar-tela-propria`, `NAV-topo-loja`. Walk na Phase C.
- `task_03` concluída: carrossel fechado, busca composta, Carregar mais, query persistente.
- QA flag-only task_03: reset `CAT-produtor-descobre-produto`; novo `CAT-carrossel-e-carregar-mais`. Walk na Phase C.
- `task_04` concluída: S2/S5–S7/S9–S11 no visual da loja; checkout pula Entrar se já for produtor; retaguarda sem login inline.
- QA flag-only task_04: reset CART/CHK/ORD listados na tarefa; novo `RET-visual-loja`. Walk na Phase C.
- Checkout pula Entrar quando `papeis` já inclui `PRODUTOR`; retaguarda sem sessão vai para `/entrar?origem=…`.
- Mock de propriedades de Alfa usa Fazenda Norte (não Sul). Cadastro novo começa sem propriedade.
- Título da retaguarda: “Pedidos da revenda”. GET `/pedidos` mock exige sessão e isola o dono.
- `task_05` / `qa_report`: jornadas atualizadas; charters `CH-descoberta-carrossel` e `CH-entrar-e-topo-loja`.
- `task_06` / `qa_execution`: 6 charters andados no stack real; 18 cenários `pass`; Sair verified.
- Phase D r1: nits de URL `pagina`, checkout propriedade, teclado da retaguarda, encode de `origem`, `SLUG` default.
