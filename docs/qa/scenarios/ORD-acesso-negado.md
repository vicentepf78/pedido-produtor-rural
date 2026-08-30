---
id: ORD-acesso-negado
area: ORD
title: Produtor não vê pedido de outro produtor
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: Link direto ao pedido de outro produtor mostra ACESSO_PEDIDO_NEGADO — “Você não pode visualizar este pedido.”
entry_points: /pedidos/{idPedido}; GET /api/v1/pedidos/{idPedido}; ACESSO_PEDIDO_NEGADO
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/24-alfa-pedido-beta.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: ORD-confirma-e-revisita; ORD-backoffice-vazio-ou-negado
---

Walk 2026-08-30 MVP1: Alfa em `/pedidos/{idBeta}` vê “Você não pode visualizar este pedido.”
