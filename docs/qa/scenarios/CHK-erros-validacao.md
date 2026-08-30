---
id: CHK-erros-validacao
area: CHK
title: Erros de validação no checkout preservam dados
persona: Produtor após falha
journey: J-produtor-pedido-rapido
expected: Conta inválida ou propriedade/retirada ausente mostra erro em foco (aria-invalid) e mantém os dados já informados e o carrinho
entry_points: /checkout; POST /api/v1/autenticacao/cadastro; POST /api/v1/autenticacao/entrada; POST /api/v1/pedidos; DADOS_CHECKOUT_OBRIGATORIOS; CREDENCIAIS_INVALIDAS; EMAIL_DUPLICADO; CARRINHO_VAZIO; NAO_AUTENTICADO
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-checkout-lixo-entrada-02-vazio.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-checkout-lixo-entrada-04-credenciais.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-checkout-lixo-entrada-06-obrigatorios.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp0.md
overlaps: CHK-identificacao-tardia; CART-convidado-persiste
---

Mensagens canônicas em `_dx.md`. Planejado para CH-checkout-lixo-entrada.
