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
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-operador-lista-retaguarda-05-pedido-alheio.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp0.md
overlaps: ORD-confirma-e-revisita; ORD-backoffice-vazio-ou-negado
---

Hot spot de isolamento. Copy de `_dx.md` prevalece sobre o texto genérico do wireframe. Planejado para CH-operador-lista-retaguarda.
