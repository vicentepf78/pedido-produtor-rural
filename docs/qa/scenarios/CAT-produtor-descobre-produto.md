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
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-pedido-rapido-money-01-catalogo.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-pedido-rapido-money-10-busca-vazia.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp0.md
overlaps: CAT-produto-indisponivel-ou-regulado
---

Catálogo S1 mobile-first. Sem aba Defensivos. Pesquisa sem correspondência mostra “Nenhum resultado” e “Limpar busca”. Planejado para CH-pedido-rapido-money.
