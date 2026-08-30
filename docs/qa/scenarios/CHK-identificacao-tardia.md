---
id: CHK-identificacao-tardia
area: CHK
title: Identificação tardia no checkout
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: Produtor entra ou cria conta só no checkout, escolhe propriedade própria e preferência de retirada, e confirma o pedido local
entry_points: /checkout; GET /api/v1/autenticacao/csrf; POST /api/v1/autenticacao/cadastro; POST /api/v1/autenticacao/entrada; GET /api/v1/produtor/propriedades; POST /api/v1/pedidos
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-pedido-rapido-money-04-checkout.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-pedido-rapido-money-05-pedido-recebido.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp0.md
overlaps: AUTH-cadastro-e-sessao; CART-convidado-persiste; ORD-confirma-e-revisita
---

Sessão `sessao` + CSRF `X-XSRF-TOKEN`. Cookie `chaveCarrinhoConvidado` sobrevive a expiração da sessão. Planejado para CH-pedido-rapido-money.
