---
status: pending
title: Carrinho, checkout, pedidos e retaguarda no visual novo
type: frontend
complexity: high
---

# Task 4: Carrinho, checkout, pedidos e retaguarda no visual novo

## Overview

Fecha a jornada da loja no visual OpenDesign: restila S2, S5–S7 e S9–S11
depois do topo/identidade (task_02) e do catálogo que adiciona ao
carrinho (task_03). O checkout pula Entrar se o produtor já estiver
identificado; carrinho vazio mostra `CARRINHO_VAZIO` e o atalho Checkout
permanece no topo; propriedade continua só no checkout; o operador usa
`TopoOperador` sem atalhos de compra, no mesmo visual da loja. Não
inventa preço, Google nem importação de catálogo.

<critical>
- ALWAYS READ `_spec.md` and its catalogs (`_user_stories.md`, `_dx.md`, `_uiux.md` when present, `_tests.md`) before starting
- REFERENCE `_spec.md` Part II for implementation details — do not duplicate here
- FOCUS ON "WHAT" — describe what needs to be accomplished, not how
- MINIMIZE CODE — show code only to illustrate current structure or problem areas
- TESTS REQUIRED — implement every test case assigned in ## Tests
</critical>

<requirements>
- MUST restilar S2, S5, S6, S7, S9, S10 e S11 segundo o Visual Contract
  e `_uiux.md`, usando o artefato
  `docs/design/opendesign/pedidos-insumos-mvp1/loja-insumos-agricolas.html`.
- MUST preservar as regras do MVP 0 já cobertas no backend (task_01):
  carrinho de convidado, snapshot imutável, idempotência, propriedade
  própria, `DEPOSITO_PRINCIPAL` e leitura da retaguarda.
- MUST, no checkout, pular Entrar/Criar conta quando a sessão já for de
  produtor; se faltar identidade, oferecer Entrar ou Criar conta no
  próprio checkout (US-007.AC-1) com o mesmo contrato da task_02.
- MUST, com carrinho vazio em `/checkout`, exibir
  `CARRINHO_VAZIO` (“Adicione ao menos um produto antes do checkout.”)
  e manter o atalho Checkout no `TopoLoja` (ADR-006; US-006.EC-2).
- MUST cadastrar propriedade só no próprio checkout quando o produtor
  ainda não tiver nenhuma; MUST NOT criar área de conta nem fluxo de
  propriedade fora de S5.
- MUST aplicar `TopoOperador` em S9/S10: Pedidos da revenda,
  identificação e Sair — sem Catálogo, Carrinho, Checkout nem Pedido
  (US-009.AC-1; ADR-006).
- MUST usar o mesmo visual da loja na lista e no detalhe da retaguarda
  (ADR-004); a capacidade permanece inspecionar pedidos e a
  confirmação — sem manutenção de catálogo, mídia, notificação nem
  cadastro operacional.
- MUST fazer o operador chegar à retaguarda pela Entrar da loja
  (ADR-007); MUST NOT embarcar o botão de protótipo “Modo retaguarda”
  nem o login inline `s8-retaguarda-login`.
- MUST alinhar a cópia de retirada à regra já existente
  `DEPOSITO_PRINCIPAL` em `frontend/src/features/checkout/opcoes.ts`
  (“Retirar na loja (Centro)”); MUST NOT copiar endereços inventados
  do HTML (`Av. Brasil, 1200`, `Rod. BR-153, km 42`).
- MUST usar produtos e preços do seed/fixture da loja (ex.: ureia
  `198.00`), não os preços e nomes de demonstração do HTML.
- MUST NOT inventar precificação negociada, login Google, recuperação
  de senha, importação de catálogo, Agrofit, ERP real, pagamento,
  entrega, receita ou multi-loja (ADR-001).
- MUST gerar o pacote de evidência visual durável de cada linha do
  Visual Contract em
  `.compozy/tasks/pedidos-insumos-mvp1/evidence/visual/task_04/<id>/`.
- SHOULD reutilizar `FormularioEntrar` / `FormularioCadastro` da
  task_02 quando o checkout ainda precisar de identidade.
</requirements>

## Visual Contract

Referência: `docs/design/opendesign/pedidos-insumos-mvp1/loja-insumos-agricolas.html`.
O protótipo rotula a retaguarda como “S8”; o mapa desta spec é S9
(lista), S10 (detalhe) e S11 (`TopoOperador`). Viewport normativa
390×844; 1440×900 só para a tabela da retaguarda.

| ID    | Reference artifact + state | Implementation target + state | Viewport | Fidelity  | Authorized differences + authority |
| ----- | -------------------------- | ----------------------------- | -------- | --------- | ---------------------------------- |
| VC-01 | `loja-insumos-agricolas.html` `s2-carrinho-empty` — carrinho vazio | `/carrinho` — sem itens | 390×844 | normative | Copy de vazio pode seguir o estado explícito de `_uiux.md` S2; atalho Checkout permanece no `TopoLoja` (ADR-006) |
| VC-02 | `loja-insumos-agricolas.html` `s2-carrinho` — carrinho com linhas, qtd e total | `/carrinho` — ureia (ou fixture seed) com quantidade e totais | 390×844 | normative | Produto, preço unitário e total vêm do seed/API, não dos preços de demonstração do HTML |
| VC-03 | `loja-insumos-agricolas.html` `s5-checkout-empty` — checkout sem itens | `/checkout` — carrinho vazio | 390×844 | normative | Mensagem `CARRINHO_VAZIO` de `_dx.md` prevalece sobre o texto do protótipo; Checkout permanece no topo |
| VC-04 | `loja-insumos-agricolas.html` `s5-checkout-auth` — precisa identificar-se | `/checkout` — itens no carrinho, sessão ausente | 390×844 | normative | Formulários Entrar/Criar conta são os da task_02; sem Google e sem “esqueci a senha” |
| VC-05 | `loja-insumos-agricolas.html` `s5-checkout` — propriedade, retirada, resumo, Confirmar pedido | `/checkout` — produtor já identificado, pronto para confirmar | 390×844 | normative | Sem bloco de login; propriedade só aqui; retirada = regra `DEPOSITO_PRINCIPAL` existente, não endereços do HTML |
| VC-06 | `loja-insumos-agricolas.html` `s6-pedido-recebido` — “Pedido recebido” + id | `/pedidos/{idPedido}` — recém-confirmado | 390×844 | normative | Selo `ACEITA` do Mock ERP já existente; atualizar não cria outro pedido (`_dx.md`, US-008.EC-2) |
| VC-07 | `loja-insumos-agricolas.html` `s7-pedidos-empty` — nenhum pedido | `/meus-pedidos` — produtor sem pedidos | 390×844 | normative | Caminho explícito para `/catalogo` (US-008.EC-3) |
| VC-08 | `loja-insumos-agricolas.html` `s7-meus-pedidos` — lista com id, total, data | `/meus-pedidos` — um ou mais pedidos do produtor | 390×844 | normative | Visual desta tarefa; o formulário Entrar em si já é da task_02 |
| VC-09 | `loja-insumos-agricolas.html` `s7-pedidos-logged-out` — pede Entrar | `/meus-pedidos` — convidado | 390×844 | normative | Cartão “precisa Entrar” desta superfície; destino `/entrar?origem=/meus-pedidos` da task_02, sem vazar lista alheia |
| VC-10 | `loja-insumos-agricolas.html` `s8-retaguarda-lista` — tabela de pedidos da revenda | `/retaguarda/pedidos` — operador com pedidos | 390×844 | normative | Rótulo de spec S9; sem botão “Modo retaguarda”; operador veio pela Entrar (ADR-007); ids/totais do seed, não do HTML |
| VC-11 | `loja-insumos-agricolas.html` `s8-retaguarda-lista` — mesma tabela em desktop | `/retaguarda/pedidos` — operador com pedidos | 1440×900 | normative | Tabela legível em desktop; mesmo visual da loja (ADR-004); sem atalhos de compra |
| VC-12 | `loja-insumos-agricolas.html` `s8-retaguarda-detalhe` — snapshot de itens e checkout | `/retaguarda/pedidos/{idPedido}` — detalhe | 390×844 | normative | Rótulo de spec S10; snapshot imutável; retirada `DEPOSITO_PRINCIPAL`, não endereço inventado |
| VC-13 | `loja-insumos-agricolas.html` header operador — Pedidos da revenda, identificação, Sair | `/retaguarda/pedidos` (e detalhe) — `TopoOperador` | 390×844 | normative | Spec S11; sem Catálogo, Carrinho, Checkout nem Pedido; sem “Modo retaguarda” / “Modo produtor” |

Evidence for each row: `.compozy/tasks/pedidos-insumos-mvp1/evidence/visual/task_04/<contract-id>/{reference.png,implementation.png,side-by-side.png,diff.png,comparison.json,review.md}`

## Subtasks

- [ ] 4.1 Restilar S2 (carrinho vazio e povoado) no visual da loja, com
      totais e estados de quantidade inválida / indisponível.
- [ ] 4.2 Restilar S5: vazio com `CARRINHO_VAZIO`, precisa de identidade
      só se faltar sessão, pronto para confirmar com propriedade e
      retirada; Checkout permanece no topo.
- [ ] 4.3 Restilar S6 (“Pedido recebido”) e o detalhe reaberto, sem
      criar segundo pedido ao atualizar.
- [ ] 4.4 Restilar S7 (lista, vazia e cartão “precisa Entrar”); o
      formulário Entrar permanece o da task_02.
- [ ] 4.5 Restilar S9/S10 no visual da loja e aplicar S11
      (`TopoOperador`) sem atalhos de compra.
- [ ] 4.6 Remover login inline de operador e o seletor de demonstração
      “Modo retaguarda”; chegada só pela Entrar (ADR-007).
- [ ] 4.7 Implementar os casos E2E desta tarefa (sem UT/IT novos).
- [ ] 4.8 Gerar o pacote de evidência visual de VC-01–VC-13.

## Implementation Details

Superfície só de frontend. Contratos HTTP de carrinho, pedido e
retaguarda já existem (task_01); sessão e `TopoLoja` vêm da task_02;
adicionar ao carrinho vem da task_03. Padrões de domínio, snapshot e
erros: `_spec.md` Parte II e `_dx.md` — não duplicar aqui.

O checkout atual ainda infere sessão por `GET propriedades` e embute
login/cadastro. Com sessão explícita da task_02, S5 deve pular essa
identidade quando `papeis` já incluir produtor. Propriedade continua
só em S5. Retirada permanece o conjunto já semeado
(`DEPOSITO_PRINCIPAL`); o HTML do protótipo não autoriza novos
endereços.

A retaguarda atual esconde a nav e traz login inline de operador. Esta
tarefa troca isso por `TopoOperador` e pelo destino da Entrar. O botão
de protótipo “Modo retaguarda” é chrome de demonstração e não embarca.

Skills: `coding-guidelines`.

### Relevant Files

- `_spec.md` — recorte da loja, invariantes 1–10, ordem de construção 6–7.
- `_uiux.md` — S2, S5, S6, S7, S9, S10, S11 e estados.
- `_user_stories.md` — US-004, US-007, US-008, US-009.
- `_dx.md` — `CARRINHO_VAZIO`, `DADOS_CHECKOUT_OBRIGATORIOS`,
  `DEPOSITO_PRINCIPAL`, GETs de pedido e retaguarda.
- `_tests.md` — definições completas de E2E-029–E2E-032 (exceto
  E2E-030), E2E-037–E2E-042, E2E-046–E2E-049.
- `docs/design/opendesign/pedidos-insumos-mvp1/loja-insumos-agricolas.html`
  — referência visual autoritativa.
- `docs/design/opendesign/pedidos-insumos-mvp1/brand-spec.md` — tokens.
- `frontend/src/features/cart/PaginaCarrinho.tsx` — S2 hoje.
- `frontend/src/features/checkout/PaginaCheckout.tsx` — login inline a
  condicionar.
- `frontend/src/features/checkout/opcoes.ts` — retirada
  `DEPOSITO_PRINCIPAL` vigente.
- `frontend/src/features/my-orders/PaginaMeusPedidos.tsx` — S7 hoje.
- `frontend/src/features/my-orders/PaginaDetalhePedido.tsx` — S6 hoje.
- `frontend/src/features/backoffice-orders/PaginaPedidosRetaguarda.tsx`
  — login inline de operador a remover.
- `frontend/src/features/backoffice-orders/PaginaDetalheRetaguarda.tsx`
  — S10 hoje.
- `frontend/src/shell/LayoutApp.tsx` — nav; `TopoOperador` da task_02.
- `frontend/e2e/cart.spec.ts`, `frontend/e2e/checkout.spec.ts`,
  `frontend/e2e/my-orders.spec.ts`,
  `frontend/e2e/backoffice-orders.spec.ts` — jornadas MVP 0 a
  realinhar aos IDs desta tarefa.

### Dependent Files

- `frontend/src/features/cart/PaginaCarrinho.tsx` — visual S2.
- `frontend/src/features/cart/api.ts` — cliente já existente.
- `frontend/src/features/checkout/PaginaCheckout.tsx` — visual S5 e
  identidade condicional.
- `frontend/src/features/checkout/api.ts` — confirmar pedido e
  propriedades.
- `frontend/src/features/my-orders/PaginaMeusPedidos.tsx` — visual S7.
- `frontend/src/features/my-orders/PaginaDetalhePedido.tsx` — visual S6.
- `frontend/src/features/my-orders/DetalhePedido.tsx` — snapshot do
  produtor.
- `frontend/src/features/backoffice-orders/PaginaPedidosRetaguarda.tsx`
  — lista S9 sem login inline.
- `frontend/src/features/backoffice-orders/PaginaDetalheRetaguarda.tsx`
  — detalhe S10.
- `frontend/src/features/backoffice-orders/DetalhePedidoRetaguarda.tsx`
  — snapshot do operador.
- `frontend/src/shell/LayoutApp.tsx` — `TopoOperador` nas rotas de
  retaguarda.
- `frontend/src/estilos.css` — tokens do artefato, sem importar a
  folha do HTML.
- `frontend/e2e/cart.spec.ts`, `frontend/e2e/checkout.spec.ts`,
  `frontend/e2e/my-orders.spec.ts`,
  `frontend/e2e/backoffice-orders.spec.ts` — E2E desta tarefa.
- `frontend/e2e/visual-task04.spec.ts` — evidência VC-01–VC-13 (caminho
  mvp1).

### Related ADRs

- [ADR-001: Recorte do MVP 1 na loja do produtor](adrs/adr-001-recorte-loja-do-produtor.md)
  — sem importação de catálogo, Google, preço negociado nem backoffice
  amplo; a retaguarda só inspeciona.
- [ADR-004: Retaguarda no mesmo visual da loja](adrs/adr-004-retaguarda-no-mesmo-visual.md)
  — S9/S10 usam o visual da loja sem crescer a capacidade.
- [ADR-006: Topo do operador, mídia de demonstração e checkout vazio](adrs/adr-006-topo-do-operador-e-midia-de-demonstracao.md)
  — `TopoOperador` sem atalhos de compra; checkout vazio explica e o
  atalho permanece.

### Web/Docs Impact

- `web/`: none — checked surfaces: `web/`; reason: este repositório é a
  loja do produto, não o daemon Compozy.
- `packages/site`: none — checked surfaces: `packages/site/`.
- QA impact: reset/add `untested` —
  `docs/qa/scenarios/CART-convidado-persiste.md`,
  `docs/qa/scenarios/CART-monta-e-ajusta.md`,
  `docs/qa/scenarios/CHK-identificacao-tardia.md`,
  `docs/qa/scenarios/CHK-erros-validacao.md`,
  `docs/qa/scenarios/ORD-confirma-e-revisita.md`,
  `docs/qa/scenarios/ORD-acesso-negado.md`,
  `docs/qa/scenarios/ORD-backoffice-lista.md`,
  `docs/qa/scenarios/ORD-backoffice-vazio-ou-negado.md`,
  mais cenários novos `RET-visual-loja`.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: none — checked surfaces: manifestos, hooks, skills,
  tools/resources, registries, bridge SDKs, MCP sidecars, `config.toml`
  de Compozy; reason: sem nova porta nem extensão.
- Agent manageability: HTTP da retaguarda já existe —
  `GET /api/v1/retaguarda/pedidos`,
  `GET /api/v1/retaguarda/pedidos/{idPedido}` — e os erros
  `CARRINHO_VAZIO`, `DADOS_CHECKOUT_OBRIGATORIOS`,
  `ACESSO_PEDIDO_NEGADO`, `ACESSO_NEGADO`. Sem CLI/UDS novos.
- Config lifecycle: none for `config.toml`. Sem chave nova; cookie
  `sessao` e `agriplataforma.gatewayErp=simulado` permanecem.

## Deliverables

- S2, S5, S6, S7, S9, S10 e S11 no visual novo, com os estados do
  Visual Contract.
- Checkout que pula identidade quando o produtor já está autenticado;
  propriedade só no checkout; retirada `DEPOSITO_PRINCIPAL`.
- Retaguarda no visual da loja com `TopoOperador`, sem botão “Modo
  retaguarda” e sem login inline de operador.
- Every test case assigned in `## Tests` implemented and passing **(REQUIRED)**
- Every Visual Contract row has a durable passing evidence bundle **(REQUIRED)**

## Tests

Cases assigned from `_tests.md`, the test contract — read each ID's full definition there before writing tests.

- [ ] E2E-029 — convidado monta o carrinho, altera, remove, converge
      duas alterações e mantém o mesmo preço unitário após Entrar.
- [ ] E2E-031 — quantidade inválida, produto indisponível e mesma
      linha ao adicionar de novo.
- [ ] E2E-032 — cem linhas de fixture com totais legíveis.
- [ ] E2E-037 — checkout com identidade, propriedade própria
      (Fazenda Norte, não Sul) ou cadastro de “Fazenda Santa Luzia”
      no próprio checkout, e `DEPOSITO_PRINCIPAL`.
- [ ] E2E-038 — dados obrigatórios, voltar preserva, sessão expirada e
      item inelegível.
- [ ] E2E-039 — duas confirmações = um pedido; interrupção sem meio
      termo.
- [ ] E2E-040 — “Pedido recebido” + `ACEITA` em ≤1 s; refresh não
      duplica; reabrir o snapshot.
- [ ] E2E-041 — lista visual de meus pedidos (vazia, povoada, muitos);
      convidado pede Entrar (formulário já da task_02) e cancelar não
      vaza lista.
- [ ] E2E-042 — pedido de outro produtor e endereço inexistente.
- [ ] E2E-046 — lista da retaguarda no visual da loja, atualizar,
      outra empresa ausente, `ACEITA` estável.
- [ ] E2E-047 — detalhe da retaguarda com snapshot imutável.
- [ ] E2E-048 — `TopoOperador` sem atalhos de compra; Sair some a
      lista; operador não usa meus pedidos.
- [ ] E2E-049 — produtor negado, lista vazia, deep link sem sessão e
      identificação falha.

Sem UT/IT nesta tarefa — o backend já está na task_01.

## Success Criteria

- Every assigned test case implemented and passing
- Checkout identificado não redispara Entrar; checkout vazio mostra
  `CARRINHO_VAZIO` e o atalho Checkout permanece
- Propriedade só existe no checkout; retirada usa `DEPOSITO_PRINCIPAL`
- `TopoOperador` sem Catálogo, Carrinho, Checkout nem Pedido
- Nenhum botão “Modo retaguarda”, login Google, preço inventado ou
  importação de catálogo
- Every Visual Contract row is `PASS` with zero unresolved blocking divergence
