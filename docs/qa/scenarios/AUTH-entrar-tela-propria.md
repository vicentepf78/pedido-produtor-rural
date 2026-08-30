---
id: AUTH-entrar-tela-propria
area: AUTH
title: Telas próprias de Entrar e Criar conta com origem
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: Convidado abre /entrar e /cadastro pelo topo, volta à origem após sucesso, operador vai à retaguarda, credenciais inválidas e e-mail duplicado permanecem na tela sem apagar o carrinho
entry_points: /entrar; /cadastro; GET /api/v1/autenticacao/sessao; POST /api/v1/autenticacao/entrada; POST /api/v1/autenticacao/cadastro
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/11-entrar-origem.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/19-entrar-invalido.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: AUTH-cadastro-e-sessao
---

Walk 2026-08-30 MVP1: `/entrar?origem=/catalogo`; Voltar sem sessão; credenciais inválidas permanecem em Entrar; operador ignora origem da loja.
