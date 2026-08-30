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
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/09-checkout-erros-vazios.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/10-checkout-senha-errada.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: CHK-identificacao-tardia; CART-convidado-persiste
---

Walk 2026-08-30 MVP1: confirmar vazio marca e-mail `aria-invalid`; senha errada “E-mail ou senha inválidos.”; checkout vazio “Adicione ao menos um produto…”.
