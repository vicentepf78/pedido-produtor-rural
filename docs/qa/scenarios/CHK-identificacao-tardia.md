---
id: CHK-identificacao-tardia
area: CHK
title: Identificação tardia no checkout
persona: Produtor rural
journey: J-first-purchase
expected: Produtor entra ou cria conta só no checkout, escolhe propriedade própria e preferência de retirada, e confirma o pedido local
entry_points: /checkout; POST /api/v1/autenticacao/cadastro; POST /api/v1/autenticacao/entrada; GET /api/v1/produtor/propriedades; POST /api/v1/pedidos
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: AUTH-cadastro-e-sessao; CART-convidado-persiste; ORD-confirma-e-revisita
---

Sessão `sessao` + CSRF `X-XSRF-TOKEN`. Cookie `chaveCarrinhoConvidado` sobrevive a expiração da sessão. Walk adiado para a fase de QA do loop (Phase C).
