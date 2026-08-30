---
status: completed
title: Real-User QA Execution
type: qa-execution
complexity: critical
---

# Task 7: Real-User QA Execution

## Overview

Executa o ciclo planejado na task_06: personas caminham as jornadas reais no
navegador, registram vereditos e bugs, e só então o MVP0 pode ser declarado
verificado. Sem evidência fresca, nenhuma afirmação de “pedido em três
minutos” vale.

<critical>
- ALWAYS READ the in-scope docs/qa/scenarios/ files, open docs/qa/bugs/, and the cycle's charters in docs/qa/charters/ before executing.
- ALWAYS READ `_spec.md` and its catalogs (`_user_stories.md`, `_dx.md`, `_uiux.md` when present, `_tests.md`) before starting
- REFERENCE `_spec.md` Part II for implementation details — do not duplicate here
- FOCUS ON "WHAT" — describe what needs to be accomplished, not how
- MINIMIZE CODE — show code only to illustrate current structure or problem areas
- TESTS REQUIRED — implement every test case assigned in ## Tests
</critical>

<requirements>
- MUST ativar `qa-execution` com `qa-docs-path=docs/qa`.
- MUST caminhar cada charter em persona, pela interface pública, até o estado
  final verdadeiro; atalho de DevTools não conta.
- MUST dirigir o fluxo de maior risco (catálogo → carrinho → checkout →
  Pedido recebido → Meus pedidos) via Playwright com `browser-use:browser`;
  fallback `agent-browser` só se `browser-use:browser` estiver indisponível.
  Não substituir em silêncio por checagens só de shell.
- MUST executar `make test-e2e-web` e `make test-integration`. O alvo
  `make test-e2e-runtime` é harness de daemon Compozy e não se aplica a este
  produto; registrar essa justificativa no relatório e exercitar as rotas
  HTTP de `_dx.md` no lugar.
- MUST ativar `eng-worktree-isolation` se houver concorrência (portas, banco
  e artefatos isolados; L-009).
- MUST registrar defeito reproduzido em
  `docs/qa/bugs/BUG-<YYYYMMDD>-<slug>.md` (dedup primeiro) e ligá-lo aos
  cenários.
- MUST restringir o fix-loop a correções pequenas; regressão red-before /
  green-after; um fix lógico por commit; o restante vai para
  “Decisions for a Human”.
- MUST atualizar vereditos dos cenários e gravar
  `docs/qa/reports/<YYYY-MM-DD>-pedidos-insumos-mvp0.md`.
- MUST sair por `make gate` com evidência fresca. NÃO editar `state.yaml`.
</requirements>

## Subtasks

- [x] 7.1 Ler README, cenários, bugs abertos e charters; criar o relatório
      com a matriz `Pending`.
- [x] 7.2 Confirmar precondições: suíte automática verde e app alcançável.
- [x] 7.3 Caminhar J-produtor-pedido-rapido em viewport móvel até “Pedido
      recebido” e Meus pedidos (meta ≤3 minutos, sem ajuda).
- [x] 7.4 Caminhar J-operador-retaguarda: lista, detalhe, vazio e recusa do
      produtor.
- [x] 7.5 Rodar o tour de cada charter, bordas e lentes experienciais.
- [x] 7.6 Arquivar bugs, aplicar só fixes do governor, reandar jornadas
      impactadas.
- [x] 7.7 Fechar o relatório datado e rodar `make gate`.

## Implementation Details

Feature UI-bearing (`_uiux.md` presente). Isolar ambiente se o loop sinalizar
concorrência. Fixtures apenas; sem dados reais (SD-008). Skills: `qa-execution`.

### Relevant Files

- `docs/qa/scenarios/*.md` — contrato vivo a caminhar.
- `docs/qa/charters/CH-*.md` — sessões desta rodada.
- `docs/qa/templates/report.md`
- `_dx.md` — rotas HTTP para comparação de estado.
- `_tests.md` — E2E-001–010 já implementados nas tasks 03–05; esta tarefa
  não os reimplementa, prova a jornada real.

### Dependent Files

- `docs/qa/reports/<YYYY-MM-DD>-pedidos-insumos-mvp0.md`
- `docs/qa/bugs/BUG-<YYYYMMDD>-<slug>.md` (somente se houver achado)
- `docs/qa/scenarios/*.md` — vereditos e `bug_ids`
- `docs/qa/state.csv` — gerado, não commitado (`.gitignore`)

### Related ADRs

- [ADR-001](adrs/adr-001-fast-non-regulated-order-proof.md) — a sessão deve
  falhar se um produto regulamentado for pedível.

### Web/Docs Impact

- `web/`: none — execução não altera código salvo fixes do governor.
- `packages/site`: none.
- QA impact: walk every scenario minted in task_06 to a recorded verdict;
  failing walk blocks completion until production code is fixed or escalated.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: none no ciclo, salvo regressão do `GatewayErp`.
- Agent manageability: comparar estado persistido via HTTP (`GET /pedidos`,
  `GET /retaguarda/pedidos`) com o que a persona vê no navegador.
- Config lifecycle: none for `config.toml`.

## Deliverables

- Relatório datado com matriz, debriefs e decisões humanas.
- Vereditos de cenário e bugs content-addressed quando couber.
- `make gate` fresco no encerramento.
- Every test case assigned in `## Tests` implemented and passing **(REQUIRED)**

## Tests

Nenhum ID novo de `_tests.md`. A verificação é o ledger de sessões: cada
jornada caminhada por uma persona, com evidência independente após refresh.
Reexecutar `make test-e2e-web` e `make test-integration` como precondição e
como fechamento.

## Success Criteria

- Skill `qa-execution` executada com `qa-docs-path=docs/qa`
- Jornada do produtor confirma pedido em ≤3 minutos sem ajuda, com
  “Pedido recebido” visível
- Operador inspeciona o mesmo pedido e a confirmação aceita
- Produto regulamentado permanece fora do pedido
- Relatório datado existe; `make gate` passou com saída fresca
- Nenhum veredito `Pass` sem evidência independente
