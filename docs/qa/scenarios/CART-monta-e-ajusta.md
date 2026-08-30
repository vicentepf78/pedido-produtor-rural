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
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/06-carrinho-aurora.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/07-carrinho-qty-2.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: CART-convidado-persiste
---

Walk 2026-08-30 MVP1: adicionou Aurora, qty 2 e 3 (375px); checkout vazio bloqueia confirmar e mostra CARRINHO_VAZIO.
