# Task Memory: qa-report

## Objective Snapshot

Planejar o ciclo targeted de QA do MVP 1 na árvore viva `docs/qa/`: jornadas com abandono, cenários `untested` e charters persona+tour+time-box. Sem código de produto além do reparo de gate.

## Important Decisions

- Worker Fable 5 / `rtk herdr` / `claude` ausentes nesta máquina. Após `make gate` verde, o orquestrador executou a skill `qa-report` localmente (alternativa segura esgotada: herdr-orchestration unhealthy; `rtk` aqui é proxy de CLI, não herdr; `claude` não está no PATH).
- `eng-qa-bootstrap` ausente; a árvore `docs/qa/` já existia do MVP 0.
- Charters novos: `CH-descoberta-carrossel`, `CH-entrar-e-topo-loja`. Reuso: money, operador, canário; garbage se sobrar caixa.
- Áreas `NAV` e `RET` registradas no README.
- Bug `BUG-20260830-sem-sair-na-interface` já marked resolved no registry (Sair no topo); veredito de retest fica para `qa-execution`.

## Learnings

- `make gate` defaulta `SLUG=pedidos-insumos-mvp0` no check-spec; o e2e da loja MVP 1 ainda roda no mesmo alvo.
- E2E-019 flakava no gate paralelo: `Promise.all` de dois `click()` no botão que some após o primeiro bloco de Fertilizantes (12 SKUs). `dispatchEvent("click")` exercita o guard `carregandoMaisRef`.

## Files / Surfaces

- `docs/qa/README.md`, `docs/qa/journeys/J-*.md`
- `docs/qa/charters/CH-descoberta-carrossel.md`, `CH-entrar-e-topo-loja.md`
- Cenários planning: `CHK-identificacao-tardia`, `AUTH-cadastro-e-sessao`, `CAT-carrossel-e-carregar-mais`, `RET-visual-loja`
- `frontend/e2e/catalog.spec.ts` (reparo de gate E2E-019)

## Errors / Corrections

- B→C `make gate` exit 2: `test-e2e-web` / E2E-019 timeout. Reparo no spec; gate rerun exit 0.

## Ready for Next Run

- `detect-phase` seguinte: `qa_execution` (task_06).
- Walk: charters na ordem do README. Não inventar evidência.

## QA Artifacts Produced

- `docs/qa/journeys/J-produtor-pedido-rapido.md`
- `docs/qa/journeys/J-operador-retaguarda.md`
- `docs/qa/journeys/J-convidado-retoma-carrinho.md`
- `docs/qa/charters/CH-descoberta-carrossel.md`
- `docs/qa/charters/CH-entrar-e-topo-loja.md`
- `docs/qa/README.md`
- `docs/qa/scenarios/CHK-identificacao-tardia.md`
- `docs/qa/scenarios/AUTH-cadastro-e-sessao.md`
- `docs/qa/scenarios/CAT-carrossel-e-carregar-mais.md`
- `docs/qa/scenarios/RET-visual-loja.md`
