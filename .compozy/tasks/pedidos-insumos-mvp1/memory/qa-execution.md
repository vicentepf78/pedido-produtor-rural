# Task Memory: qa-execution

## Objective Snapshot

Caminhar os charters do ciclo MVP 1 na UI pública, registrar vereditos e sair por `make gate`.

## Important Decisions

- `browser-use:browser` e `agent-browser` ausentes. Walk de maior risco via Playwright no stack real (`local` + Vite), sem mock e2e.
- `eng-worktree-isolation` não ativado: portas 5173/8080 livres; um tenant Compose.
- `make test-e2e-runtime` não se aplica (harness Compozy). HTTP `_dx.md` no lugar.
- Zero fails no walk; nenhum auto-fix. `BUG-20260830-sem-sair-na-interface` retestado → verified.

## Learnings

- `waitForURL(/catalogo/)` casa `origem=/catalogo` em `/entrar`. Esperar `btn-sair`.
- Aurora não está no primeiro bloco de 10 do recorte Todos.
- GET `/produtor/propriedades` devolve `{ itens: [...] }`, não lista nua.

## Files / Surfaces

- `docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md`
- `docs/qa/scenarios/*.md` (18 → `qa_status: pass`)
- `docs/qa/bugs/BUG-20260830-sem-sair-na-interface.md`
- evidência gitignored em `docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/`

## Errors / Corrections

- Primeira tentativa do walk: timeout em Aurora (não está na página 1) e `waitForURL` falso-positivo no Entrar. Script ajustado; rerun 48/48.

## Ready for Next Run

- Relatório ready; `make gate` exit 0. Próximo detect: Phase D `peer_review`.
- E2E-045 falha se o Vite do walk real for reutilizado (`reuseExistingServer`); liberar :5173 antes do e2e.
- Pedidos de evidência: Alfa UI `36b95df8-3e2c-485a-b952-3e38975a09ef`; Beta `9963e113-fd40-49ca-a891-972770ab45d0`; idempotência HTTP `722e3c3c-e962-442d-a496-c62217683d90`.

## QA Artifacts Produced

- `docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md`
- vereditos em `docs/qa/scenarios/`
- `docs/qa/bugs/BUG-20260830-sem-sair-na-interface.md` (verified)
