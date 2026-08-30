---
id: AUTH-cadastro-e-sessao
area: AUTH
title: Cadastro, entrada e sessão do produtor
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: O produtor cria ou entra na conta no checkout, recebe sessão por cookie HttpOnly, lista só as próprias propriedades e, se a sessão expirar, volta a se identificar sem perder o carrinho de convidado
entry_points: /checkout; GET /api/v1/autenticacao/csrf; POST /api/v1/autenticacao/cadastro; POST /api/v1/autenticacao/entrada; POST /api/v1/autenticacao/saida; GET /api/v1/produtor/propriedades; GET /actuator/health; cookie sessao; server.servlet.session.cookie.name; server.servlet.session.timeout; SPRING_DATASOURCE_URL; SPRING_DATASOURCE_USERNAME; SPRING_DATASOURCE_PASSWORD
qa_status: pass
bug_ids: BUG-20260830-sem-sair-na-interface
fix_status: pending
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-checkout-lixo-entrada-03-email-duplicado.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-checkout-lixo-entrada-07-logout-carrinho.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp0.md
overlaps: CHK-identificacao-tardia
---

Cookie `sessao` HttpOnly+Secure (exceto perfil local). CSRF via `XSRF-TOKEN`. Health e datasource são pré-condição de sessão, não casos soltos. E-mail duplicado e credenciais inválidas não vazam senha. Logout e expiração não apagam `chaveCarrinhoConvidado`. Planejado para CH-checkout-lixo-entrada.

Walk 2026-08-30: cadastro duplicado, senha errada e `POST /autenticacao/saida` preservaram o carrinho. Friction: não há Sair na UI (`BUG-20260830-sem-sair-na-interface`).
