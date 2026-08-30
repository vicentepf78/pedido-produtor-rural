---
id: CART-convidado-persiste
area: CART
title: Carrinho de convidado persiste no navegador
persona: Produtora no campo
journey: J-convidado-retoma-carrinho
expected: O carrinho sobrevive a recarregar a página via cookie chaveCarrinhoConvidado; mutação recusada preserva o último estado confirmado; logout de identidade não apaga o cookie
entry_points: /carrinho; GET /api/v1/carrinhos/convidado; POST /api/v1/autenticacao/saida; cookie chaveCarrinhoConvidado
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/06-carrinho-aurora.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/25-canario-375.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: AUTH-cadastro-e-sessao; CART-monta-e-ajusta
---

Walk 2026-08-30 MVP1: segunda aba restaurou Aurora; cookie após Sair; qty/total visíveis em 375px.
