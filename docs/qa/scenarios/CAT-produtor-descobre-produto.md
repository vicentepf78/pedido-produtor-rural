---
id: CAT-produtor-descobre-produto
area: CAT
title: Produtor descobre produto no catálogo curado
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: O produtor navega ou pesquisa os 30 produtos não regulamentados, vê nome, descrição curta, preço, unidade e imagem ou placeholder, e consegue adicionar um item disponível
entry_points: /; /catalogo; GET /api/v1/catalogo/produtos
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/01-catalogo-todos.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp1/03-catalogo-fertilizantes-ureia.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp1.md
overlaps: CAT-produto-indisponivel-ou-regulado
---

Walk 2026-08-30 MVP1: recorte sem Defensivos; Fertilizantes+ureia mostra Ureia 45% N; Aurora disponível no detalhe/busca.
