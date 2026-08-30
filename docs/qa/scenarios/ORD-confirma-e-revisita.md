---
id: ORD-confirma-e-revisita
area: ORD
title: Confirmação imediata e revisita do pedido
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: Após confirmar, “Pedido recebido” e o identificador aparecem em até 1s; Meus pedidos reabre o snapshot imutável (itens, preços, propriedade, retirada, total)
entry_points: /pedidos/{idPedido}; /meus-pedidos; POST /api/v1/pedidos; GET /api/v1/pedidos; GET /api/v1/pedidos/{idPedido}
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/14-pedido-recebido.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/15-meus-pedidos.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: CHK-identificacao-tardia; INT-mock-erp-aceita
---

Walk 2026-08-30 MVP1: Pedido recebido `36b95df8-…` Aceita; reload e Meus pedidos iguais. Replay HTTP da mesma Idempotency-Key devolveu `722e3c3c-…` duas vezes.
