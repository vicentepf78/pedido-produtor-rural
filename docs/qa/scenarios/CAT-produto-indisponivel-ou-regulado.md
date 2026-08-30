---
id: CAT-produto-indisponivel-ou-regulado
area: CAT
title: Produto indisponível visível e produto regulado oculto
persona: Produtor rural
journey: J-first-purchase
expected: Item indisponível aparece sem ação de compra ativa; acesso direto a produto regulamentado é negado sem revelar o conteúdo; imagem ausente não quebra a tela
entry_points: /catalogo; /catalogo/{idProduto}; GET /api/v1/catalogo/produtos/{id}
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: CAT-produtor-descobre-produto
---

Ureia 45% N permanece listada com botão Indisponível. Herbicida regulado não entra na listagem. Walk adiado para a fase de QA do loop (Phase C).
