---
id: CAT-paginacao-catalogo
area: CAT
title: Paginação e categorias fechadas do catálogo
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: Listagem sem consulta ou com consulta em branco devolve o recorte elegível; tamanhos 10/15/30/50 funcionam; tamanho 24 é recusado; Defensivos é recusado; página 2 não repete IDs
entry_points: GET /api/v1/catalogo/produtos; categoria; consulta; pagina; tamanhoPagina; TAMANHO_PAGINA_INVALIDO; CATEGORIA_INVALIDA
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: CAT-produtor-descobre-produto
---

Walk deferred to Phase C. Contrato HTTP do MVP 1 quebra o vazio em busca em branco do MVP 0.
