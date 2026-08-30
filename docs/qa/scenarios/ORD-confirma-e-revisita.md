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
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-pedido-rapido-money-05-pedido-recebido.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-pedido-rapido-money-06-reload-detalhe.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-pedido-rapido-money-07-meus-pedidos.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp0.md
overlaps: CHK-identificacao-tardia; INT-mock-erp-aceita
---

Idempotência: mesma chave não cria segundo pedido. Snapshot imutável após confirmar. Planejado para CH-pedido-rapido-money.
