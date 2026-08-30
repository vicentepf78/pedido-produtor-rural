---
id: CAT-carrossel-e-carregar-mais
area: CAT
title: Carrossel fechado, busca composta e Carregar mais
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: O produtor recorta pelos cards Todos / Sementes / Fertilizantes / Correção, soma busca ao recorte, limpa os dois filtros, escolhe 10 / 15 / 30 / 50 e avança com Carregar mais sem scroll infinito; voltar do detalhe mantém os filtros
entry_points: /catalogo; GET /api/v1/catalogo/produtos; categoria; consulta; pagina; tamanhoPagina
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: CAT-produtor-descobre-produto; CAT-paginacao-catalogo
---

Walk deferred to Phase C. Superfície S1 do MVP 1: carrossel fechado, Limpar filtros, bloco padrão 10 e Carregar mais via `pagina`.
