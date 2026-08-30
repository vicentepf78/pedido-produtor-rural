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
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-operador-lista-retaguarda-01-lista.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-operador-lista-retaguarda-02-detalhe.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-operador-lista-retaguarda-03-refresh.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp0.md
overlaps: INT-mock-erp-aceita
---

Acesso com sessão de operador (`operador.revenda@example.com`). Sem atalho Backoffice na navegação do produtor. Tenant semeado delimita a lista. Planejado para CH-operador-lista-retaguarda.
