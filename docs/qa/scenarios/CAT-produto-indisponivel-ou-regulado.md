---
id: CAT-produto-indisponivel-ou-regulado
area: CAT
title: Produto indisponível visível e produto regulado oculto
persona: Produtor rural
journey: J-produtor-pedido-rapido
expected: Item indisponível aparece sem ação de compra ativa; acesso direto a produto regulamentado é negado sem revelar o conteúdo; imagem ausente não quebra a tela
entry_points: /catalogo; /catalogo/{idProduto}; GET /api/v1/catalogo/produtos/{idProduto}; POST /api/v1/carrinhos/convidado/itens; PRODUTO_NAO_ELEGIVEL; PRODUTO_INDISPONIVEL
qa_status: pass
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence: docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-pedido-rapido-money-08-regulado.png; docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-pedido-rapido-money-09-ureia.png
last_report: docs/qa/reports/2026-08-30-pedidos-insumos-mvp0.md
overlaps: CAT-produtor-descobre-produto
---

Ureia 45% N permanece listada com botão Indisponível. Herbicida regulado `10000000-0000-4000-8000-000000000099` não entra na listagem. Hot spot ADR-001. Planejado para CH-pedido-rapido-money.

Walk 2026-08-30: URL direta mostrou “Acesso negado” sem revelar o produto; `GET /api/v1/catalogo/produtos/{id}` devolveu 404 `PRODUTO_NAO_ELEGIVEL`. A UI não cita o texto canônico de `_dx.md` (paper cut opaco).
