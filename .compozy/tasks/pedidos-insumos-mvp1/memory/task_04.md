# Task Memory: task_04

## Objective Snapshot

Restilar S2/S5–S7/S9–S11 no visual da loja: checkout pula identidade quando o produtor já está autenticado; carrinho vazio mostra `CARRINHO_VAZIO`; propriedade só no checkout; retaguarda sem login inline, com `TopoOperador`.

## Important Decisions

- Checkout lê `ProvedorSessao`: `papeis` com `PRODUTOR` esconde Entrar/Criar conta; convidado identifica-se no próprio checkout.
- Carrinho vazio em `/checkout` não renderiza o formulário; o atalho Checkout permanece no `TopoLoja` (task_02).
- Campo `checkout-property-nome` só aparece quando o produtor autenticado ainda não tem propriedade.
- Propriedades de Alfa no mock: Fazenda Norte e Sítio São João. Fazenda Sul só existe para Beta.
- Cadastro novo (`usuarioAtual=novo`) começa sem propriedade e registra o nome no próprio checkout.
- GET `/pedidos` no mock exige sessão; pedido de outro dono devolve `ACESSO_PEDIDO_NEGADO`.
- Retaguarda sem sessão redireciona para `/entrar?origem=…` (ADR-007). Alfa autenticado permanece e vê `ACESSO_NEGADO`.
- Operador em `/meus-pedidos` não vê lista própria; aponta para Pedidos da revenda.
- Título da lista da retaguarda é “Pedidos da revenda” (não “Pedidos do backoffice”).
- `eng-ui-screenshot` ausente: VC-01–VC-13 sem bundle companion; sem pacote substituto.
- Peer review e walk de QA adiados (Phase D / Phase C). Flag-only neste checkpoint.

## Learnings

- Seed ureia continua indisponível @ 178.00; e2e adiciona AURORA @ 620.00.
- POST `/pedidos` no mock agora exige sessão e honra `Idempotency-Key`.
- `getByLabel("Propriedade")` colide com “Nome da propriedade”; usar `{ exact: true }` ou `data-od-id`.
- Depois do login no checkout a identidade some; “voltar preserva” afirma propriedade/rascunho, não o e-mail.
- Título “Pedidos da revenda” no `h1` e no `TopoOperador` quebra `getByText` em modo estrito.

## Files / Surfaces

- `frontend/src/features/cart/PaginaCarrinho.tsx`
- `frontend/src/features/checkout/PaginaCheckout.tsx`
- `frontend/src/features/my-orders/{PaginaMeusPedidos,PaginaDetalhePedido,DetalhePedido}.tsx`
- `frontend/src/features/backoffice-orders/{PaginaPedidosRetaguarda,PaginaDetalheRetaguarda,DetalhePedidoRetaguarda,api}.ts(x)`
- `frontend/src/shell/LayoutApp.tsx`, `frontend/src/estilos.css`
- `frontend/e2e/{cart,checkout,my-orders,backoffice-orders,identity-topo}.spec.ts`, `frontend/e2e/helpers/apiMock.ts`
- QA: reset CART/CHK/ORD listados em task_04; novo `RET-visual-loja`

## Errors / Corrections

- E2E-037/037b/037c/038/039/040: labels ambíguos — `getByLabel(..., { exact: true })`.
- E2E-038: após identificar, o e-mail some; preservar rascunho/propriedade; item inelegível no re-login.
- E2E-042: mock só negava quando `usuarioAtual === "beta"`; agora qualquer dono diferente recebe 403.
- E2E-035: `getByText("Pedidos da revenda")` → heading na lista e link no catálogo do operador.

## Ready for Next Run

- Playwright focado 16/16 (cart/checkout/my-orders/backoffice) e regressão 52/52 com catalog + identity-topo, 2026-08-30.
- `eng-ui-screenshot` ausente: VC-01–VC-13 sem bundle companion.
- Próxima detect-phase esperada: `task_05` (qa-report).
