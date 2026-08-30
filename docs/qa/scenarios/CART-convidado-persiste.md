---
id: CART-convidado-persiste
area: CART
title: Carrinho de convidado persiste no navegador
persona: Produtora no campo
journey: J-convidado-retoma-carrinho
expected: O carrinho sobrevive a recarregar a página via cookie chaveCarrinhoConvidado; mutação recusada preserva o último estado confirmado; logout de identidade não apaga o cookie
entry_points: /carrinho; GET /api/v1/carrinhos/convidado; POST /api/v1/autenticacao/saida; cookie chaveCarrinhoConvidado
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-canario-retoma-carrinho-02-retorno.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-checkout-lixo-entrada-07-logout-carrinho.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp0.md
overlaps: AUTH-cadastro-e-sessao; CART-monta-e-ajusta
---

Cookie HttpOnly, SameSite=Lax, Secure fora do perfil local. Canário de continuidade. Planejado para CH-canario-retoma-carrinho e CH-checkout-lixo-entrada.
