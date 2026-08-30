# Task Memory: task_06

## Objective Snapshot

QA Plan and Session Charters (`qa-report`, `qa-docs-path=docs/qa`). Status do
task file permanece `in_progress`. Sem commit.

## Important Decisions

- Personas alinhadas às histórias: `Produtor rural` e `Operador da revenda`,
  mais Mobile (`Produtora no campo`), A11y (`Produtor com leitor de tela`) e
  Recovering (`Produtor após falha`). Sem charter dedicado de A11y neste
  targeted — paper cuts no debrief.
- Jornadas obrigatórias com Mermaid + abandono + true end:
  `J-produtor-pedido-rapido`, `J-operador-retaguarda`.
- Canário adjacente: `J-convidado-retoma-carrinho` (Interrupt, continuidade do
  cookie de convidado), não um terceiro happy path de pedido.
- Charters por risco: Money (pedido + hot spots) → Garbage (checkout) →
  Feature (retaguarda) → Interrupt (canário).
- Health, cookies de sessão e chaves `SPRING_DATASOURCE_*` /
  `agriplataforma.*` entram como `entry_points` dos cenários existentes, sem
  cenário `HEALTH-*` ou `CFG-*`.
- Nenhum bug mintado: o registry está vazio; `qa-execution` é quem registra.
- Subtasks 6.1–6.6 marcadas no task file; `status:` não foi mudado para
  `completed`.

## Learnings

- Árvore já tinha README, templates e 12 cenários `untested`; faltavam
  `personas.md`, `journeys/` e `charters/`.
- `materialize_state.py` aceitou os 12 frontmatters (view gerada em
  `docs/qa/state.csv`, gitignored).
- `GET /api/v1/retaguarda/pedidos/{id}` e `ACESSO_NEGADO` são superfície das
  tasks 01–05, mas `_dx.md` não os lista.

## Files / Surfaces

- `docs/qa/personas.md`, `docs/qa/README.md`
- `docs/qa/journeys/J-produtor-pedido-rapido.md`
- `docs/qa/journeys/J-operador-retaguarda.md`
- `docs/qa/journeys/J-convidado-retoma-carrinho.md`
- `docs/qa/charters/CH-pedido-rapido-money.md`
- `docs/qa/charters/CH-checkout-lixo-entrada.md`
- `docs/qa/charters/CH-operador-lista-retaguarda.md`
- `docs/qa/charters/CH-canario-retoma-carrinho.md`
- 12 arquivos em `docs/qa/scenarios/` (jornada e `entry_points` atualizados;
  `qa_status: untested`)

## Errors / Corrections

- Nenhuma. Auto-commit desligado; sem push.

## Ready for Next Run

- `task_07` (`qa-execution`) caminha os quatro charters e atualiza vereditos.
- Não implementar produto nesta fatia.
