---
status: pending
title: Carrossel, busca composta e Carregar mais
type: frontend
complexity: high
---

# Task 3: Carrossel, busca composta e Carregar mais

## Overview

Entrega a superfície S1 da loja: carrossel fechado Todos / Sementes /
Fertilizantes / Correção, busca que soma à categoria, tamanho de bloco
10 / 15 / 30 / 50 e Carregar mais. Depende da API paginada da task_01 e
do topo da task_02; é a descoberta que o produtor usa antes do detalhe,
do carrinho e do checkout.

<critical>
- ALWAYS READ `_spec.md` and its catalogs (`_user_stories.md`, `_dx.md`, `_uiux.md` when present, `_tests.md`) before starting
- REFERENCE `_spec.md` Part II for implementation details — do not duplicate here
- FOCUS ON "WHAT" — describe what needs to be accomplished, not how
- MINIMIZE CODE — show code only to illustrate current structure or problem areas
- TESTS REQUIRED — implement every test case assigned in ## Tests
</critical>

<requirements>
- MUST implementar a superfície S1 em `frontend/src/features/catalog`
  segundo o Visual Contract e `_uiux.md` (US-001–US-003).
- MUST exibir carrossel fechado com exatamente Todos, Sementes,
  Fertilizantes e Correção, cada card com foto de demonstração e rótulo;
  o card escolhido permanece marcado.
- MUST NOT acrescentar Defensivos nem qualquer outra categoria ao
  carrossel (ADR-003).
- MUST consultar `GET /api/v1/catalogo/produtos` com `categoria`,
  `consulta`, `pagina` e `tamanhoPagina`; categoria e busca somam.
- MUST tratar `consulta` ausente ou em branco como recorte só da
  categoria atual — não como página vazia.
- MUST oferecer Limpar filtros: categoria volta a Todos, busca vazia,
  listagem sem recorte.
- MUST usar tamanho padrão 10 e aceitar só 10, 15, 30 e 50; valor
  inválido permanece o último válido ou o padrão 10.
- MUST recomeçar a listagem do início do recorte ao mudar o tamanho;
  MUST NOT somar o novo tamanho ao que já estava na tela.
- MUST avançar com Carregar mais (`pagina+1`, mesmos filtros e tamanho),
  acrescentando o próximo bloco sem remover os itens visíveis.
- MUST NOT implementar scroll infinito automático.
- MUST ocultar Carregar mais quando `itens` acumulados `>= total`, no
  último bloco parcial e em recorte vazio.
- MUST preservar categoria, busca e tamanho ao voltar do detalhe do
  produto (query da página, Parte II).
- MUST manter carrossel e listagem utilizáveis para convidado e sessão
  expirada; MUST negar deep link de produto regulamentado ou oculto com
  `PRODUTO_NAO_ELEGIVEL` sem revelar o item.
- MUST listar só os 30 produtos semeados; MUST NOT inventar SKUs do
  protótipo HTML.
- MUST seguir a skill `coding-guidelines`.
</requirements>

## Visual Contract

Referência: `docs/design/opendesign/pedidos-insumos-mvp1/loja-insumos-agricolas.html`
(S1). Viewport móvel 390×844 é o normativo; 1440×900 cobre a grade
adjacente. Enumerar estados; não usar “todos os estados”.

| ID    | Reference artifact + state           | Implementation target + state | Viewport | Fidelity  | Authorized differences + authority |
| ----- | ------------------------------------ | ----------------------------- | -------- | --------- | ---------------------------------- |
| VC-01 | `loja-insumos-agricolas.html` S1 — carrossel com categoria marcada | `/catalogo` — Todos ou Fertilizantes marcado, listagem do recorte | 390×844 | normative | 30 produtos semeados no lugar dos SKUs inventados do HTML; thumbs de categoria podem ser SVG/demo em `frontend/public/media/categorias/`; sem rótulo de tela “S1 — Catálogo”; `_uiux.md` S1 + ADR-003 + ADR-006 |
| VC-02 | mesma referência S1 — busca composta (categoria + nome) | `/catalogo` — Fertilizantes + `consulta=ureia` | 390×844 | normative | mesmos SKUs semeados; sem rótulo “S1 — Catálogo”; `_uiux.md` S1 + US-002 |
| VC-03 | mesma referência S1 — vazio com Limpar filtros | `/catalogo` — recorte sem correspondência | 390×844 | normative | cópia “Limpar filtros” (não “Limpar busca” do MVP 0); sem rótulo “S1 — Catálogo”; `_uiux.md` S1 + US-002.EC-2 |
| VC-04 | mesma referência S1 — Carregar mais visível | `/catalogo` — Todos, tamanho 10, primeiro bloco, botão visível | 390×844 | normative | paginação real via `pagina`; sem scroll infinito; sem rótulo “S1 — Catálogo”; ADR-003 + ADR-005 |
| VC-05 | mesma referência S1 — fim da lista | `/catalogo` — recorte esgotado, sem Carregar mais | 390×844 | normative | botão ausente quando `itens.length >= total`; sem rótulo “S1 — Catálogo”; US-003.AC-4 |
| VC-06 | mesma referência S1 — controle de tamanho 10 / 15 / 30 / 50 | `/catalogo` — controle visível, padrão 10 | 390×844 | normative | só os quatro valores; sem rótulo “S1 — Catálogo”; ADR-005 |
| VC-07 | mesma referência S1 — grade de produtos povoada | `/catalogo` — carrossel + grade utilizável | 1440×900 | adjacent | grade pode alargar; hierarquia, rótulos e controles iguais ao móvel; 30 produtos semeados; sem rótulo “S1 — Catálogo”; `_uiux.md` restrição mobile-first |

Diferenças autorizadas globais desta tarefa: o HTML de referência usa
SKUs e thumbs SVG inventados — a implementação MUST usar os 30 produtos
semeados; imagens de categoria MAY ser os assets de demonstração em
`frontend/public/media/categorias/`; MUST NOT reproduzir o rótulo de
tela “S1 — Catálogo” do protótipo.

Evidence for each row: `.compozy/tasks/pedidos-insumos-mvp1/evidence/visual/task_03/<id>/{reference.png,implementation.png,side-by-side.png,diff.png,comparison.json,review.md}`

## Subtasks

- [ ] 3.1 Substituir as abas derivadas dos produtos pelo carrossel
      fechado Todos / Sementes / Fertilizantes / Correção, com foto de
      demonstração, rótulo, card marcado e deslocamento horizontal.
- [ ] 3.2 Enviar categoria e busca compostas a
      `GET /api/v1/catalogo/produtos` e recarregar a primeira página a
      cada mudança estável de filtro.
- [ ] 3.3 Exibir Limpar filtros quando categoria ≠ Todos ou busca
      preenchida; restaurar Todos, busca vazia e o conjunto elegível.
- [ ] 3.4 Entregar o controle de tamanho 10 / 15 / 30 / 50 e recomeçar
      a lista ao trocar o valor.
- [ ] 3.5 Entregar Carregar mais por `pagina`, sem scroll infinito, sem
      duplicar item e sem o botão no fim da lista ou no vazio.
- [ ] 3.6 Preservar categoria, busca e tamanho ao abrir o detalhe e
      voltar a `/catalogo`.
- [ ] 3.7 Cobrir vazio, foto de categoria ausente, erro com tentar de
      novo e deep link `PRODUTO_NAO_ELEGIVEL`.
- [ ] 3.8 Implementar E2E-001–E2E-028.
- [ ] 3.9 Gerar o pacote de evidência visual de cada linha VC-01–VC-07.
- [ ] 3.10 Resetar os cenários QA desta fatia para `untested`.

## Implementation Details

A task_01 já entrega o contrato HTTP de catálogo. Esta tarefa só
consome `_dx.md`: `categoria` (`Todos` / ausente = todos os elegíveis;
`Sementes` / `Fertilizantes` / `Correção`), `consulta` em branco lista
o recorte, `pagina` a partir de 1, `tamanhoPagina` em `{10,15,30,50}`.
Carregar mais é `pagina+1` com os mesmos filtros. Não há endpoint de
categorias — o carrossel é conjunto fechado no cliente, com mídia
estática em `frontend/public/media/categorias/` (ADR-006). Filtros e
tamanho ficam na query da página para sobreviver ao detalhe.

Hoje `PaginaCatalogo.tsx` deriva abas dos itens, filtra categoria no
cliente e faz um fetch com `tamanhoPagina=30`. `api.ts` ainda não envia
`categoria`. Esses dois comportamentos são o alvo de substituição.
`CartaoProduto` evolui o visual, não a regra. O topo vem da task_02;
esta tarefa não reconstrói o shell.

Padrões e interfaces: `_spec.md` Parte II (`ConsultaCatalogo`,
`BuscaProduto`, `Paginacao`). Skill: `coding-guidelines`.

### Relevant Files

- `_spec.md` — Parte I descoberta; Parte II listagem, query da página,
  mídia estática de categoria.
- `_user_stories.md` — US-001, US-002, US-003 e casos de borda.
- `_dx.md` — `GET /api/v1/catalogo/produtos` e erros
  `CATEGORIA_INVALIDA`, `TAMANHO_PAGINA_INVALIDO`, `PRODUTO_NAO_ELEGIVEL`.
- `_uiux.md` — S1; `CarrosselCategoria`, `ControleTamanhoLista`,
  `BotaoCarregarMais`.
- `_tests.md` — E2E-001–E2E-028.
- `docs/design/opendesign/pedidos-insumos-mvp1/loja-insumos-agricolas.html`
  — S1 autoritativo.
- `frontend/src/features/catalog/PaginaCatalogo.tsx` — abas e fetch
  único atuais.
- `frontend/src/features/catalog/api.ts` — `tamanhoPagina=30` fixo, sem
  `categoria`.
- `frontend/src/features/catalog/CartaoProduto.tsx` — cartão da listagem.
- `frontend/e2e/catalog.spec.ts` — suíte E2E do MVP 0 a substituir.

### Dependent Files

- `frontend/src/features/catalog/PaginaCatalogo.tsx` — S1: carrossel,
  busca composta, tamanho, Carregar mais, query persistente.
- `frontend/src/features/catalog/api.ts` — `categoria`, `consulta`,
  `pagina`, `tamanhoPagina`.
- `frontend/src/features/catalog/CarrosselCategoria.tsx` — novo; cards
  com foto de demonstração.
- `frontend/src/features/catalog/ControleTamanhoLista.tsx` — novo;
  10 / 15 / 30 / 50.
- `frontend/src/features/catalog/BotaoCarregarMais.tsx` — novo; ausente
  quando a lista acabou.
- `frontend/src/features/catalog/CartaoProduto.tsx` — visual da grade S1.
- `frontend/public/media/categorias/` — thumbs de demonstração do
  carrossel (ainda não existem; hoje só `media/produtos/`).
- `frontend/e2e/catalog.spec.ts` — E2E-001–E2E-028.
- `frontend/src/App.tsx` — `/catalogo` e `/catalogo/:idProduto` já
  existem; query de filtros precisa sobreviver à rota de detalhe.

### Related ADRs

- [ADR-003: Carrossel de categorias, busca composta e Carregar mais](adrs/adr-003-descoberta-por-categoria-busca-e-carregar-mais.md) — quatro categorias, filtros que somam, Carregar mais em vez de scroll infinito, sem Defensivos.
- [ADR-005: Retorno após Entrar, Pedido visível e tamanho da lista](adrs/adr-005-retorno-pos-entrar-e-tamanho-da-lista.md) — bloco padrão 10 e opções 15 / 30 / 50; trocar o tamanho recomeça a lista.
- [ADR-006: Topo do operador, mídia de demonstração e checkout vazio](adrs/adr-006-topo-do-operador-e-midia-de-demonstracao.md) — fotos do carrossel são demonstração, não acervo da revenda.

### Web/Docs Impact

- `web/`: none — checked surfaces: `web/`; reason: esta fatia é a loja
  em `frontend/src/features/catalog`, não o site Compozy.
- `packages/site`: none — checked surfaces: `packages/site/`.
- QA impact: reset to `untested`
  `docs/qa/scenarios/CAT-produtor-descobre-produto.md`; new scenario —
  add `docs/qa/scenarios/CAT-carrossel-e-carregar-mais.md` with
  `qa_status: untested`.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: none — checked surfaces: Agrofit, hooks, MCP,
  manifestos; reason: carrossel fechado e mídia estática, sem ingestão
  de catálogo.
- Agent manageability: none nesta tarefa — o HTTP de catálogo
  (`GET /api/v1/catalogo/produtos` com `categoria`, `consulta`,
  `pagina`, `tamanhoPagina` e erros determinísticos) já está na
  task_01. Sem CLI/UDS.
- Config lifecycle: none for `config.toml`.

## Deliverables

- Superfície S1 com carrossel, busca composta, Limpar filtros, controle
  de tamanho e Carregar mais, consumindo a API da task_01.
- Query de filtros persistente ao voltar do detalhe.
- Every test case assigned in `## Tests` implemented and passing **(REQUIRED)**
- Every Visual Contract row has a durable passing evidence bundle **(REQUIRED)**

## Tests

Cases assigned from `_tests.md`, the test contract — read each ID's full definition there before writing tests.

- [ ] E2E-001, E2E-002, E2E-003 — carrossel com as quatro categorias,
      Fertilizantes marcado, sem Defensivos nem produto regulamentado.
- [ ] E2E-004, E2E-005, E2E-006, E2E-007 — foto de categoria ausente,
      categoria vazia, toques rápidos, overflow horizontal com controle.
- [ ] E2E-008, E2E-009, E2E-010, E2E-011 — convidado ou sessão expirada,
      deep link `PRODUTO_NAO_ELEGIVEL`, interrupção ao trocar categoria,
      conjunto fechado de quatro categorias.
- [ ] E2E-012, E2E-013, E2E-014, E2E-015, E2E-016 — busca composta,
      Limpar filtros, texto hostil, busca em branco, estado vazio.
- [ ] E2E-017, E2E-018, E2E-019, E2E-020 — três blocos 10+10+10, reset
      ao mudar tamanho (busca permanece ao trocar categoria), Carregar
      mais duplicado, dois textos em sequência.
- [ ] E2E-021, E2E-022, E2E-023 — volta do detalhe com filtros, primeira
      visita (Todos, busca vazia, tamanho 10), controle 10 / 15 / 30 / 50.
- [ ] E2E-024, E2E-025, E2E-026, E2E-027, E2E-028 — tamanho 50 no
      conjunto de 30, último bloco parcial, interrupção ao carregar
      mais, tamanho inválido, recorte vazio sem Carregar mais.

## Success Criteria

- Every assigned test case implemented and passing
- Carrossel sem Defensivos; listagem sem scroll infinito
- Trocar o tamanho recomeça a lista; voltar do detalhe mantém os filtros
- Every Visual Contract row is `PASS` with zero unresolved blocking divergence
