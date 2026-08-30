---
status: pending
title: Topo da loja, Entrar, Criar conta e destino por papel
type: frontend
complexity: high
---

# Task 2: Topo da loja, Entrar, Criar conta e destino por papel

## Overview

Entrega o cabeçalho de compra `TopoLoja` no lugar da `bottom-nav`, as
rotas próprias `/entrar` e `/cadastro` com retorno por `origem`, e o
destino por papel após a identidade (produtor volta à origem; operador
vai a `/retaguarda/pedidos`). Sem este recorte o produtor continua
preso ao login só no checkout e o operador cai no topo de compra.

<critical>
- ALWAYS READ `_spec.md` and its catalogs (`_user_stories.md`, `_dx.md`, `_uiux.md` when present, `_tests.md`) before starting
- REFERENCE `_spec.md` Part II for implementation details — do not duplicate here
- FOCUS ON "WHAT" — describe what needs to be accomplished, not how
- MINIMIZE CODE — show code only to illustrate current structure or problem areas
- TESTS REQUIRED — implement every test case assigned in ## Tests
</critical>

<requirements>
- MUST implementar S8 (`_uiux.md`): cabeçalho com Catálogo, Carrinho
  (badge), Checkout, Pedido e Entrar **ou** identificação curta + Sair;
  atalhos utilizáveis por toque e teclado em 390 px.
- MUST publicar as rotas de `_dx.md` `/entrar` e `/cadastro`, aceitando
  query `origem` só como caminho relativo da lista permitida:
  `/catalogo`, `/carrinho`, `/checkout`, `/meus-pedidos`,
  `/pedidos/{id}`, `/retaguarda/pedidos` e
  `/retaguarda/pedidos/{id}`; qualquer outro valor, inclusive URL
  absoluta externa, cai em `/catalogo` (invariante 10).
- MUST ler a sessão de `GET /api/v1/autenticacao/sessao` (task_01) e
  os `papeis` / `email` / `nome` das respostas de cadastro e entrada
  para decidir o topo e o destino.
- MUST, se `papeis` contém `OPERADOR_REVENDA`, ignorar `origem` da
  loja (inclusive `/checkout`) e navegar para `/retaguarda/pedidos`
  com `TopoOperador` (Pedidos da revenda, identificação, Sair) e sem
  Catálogo, Carrinho, Checkout nem Pedido (US-005.EC-8, ADR-007).
- MUST preservar o carrinho de convidado em Sair (um ou dois toques)
  e ao falhar credenciais; `POST /api/v1/autenticacao/saida` não apaga
  `chaveCarrinhoConvidado` (invariante 9, E2E-030).
- MUST, em `/meus-pedidos` sem conta, pedir Entrar (não listar pedido
  alheio) e, após sucesso com `origem=/meus-pedidos`, voltar a
  `/meus-pedidos` (US-006.EC-1, E2E-044).
- MUST manter o atalho Checkout visível no `TopoLoja` com carrinho
  vazio; a página já explica `CARRINHO_VAZIO` (US-006.EC-2, ADR-006).
- MUST enviar mutações de cadastro, entrada e saída com o CSRF já
  existente em `frontend/src/infra/http.ts` (`X-XSRF-TOKEN`).
- MUST NOT oferecer Entrar com Google, “esqueci a senha”, muralha de
  login antes do catálogo, nem o interruptor de protótipo
  “Modo retaguarda”.
- MUST NOT deixar `bottom-nav` no rodapé de nenhuma tela da loja.
- MUST NOT implementar o carrossel, a busca composta, o Carregar mais
  nem o restyle de checkout / pedido / retaguarda — isso é das
  tarefas 03 e 04.
- MUST ativar a skill `coding-guidelines` ao implementar.
</requirements>

## Visual Contract

Referência autoritativa:
`docs/design/opendesign/pedidos-insumos-mvp1/loja-insumos-agricolas.html`.
Viewport 390×844 é normativo; um desktop 1440×900 cobre o wrap do
topo. Cada linha abaixo é um estado nomeado — não usar “todos os
estados” nem “paridade de screenshot” como substituto.

| ID    | Reference artifact + state | Implementation target + state | Viewport | Fidelity  | Authorized differences + authority |
| ----- | -------------------------- | ----------------------------- | -------- | --------- | ---------------------------------- |
| VC-01 | `loja-insumos-agricolas.html` — `ProducerTopBar` convidado (`data-od-id="producer-top-bar"` + `btn-entrar`) em visão de catálogo | `/catalogo` — `TopoLoja` convidado com Entrar; sem `bottom-nav` | 390×844 | normative | Sem chip `S1` / `screen-label`; SKUs do HTML são demo visual, não produtos da semente; `_uiux.md` S8 |
| VC-02 | mesma referência — `ProducerTopBar` autenticado (`user-name` + `btn-sair`) | `/catalogo` — `TopoLoja` produtor com nome ou e-mail + Sair; sem Entrar | 390×844 | normative | Identificação vem de `GET /api/v1/autenticacao/sessao` (nome ou e-mail), não do `user` local do protótipo; ADR-005 |
| VC-03 | mesma referência — `AuthForm` modo Entrar (`s3-entrar`) | `/entrar` — formulário e-mail + senha e caminho para `/cadastro` | 390×844 | normative | Sem rótulo `S3 — Entrar`; sem Google; sem “esqueci a senha”; `_uiux.md` S3, ADR-002 |
| VC-04 | mesma referência — `AuthForm` modo Criar conta (`s4-criar-conta`) | `/cadastro` — formulário nome + e-mail + senha e caminho para `/entrar` | 390×844 | normative | Campo **nome** é obrigatório no contrato (`_dx.md` POST cadastro); o HTML de demo omite nome — a implementação o inclui; sem “esqueci a senha”; `_uiux.md` S4 |
| VC-05 | mesma referência — `AuthForm` Entrar com `error` (`CREDENCIAIS_INVALIDAS`) | `/entrar` — permanece na tela com “E-mail ou senha inválidos.”; carrinho intacto | 390×844 | normative | Mensagem de `_dx.md` (`CREDENCIAIS_INVALIDAS`); US-005.EC-1 |
| VC-06 | mesma referência — `MeusPedidosView` sem usuário (`s7-pedidos-logged-out`, `btn-pedidos-entrar`) | `/meus-pedidos` — pede Entrar; não revela lista | 390×844 | normative | Ação Entrar navega a `/entrar?origem=/meus-pedidos`, não ao checkout do MVP 0; US-006.EC-1 |
| VC-07 | mesma referência — `ProducerTopBar` com marca, atalhos e área do usuário na mesma faixa | `/catalogo` — `TopoLoja` convidado; atalhos e Entrar utilizáveis após wrap | 1440×900 | adjacent | Wrap da barra é esperado; hierarquia e rótulos iguais ao normativo; sem coluna “demo”; `_uiux.md` restrição mobile-first |

Diferenças autorizadas globais desta tarefa:

- O botão flutuante `demo-switch` (“Modo retaguarda” / “Modo
  produtor”) é só do protótipo — **não embarcar**.
- SKUs e fotos do HTML são demonstração visual, não o conjunto
  semeado; o fundo do catálogo nesta tarefa permanece o do MVP 0.
- O protótipo guarda usuário em estado local; a implementação usa a
  API real de sessão (`GET sessao`, `POST entrada` / `cadastro` /
  `saida`) entregue pela task_01.
- `TopoOperador` após login de operador precisa existir o suficiente
  para E2E-035 (sem atalhos de compra); o restyle visual de S9/S10 é
  da task_04.

Evidence for each row: `.compozy/tasks/pedidos-insumos-mvp1/evidence/visual/task_02/<contract-id>/{reference.png,implementation.png,side-by-side.png,diff.png,comparison.json,review.md}`

## Subtasks

- [ ] 2.1 Substituir a `bottom-nav` pelo `TopoLoja` em todas as telas
      da loja do produtor (Catálogo, Carrinho, Checkout, Pedido).
- [ ] 2.2 Exibir Entrar (convidado) ou nome/e-mail + Sair (produtor)
      a partir de `GET /api/v1/autenticacao/sessao`; atualizar a
      página preserva papel e carrinho.
- [ ] 2.3 Publicar `/entrar` e `/cadastro` com query `origem`,
      formulários de e-mail e senha (cadastro também nome) e link
      entre as duas telas.
- [ ] 2.4 Resolver o destino pós-sucesso pela whitelist de `origem`
      para PRODUTOR e ignorar `origem` quando o papel for
      `OPERADOR_REVENDA`.
- [ ] 2.5 Encerrar sessão pelo topo sem apagar o carrinho de
      convidado; dois toques em Sair geram uma única saída.
- [ ] 2.6 Em `/meus-pedidos` sem conta, pedir Entrar e devolver à
      mesma origem após sucesso.
- [ ] 2.7 Trocar o topo para `TopoOperador` após entrada de operador
      e manter `TopoLoja` (com `ACESSO_NEGADO`) se um produtor abrir
      `/retaguarda/pedidos`.
- [ ] 2.8 Implementar UT-071, UT-072 e os E2E atribuídos.
- [ ] 2.9 Gerar o pacote de evidência visual de cada linha VC-01–VC-07.

## Implementation Details

Depende da task_01 (`GET sessao` + `email` / `papeis` na resposta de
entrada e cadastro). Frontend `shell` e feature `identity` segundo a
Parte II: o shell não calcula preço; identity só lê o contrato
público de sessão. Padrões de CSRF e cookie já estão em
`frontend/src/infra/http.ts`. Referência de DX: rotas do navegador e
autenticação. Skills: `coding-guidelines`.

O checkout e a retaguarda **continuam com o visual do MVP 0** nesta
tarefa; só o destino, o topo e as telas próprias de identidade
mudam. Formulários S3/S4 desta tarefa não precisam substituir o
login inline do checkout — isso é da task_04.

### Relevant Files

- `_spec.md` Parte II — `ConsultaIdentidade` / `ComandoIdentidade`,
  invariantes 8–10, ordem de construção item 4.
- `_dx.md` — rotas `/entrar`, `/cadastro`, whitelist de `origem`,
  `GET sessao`, `POST cadastro` / `entrada` / `saida`.
- `_uiux.md` — S3, S4, S7 (estado “precisa Entrar”), S8; S11 só o
  necessário para o destino do operador.
- `_user_stories.md` — US-005, US-006.
- `_tests.md` — UT-071, UT-072, E2E-030, E2E-033–E2E-036,
  E2E-043–E2E-045.
- `frontend/src/shell/LayoutApp.tsx` — `bottom-nav` a remover;
  ponto de montagem do `TopoLoja`.
- `frontend/src/App.tsx` — sem `/entrar` nem `/cadastro`.
- `frontend/src/infra/http.ts` — CSRF (`GET /api/v1/autenticacao/csrf`
  + `X-XSRF-TOKEN`) e `invalidarCsrf` após mutação de sessão.
- `frontend/src/features/checkout/api.ts` — `cadastrar`, `entrar`,
  `sair` hoje acoplados ao checkout; a feature identity passa a
  ser a dona dessas chamadas.
- `docs/design/opendesign/pedidos-insumos-mvp1/loja-insumos-agricolas.html`
  — `ProducerTopBar`, `AuthForm`, `MeusPedidosView` deslogado.

### Dependent Files

- `frontend/src/features/identity/` — páginas `/entrar` e `/cadastro`,
  resolução de `origem`, consumo de sessão (arquivos novos).
- `frontend/src/shell/` — `TopoLoja`; `TopoOperador` mínimo para
  E2E-035 (sem restyle de S9/S10).
- `frontend/src/estilos.css` — regras de `.bottom-nav` a retirar;
  estilos do topo e dos formulários S3/S4.
- `frontend/src/features/my-orders/PaginaMeusPedidos.tsx` — convidado
  pede Entrar (hoje manda ao checkout).
- `frontend/src/estado/ProvedorCarrinho.tsx` — Sair não pode limpar
  o carrinho de convidado.
- `frontend/e2e/helpers/apiMock.ts` — `GET sessao`, `email` nas
  respostas, destino de operador; mocks atuais omitem `email`.
- `frontend/e2e/*.spec.ts` — atalhos passam do rodapé para o topo;
  novos casos desta tarefa.
- `frontend/src/features/checkout/PaginaCheckout.tsx` e
  `frontend/src/features/backoffice-orders/PaginaPedidosRetaguarda.tsx`
  — não restilizar; apenas conviver com o novo topo e com a sessão
  já estabelecida pela Entrar da loja.

### Related ADRs

- [ADR-002](adrs/adr-002-identificacao-convidado-e-tela-entrar.md) —
  convidado no catálogo; tela própria de Entrar; Google fora.
- [ADR-005](adrs/adr-005-retorno-pos-entrar-e-tamanho-da-lista.md) —
  volta à origem; Pedido visível sem conta; identificação + Sair;
  sem “esqueci a senha”.
- [ADR-006](adrs/adr-006-topo-do-operador-e-midia-de-demonstracao.md) —
  operador não usa o topo de compra; Checkout vazio não esconde o
  atalho.
- [ADR-007](adrs/adr-007-operador-na-entrar-da-loja.md) — conta
  OPERADOR_REVENDA na Entrar da loja vai à retaguarda.

### Web/Docs Impact

- `web/`: none — checked surfaces: este repo não tem `web/` Compozy;
  a UI vive em `frontend/`.
- `packages/site`: none — checked; reason: não há site de docs gerado.
- QA impact: resetar `docs/qa/scenarios/AUTH-cadastro-e-sessao.md`
  para `untested`; mintar `docs/qa/scenarios/AUTH-entrar-tela-propria.md`
  e `docs/qa/scenarios/NAV-topo-loja.md` como `untested`.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: none — checked surfaces: hooks, MCP, registries;
  reason: identidade da loja não abre ponto de extensão neste MVP.
- Agent manageability: o navegador usa `GET /api/v1/autenticacao/sessao`,
  `POST /api/v1/autenticacao/entrada`,
  `POST /api/v1/autenticacao/cadastro` e
  `POST /api/v1/autenticacao/saida` (mais CSRF). Sem CLI/UDS.
- Config lifecycle: none — checked; sem chave nova nem `config.toml`.

## Deliverables

- `TopoLoja` no cabeçalho; `bottom-nav` removida.
- Rotas `/entrar` e `/cadastro` com `origem` na whitelist e destino
  por papel.
- Sair no topo preserva o carrinho de convidado.
- Operador autenticado pela Entrar da loja chega em
  `/retaguarda/pedidos` com `TopoOperador`.
- Every test case assigned in `## Tests` implemented and passing **(REQUIRED)**
- Every Visual Contract row has a durable passing evidence bundle **(REQUIRED)**

## Tests

Cases assigned from `_tests.md`, the test contract — read each ID's full definition there before writing tests.

- [ ] UT-071, UT-072 — whitelist de `origem` do produtor; operador
      ignora `origem` da loja e resolve `/retaguarda/pedidos`; origem
      absoluta ou desconhecida cai em `/catalogo`.
- [ ] E2E-030 — Sair mantém itens de convidado; checkout vazio explica
      `CARRINHO_VAZIO`; atalho Checkout permanece no `TopoLoja`.
- [ ] E2E-033 — Entrar no topo a partir de `/catalogo`, `/carrinho`,
      `/checkout` e `/meus-pedidos` volta à origem com sessão e
      carrinho intactos.
- [ ] E2E-034 — credenciais inválidas, campos vazios, já autenticado
      em `/entrar`, cancelar/voltar.
- [ ] E2E-035 — operador em `/entrar?origem=/checkout` vai a
      `/retaguarda/pedidos` com `TopoOperador`.
- [ ] E2E-036 — cadastro com `origem=/meus-pedidos`; e-mail duplicado
      aponta para Entrar; sem “esqueci a senha”.
- [ ] E2E-043 — `TopoLoja` nas telas da loja; sem barra no rodapé;
      Sair (inclusive dois toques) volta a convidado e mantém o
      carrinho.
- [ ] E2E-044 — Pedido no topo sem conta pede Entrar; 390 px
      utilizável; produtor em `/retaguarda/pedidos` vê
      `ACESSO_NEGADO` com `TopoLoja`.
- [ ] E2E-045 — atualizar `/catalogo` preserva papel e carrinho.

## Success Criteria

- Every assigned test case implemented and passing
- Nenhuma tela da loja do produtor mostra `bottom-nav`
- `origem` absoluta ou fora da whitelist nunca navega para fora da
  loja
- Operador nunca permanece no topo de compra após Entrar
- Não há Google nem “esqueci a senha” nas telas S3/S4
- Every Visual Contract row is `PASS` with zero unresolved blocking divergence
