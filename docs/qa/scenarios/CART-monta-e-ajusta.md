---
id: CART-monta-e-ajusta
area: CART
title: Monta e ajusta o carrinho de convidado
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: Adicionar quantidade positiva calcula totais; alterar e remover atualiza o total; quantidade inválida é rejeitada; o mesmo produto consolida em uma linha
entry_points: /carrinho; POST /api/v1/carrinhos/convidado/itens; PATCH /api/v1/carrinhos/convidado/itens/{idProduto}; DELETE /api/v1/carrinhos/convidado/itens/{idProduto}; QUANTIDADE_INVALIDA; CARRINHO_VAZIO
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-pedido-rapido-money-03-quantidade.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-canario-retoma-carrinho-03-patch-reload.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp0.md
overlaps: CART-convidado-persiste
---

Totais só com preço unitário atual. Checkout permanece bloqueado com carrinho vazio. Planejado para CH-pedido-rapido-money e CH-canario-retoma-carrinho.
