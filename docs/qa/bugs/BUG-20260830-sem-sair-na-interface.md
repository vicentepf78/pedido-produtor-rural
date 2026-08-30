# BUG-20260830-sem-sair-na-interface: produtor não encontra como encerrar a sessão

- **Status:** resolved
- **Impact (user-side):** Friction
- **Severity:** Medium · **Priority:** P2
- **Persona Affected:** Produtor após falha
- **Journey Step:** J-produtor-pedido-rapido, step 3 (identificação)
- **Scenarios:** AUTH-cadastro-e-sessao
- **Found:** 2026-08-30 · **Report:** docs/qa/reports/2026-08-30-pedidos-insumos-mvp0.md

## Summary

Depois de entrar no checkout, o produtor não acha um controle de Sair na navegação
nem no checkout. A sessão só cai por expiração ou por `POST /api/v1/autenticacao/saida`,
que um produtor de primeira visita não conhece. O carrinho de convidado permanece
quando a sessão é encerrada pela API — o problema é achar a saída, não perder o
carrinho.

## Reproduction

- **Charter:** CH-checkout-lixo-entrada · **Tour:** Garbage Tour
- **Environment:** phone-large 390×844 / Chromium / pt-BR / stack local (Vite 5173 + Spring `local`)

1. Abre `/catalogo`, adiciona um item e vai a `/checkout`.
2. Entra com `produtor.alfa@example.com`.
3. Procura na barra inferior e no checkout um controle para sair da conta.

**Expected:** um caminho visível para encerrar a sessão, no idioma do produtor.
**Actual (antes):** a nav só oferece Catálogo, Carrinho, Checkout e Pedidos. Nenhum Sair.

**Fix (rodada 1):** botão Sair no checkout após identidade; `POST /autenticacao/saida` não apaga o cookie do carrinho.

## Evidence

- docs/qa/evidence/2026-08-30-pedidos-insumos-mvp0/CH-checkout-lixo-entrada-07-logout-carrinho.png
- `POST /api/v1/autenticacao/saida` devolveu 204; `GET /api/v1/pedidos` passou a 401; o cookie `chaveCarrinhoConvidado` e o total do carrinho permaneceram.

## Fix

<!-- filled when status moves to fixed -->
- **Root cause:** a casca do SPA não expõe a rota de saída já existente na API.
- **Fix commit:**
- **Regression test:**
