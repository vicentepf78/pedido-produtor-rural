---
id: CAT-carrossel-e-carregar-mais
area: CAT
title: Carrossel fechado, busca composta e Carregar mais
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: O produtor recorta pelos cards Todos / Sementes / Fertilizantes / Correção, soma busca ao recorte, limpa os dois filtros, escolhe 10 / 15 / 30 / 50 e avança com Carregar mais sem scroll infinito; voltar do detalhe mantém os filtros
entry_points: /catalogo; GET /api/v1/catalogo/produtos; categoria; consulta; tamanhoPagina; GET /api/v1/catalogo/produtos/{idProduto}
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/01-catalogo-todos.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/04-catalogo-carregar-mais.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: CAT-produtor-descobre-produto; CAT-paginacao-catalogo
---

Walk 2026-08-30 MVP1: carrossel Todos/Sementes/Fertilizantes/Correção; bloco 10; Carregar mais 10→20; Limpar filtros volta a `/catalogo`; query sem `pagina`.
