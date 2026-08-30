---
id: ORD-backoffice-lista
area: ORD
title: Operador lista e inspeciona pedidos da retaguarda
persona: Operador da revenda
journey: J-reseller-inspect
expected: GET /api/v1/retaguarda/pedidos mostra identificador, produtor, total, criação e confirmação aceita; o detalhe é somente leitura (snapshot imutável)
entry_points: /retaguarda/pedidos; /retaguarda/pedidos/{idPedido}; GET /api/v1/retaguarda/pedidos; GET /api/v1/retaguarda/pedidos/{idPedido}
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: INT-mock-erp-aceita
---

Acesso com sessão de operador (`operador.revenda@example.com`). Sem atalho Backoffice na navegação do produtor. Walk adiado para a fase de QA do loop (Phase C).
