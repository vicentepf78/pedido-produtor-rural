---
status: completed
title: Catálogo paginado, sessão e contratos HTTP
type: backend
complexity: high
---

# Task 1: Catálogo paginado, sessão e contratos HTTP

## Overview

Esta fatia altera a paginação e o filtro do catálogo no backend e a sessão
de identidade, para que o frontend consiga carrossel, Carregar mais e
cabeçalho. Não cria módulos novos: `catalog` e `identity` já existem no
monólito modular. Sem estes contratos HTTP, as tarefas 02–04 não conseguem
montar a loja do MVP 1 sobre a prova do MVP 0.

<critical>
- ALWAYS READ `_spec.md` and its catalogs (`_user_stories.md`, `_dx.md`, `_uiux.md` when present, `_tests.md`) before starting
- REFERENCE `_spec.md` Part II for implementation details — do not duplicate here
- FOCUS ON "WHAT" — describe what needs to be accomplished, not how
- MINIMIZE CODE — show code only to illustrate current structure or problem areas
- TESTS REQUIRED — implement every test case assigned in ## Tests
</critical>

<requirements>
- MUST alinhar `ConsultaCatalogo`, `BuscaProduto` e `Paginacao` à Parte II
  de `_spec.md` e ao contrato de `_dx.md` (`categoria`, `consulta`,
  `pagina`, `tamanhoPagina`); `Paginacao` MUST rejeitar tamanho fora do
  conjunto **antes** de consultar o repositório.
- MUST usar `tamanhoPagina` padrão `10` e aceitar somente `10`, `15`,
  `30` e `50`. `tamanhoPagina=24` (default do MVP 0) MUST devolver
  `TAMANHO_PAGINA_INVALIDO`. MUST NOT haver shim, fallback nem
  compatibilidade com o cliente antigo.
- MUST listar o recorte da categoria quando `consulta` estiver ausente,
  vazia ou só com espaços — NÃO uma página vazia (quebra consciente do
  MVP 0 em `ServicoCatalogo`).
- MUST aceitar `categoria` ausente, em branco ou `Todos` (todos os
  elegíveis), e os valores `Sementes`, `Fertilizantes` e `Correção`.
  `Defensivos` e qualquer outro valor MUST devolver `CATEGORIA_INVALIDA`.
- MUST excluir produtos regulamentados de `itens` e de `total`; detalhe
  de regulamentado ou inexistente continua `PRODUTO_NAO_ELEGIVEL` sem
  revelar o conteúdo.
- MUST expor `GET /api/v1/autenticacao/sessao` com `permitAll`; convidado
  responde `{ "autenticado": false }`; autenticado devolve `idUsuario`,
  `nome`, `email` e `papeis` conforme `_dx.md`. Senha nunca na resposta.
- MUST fazer `POST /api/v1/autenticacao/cadastro` e
  `POST /api/v1/autenticacao/entrada` devolverem `email` e `papeis` além
  de `idUsuario` e `nome`. Cadastro pela loja MUST criar somente
  `PRODUTOR`.
- MUST NOT criar tabelas nem colunas novas; MUST NOT integrar Google nem
  outro provedor externo; MUST NOT publicar endpoint de listagem de
  categorias (o carrossel é conjunto fechado no cliente).
- MUST NOT criar módulos, pacotes internos ou schemas novos; `catalog` e
  `cart` permanecem CLOSED com as `allowedDependencies` atuais.
- MUST implementar todo ID de teste atribuído em `## Tests`; MUST NOT
  implementar UT-071 nem UT-072 (pertencem à task_02).
- MUST NOT implementar telas, rotas ou componentes do frontend
  (`/catalogo`, `/entrar`, `/cadastro`, topo, carrossel). Essas
  superfícies são das tarefas 02–04, que consomem este contrato.
- MUST NOT inventar regra comercial, fiscal, regulatória ou de
  segurança; decisões em aberto ficam nas Open Questions da spec.
</requirements>

## Subtasks

- [ ] 1.1 Alinhar `ConsultaCatalogo`, `BuscaProduto` e `Paginacao` ao
      contrato da Parte II e de `_dx.md` (categoria + tamanhos fechados).
- [ ] 1.2 Fazer `consulta` ausente, vazia ou só com espaços listar o
      recorte da categoria, e não uma página vazia.
- [ ] 1.3 Aceitar somente `Todos`, `Sementes`, `Fertilizantes` e
      `Correção`; recusar `Defensivos` e demais valores.
- [ ] 1.4 Fechar `tamanhoPagina` em `10` (padrão), `15`, `30` e `50`;
      recusar `24` e qualquer outro valor sem shim.
- [ ] 1.5 Garantir que produto regulamentado nunca entre em `itens` nem
      em `total`.
- [ ] 1.6 Publicar `GET /api/v1/autenticacao/sessao` como `permitAll`.
- [ ] 1.7 Devolver `email` e `papeis` em cadastro e entrada; cadastro
      pela loja só com papel `PRODUTOR`.
- [ ] 1.8 Mapear `CATEGORIA_INVALIDA` e `TAMANHO_PAGINA_INVALIDO` nas
      respostas HTTP de `_dx.md`.
- [ ] 1.9 Manter os contratos HTTP já existentes de carrinho, pedido,
      propriedade e retaguarda passando sob a sessão e o catálogo novos.
- [ ] 1.10 Implementar todos os casos unitários atribuídos em `## Tests`.
- [ ] 1.11 Implementar todos os casos de integração atribuídos em
      `## Tests`.
- [ ] 1.12 Recolocar os cenários QA `CAT-*` e `AUTH-*` em `untested` e
      criar os que faltarem para paginação do catálogo e `GET sessao`.

## Implementation Details

Não há módulo novo. A fatia altera contratos públicos já existentes em
`catalog` e `identity`. Seguir `_spec.md` Parte II (Interfaces principais,
Modelos de dados, Endpoints da API, Sequenciamento itens 2 e 3) e
`_dx.md`. Não duplicar assinaturas aqui. Classes, métodos, atributos e
campos de API em português brasileiro, `camelCase` quoted. Aplicar as
skills `coding-guidelines`, `java-junit` e `modular-architecture`.

Estado atual que esta tarefa quebra de propósito:

- `ServicoCatalogo.listarProdutosVisiveis` trata `consulta` em branco
  como página vazia (`itens=[]`, `total=0`).
- `Paginacao.de` usa default `24` e teto `100`.
- `BuscaProduto` só carrega `consulta`; `CatalogoApi` não aceita
  `categoria`.
- `AutenticacaoApi` publica csrf, cadastro, entrada e saída; falta
  `GET /sessao`. `RespostaAutenticacao` não inclui `email`.
- `ConfiguracaoSeguranca` não lista `GET /api/v1/autenticacao/sessao` em
  `permitAll`.
- `CatalogoIT` ainda envia `tamanhoPagina=24` (vai passar a ser
  `TAMANHO_PAGINA_INVALIDO`).

`RepositorioProduto` ainda não recorta por `categoria`. `UsuarioAutenticado`
já carrega `email` e `papel`; o vazamento está na API, não no registro de
sessão. `ServicoIdentidade.cadastrar` já grava somente `PRODUTOR`. Sem
migração Flyway nova: `Produto.categoria`, `Produto.regulado` e
`Usuario.papel` já existem.

Pontos de integração: `ConsultaCatalogo` continua o único contrato
público de catálogo que `cart` e a API consomem; `ConsultaIdentidade` /
`ComandoIdentidade` continuam o contrato público de identidade. Outro
módulo não importa entidade, repositório, tabela nem DTO de fornecedor.

### Relevant Files

- `.compozy/tasks/pedidos-insumos-mvp1/_spec.md` — Parte II:
  `ConsultaCatalogo`, `BuscaProduto`, `Paginacao`, `ConsultaIdentidade`,
  `GET sessao`, quebra do vazio em busca em branco.
- `.compozy/tasks/pedidos-insumos-mvp1/_dx.md` — query params do
  catálogo, `GET /api/v1/autenticacao/sessao`, corpos de cadastro/entrada
  com `email` e `papeis`, códigos `CATEGORIA_INVALIDA` e
  `TAMANHO_PAGINA_INVALIDO`.
- `.compozy/tasks/pedidos-insumos-mvp1/_tests.md` — definições canônicas
  de cada ID atribuído; ler o caso inteiro antes de escrever o teste.
- `.compozy/tasks/pedidos-insumos-mvp1/_user_stories.md` — US-001 a
  US-003 (descoberta) e US-005 (identidade visível).
- `backend/src/main/java/br/agriplataforma/catalog/application/ServicoCatalogo.java`
  — `consulta` em branco vira página vazia; precisa listar o recorte.
- `backend/src/main/java/br/agriplataforma/catalog/application/Paginacao.java`
  — default 24 / máx. 100; fechar em 10 / 15 / 30 / 50.
- `backend/src/main/java/br/agriplataforma/catalog/application/BuscaProduto.java`
  — só `consulta`; incluir `categoria`.
- `backend/src/main/java/br/agriplataforma/catalog/application/ConsultaCatalogo.java`
  — contrato público de listagem e detalhe visível.
- `backend/src/main/java/br/agriplataforma/catalog/api/CatalogoApi.java`
  — query params públicos; falta `categoria`.
- `backend/src/main/java/br/agriplataforma/catalog/api/TratamentoExcecoesCatalogo.java`
  — hoje mapeia `PRODUTO_INDISPONIVEL` / `PRODUTO_NAO_ELEGIVEL`; precisa
  dos códigos novos de categoria e tamanho.
- `backend/src/main/java/br/agriplataforma/catalog/infrastructure/RepositorioProduto.java`
  — consultas atuais sem filtro de categoria.
- `backend/src/main/java/br/agriplataforma/identity/api/AutenticacaoApi.java`
  — csrf / cadastro / entrada / saida; falta sessao.
- `backend/src/main/java/br/agriplataforma/identity/api/RespostaAutenticacao.java`
  — `idUsuario`, `nome`, `papeis`; falta `email`.
- `backend/src/main/java/br/agriplataforma/identity/infrastructure/ConfiguracaoSeguranca.java`
  — matriz HTTP; `GET /sessao` ainda não é `permitAll`.
- `backend/src/main/java/br/agriplataforma/identity/application/ServicoIdentidade.java`
  — `usuarioAtual()`, cadastro sempre `PRODUTOR`.
- `backend/src/main/java/br/agriplataforma/identity/application/ConsultaIdentidade.java`
  — contrato que `GET /sessao` deve ler.
- `backend/src/main/java/br/agriplataforma/identity/application/ComandoIdentidade.java`
  — `cadastrar` / `entrar`.
- `backend/src/main/java/br/agriplataforma/identity/application/UsuarioAutenticado.java`
  — já inclui `email` e `papel`.
- `backend/src/test/java/br/agriplataforma/catalog/api/CatalogoIT.java`
  — ITs de catálogo ainda no contrato MVP 0 (`tamanhoPagina=24`).
- `backend/src/test/java/br/agriplataforma/identity/api/CadastroSessaoIT.java`
  — cadastro/entrada sem `GET /sessao` nem `email` na resposta.
- `backend/src/test/java/br/agriplataforma/ModulithArchitectureTest.java`
  — UT-075 / UT-076 e limites CLOSED.
- `CLAUDE.md` — monólito modular, camelCase quoted, sem inventar regra
  comercial.

### Dependent Files

- `backend/src/main/java/br/agriplataforma/catalog/package-info.java` —
  `catalog` permanece CLOSED (`allowedDependencies` só
  `tenant :: application`).
- `backend/src/main/java/br/agriplataforma/cart/package-info.java` —
  `cart` permanece CLOSED; consome só `catalog :: application`.
- `backend/src/main/java/br/agriplataforma/identity/api/SessaoHttp.java`
  — cookie `sessao` HttpOnly já existente; `GET /sessao` lê o mesmo
  contexto.
- `backend/src/main/java/br/agriplataforma/identity/api/TratamentoExcecoesIdentidade.java`
  — erros de cadastro/entrada sem vazar senha.
- `backend/src/test/java/br/agriplataforma/catalog/ConsultaCatalogoTest.java`
  — unitários de catálogo a remapearem para UT-001–UT-024 e UT-073.
- `backend/src/test/java/br/agriplataforma/identity/CadastroAutenticacaoTest.java`
  — unitários de identidade a remapearem para UT-025–UT-035.
- `backend/src/test/java/br/agriplataforma/identity/SessaoExpiradaTest.java`
  — sessão inválida sem apagar carrinho (UT-035 / IT-045).
- `backend/src/test/java/br/agriplataforma/cart/ServicoCarrinhoTest.java`
  — UT-036–UT-046.
- `backend/src/test/java/br/agriplataforma/cart/api/CarrinhoConvidadoIT.java`
  — IT-013, IT-014, IT-026–IT-028, IT-034, IT-044.
- `backend/src/test/java/br/agriplataforma/order/ServicoPedidoTest.java`
  — UT-047–UT-062.
- `backend/src/test/java/br/agriplataforma/order/api/PedidosIT.java` —
  IT-015–IT-017, IT-029, IT-030, IT-037, IT-039, IT-047, IT-048.
- `backend/src/test/java/br/agriplataforma/order/api/ConfirmacaoErpIT.java`
  — confirmação `ACEITA` do `GatewayErpSimulado`.
- `backend/src/test/java/br/agriplataforma/backoffice/ServicoRetaguardaTest.java`
  — UT-065–UT-070, UT-074.
- `backend/src/test/java/br/agriplataforma/backoffice/api/RetaguardaIT.java`
  — IT-018, IT-019, IT-031, IT-038, IT-049.
- `backend/src/test/java/br/agriplataforma/integration/GatewayErpSimuladoTest.java`
  — UT-058.
- `backend/src/test/java/br/agriplataforma/MigracaoEArquiteturaIT.java`
  — IT-023 (Flyway em PostgreSQL vazio + `ApplicationModules.verify()`).
- `docs/qa/scenarios/CAT-*.md` e `docs/qa/scenarios/AUTH-*.md` — reset
  para `untested`; cenários novos se paginação CAT / `GET sessao` não
  existirem.
- `frontend/` — consumidor deste contrato nas tarefas 02–04; esta
  tarefa NÃO altera telas.

### Related ADRs

- [ADR-001: Recorte do MVP 1 na loja do produtor](adrs/adr-001-recorte-loja-do-produtor.md)
  — esta fatia entrega só contratos da loja; sem backoffice amplo, Google,
  Agrofit ou ERP real.
- [ADR-003: Carrossel de categorias, busca composta e Carregar mais](adrs/adr-003-descoberta-por-categoria-busca-e-carregar-mais.md)
  — categoria e busca somam no servidor; Defensivos fora; Carregar mais é
  `pagina+1` com os mesmos filtros.
- [ADR-005: Retorno após Entrar, Pedido visível e tamanho da lista](adrs/adr-005-retorno-pos-entrar-e-tamanho-da-lista.md)
  — conjunto fechado 10 / 15 / 30 / 50; padrão 10; a API devolve `email`
  e `papeis` para o topo.
- [ADR-007: Conta de operador na Entrar da loja vai à retaguarda](adrs/adr-007-operador-na-entrar-da-loja.md)
  — entrada de operador devolve `papeis=["OPERADOR_REVENDA"]`; o cliente
  (task_02) decide o destino. Cadastro pela loja continua só `PRODUTOR`.

### Web/Docs Impact

- `web/`: none — checked surfaces: this repo has no Compozy `web/` or
  `packages/site`; storefront is `frontend/` and is consumed by tasks
  02–04 after this contract.
- `packages/site`: none — checked; reason: not a Compozy docs site.
- QA impact: reset `docs/qa/scenarios/CAT-*.md` and
  `docs/qa/scenarios/AUTH-*.md` to untested; add new content-addressed
  scenarios if CAT pagination / AUTH sessao are missing.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: none — checked manifests/hooks/MCP; this product is not
  a Compozy extension.
- Agent manageability: HTTP in `_dx.md` (catalog query params, GET
  sessao, error codes `CATEGORIA_INVALIDA`, `TAMANHO_PAGINA_INVALIDO`).
  No CLI/UDS.
- Config lifecycle: none — checked; no new `config.toml`; existing
  `agriplataforma.idTenantSemeado` unchanged.

## Deliverables

- `GET /api/v1/catalogo/produtos` com `categoria`, `consulta`, `pagina` e
  `tamanhoPagina` fechado conforme `_dx.md`.
- `GET /api/v1/autenticacao/sessao` `permitAll`, com corpo de convidado e
  de autenticado.
- Cadastro e entrada devolvendo `email` e `papeis`; cadastro só
  `PRODUTOR`.
- Nenhuma tabela, coluna, módulo ou tela de frontend nova.
- Every test case assigned in `## Tests` implemented and passing
  **(REQUIRED)**

## Tests

Cases assigned from `_tests.md`, the test contract — read each ID's full
definition there before writing tests.

- [x] UT-001, UT-002, UT-003, UT-004, UT-005, UT-006, UT-007, UT-008,
      UT-009, UT-010, UT-011, UT-012, UT-013, UT-014, UT-015, UT-016,
      UT-017, UT-018, UT-019, UT-020, UT-021, UT-022, UT-023, UT-024,
      UT-073 — `ConsultaCatalogo`, `BuscaProduto` e `Paginacao`: recorte
      com busca em branco, categorias fechadas, tamanhos 10/15/30/50,
      recusa de 24 e de Defensivos, regulamentado oculto, página 2 sem
      repetir IDs, concorrência de `pagina+1`.
- [x] UT-025, UT-026, UT-027, UT-028, UT-029, UT-030, UT-031, UT-032,
      UT-033, UT-034, UT-035 — `ConsultaIdentidade` e
      `ComandoIdentidade`: sessão atual (convidado, Alfa, operador),
      cadastro só `PRODUTOR`, entrada, e-mail duplicado, credenciais
      inválidas, encerrar sem apagar carrinho.
- [x] UT-036, UT-037, UT-038, UT-039, UT-040, UT-041, UT-042, UT-043,
      UT-044, UT-045, UT-046 — `ConsultaCarrinho` / `ServicoCarrinho`:
      convidado, quantidade, indisponível, regulamentado, preço único.
- [x] UT-047, UT-048, UT-049, UT-050, UT-051, UT-052, UT-053, UT-054,
      UT-055, UT-056, UT-057, UT-058, UT-059, UT-060, UT-061, UT-062 —
      `ComandoPedido` / `ConsultaPedido` / `GatewayErpSimulado`: criar,
      idempotência, snapshot, operador recusado, confirmação `ACEITA`.
- [x] UT-063, UT-064 — `ConsultaPropriedades` / `ComandoPropriedades`:
      propriedade própria no checkout.
- [x] UT-065, UT-066, UT-067, UT-068, UT-069, UT-070, UT-074 —
      `ConsultaRetaguarda`: lista e detalhe do operador, vazio, isolamento
      por tenant e por papel.
- [x] UT-075, UT-076 — limites Modulith: `ApplicationModules.verify()` e
      `catalog`/`cart` CLOSED.
- [x] IT-001, IT-002, IT-003, IT-004, IT-005, IT-006, IT-007, IT-008,
      IT-022, IT-032, IT-033, IT-035, IT-041, IT-042, IT-043 — catálogo
      HTTP: busca em branco lista o recorte, `categoria`, tamanhos
      fechados, `tamanhoPagina=24` recusado, regulamentado oculto.
- [x] IT-009, IT-010, IT-011, IT-012, IT-024, IT-025, IT-036, IT-040,
      IT-045, IT-046 — identidade HTTP: `GET /sessao` `permitAll`,
      cadastro/entrada com `email` e `papeis`, CSRF, saída, sessão
      inválida.
- [x] IT-013, IT-014, IT-026, IT-027, IT-028, IT-034, IT-044 — carrinho
      de convidado HTTP.
- [x] IT-015, IT-016, IT-017, IT-029, IT-030, IT-037, IT-039, IT-047,
      IT-048 — pedidos do produtor HTTP.
- [x] IT-020, IT-021 — propriedades do autenticado.
- [x] IT-018, IT-019, IT-031, IT-038, IT-049 — retaguarda HTTP.
- [x] IT-023 — Flyway em PostgreSQL vazio e verificação Modulith.

Os casos de destino por `origem` e topo do operador ficam na task_02.
Nenhum E2E nesta tarefa.

## Success Criteria

- Every assigned test case implemented and passing
- `consulta` ausente ou em branco lista o recorte elegível; a suíte
  antiga que esperava página vazia é substituída
- `tamanhoPagina=24` devolve `TAMANHO_PAGINA_INVALIDO`; omitir o
  parâmetro usa `10`
- `GET /api/v1/autenticacao/sessao` responde a convidado e a autenticado
  sem exigir login
- Cadastro e entrada devolvem `email` e `papeis`; cadastro só `PRODUTOR`
- Nenhuma tabela, coluna, módulo ou tela de frontend foi criada
- `make test` e `make test-integration` passam com evidência fresca de
  comando
