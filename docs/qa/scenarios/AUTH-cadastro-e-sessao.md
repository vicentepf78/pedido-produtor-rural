---
id: AUTH-cadastro-e-sessao
area: AUTH
title: Cadastro, entrada e sessão do produtor
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: O produtor cria ou entra na conta no checkout, recebe sessão por cookie HttpOnly, lista só as próprias propriedades e, se a sessão expirar, volta a se identificar sem perder o carrinho de convidado
entry_points: /entrar; /cadastro; /checkout; GET /api/v1/autenticacao/sessao; GET /api/v1/autenticacao/csrf; POST /api/v1/autenticacao/cadastro; POST /api/v1/autenticacao/entrada; POST /api/v1/autenticacao/saida; GET /api/v1/produtor/propriedades; GET /actuator/health; cookie sessao; server.servlet.session.cookie.name; server.servlet.session.timeout
qa_status: pass
bug_ids: BUG-20260830-sem-sair-na-interface
fix_status: pending
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/18-cadastro-duplicado.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/16-apos-sair.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: CHK-identificacao-tardia
---

Cookie `sessao` HttpOnly+Secure (exceto perfil local). CSRF via `XSRF-TOKEN`. Health e datasource são pré-condição de sessão, não casos soltos. E-mail duplicado e credenciais inválidas não vazam senha. Logout e expiração não apagam `chaveCarrinhoConvidado`. Planejado para CH-checkout-lixo-entrada.

Walk 2026-08-30 MVP1: cadastro duplicado (“já está cadastrado”); senha errada; Sair no topo em dois toques; cookie do carrinho permanece. `BUG-20260830-sem-sair-na-interface` verified.
