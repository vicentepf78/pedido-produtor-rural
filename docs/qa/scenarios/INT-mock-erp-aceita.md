---
id: INT-mock-erp-aceita
area: INT
title: Mock ERP aceita após persistir o pedido local
persona: Produtor rural
journey: J-first-purchase
expected: POST /api/v1/pedidos persiste o pedido local e então registra confirmação ACEITA do GatewayErpSimulado, sem o mock criar ou alterar o snapshot
entry_points: POST /api/v1/pedidos
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: ORD-confirma-e-revisita
---

O adaptador simulado aceita de forma síncrona, sem rede, segredo, retry ou broker. Pedido local permanece se o adaptador na porta for substituído. Walk adiado para a fase de QA do loop (Phase C).
