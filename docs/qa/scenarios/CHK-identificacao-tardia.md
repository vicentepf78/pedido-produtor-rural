---
id: CHK-identificacao-tardia
area: CHK
title: Identificação tardia no checkout
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: Se já for produtor o checkout pula Entrar; senão identifica-se no próprio checkout, cadastra propriedade só ali se não tiver nenhuma, escolhe retirada e confirma
entry_points: /checkout; GET /api/v1/autenticacao/sessao; GET /api/v1/autenticacao/csrf; POST /api/v1/autenticacao/cadastro; POST /api/v1/autenticacao/entrada; GET /api/v1/produtor/propriedades; POST /api/v1/produtor/propriedades; POST /api/v1/pedidos
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/08-checkout-convidado.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/13-checkout-produtor.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: AUTH-cadastro-e-sessao; CART-convidado-persiste; ORD-confirma-e-revisita
---

Walk 2026-08-30 MVP1: convidado vê Identifique-se no checkout; após Entrar como Alfa o bloco some e confirma com Fazenda Norte + Retirar na loja (Centro).
