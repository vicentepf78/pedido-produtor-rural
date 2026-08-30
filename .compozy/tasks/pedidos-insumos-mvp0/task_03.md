---
status: pending
title: Catálogo curado e carrinho de convidado
type: fullstack
complexity: high
---

# Task 3: Catálogo curado e carrinho de convidado

## Overview

Entrega a descoberta dos 30 produtos não regulamentados e o carrinho de
convidado com totais simples. É a primeira fatia utilizável pelo produtor e
precisa casar com o wireframe mobile-first antes do checkout.

<critical>
- ALWAYS READ `_spec.md` and its catalogs (`_user_stories.md`, `_dx.md`, `_uiux.md` when present, `_tests.md`) before starting
- REFERENCE `_spec.md` Part II for implementation details — do not duplicate here
- FOCUS ON "WHAT" — describe what needs to be accomplished, not how
- MINIMIZE CODE — show code only to illustrate current structure or problem areas
- TESTS REQUIRED — implement every test case assigned in ## Tests
</critical>

<requirements>
- MUST semear exatamente 30 produtos não regulamentados via Flyway, iguais em
  todos os ambientes (SD-009).
- MUST implementar `ConsultaCatalogo` e as rotas `GET /api/v1/catalogo/produtos`
  e detalhe compatível com `_dx.md`.
- MUST ocultar produto regulamentado do catálogo e negar pedido/link direto
  com `PRODUTO_NAO_ELEGIVEL`.
- MUST listar produto indisponível sem ação de compra ativa
  (`PRODUTO_INDISPONIVEL` ao tentar adicionar).
- MUST implementar carrinho de convidado (`POST /api/v1/carrinhos/convidado/itens`
  e mutações de quantidade/remoção) persistente no mesmo navegador até a
  confirmação ou limpeza dos dados.
- MUST calcular total da linha e do carrinho só com preço unitário atual; sem
  condição de pagamento, desconto ou entrega.
- MUST rejeitar quantidade zero, negativa ou não inteira com
  `QUANTIDADE_INVALIDA` e consolidar linhas do mesmo produto.
- MUST manter 100 itens com quantidades e totais exatos.
- MUST preservar o último estado confirmado do carrinho se a alteração falhar.
- MUST implementar as superfícies S1 e S2 em `frontend/src/features/{catalog,cart}`
  segundo o Visual Contract abaixo.
- MUST NOT criar pedido, autenticação de checkout ou backoffice nesta tarefa.
</requirements>

## Visual Contract

Referência: `references/mvp0-pedidos-insumos.html` (OpenDesign Cloud
`mvp0-pedidos-insumos.html`). Viewport móvel é o normativo; desktop precisa
permanecer utilizável.

| ID | Reference artifact + state | Implementation target + state | Viewport | Fidelity | Authorized differences + authority |
| --- | --- | --- | --- | --- | --- |
| VC-01 | `references/mvp0-pedidos-insumos.html` — Tela 1 Catálogo, estado Resultados com cartões de produto | `/` — catálogo povoado com fixtures | 390×844 | normative | 30 produtos reais no lugar dos 4 de demo; sem barra “Wireframe” / chips de estado; `_uiux.md` + SD-009 |
| VC-02 | mesma referência — Catálogo, Busca vazia / Nenhum resultado com “Limpar busca” | `/` — pesquisa sem correspondência | 390×844 | normative | None |
| VC-03 | mesma referência — cartão `unavailable` (Ureia) com botão Indisponível | `/` — produto indisponível visível | 390×844 | normative | None |
| VC-04 | mesma referência — thumb “Sem imagem” | `/` — produto sem `urlImagem` | 390×844 | normative | Placeholder seguro; sem imagem quebrada; `_uiux.md` EC-2 |
| VC-05 | mesma referência — Tela 2 Carrinho vazio | `/carrinho` — vazio, checkout bloqueado | 390×844 | normative | None |
| VC-06 | mesma referência — Carrinho com linhas, qtd, total da linha e total | `/carrinho` — um ou mais itens | 390×844 | normative | None |
| VC-07 | mesma referência — Catálogo em coluna central | `/` — catálogo utilizável | 1440×900 | adjacent | Layout desktop pode alargar a coluna; hierarquia, rótulos e totais iguais; `_uiux.md` restrição mobile-first |

Diferenças autorizadas globais desta tarefa: omitir a aba “Defensivos” do
wireframe (SD-003 / ADR-001 — produtos regulamentados não entram no catálogo
pedido); navegação real sem o atalho “Backoffice (operador)” na jornada do
produtor.

Evidence for each row: `.compozy/tasks/pedidos-insumos-mvp0/evidence/visual/task_03/<contract-id>/{reference.png,implementation.png,side-by-side.png,diff.png,comparison.json,review.md}`

## Subtasks

- [ ] 3.1 Semear 30 produtos não regulamentados e a consulta pública de catálogo.
- [ ] 3.2 Recusar produto regulamentado, oculto ou indisponível para pedido.
- [ ] 3.3 Implementar carrinho de convidado, consolidação, totais e escala de 100 itens.
- [ ] 3.4 Persistir o carrinho no navegador e preservar estado confirmado em falha.
- [ ] 3.5 Construir S1 e S2 mobile-first a partir do Visual Contract.
- [ ] 3.6 Implementar UT-001–UT-012, IT-001–IT-004 e E2E-001–E2E-004.
- [ ] 3.7 Gerar o pacote de evidência visual de cada linha VC-01–VC-07.

## Implementation Details

`catalog` é dono do produto e da disponibilidade. `cart` lê produto só via
`ConsultaCatalogo` e não cria pedido. Frontend em `features/catalog` e
`features/cart` consome `_dx.md` e não recalcula regra de preço além do
contrato. Imagens: valor de apresentação seguro; Cloudinary está fora do MVP0.

### Relevant Files

- `_spec.md` — `ConsultaCatalogo`, entidade Produto, regras 2–4.
- `_dx.md` — `GET /api/v1/catalogo/produtos`, `POST /api/v1/carrinhos/convidado/itens`.
- `_uiux.md` — S1, S2 e estados.
- `_tests.md` — UT-001–012, IT-001–004, E2E-001–004.
- `references/mvp0-pedidos-insumos.html` — Tela 1 e Tela 2.
- `backend/src/main/java/br/agriplataforma/{catalog,cart}/` — módulos da fundação.

### Dependent Files

- `backend/src/main/resources/db/migration/catalog/` — seed dos 30 produtos.
- `backend/src/main/java/br/agriplataforma/catalog/application/ConsultaCatalogo.java`
- `backend/src/main/java/br/agriplataforma/catalog/api/` — listagem e detalhe.
- `backend/src/main/java/br/agriplataforma/cart/api/` — itens do convidado.
- `frontend/src/features/catalog/` — cartão de produto, busca, categorias.
- `frontend/src/features/cart/` — linha do carrinho e total.
- `frontend/e2e/catalog.spec.ts`, `frontend/e2e/cart.spec.ts` — E2E-001–004.

### Related ADRs

- [ADR-001](adrs/adr-001-fast-non-regulated-order-proof.md) — catálogo
  selecionado sem proteção de cultivos.

### Web/Docs Impact

- `web/`: none — checked surfaces: `web/`; reason: implementação em
  `frontend/src/features/catalog` e `frontend/src/features/cart`.
- `packages/site`: none — checked surfaces: `packages/site/`.
- QA impact: new scenarios — add or reset to `untested`
  `docs/qa/scenarios/CAT-produtor-descobre-produto.md`,
  `docs/qa/scenarios/CAT-produto-indisponivel-ou-regulado.md`,
  `docs/qa/scenarios/CART-monta-e-ajusta.md`,
  `docs/qa/scenarios/CART-convidado-persiste.md`.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: none — checked surfaces: Agrofit, hooks, MCP; reason: seed
  Flyway manual, sem ingestão externa (SD-009).
- Agent manageability: `GET /api/v1/catalogo/produtos`,
  `POST /api/v1/carrinhos/convidado/itens`, erros `PRODUTO_NAO_ELEGIVEL`,
  `PRODUTO_INDISPONIVEL`, `QUANTIDADE_INVALIDA`. Sem CLI/UDS.
- Config lifecycle: none for `config.toml`. Seed via migração, não por
  variável de ambiente de catálogo.

## Deliverables

- Catálogo de 30 produtos e superfícies S1/S2 mobile-first.
- Carrinho de convidado com totais e persistência no navegador.
- Every test case assigned in `## Tests` implemented and passing **(REQUIRED)**
- Every Visual Contract row has a durable passing evidence bundle **(REQUIRED)**

## Tests

Cases assigned from `_tests.md`, the test contract — read each ID's full definition there before writing tests.

- [ ] UT-001, UT-002, UT-003, UT-004, UT-005 — descoberta, elegibilidade, vazio, indisponível, imagem.
- [ ] UT-006, UT-007, UT-008, UT-009, UT-010, UT-011, UT-012 — totais, mutação, quantidade, consolidação, escala, interrupção.
- [ ] IT-001, IT-002, IT-003, IT-004 — API de catálogo, produto oculto, carrinho e persistência.
- [ ] E2E-001, E2E-002, E2E-003, E2E-004 — jornadas móveis de catálogo e carrinho.

## Success Criteria

- Every assigned test case implemented and passing
- Nenhum produto regulamentado é listado ou pedível
- Checkout permanece bloqueado com carrinho vazio
- Every Visual Contract row is `PASS` with zero unresolved blocking divergence
