---
id: NAV-topo-loja
area: NAV
title: Topo da loja substitui a barra inferior
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: Catálogo, Carrinho, Checkout e Pedido ficam no cabeçalho; convidado vê Entrar; produtor vê identificação e Sair; Sair não apaga o carrinho; operador não usa este topo
entry_points: /catalogo; /carrinho; /checkout; /meus-pedidos; POST /api/v1/autenticacao/saida
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: AUTH-entrar-tela-propria
---

Walk deferred to Phase C. Substitui a `bottom-nav` do MVP 0. Checkout vazio mantém o atalho no topo.
