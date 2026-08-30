---
id: ORD-confirma-e-revisita
area: ORD
title: Confirmação imediata e revisita do pedido
persona: Produtor rural
journey: J-first-purchase
expected: Após confirmar, “Pedido recebido” e o identificador aparecem em até 1s; Meus pedidos reabre o snapshot imutável (itens, preços, propriedade, retirada, total)
entry_points: /pedidos/{idPedido}; /meus-pedidos; POST /api/v1/pedidos; GET /api/v1/pedidos; GET /api/v1/pedidos/{idPedido}
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: CHK-identificacao-tardia
---

`confirmacao` pode permanecer PENDENTE até a task_05 (Mock ERP). Idempotência: mesma chave não cria segundo pedido. Walk adiado para a fase de QA do loop (Phase C).
