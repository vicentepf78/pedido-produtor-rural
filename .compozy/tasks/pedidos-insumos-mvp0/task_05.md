---
status: completed
title: Mock ERP e backoffice somente leitura
type: fullstack
complexity: high
---

# Task 5: Mock ERP e backoffice somente leitura

## Overview

Liga a confirmação simulada depois do pedido local e entrega a lista/detalhe
somente leitura para o operador da revenda. Fecha a fatia vertical do MVP0:
o produtor confirma e o operador inspeciona o resultado.

<critical>
- ALWAYS READ `_spec.md` and its catalogs (`_user_stories.md`, `_dx.md`, `_uiux.md` when present, `_tests.md`) before starting
- REFERENCE `_spec.md` Part II for implementation details — do not duplicate here
- FOCUS ON "WHAT" — describe what needs to be accomplished, not how
- MINIMIZE CODE — show code only to illustrate current structure or problem areas
- TESTS REQUIRED — implement every test case assigned in ## Tests
</critical>

<requirements>
- MUST implementar `GatewayErp` em `order` e somente `GatewayErpSimulado` em
  `integration` (SD-004).
- MUST persistir o pedido local antes de receber a confirmação aceita; o mock
  NÃO DEVE criar, alterar ou apagar pedido (invariantes 6 e SD-005).
- MUST aceitar sempre de forma síncrona, sem rede, segredo, retry ou broker.
- MUST implementar `GET /api/v1/retaguarda/pedidos` (e detalhe somente leitura)
  delimitado ao tenant configurado.
- MUST negar papel `PRODUTOR` no backoffice (`UT-025` / `IT-010`).
- MUST mostrar lista vazia explícita quando não houver pedidos.
- MUST incluir no resumo: identificador, produtor, total, criação e
  confirmação aceita.
- MUST construir S6 segundo o Visual Contract; operador não edita pedido nem
  catálogo.
- MUST NOT introduzir ERP real, WhatsApp, e-mail ou reserva de estoque.
</requirements>

## Visual Contract

Referência: `references/mvp0-pedidos-insumos.html` — Tela 6.

| ID | Reference artifact + state | Implementation target + state | Viewport | Fidelity | Authorized differences + authority |
| --- | --- | --- | --- | --- | --- |
| VC-01 | Tela 6 — lista com id, produtor, total, data, pills Recebido/Aceita | `/retaguarda/pedidos` — lista povoada | 390×844 | normative | Acesso só com sessão de operador; sem atalho fantasma na nav do produtor; `_uiux.md` S6 + US-005 |
| VC-02 | Tela 6 — estado vazio explícito (espelhar `status-box` do catálogo/carrinho) | `/retaguarda/pedidos` — nenhum pedido | 390×844 | normative | Copy explícita de vazio; `_user_stories.md` US-005 EC-2 |
| VC-03 | Detalhe somente leitura com banner “Snapshot imutável” | `/retaguarda/pedidos/{idPedido}` | 390×844 | normative | None |
| VC-04 | Catálogo “Acesso negado” / permissão | produtor abre `/retaguarda/pedidos` | 390×844 | normative | Copy de permissão de operador; não vazar lista; `_dx.md` + US-005 EC-1 |
| VC-05 | Tela 6 em coluna central | `/retaguarda/pedidos` utilizável | 1440×900 | adjacent | Tabela/lista pode usar mais largura; campos iguais; `_uiux.md` |

Evidence for each row: `.compozy/tasks/pedidos-insumos-mvp0/evidence/visual/task_05/<contract-id>/{reference.png,implementation.png,side-by-side.png,diff.png,comparison.json,review.md}`

## Subtasks

- [x] 5.1 Implementar `GatewayErpSimulado` atrás da porta de `order`.
- [x] 5.2 Registrar confirmação aceita sem mutar o snapshot do pedido.
- [x] 5.3 Publicar listagem e detalhe de retaguarda por tenant e papel.
- [x] 5.4 Construir S6 somente leitura.
- [x] 5.5 Implementar UT-021, UT-024, UT-025, IT-007, IT-009, IT-010, E2E-009, E2E-010.
- [x] 5.6 Gerar evidência visual de VC-01–VC-05.

## Implementation Details

`integration` depende só do contrato público de `order`. A raiz de composição
liga o adaptador. `backoffice` lê resumos; não escreve pedido. Frontend em
`features/backoffice-orders`. Fakes permitidos só na porta de I/O do mock.

### Relevant Files

- `_spec.md` — `GatewayErp`, módulo `integration`/`backoffice`, invariante 6.
- `_dx.md` — `GET /api/v1/retaguarda/pedidos`.
- `_uiux.md` — S6.
- `_user_stories.md` — US-005.
- `adrs/adr-001-fast-non-regulated-order-proof.md` — confirmação simulada fora
  da autoridade do pedido.

### Dependent Files

- `backend/src/main/java/br/agriplataforma/order/domain/GatewayErp.java`
- `backend/src/main/java/br/agriplataforma/integration/infrastructure/GatewayErpSimulado.java`
- `backend/src/main/java/br/agriplataforma/backoffice/api/` — retaguarda.
- `frontend/src/features/backoffice-orders/`
- `frontend/e2e/backoffice-orders.spec.ts`

### Related ADRs

- [ADR-001](adrs/adr-001-fast-non-regulated-order-proof.md) — mock isolado da
  venda regulamentada e do ERP real.

### Web/Docs Impact

- `web/`: none — checked surfaces: `web/`; reason: UI em
  `frontend/src/features/backoffice-orders`.
- `packages/site`: none — checked surfaces: `packages/site/`.
- QA impact: new scenarios — add or reset to `untested`
  `docs/qa/scenarios/INT-mock-erp-aceita.md`,
  `docs/qa/scenarios/ORD-backoffice-lista.md`,
  `docs/qa/scenarios/ORD-backoffice-vazio-ou-negado.md`.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: `GatewayErp` é o único ponto futuro; o mock é a única
  implementação no MVP0. Sem MCP/hooks.
- Agent manageability: `GET /api/v1/retaguarda/pedidos`, detalhe somente
  leitura, recusa de produtor, logs de confirmação aceita (sem PII).
- Config lifecycle: perfil ativo do adaptador simulado em `application.yml`;
  nenhum `config.toml` nem credencial de provedor.

## Deliverables

- Confirmação mock sempre aceita após persistir o pedido.
- Backoffice somente leitura do tenant configurado.
- Superfície S6 com evidência visual.
- Every test case assigned in `## Tests` implemented and passing **(REQUIRED)**
- Every Visual Contract row has a durable passing evidence bundle **(REQUIRED)**

## Tests

Cases assigned from `_tests.md`, the test contract — read each ID's full definition there before writing tests.

- [x] UT-021 — mock aceita pedido local já persistido.
- [x] UT-024, UT-025 — resumo do operador e recusa do produtor.
- [x] IT-007 — `POST /api/v1/pedidos` persiste antes da confirmação aceita.
- [x] IT-009, IT-010 — lista da retaguarda por tenant, vazio e recusa.
- [x] E2E-009, E2E-010 — operador inspeciona; vazio e rota negada ao produtor.

## Success Criteria

- Every assigned test case implemented and passing
- Pedido local existe mesmo se o teste substituir o adaptador na porta
- Operador vê confirmação aceita; produtor não lista a retaguarda
- Every Visual Contract row is `PASS` with zero unresolved blocking divergence
