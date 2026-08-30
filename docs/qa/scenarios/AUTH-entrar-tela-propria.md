---
id: AUTH-entrar-tela-propria
area: AUTH
title: Telas próprias de Entrar e Criar conta com origem
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: Convidado abre /entrar e /cadastro pelo topo, volta à origem após sucesso, operador vai à retaguarda, credenciais inválidas e e-mail duplicado permanecem na tela sem apagar o carrinho
entry_points: /entrar; /cadastro; GET /api/v1/autenticacao/sessao; POST /api/v1/autenticacao/entrada; POST /api/v1/autenticacao/cadastro
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: AUTH-cadastro-e-sessao
---

Walk deferred to Phase C. Superfície nova do MVP 1: S3/S4 com whitelist de `origem` e destino por papel.
