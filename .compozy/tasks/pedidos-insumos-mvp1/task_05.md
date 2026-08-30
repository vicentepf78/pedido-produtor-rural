---
status: pending
title: QA Plan and Session Charters
type: qa-report
complexity: high
---

# Task 5: QA Plan and Session Charters

## Overview

Planeja o ciclo de QA do MVP 1 na árvore viva `docs/qa/`: atualiza
jornadas, mint ou reseta cenários e escreve charters de sessão para a
loja com topo, carrossel e login próprio. Sem este plano a execução não
tem persona nem superfície de entrada.

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
  existe do MVP 0; completar o que faltar).
- MUST mapear jornadas com fluxograma (entrada → ações → abandono →
  estado final verdadeiro) antes de mintar cenários.
- MUST cobrir toda superfície pública tocada pelas tarefas 01–04: rotas
  web novas (`/entrar`, `/cadastro`), rotas HTTP novas ou alteradas de
  `_dx.md` (`GET sessao`, `categoria`, `tamanhoPagina`), health e chaves
  de sessão — como `entry_points` dos cenários, não como casos soltos.
- MUST derivar charters (persona + jornada + um tour + time-box) no tier
  targeted, mais uma jornada canário adjacente.
- MUST deixar `qa_status: untested` em todo cenário novo ou resetado.
- MUST NOT criar árvore `qa/` por rodada, `TC-*`, nem
  `verification-report.md`.
- MUST NOT implementar código de produto nesta tarefa.
</requirements>

## Subtasks

- [ ] 5.1 Ler `docs/qa/README.md`, templates, bugs abertos e cenários do
      MVP 0; anotar o que resetar.
- [ ] 5.2 Atualizar jornadas do produtor (carrossel, Entrar no topo,
      retorno à origem) e do operador (topo próprio).
- [ ] 5.3 Mintar ou resetar os cenários em Deliverables, com
      `entry_points` HTTP e web.
- [ ] 5.4 Escrever charters em `docs/qa/charters/` (targeted + canário).
- [ ] 5.5 Mapear hot spots das invariantes da Parte II e ADR-003 / ADR-007
      para a seleção de charters.
- [ ] 5.6 Validar completeza do ciclo (jornada com charter, cenário com
      id, taxonomia considerada).

## Implementation Details

A árvore `docs/qa/` já tem personas e jornadas do MVP 0. Este ciclo
acrescenta descoberta paginada, telas de Entrar e visual único da
retaguarda. Códigos de área: AUTH, CAT, CART, CHK, ORD, INT, NAV.
Skills: `qa-report`.

### Relevant Files

- `docs/qa/README.md` — códigos de área e contrato da árvore.
- `docs/qa/templates/{scenario,charter,bug,report}.md`
- `_spec.md` Parte II — invariantes e superfícies.
- `_dx.md` — rotas e erros.
- `_uiux.md` — S1–S11.
- `adrs/adr-003-descoberta-por-categoria-busca-e-carregar-mais.md`
- `adrs/adr-007-operador-na-entrar-da-loja.md`

### Dependent Files

- `docs/qa/personas.md`
- `docs/qa/journeys/` — jornadas atualizadas ou novas
- `docs/qa/scenarios/CAT-*.md`, `AUTH-*.md`, `CART-*.md`, `CHK-*.md`,
  `ORD-*.md` e cenários novos de NAV / retaguarda visual
- `docs/qa/charters/CH-*.md`

### Related ADRs

- [ADR-003](adrs/adr-003-descoberta-por-categoria-busca-e-carregar-mais.md)
- [ADR-007](adrs/adr-007-operador-na-entrar-da-loja.md)

### Web/Docs Impact

- `web/`: none — checked surfaces: este repo não tem `web/` Compozy.
- `packages/site`: none — checked; reason: não há site de docs gerado.
- QA impact: esta tarefa **é** o reset/mint dos cenários; não implementa
  comportamento de produto.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: none — checked; QA não abre pontos de extensão.
- Agent manageability: os `entry_points` dos cenários devem listar as
  rotas HTTP de `_dx.md` (incluindo `GET /api/v1/autenticacao/sessao`).
- Config lifecycle: none — checked; sem chave nova.

## Deliverables

- Jornadas atualizadas com abandono e estado final verdadeiro.
- Cenários novos ou resetados em `untested` cobrindo CAT paginado,
  AUTH tela própria, NAV topo, CHK/ORD e retaguarda visual.
- Charters targeted + um canário.
- Nenhum código de produto.

## Tests

Esta tarefa não implementa IDs de `_tests.md`. A cobertura automática
já foi atribuída às tarefas 01–04. O plano de sessão deve **citar** as
jornadas que esses E2E exercitam, sem reatribuir IDs.

## Success Criteria

- Toda superfície pública das tarefas 01–04 aparece como `entry_point`
  de algum cenário.
- Todo cenário no ciclo está `untested` ou é explicitamente herdado com
  justificativa.
- Charters nomeiam persona, jornada, tour e time-box.
- Nenhuma árvore `qa/` paralela foi criada.
