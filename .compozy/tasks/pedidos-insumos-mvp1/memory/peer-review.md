# Task Memory: peer-review

## Objective Snapshot

Phase D rodada 2 incremental: confirmar remediações D1 intactas após `--verify-fail` do blocker de Phase E; SHIP sem mudança de produto.

## Important Decisions

- Base do review = `66e004c` (início do MVP1), não `main` inteiro (MVP0 já teve SHIP).
- Lane `codex` via Task (`gpt-5.6-sol-medium`); herdr/`claude` ausentes — mesma falha do qa-report.
- Veredito da rodada: **SHIP** após remediação dos nits D1-01…D1-05.

## Learnings

- `make gate` sem `SLUG=` usava o spec do MVP0; o default precisa acompanhar o loop ativo.
- Select vazio “Propriedade” no checkout quebra `getByLabel` e o fluxo de nome da propriedade.

## Files / Surfaces

- `evidence/peer-review/round-1/review.md`
- `evidence/peer-review/round-1/state.json`
- `Makefile`, `PaginaCatalogo.tsx`, `PaginaCheckout.tsx`, `PaginaPedidosRetaguarda.tsx`, `TopoLoja.tsx`, `estilos.css`, `catalog.spec.ts`

## Errors / Corrections

- `make gate` falhou no e2e: foco do select de propriedade após login (DOM ainda não pintado) e URL `origem` agora encoded. Foco adiado com `setTimeout(0)`; E2E-033 aceita `%2F`.

## Ready for Next Run

- Rodada 2 SHIP; detect seguinte esperado: `phase=E action=await_ci` após `--verify-pass`.
- Phase E ainda precisa de `gh auth` / `GH_TOKEN` para draft PR e CI no HEAD.

## Round 2

- Verdict: SHIP
- Findings: `evidence/peer-review/round-2/review.md`
- Remediado: nenhum (código de produto inalterado; D1-01…D1-05 intactos)
- Lane: `gpt-5.6-sol-medium` no limite; `composer-2.5-fast` + `inherit`

## Round 1

- Verdict: SHIP
- Findings: `evidence/peer-review/round-1/review.md`
- Remediado: URL `pagina` no catálogo; select/nome no checkout; teclado na tabela da retaguarda; encode de `origem`; `SLUG` default MVP1
