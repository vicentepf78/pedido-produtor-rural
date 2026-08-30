---
id: CAT-paginacao-catalogo
area: CAT
title: Paginação e categorias fechadas do catálogo
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: Listagem sem consulta ou com consulta em branco devolve o recorte elegível; tamanhos 10/15/30/50 funcionam; tamanho 24 é recusado; Defensivos é recusado; página 2 não repete IDs
entry_points: GET /api/v1/catalogo/produtos; categoria; consulta; pagina; tamanhoPagina; TAMANHO_PAGINA_INVALIDO; CATEGORIA_INVALIDA
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/01-catalogo-todos.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/04-catalogo-carregar-mais.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: CAT-produtor-descobre-produto
---

Walk 2026-08-30 MVP1: `tamanhoPagina=15` lista 15; `tamanhoPagina=24` HTTP 400 `TAMANHO_PAGINA_INVALIDO`; busca em branco lista o recorte.
