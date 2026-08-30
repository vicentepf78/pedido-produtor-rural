# Task Memory: task_05

## Objective Snapshot

Mock ERP + backoffice somente leitura; S6; VC-01–05. Auto-commit desligado; status do task file permanece `in_progress`.

## Important Decisions

- `GatewayErp` fica em `order.application` (named interface), não em `domain` — o caminho do task file é paráfrase; o contrato público do módulo vence.
- Persistência do pedido local commita (`TransactionTemplate`) antes de `GatewayErp.aceitar`. O mock só devolve `ACEITA`; `order` grava `confirmacao` sem o adaptador tocar repositório.
- `nomeProdutor` entra no snapshot do pedido (como `nomePropriedade`) para o resumo da retaguarda sem cruzar identidade na leitura.
- `GET /api/v1/retaguarda/pedidos/{id}` não está em `_dx.md`; US-005 AC-2 e VC-03 exigem o detalhe somente leitura.
- Visual: Playwright + Pillow (`VISUAL_TASK05=1`); orquestrador reviu pares e marcou VC-01–05 PASS.

## Learnings

- Tela 6 do wireframe não tem estado vazio; a implementação adiciona `status-box` (US-005 EC-2). VC-04 usa o chip de catálogo “Acesso negado” como referência.
- `page.route` do Playwright não afeta a página `file://` do wireframe; `waitUntil: networkidle` evita corrida do Babel/CDN.
- IT-010 precisa limpar `"order"."pedido"` — contextos Testcontainers podem compartilhar o banco.

## Files / Surfaces

- `order`: porta + persist-before-accept + `ConsultaPedidoRetaguarda`.
- `integration.infrastructure.GatewayErpSimulado`.
- `backoffice` API `GET /api/v1/retaguarda/pedidos` (+ detalhe).
- `frontend/src/features/backoffice-orders/`.
- QA: `INT-mock-erp-aceita`, `ORD-backoffice-lista`, `ORD-backoffice-vazio-ou-negado` (untested).

## Errors / Corrections

- Captura visual falhou no primeiro `domcontentloaded`; corrigido com `networkidle`.

## Ready for Next Run

- Orquestrador: UT/IT/E2E verdes; VC-01–05 PASS.
- Próxima: Phase C `qa-report` (task_06). Peer-review Phase D.
