---
id: CHK-erros-validacao
area: CHK
title: Erros de validação no checkout preservam dados
persona: Produtor rural
journey: J-first-purchase
expected: Conta inválida ou propriedade/retirada ausente mostra erro em foco (aria-invalid) e mantém os dados já informados e o carrinho
entry_points: /checkout; DADOS_CHECKOUT_OBRIGATORIOS; CREDENCIAIS_INVALIDAS; EMAIL_DUPLICADO; CARRINHO_VAZIO
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: CHK-identificacao-tardia; CART-convidado-persiste
---

Mensagens canônicas em `_dx.md`. Walk adiado para a fase de QA do loop (Phase C).
