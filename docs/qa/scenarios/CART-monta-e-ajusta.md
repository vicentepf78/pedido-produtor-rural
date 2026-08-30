---
id: CART-monta-e-ajusta
area: CART
title: Monta e ajusta o carrinho de convidado
persona: Produtor rural
journey: J-first-purchase
expected: Adicionar quantidade positiva calcula totais; alterar e remover atualiza o total; quantidade inválida é rejeitada; o mesmo produto consolida em uma linha
entry_points: /carrinho; POST /api/v1/carrinhos/convidado/itens
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: CART-convidado-persiste
---

Totais só com preço unitário atual. Checkout permanece bloqueado com carrinho vazio. Walk adiado para a fase de QA do loop (Phase C).
