---
id: INT-mock-erp-aceita
area: INT
title: Mock ERP aceita após persistir o pedido local
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: POST /api/v1/pedidos persiste o pedido local e então registra confirmação ACEITA do GatewayErpSimulado, sem o mock criar ou alterar o snapshot
entry_points: POST /api/v1/pedidos; GET /api/v1/pedidos/{idPedido}; agriplataforma.gatewayErp
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/14-pedido-recebido.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/21-operador-detalhe.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: ORD-confirma-e-revisita; ORD-backoffice-lista
---

Walk 2026-08-30 MVP1: UI e HTTP devolveram ACEITA após persistir; replay da mesma chave não criou segundo id.
