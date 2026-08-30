---
id: CART-convidado-persiste
area: CART
title: Carrinho de convidado persiste no navegador
persona: Produtor rural
journey: J-first-purchase
expected: O carrinho sobrevive a recarregar a página via cookie chaveCarrinhoConvidado; mutação recusada preserva o último estado confirmado; logout de identidade não apaga o cookie
entry_points: /carrinho; GET /api/v1/carrinhos/convidado; cookie chaveCarrinhoConvidado
qa_status: untested
bug_ids:
fix_status:
retest_status:
fix_commits:
evidence:
last_report:
overlaps: AUTH-cadastro-e-sessao; CART-monta-e-ajusta
---

Cookie HttpOnly, SameSite=Lax, Secure fora do perfil local. Walk adiado para a fase de QA do loop (Phase C).
