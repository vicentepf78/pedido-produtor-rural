---
id: AUTH-cadastro-e-sessao
area: AUTH
title: Cadastro, entrada e sessão do produtor
persona: Produtor rural
journey: J-first-purchase
expected: O produtor cria ou entra na conta no checkout, recebe sessão por cookie HttpOnly, lista só as próprias propriedades e, se a sessão expirar, volta a se identificar sem perder o carrinho de convidado
entry_points: /checkout; POST /api/v1/autenticacao/cadastro; POST /api/v1/autenticacao/entrada; GET /api/v1/produtor/propriedades
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: CHK-identificacao-tardia
---

Cadastro e entrada na superfície `/api/v1/autenticacao`. Cookie `sessao` HttpOnly+Secure (exceto perfil local). CSRF via `XSRF-TOKEN`. E-mail duplicado e credenciais inválidas não vazam senha. Logout e expiração não apagam `chaveCarrinhoConvidado`. Walk adiado para a fase de QA do loop.
