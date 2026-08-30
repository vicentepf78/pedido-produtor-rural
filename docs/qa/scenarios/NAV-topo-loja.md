---
id: NAV-topo-loja
area: NAV
title: Topo da loja substitui a barra inferior
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: Catálogo, Carrinho, Checkout e Pedido ficam no cabeçalho; convidado vê Entrar; produtor vê identificação e Sair; Sair não apaga o carrinho; operador não usa este topo
entry_points: /catalogo; /carrinho; /checkout; /meus-pedidos; POST /api/v1/autenticacao/saida
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/12-apos-entrar-alfa.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/16-apos-sair.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: AUTH-entrar-tela-propria
---

Walk 2026-08-30 MVP1: Catálogo/Carrinho/Checkout/Pedido no cabeçalho; Entrar no convidado; Sair em dois toques; checkout vazio mantém o atalho. Sem bottom-nav.
