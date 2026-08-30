---
id: AUTH-sessao-atual
area: AUTH
title: Sessão atual permitAll
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: GET /api/v1/autenticacao/sessao sem cookie devolve autenticado false; com sessão devolve email e papeis; cadastro e entrada também devolvem email e papeis
entry_points: GET /api/v1/autenticacao/sessao; POST /api/v1/autenticacao/cadastro; POST /api/v1/autenticacao/entrada
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/12-apos-entrar-alfa.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: AUTH-cadastro-e-sessao
---

Walk 2026-08-30 MVP1: GET `/sessao` convidado `autenticado=false`; após Alfa, `email` e `papeis` incluem `PRODUTOR`.
