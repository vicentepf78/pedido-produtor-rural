# Task Memory: task_04

## Objective Snapshot

Checkout, pedido local idempotente e Meus pedidos; S3/S4/S5; Visual Contract VC-01–07. Auto-commit desligado; status do task file permanece `in_progress`.

## Important Decisions

- `order` depende de `cart`/`producer`/`identity`/`catalog` só pelo pacote `application` (evita ciclo cart↔order). Carrinho esvazia via porta pública após confirmar.
- `confirmacao` fica `PENDENTE` até a task_05; `situacao=RECEBIDO`. Porta `GatewayErp` existe como contrato vazio — sem `GatewayErpSimulado`.
- Idempotência: header `Idempotency-Key` (corpo permanece o de `_dx.md`); chave derivada se o header faltar.
- CTA Confirmar permanece habilitado com itens no carrinho para E2E-006/VC-02 mostrarem erro; o wireframe desabilita o botão quando o form é inválido.
- Cadastro novo não tem propriedades (IT-005); E2E usa mock; seed `produtor.alfa` tem Fazenda Norte.
- Captura visual: CSS esconde demo-bar / wire-label / Backoffice; clique no Confirmar do wireframe via `__reactProps.onClick` (botão `disabled` o React 18 ignora click nativo).

## Learnings

- `eng-ui-screenshot` ausente: `VISUAL_TASK04=1 npx playwright test e2e/visual-task04.spec.ts` + `python3 frontend/scripts/comparar-visual-task04.py`.
- `getByRole` não acha botões do demo-bar com `display:none`; usar locator + `__reactProps`.
- Cookie `chaveCarrinhoConvidado` não deve ser apagado no login/logout.
- Copy `ACESSO_PEDIDO_NEGADO`: “Você não pode visualizar este pedido.”

## Files / Surfaces

- `backend/.../order/` — ComandoPedido, ServicoPedido, API POST/GET, Flyway V2 pedido/itemPedido.
- `frontend/src/features/checkout/`, `frontend/src/features/my-orders/`, rota `/pedidos/:idPedido`.
- `frontend/e2e/checkout.spec.ts`, `my-orders.spec.ts`, `visual-task04.spec.ts`.
- `docs/qa/scenarios/CHK-identificacao-tardia.md`, `CHK-erros-validacao.md`, `ORD-confirma-e-revisita.md`, `ORD-acesso-negado.md` (untested; walk Phase C).
- Evidência: `.compozy/tasks/pedidos-insumos-mvp0/evidence/visual/task_04/VC-01`–`VC-07`.

## Errors / Corrections

- Clique Playwright no Confirmar desabilitado do wireframe não dispara `onClick` (React trata o fiber como disabled).
- Forçar `btn.disabled=false` também não basta — chamar `__reactProps.onClick`.

## Ready for Next Run

- Orquestrador reviu pares VC-01–07 e reexecutou UT/IT/E2E (30/11/8, 0 fail).
- task_05: `GatewayErpSimulado`, `confirmacao=ACEITA`, lista do operador. Não reabrir checkout/Meus pedidos.
- QA walk e peer-review adiados (Phase C / D).
