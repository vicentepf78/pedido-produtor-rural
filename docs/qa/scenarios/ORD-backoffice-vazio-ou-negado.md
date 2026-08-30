---
id: ORD-backoffice-vazio-ou-negado
area: ORD
title: Retaguarda vazia explícita e recusa do produtor
persona: Operador da revenda
journey: J-operador-retaguarda
expected: Operador sem pedidos vê estado vazio explícito; produtor autenticado em /retaguarda/pedidos recebe ACESSO_NEGADO e não vê a lista
entry_points: /retaguarda/pedidos; GET /api/v1/retaguarda/pedidos; GET /api/v1/retaguarda/pedidos/{idPedido}; ACESSO_NEGADO
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/23-alfa-retaguarda-negado.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: ORD-backoffice-lista; ORD-acesso-negado
---

Copy de permissão de operador; a lista não vaza para o papel PRODUTOR. `ACESSO_NEGADO` não está na tabela de erros de `_dx.md`. Planejado para CH-operador-lista-retaguarda.

Walk 2026-08-30: produtor em `/retaguarda/pedidos` viu “Acesso negado” e `GET /api/v1/retaguarda/pedidos` devolveu 403 `ACESSO_NEGADO`. O estado vazio do operador não foi alcançável neste tenant depois do pedido do charter Money.
