---
status: pending
title: Checkout, pedido idempotente e Meus pedidos
type: fullstack
complexity: high
---

# Task 4: Checkout, pedido idempotente e Meus pedidos

## Overview

Fecha a jornada do produtor: identificação tardia, propriedade, retirada,
confirmação idempotente do pedido local e histórico em Meus pedidos. O pedido
local é a fonte de autoridade; a confirmação do Mock ERP entra só na task_05.

<critical>
- ALWAYS READ `_spec.md` and its catalogs (`_user_stories.md`, `_dx.md`, `_uiux.md` when present, `_tests.md`) before starting
- REFERENCE `_spec.md` Part II for implementation details — do not duplicate here
- FOCUS ON "WHAT" — describe what needs to be accomplished, not how
- MINIMIZE CODE — show code only to illustrate current structure or problem areas
- TESTS REQUIRED — implement every test case assigned in ## Tests
</critical>

<requirements>
- MUST implementar `ComandoPedido.criar` e as rotas `POST /api/v1/pedidos`,
  `GET /api/v1/pedidos`, `GET /api/v1/pedidos/{idPedido}` conforme `_dx.md`.
- MUST exigir produtor autenticado, propriedade própria e preferência de
  retirada; caso contrário `DADOS_CHECKOUT_OBRIGATORIOS`.
- MUST rejeitar checkout com carrinho vazio (`CARRINHO_VAZIO`).
- MUST persistir snapshot imutável (itens, preços, quantidades, total,
  propriedade, retirada) no momento da confirmação.
- MUST mapear uma chave de idempotência para no máximo um pedido local;
  repetir o POST devolve a confirmação original.
- MUST negar leitura do pedido de outro produtor com `ACESSO_PEDIDO_NEGADO`.
- MUST exibir “Pedido recebido” e o identificador em até um segundo após
  confirmar, sem esperar sistema externo (SD-005; Mock ERP ainda não é
  obrigatório neste entregável).
- MUST construir S3, S4 e S5 segundo o Visual Contract.
- MUST preservar dados já informados no checkout ao voltar ou ao falhar
  validação, e preservar o carrinho se a sessão expirar.
- MUST NOT chamar DTO de fornecedor nem implementar `GatewayErpSimulado`.
- MUST NOT alterar preço do snapshot se o catálogo mudar depois.
</requirements>

## Visual Contract

Referência: `references/mvp0-pedidos-insumos.html`.

| ID | Reference artifact + state | Implementation target + state | Viewport | Fidelity | Authorized differences + authority |
| --- | --- | --- | --- | --- | --- |
| VC-01 | Tela 3 Checkout — Entrar/Criar conta, propriedade, retirada, resumo e Confirmar pedido | `/checkout` — formulário completo habilitado | 390×844 | normative | Rotas de autenticação reais da task_02 no lugar do estado local do wireframe |
| VC-02 | Tela 3 — campos inválidos com erro inline (`aria-invalid`) | `/checkout` — e-mail/senha/propriedade/retirada ausentes | 390×844 | normative | None |
| VC-03 | Tela 4 — caixa “Pedido recebido” + identificador + pills Situação/Confirmação | `/pedidos/{idPedido}` — recém-confirmado | 390×844 | normative | `confirmacao` pode permanecer pendente até a task_05; rótulo “Pedido recebido” e `idPedido` são obrigatórios; `_dx.md` + SD-005 |
| VC-04 | Tela 5 — lista Meus pedidos com id, total, data, pills | `/meus-pedidos` — um ou mais pedidos | 390×844 | normative | None |
| VC-05 | Tela 5 detalhe — itens, propriedade, retirada, total | `/meus-pedidos` → detalhe do pedido criado | 390×844 | normative | None |
| VC-06 | Tela 1 estado Acesso negado (copy de permissão) | link direto ao pedido de outro produtor | 390×844 | normative | Copy `ACESSO_PEDIDO_NEGADO` de `_dx.md` prevalece sobre o texto genérico do wireframe |
| VC-07 | Tela 3 em coluna central | `/checkout` utilizável | 1440×900 | adjacent | Coluna pode alargar; campos, resumo e CTA iguais; `_uiux.md` |

Evidence for each row: `.compozy/tasks/pedidos-insumos-mvp0/evidence/visual/task_04/<contract-id>/{reference.png,implementation.png,side-by-side.png,diff.png,comparison.json,review.md}`

## Subtasks

- [ ] 4.1 Implementar comando de criação de pedido e snapshot imutável.
- [ ] 4.2 Validar carrinho, propriedade, retirada e sessão no checkout.
- [ ] 4.3 Garantir idempotência e recusa de acesso cruzado entre produtores.
- [ ] 4.4 Expor listagem e detalhe em Meus pedidos.
- [ ] 4.5 Construir S3, S4 e S5 mobile-first.
- [ ] 4.6 Implementar os casos UT/IT/E2E desta tarefa.
- [ ] 4.7 Gerar evidência visual de VC-01–VC-07.

## Implementation Details

`order` é dono do snapshot. `cart` cria pedido só via `ComandoPedido`.
Checkout no frontend (`features/checkout`, `features/my-orders`) usa sessão
da task_02 e carrinho da task_03. Porta `GatewayErp` pode existir como
contrato vazio; o adaptador simulado é da task_05. Itens de pedido são tabela
relacional, não JSON.

### Relevant Files

- `_spec.md` — `ComandoPedido`, entidades Pedido/Item, invariantes 4–5.
- `_dx.md` — `POST /api/v1/pedidos` e GETs do produtor.
- `_uiux.md` — S3, S4, S5.
- `_user_stories.md` — US-003, US-004.
- `backend/src/main/java/br/agriplataforma/order/` — módulo da fundação.
- `references/mvp0-pedidos-insumos.html` — Telas 3–5.

### Dependent Files

- `backend/src/main/java/br/agriplataforma/order/application/ComandoPedido.java`
- `backend/src/main/java/br/agriplataforma/order/api/` — pedidos do produtor.
- `backend/src/main/resources/db/migration/order/` — pedido, itens, chave de idempotência.
- `frontend/src/features/checkout/` — formulário S3.
- `frontend/src/features/my-orders/` — lista e detalhe S4/S5.
- `frontend/e2e/checkout.spec.ts`, `frontend/e2e/my-orders.spec.ts`.

### Related ADRs

- [ADR-001](adrs/adr-001-fast-non-regulated-order-proof.md) — pedido local
  rápido é a hipótese desta prova.

### Web/Docs Impact

- `web/`: none — checked surfaces: `web/`; reason: UI em
  `frontend/src/features/checkout` e `frontend/src/features/my-orders`.
- `packages/site`: none — checked surfaces: `packages/site/`.
- QA impact: new scenarios — add or reset to `untested`
  `docs/qa/scenarios/CHK-identificacao-tardia.md`,
  `docs/qa/scenarios/CHK-erros-validacao.md`,
  `docs/qa/scenarios/ORD-confirma-e-revisita.md`,
  `docs/qa/scenarios/ORD-acesso-negado.md`.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: `GatewayErp` permanece porta não implementada até a task_05.
  Nenhum hook/MCP.
- Agent manageability: `POST /api/v1/pedidos`, `GET /api/v1/pedidos`,
  `GET /api/v1/pedidos/{idPedido}`, erros `CARRINHO_VAZIO`,
  `DADOS_CHECKOUT_OBRIGATORIOS`, `ACESSO_PEDIDO_NEGADO`, resposta idempotente.
- Config lifecycle: none for `config.toml`. Sem broker ou fila.

## Deliverables

- Checkout com identificação tardia, propriedade e retirada.
- Pedido local idempotente e Meus pedidos.
- Superfícies S3–S5 com evidência visual.
- Every test case assigned in `## Tests` implemented and passing **(REQUIRED)**
- Every Visual Contract row has a durable passing evidence bundle **(REQUIRED)**

## Tests

Cases assigned from `_tests.md`, the test contract — read each ID's full definition there before writing tests.

- [ ] UT-013, UT-014, UT-015 — checkout válido, carrinho vazio, dados obrigatórios.
- [ ] UT-018, UT-020, UT-022, UT-023 — idempotência, snapshot, imutabilidade de preço, permissão.
- [ ] IT-006, IT-008 — POST duplicado e GET negado / refresh sem novo pedido.
- [ ] E2E-005, E2E-006, E2E-007, E2E-008 — confirmar, erros de checkout, revisitar, acesso negado.

A persistência do pedido antes da confirmação do Mock ERP é da task_05.

## Success Criteria

- Every assigned test case implemented and passing
- Duas confirmações com a mesma chave geram um único `idPedido`
- Alterar preço do catálogo não muda pedido confirmado
- “Pedido recebido” aparece em ≤1 s na jornada E2E-005
- Every Visual Contract row is `PASS` with zero unresolved blocking divergence
