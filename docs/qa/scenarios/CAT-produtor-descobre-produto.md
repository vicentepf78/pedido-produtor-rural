---
id: CAT-produtor-descobre-produto
area: CAT
title: Produtor descobre produto no catálogo curado
persona: Produtor rural
journey: J-first-purchase
expected: O produtor navega ou pesquisa os 30 produtos não regulamentados, vê nome, descrição curta, preço, unidade e imagem ou placeholder, e consegue adicionar um item disponível
entry_points: /catalogo; GET /api/v1/catalogo/produtos
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: CAT-produto-indisponivel-ou-regulado
---

Catálogo S1 mobile-first. Sem aba Defensivos. Pesquisa sem correspondência mostra “Nenhum resultado” e “Limpar busca”. Walk adiado para a fase de QA do loop (Phase C).
