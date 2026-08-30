---
id: ORD-backoffice-vazio-ou-negado
area: ORD
title: Retaguarda vazia explícita e recusa do produtor
persona: Operador da revenda; Produtor rural
journey: J-reseller-inspect
expected: Operador sem pedidos vê estado vazio explícito; produtor autenticado em /retaguarda/pedidos recebe ACESSO_NEGADO e não vê a lista
entry_points: /retaguarda/pedidos; GET /api/v1/retaguarda/pedidos
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: ORD-backoffice-lista
---

Copy de permissão de operador; a lista não vaza para o papel PRODUTOR. Walk adiado para a fase de QA do loop (Phase C).
