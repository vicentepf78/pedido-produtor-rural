# Task Memory: task_07

## Objective Snapshot

Real-user QA execution (`qa-execution`, `qa-docs-path=docs/qa`). Auto-commit
desligado. Não alterar `state.yaml` nem marcar o task file `completed`.

## Important Decisions

- Relatório criado antes da primeira sessão (16 linhas). Walk no stack real
  (Postgres + Spring `local` + Vite 5173). Playwright E2E mockado só como
  precondição.
- Driver: Playwright contra o vivo. MCP cursor-ide-browser perdeu a aba
  (`Browser view not found`); sem substituição por E2E mockado.
- `make test-e2e-runtime` não se aplica (harness Compozy); HTTP de `_dx.md`
  no lugar.
- Único achado (Sair ausente) foi a Friction; governor recusou auto-fix
  (trade-off de produto) → Decisions for a Human.
- Estado vazio da retaguarda não foi caminhado: Money já tinha pedido no
  tenant único.

## Learnings

- Native `<option>` de propriedade só existe depois do primeiro Confirmar;
  waiter de visible no option quebra o driver, não o produto.
- Cookie `sessao` pode permanecer no jar após saida; a prova é 401 em
  `GET /api/v1/pedidos`.
- Pedido Alfa desta rodada: `528175d5-86a1-44b9-b638-3431bb1baee9`
  (2× Aurora, R$ 1.240,00, Fazenda Norte, ACEITA).

## Files / Surfaces

- `docs/qa/reports/2026-08-30-pedidos-insumos-mvp0.md`
- `docs/qa/bugs/BUG-20260830-sem-sair-na-interface.md`
- 12 cenários com `qa_status` atualizado
- Evidências gitignored em `docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/`

## Errors / Corrections

- Primeira passagem do walker falhou em option hidden e heading Carrinho
  não-exato. Reexecução limpa; esses erros não foram mintados como bug
  de produto.

## Ready for Next Run

- Relatório fechado. Phase C pronta (Friction só). Peer-review é Phase D.
