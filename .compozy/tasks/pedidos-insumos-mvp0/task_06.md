---
status: completed
title: QA Plan and Session Charters
type: qa-report
complexity: high
---

# Task 6: QA Plan and Session Charters

## Overview

Planeja o ciclo de QA do MVP0 como documentação viva em `docs/qa/`: jornadas,
cenários e charters de sessão. Sem este plano, a execução não tem persona,
fluxo nem superfície de entrada.

<critical>
- ALWAYS READ `_spec.md`, every ADR, and every per-task memory file before planning.
- ALWAYS READ `_spec.md` and its catalogs (`_user_stories.md`, `_dx.md`, `_uiux.md` when present, `_tests.md`) before starting
- REFERENCE `_spec.md` Part II for implementation details — do not duplicate here
- FOCUS ON "WHAT" — describe what needs to be accomplished, not how
- MINIMIZE CODE — show code only to illustrate current structure or problem areas
- TESTS REQUIRED — implement every test case assigned in ## Tests
</critical>

<requirements>
- MUST ativar a skill `qa-report` com `qa-docs-path=docs/qa` (a árvore já
  existe; completar o que faltar, inclusive `personas.md`).
- MUST mapear jornadas com fluxograma (entrada → ações → abandono → estado
  final verdadeiro) antes de mintar cenários.
- MUST cobrir toda superfície pública tocada pelas tarefas 01–05: rotas web
  do produtor e do operador, rotas HTTP de `_dx.md`, health, e chaves de
  configuração de sessão/banco — como `entry_points` dos cenários, não como
  casos soltos.
- MUST derivar charters de sessão (persona + jornada + um tour + time-box)
  no tier targeted, mais uma jornada canário adjacente.
- MUST deixar `qa_status: untested` em todo cenário novo ou resetado.
- MUST NOT criar árvore `qa/` por rodada, `TC-*`, nem `verification-report.md`.
</requirements>

## Subtasks

- [x] 6.1 Ler `docs/qa/README.md`, templates e bugs abertos; criar `personas.md`.
- [x] 6.2 Escrever `docs/qa/journeys/J-produtor-pedido-rapido.md` e
      `docs/qa/journeys/J-operador-retaguarda.md` com Mermaid e abandono.
- [x] 6.3 Mintar ou atualizar os cenários listados em Deliverables, com
      `entry_points` HTTP e web.
- [x] 6.4 Escrever charters em `docs/qa/charters/` (targeted + canário).
- [x] 6.5 Mapear hot spots de regressão das invariantes da Parte II e do
      ADR-001 para a seleção de charters.
- [x] 6.6 Validar completeza do ciclo (jornada com charter, cenário com id,
      taxonomia considerada).

## Implementation Details

A árvore `docs/qa/` já tem README e templates; `journeys/`, `scenarios/`,
`charters/`, `bugs/` e `reports/` estão vazios (só `.gitkeep`). Códigos de
área: AUTH, CAT, CART, CHK, ORD, INT. Skills: `qa-report`.

### Relevant Files

- `docs/qa/README.md` — códigos de área e contrato da árvore.
- `docs/qa/templates/{scenario,charter,bug,report}.md`
- `_spec.md` Parte II — invariantes e superfícies.
- `_dx.md` — rotas e erros.
- `_uiux.md` — S1–S6.
- `adrs/adr-001-fast-non-regulated-order-proof.md`

### Dependent Files

- `docs/qa/personas.md`
- `docs/qa/journeys/J-produtor-pedido-rapido.md`
- `docs/qa/journeys/J-operador-retaguarda.md`
- `docs/qa/scenarios/CAT-produtor-descobre-produto.md`
- `docs/qa/scenarios/CAT-produto-indisponivel-ou-regulado.md`
- `docs/qa/scenarios/CART-monta-e-ajusta.md`
- `docs/qa/scenarios/CART-convidado-persiste.md`
- `docs/qa/scenarios/AUTH-cadastro-e-sessao.md`
- `docs/qa/scenarios/CHK-identificacao-tardia.md`
- `docs/qa/scenarios/CHK-erros-validacao.md`
- `docs/qa/scenarios/ORD-confirma-e-revisita.md`
- `docs/qa/scenarios/ORD-acesso-negado.md`
- `docs/qa/scenarios/INT-mock-erp-aceita.md`
- `docs/qa/scenarios/ORD-backoffice-lista.md`
- `docs/qa/scenarios/ORD-backoffice-vazio-ou-negado.md`
- `docs/qa/charters/CH-*.md`

### Related ADRs

- [ADR-001](adrs/adr-001-fast-non-regulated-order-proof.md) — hipótese de
  pedido rápido e exclusão de regulamentados.

### Web/Docs Impact

- `web/`: none — checked surfaces: `web/`; reason: planejamento QA não altera
  o SPA.
- `packages/site`: none.
- QA impact: this task *creates* the living scenario files above; all start
  `untested` for task_07.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: none — planejamento.
- Agent manageability: cada cenário deve citar as rotas HTTP de `_dx.md` como
  `entry_points` ao lado das rotas web.
- Config lifecycle: none for `config.toml`. Documentar no README de QA os
  comandos `make test`, `make test-integration` e `make test-e2e-web`.

## Deliverables

- Personas, duas jornadas, cenários content-addressed e charters do ciclo.
- Mapa de hot spots (idempotência, produto regulamentado, isolamento de
  pedido, mock que não muta pedido).
- Every test case assigned in `## Tests` implemented and passing **(REQUIRED)**

## Tests

Esta tarefa não recebe IDs de `_tests.md`. A verificação é a completeza do
ciclo `qa-report`: toda jornada com fluxograma e charter; todo cenário com
id, jornada e `qa_status: untested`.

## Success Criteria

- Skill `qa-report` executada com `qa-docs-path=docs/qa`
- Jornadas do produtor e do operador publicadas com abandono
- Cenários das tarefas 01–05 existem e estão `untested`
- Charters cobrem o tier targeted e um canário adjacente
