---
status: pending
title: Real-User QA Execution
type: qa-execution
complexity: critical
---

# Task 6: Real-User QA Execution

## Overview

Executa o ciclo planejado na task_05: personas caminham as jornadas reais
no navegador, registram vereditos e bugs, e só então o MVP 1 pode ser
declarado verificado. Sem evidência fresca, nenhuma afirmação de
descoberta rápida ou login no topo vale.

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
- MUST caminhar cada charter em persona, pela interface pública, até o
  estado final verdadeiro; atalho de DevTools não conta.
- MUST dirigir o fluxo de maior risco (catálogo com carrossel →
  Carregar mais → carrinho → Entrar no topo → checkout → Pedido
  recebido → retaguarda) via Playwright com `browser-use:browser`;
  fallback `agent-browser` só se `browser-use:browser` estiver
  indisponível. Não substituir em silêncio por checagens só de shell.
- MUST executar `make test-e2e-web` e `make test-integration`. O alvo
  `make test-e2e-runtime` é harness de daemon Compozy e não se aplica a
  este produto; registrar essa justificativa no relatório e exercitar
  as rotas HTTP de `_dx.md` no lugar (incluindo `GET sessao` e
  `tamanhoPagina=24` → `TAMANHO_PAGINA_INVALIDO`).
- MUST ativar `eng-worktree-isolation` se houver concorrência (portas,
  banco e artefatos isolados; L-009).
- MUST registrar defeito reproduzido em
  `docs/qa/bugs/BUG-<YYYYMMDD>-<slug>.md` (dedup primeiro) e ligá-lo
  aos cenários.
- MUST restringir o fix-loop a correções pequenas; regressão
  red-before / green-after; um fix lógico por commit; o restante vai
  para “Decisions for a Human”.
- MUST atualizar vereditos dos cenários e gravar
  `docs/qa/reports/<YYYY-MM-DD>-pedidos-insumos-mvp1.md`.
- MUST sair por `make gate` com evidência fresca. NÃO editar
  `state.yaml`.
</requirements>

## Subtasks

- [ ] 6.1 Ler README, cenários, bugs abertos e charters; criar o
      relatório com a matriz `Pending`.
- [ ] 6.2 Confirmar precondições: suíte automática verde e app
      alcançável com perfil `local`.
- [ ] 6.3 Caminhar a jornada do produtor em viewport móvel: carrossel,
      busca composta, tamanho 10, Carregar mais, Entrar, origem,
      Pedido recebido.
- [ ] 6.4 Caminhar operador: Entrar pela loja → retaguarda, topo
      próprio, lista e detalhe; produtor recusado.
- [ ] 6.5 Rodar o tour de cada charter, bordas e lentes experienciais.
- [ ] 6.6 Arquivar bugs, aplicar só fixes do governor, reandar
      jornadas impactadas.
- [ ] 6.7 Fechar o relatório datado e rodar `make gate`.

## Implementation Details

Feature UI-bearing (`_uiux.md` presente). Isolar ambiente se o loop
sinalizar concorrência. Fixtures apenas; sem dados reais (SD-008).
Skills: `qa-execution`.

### Relevant Files

- `docs/qa/scenarios/*.md` — contrato vivo a caminhar.
- `docs/qa/charters/CH-*.md` — sessões desta rodada.
- `docs/qa/templates/report.md`
- `_dx.md` — rotas HTTP para comparação de estado.
- `_tests.md` — E2E já implementados nas tasks 02–04; esta tarefa não
  os reimplementa, prova a jornada real.

### Dependent Files

- `docs/qa/reports/<YYYY-MM-DD>-pedidos-insumos-mvp1.md`
- `docs/qa/bugs/BUG-<YYYYMMDD>-<slug>.md` (somente se houver achado)
- `docs/qa/scenarios/*.md` — vereditos e `bug_ids`

### Related ADRs

- [ADR-003](adrs/adr-003-descoberta-por-categoria-busca-e-carregar-mais.md)
- [ADR-007](adrs/adr-007-operador-na-entrar-da-loja.md)

### Web/Docs Impact

- `web/`: none — checked surfaces: sem `web/` Compozy.
- `packages/site`: none — checked.
- QA impact: esta tarefa fecha os vereditos dos cenários do ciclo.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: none — checked.
- Agent manageability: comparar estado HTTP (`GET sessao`, catálogo,
  retaguarda) com o que a persona viu na UI.
- Config lifecycle: none — checked.

## Deliverables

- Relatório datado em `docs/qa/reports/`.
- Vereditos de cenário atualizados.
- Bugs registrados só se reproduzidos.
- `make gate` verde com saída fresca.

## Tests

Esta tarefa não implementa IDs de `_tests.md`. Executa o contrato vivo
de `docs/qa/` e a suíte já entregue pelas tarefas 01–04.

## Success Criteria

- Cada charter caminhado até estado final verdadeiro.
- Relatório datado com matriz de vereditos.
- `make gate` passou com evidência desta execução.
- `state.yaml` não foi editado à mão.
