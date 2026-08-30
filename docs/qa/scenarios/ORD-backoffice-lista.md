---
id: ORD-backoffice-lista
area: ORD
title: Operador lista e inspeciona pedidos da retaguarda
persona: Operador da revenda
journey: J-operador-retaguarda
expected: GET /api/v1/retaguarda/pedidos mostra identificador, produtor, total, criação e confirmação aceita; o detalhe é somente leitura (snapshot imutável)
entry_points: /retaguarda/pedidos; /retaguarda/pedidos/{idPedido}; GET /api/v1/retaguarda/pedidos; GET /api/v1/retaguarda/pedidos/{idPedido}; agriplataforma.idTenantSemeado; AGRIPLATAFORMA_ID_TENANT_SEMEADO
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/20-operador-retaguarda.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/21-operador-detalhe.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: INT-mock-erp-aceita
---

Walk 2026-08-30 MVP1: operador viu o pedido Alfa na lista e no detalhe readonly; snapshot Aceita.
