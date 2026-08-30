---
id: AUTH-sessao-atual
area: AUTH
title: Sessão atual permitAll
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: GET /api/v1/autenticacao/sessao sem cookie devolve autenticado false; com sessão devolve email e papeis; cadastro e entrada também devolvem email e papeis
entry_points: GET /api/v1/autenticacao/sessao; POST /api/v1/autenticacao/cadastro; POST /api/v1/autenticacao/entrada
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: AUTH-cadastro-e-sessao
---

Walk deferred to Phase C. Superfície nova do MVP 1 para o cabeçalho da loja.
