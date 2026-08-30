---
id: RET-visual-loja
area: RET
title: Retaguarda no visual da loja com TopoOperador
persona: Operador da revenda
journey: J-operador-retaguarda
expected: Lista e detalhe da retaguarda usam o visual da loja; TopoOperador mostra Pedidos da revenda, identificação e Sair, sem atalhos de compra; chegada só pela Entrar da loja
entry_points: /entrar; /retaguarda/pedidos; /retaguarda/pedidos/{idPedido}; GET /api/v1/autenticacao/sessao; GET /api/v1/retaguarda/pedidos; GET /api/v1/retaguarda/pedidos/{idPedido}
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/20-operador-retaguarda.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: ORD-backoffice-lista; ORD-backoffice-vazio-ou-negado; NAV-topo-loja
---

Walk 2026-08-30 MVP1: `/entrar?origem=/checkout` com operador cai em Pedidos da revenda; TopoOperador sem Catálogo/Carrinho/Checkout; sem login inline.
