# Especificação de testes: pedidos de insumos agrícolas MVP 1

Contrato de teste canônico do MVP 1. Complementar a `_spec.md`.
Derivado de `_user_stories.md` (comportamento), `_spec.md` Parte II
(componentes), `_dx.md` (rotas e códigos de erro) e `_uiux.md` (S1–S11).

Numeração própria desta versão: `UT-001`, `IT-001` e `E2E-001` recomeçam
aqui. IDs do MVP 0 não se aplicam a este arquivo.

## Strategy

- Frameworks e harnesses: JUnit 5 no domínio e na aplicação; IT de API e
  migração com PostgreSQL via Testcontainers; Spring Modulith para
  limites de módulo; Playwright nas jornadas de navegador (viewport
  móvel 390 px e desktop).
- Fixtures: as mesmas 30 produtos não regulamentados em todos os
  ambientes de teste, mais um produto regulamentado oculto que nunca
  entra em `itens` nem em `total`. Contas fixture Alfa
  (`produtor.alfa@example.com`), Beta (`produtor.beta@example.com`) e
  operador (`operador.revenda@example.com`). Senha só como
  **senha de fixture** no código de teste — nunca em log, memória de
  task ou documentação como segredo de produção.
- Fake somente na porta `GatewayErp` (`GatewayErpSimulado`). Demais
  fronteiras I/O dos unitários usam dublês locais; ITs usam a fiação
  real entre módulos e o banco.
- Execução: suíte unitária no módulo Gradle/Maven do backend; ITs
  sobem o contexto Spring com Testcontainers; Playwright contra a loja
  nas rotas de `_dx.md`. Isolar o estado do banco entre testes
  paralelos. Nunca alterar configuração de processo inteiro.
- Convenções: um comportamento observável por caso; valores e códigos
  de erro literais de `_dx.md`; testes de unidade rotulados
  `happy`, `error`, `boundary`, `concurrency`, `idempotency`,
  `ordering` ou `state`.

Quebra consciente MVP 0 → MVP 1 (sem shim):

- `consulta` ausente ou em branco lista o recorte elegível — não
  devolve página vazia.
- `tamanhoPagina=24` (default do MVP 0) é recusado com
  `TAMANHO_PAGINA_INVALIDO`. Aceitos: `10`, `15`, `30`, `50`. Padrão
  `10`.

## Coverage Matrix

| Fonte | Comportamento | Unit | Integration | E2E |
| --- | --- | --- | --- | --- |
| US-001 | Escolhe categoria no carrossel | UT-019, UT-024 | IT-001, IT-003, IT-032 | E2E-001, E2E-002 |
| US-001.EC-1 | Foto de categoria ausente | UT-020 | — | E2E-004 |
| US-001.EC-2 | Categoria sem produtos elegíveis | UT-022 | IT-041 | E2E-005 |
| US-001.EC-3 | Toques rápidos em duas categorias | UT-018 | — | E2E-006 |
| US-001.EC-4 | Carrossel não cabe na tela | — | — | E2E-007 |
| US-001.EC-5 | Convidado ou conta expirada | UT-025 | IT-001, IT-009 | E2E-008 |
| US-001.EC-6 | Endereço de produto oculto ou regulamentado | UT-012, UT-021 | IT-008, IT-022 | E2E-009 |
| US-001.EC-7 | Interrupção ao trocar categoria | — | — | E2E-010 |
| US-001.EC-8 | Conjunto de 30 itens, quatro categorias | UT-024 | IT-001 | E2E-011 |
| US-002 | Pesquisa somando à categoria | UT-002 | IT-003 | E2E-012, E2E-013 |
| US-002.EC-1 | Busca em branco respeita só a categoria | UT-001, UT-003 | IT-002 | E2E-015 |
| US-002.EC-2 | Nenhum produto corresponde | UT-016 | IT-042 | E2E-016 |
| US-002.EC-3 | Texto hostil, só espaços ou longo | UT-017 | IT-043 | E2E-014 |
| US-002.EC-4 | Trocar categoria com busca preenchida | UT-023 | — | E2E-018 |
| US-002.EC-5 | Trocar busca com categoria marcada | UT-023 | IT-003 | E2E-019 |
| US-002.EC-6 | Dois textos em sequência rápida | UT-018 | — | E2E-020 |
| US-002.EC-7 | Voltar do detalhe preserva filtros | — | — | E2E-021 |
| US-002.EC-8 | Primeira visita: Todos, busca vazia, tamanho 10 | UT-008 | IT-001, IT-035 | E2E-022 |
| US-003 | Tamanho da lista e Carregar mais | UT-008, UT-009, UT-013 | IT-006, IT-007 | E2E-017, E2E-023 |
| US-003.EC-1 | Recorte menor que o tamanho (50 no conjunto de 30) | UT-015 | IT-033 | E2E-024 |
| US-003.EC-2 | Mudar tamanho depois de vários blocos | UT-014 | — | E2E-018 |
| US-003.EC-3 | Carregar mais no último bloco parcial | UT-013 | IT-007 | E2E-025 |
| US-003.EC-4 | Carregar mais duas vezes ao mesmo tempo | UT-073 | — | E2E-019 |
| US-003.EC-5 | Interrupção ao carregar mais | — | — | E2E-026 |
| US-003.EC-6 | Tamanho fora de 10, 15, 30, 50 | UT-006, UT-007 | IT-005 | E2E-027 |
| US-003.EC-7 | Recorte vazio sem Carregar mais | UT-022 | IT-041 | E2E-028 |
| US-003.EC-8 | Todos com tamanho 10: três blocos 10+10+10 | UT-014 | IT-007 | E2E-017 |
| US-004 | Monta o pedido como convidado | UT-036 | IT-013 | E2E-029 |
| US-004.EC-1 | Quantidade zero, negativa ou não numérica | UT-039 | IT-027 | E2E-031 |
| US-004.EC-2 | Produto indisponível | UT-040 | IT-028 | E2E-031 |
| US-004.EC-3 | Adicionar o mesmo produto de novo | UT-041 | IT-013 | E2E-031 |
| US-004.EC-4 | Carrinho vazio no checkout | UT-048 | IT-026 | E2E-030 |
| US-004.EC-5 | Interrupção ao alterar o carrinho | UT-043 | — | E2E-030 |
| US-004.EC-6 | Cem linhas ou quantidades altas | UT-042 | — | E2E-032 |
| US-004.EC-7 | Duas alterações ao mesmo tempo | UT-044 | — | E2E-029 |
| US-004.EC-8 | Mesmo preço unitário para convidado e autenticado | UT-045 | IT-044 | E2E-029 |
| US-005 | Entra ou cria conta e volta | UT-029, UT-033 | IT-010, IT-011 | E2E-033, E2E-036 |
| US-005.EC-1 | E-mail ou senha inválidos | UT-031 | IT-025 | E2E-034 |
| US-005.EC-2 | E-mail já usado ao criar conta | UT-030 | IT-024 | E2E-036 |
| US-005.EC-3 | Sessão expirada no checkout | UT-051 | IT-045 | E2E-038 |
| US-005.EC-4 | Já identificado abre Entrar | UT-034 | IT-010 | E2E-034 |
| US-005.EC-5 | Envio repetido do mesmo cadastro ou entrada | UT-033 | IT-024 | E2E-036 |
| US-005.EC-6 | Campos vazios | UT-032 | IT-046 | E2E-034 |
| US-005.EC-7 | Abrir Entrar e cancelar / voltar | — | — | E2E-034 |
| US-005.EC-8 | Conta de operador na Entrar da loja | UT-027, UT-072 | IT-011 | E2E-035 |
| US-006 | Topo da loja | UT-071 | — | E2E-043 |
| US-006.EC-1 | Pedido no topo sem conta | — | IT-009 | E2E-044 |
| US-006.EC-2 | Checkout no topo com carrinho vazio | UT-048 | IT-026 | E2E-030 |
| US-006.EC-3 | Tela estreita | — | — | E2E-044 |
| US-006.EC-4 | Operador autenticado usa topo da US-009 | UT-072 | — | E2E-035, E2E-048 |
| US-006.EC-5 | Atualizar a página preserva papel e carrinho | UT-035 | IT-012, IT-034 | E2E-045 |
| US-006.EC-6 | Dois toques em Sair | UT-035 | IT-012 | E2E-043 |
| US-006.EC-7 | Deep link de retaguarda com conta de produtor | UT-066 | IT-019 | E2E-044 |
| US-007 | Confirma o pedido | UT-047 | IT-015 | E2E-037 |
| US-007.EC-1 | Propriedade ou retirada ausente | UT-049 | IT-029 | E2E-038 |
| US-007.EC-2 | Duas confirmações do mesmo checkout | UT-050, UT-059 | IT-016 | E2E-039 |
| US-007.EC-3 | Voltar no checkout preserva o informado | — | — | E2E-038 |
| US-007.EC-4 | Carrinho vazio no checkout | UT-048 | IT-026 | E2E-030 |
| US-007.EC-5 | Interrupção na confirmação | UT-060 | — | E2E-039 |
| US-007.EC-6 | Produto do carrinho deixou de ser elegível | UT-052 | IT-047 | E2E-038 |
| US-007.EC-7 | Conta expirada no confirmar | UT-051 | IT-045 | E2E-038 |
| US-007.EC-8 | Propriedades de outro produtor | UT-053, UT-064 | IT-020 | E2E-037 |
| US-008 | Pedido recebido e meus pedidos | UT-047, UT-061 | IT-015, IT-037 | E2E-040, E2E-041 |
| US-008.EC-1 | Abrir pedido de outro produtor | UT-056 | IT-017 | E2E-042 |
| US-008.EC-2 | Atualizar “Pedido recebido” não cria outro | UT-050 | IT-016 | E2E-040 |
| US-008.EC-3 | Lista sem pedidos | UT-061 | IT-048 | E2E-041 |
| US-008.EC-4 | Convidado cancela o Entrar em meus pedidos | UT-025 | IT-009 | E2E-041 |
| US-008.EC-5 | Confirmação simulada ACEITA | UT-058, UT-062 | IT-015 | E2E-040 |
| US-008.EC-6 | Muitos pedidos | — | IT-037 | E2E-041 |
| US-008.EC-7 | Endereço de pedido inexistente | UT-057 | IT-039 | E2E-042 |
| US-008.EC-8 | Operador não usa meus pedidos | UT-055 | IT-030 | E2E-048 |
| US-009 | Inspeciona pedidos na retaguarda | UT-065 | IT-018, IT-038 | E2E-046, E2E-047 |
| US-009.EC-1 | Conta de produtor abre a retaguarda | UT-066 | IT-019 | E2E-049 |
| US-009.EC-2 | Nenhum pedido | UT-074 | IT-049 | E2E-049 |
| US-009.EC-3 | Confirmação bem-sucedida repetida | UT-068 | IT-016 | E2E-046 |
| US-009.EC-4 | Lista desatualizada | — | IT-038 | E2E-046 |
| US-009.EC-5 | Sair do operador | UT-035 | IT-012 | E2E-048 |
| US-009.EC-6 | Pedido de outra empresa comercial | UT-069 | IT-018 | E2E-046 |
| US-009.EC-7 | Deep link de detalhe sem sessão | UT-067 | IT-031 | E2E-049 |
| US-009.EC-8 | Identificação falha | UT-031 | IT-025 | E2E-049 |
| ConsultaCatalogo / BuscaProduto / Paginacao | Listagem, recorte e tamanhos fechados | UT-001–UT-024 | IT-001–IT-008, IT-022, IT-032, IT-033, IT-035, IT-041–IT-043 | E2E-001–E2E-028 |
| ConsultaIdentidade / ComandoIdentidade | Sessão atual, cadastro e entrada | UT-025–UT-035, UT-071–UT-072 | IT-009–IT-012, IT-024, IT-025, IT-036, IT-040, IT-045, IT-046 | E2E-033–E2E-036 |
| ConsultaCarrinho / ServicoCarrinho | Carrinho de convidado | UT-036–UT-046 | IT-013, IT-014, IT-026–IT-028, IT-034, IT-044 | E2E-029–E2E-032 |
| ComandoPedido / ConsultaPedido | Pedido local e histórico do produtor | UT-047–UT-062 | IT-015–IT-017, IT-029, IT-030, IT-037, IT-039, IT-047, IT-048 | E2E-037–E2E-042 |
| ConsultaPropriedades / ComandoPropriedades | Propriedades próprias no checkout | UT-063–UT-064 | IT-020, IT-021 | E2E-037 |
| ConsultaRetaguarda | Leitura do operador | UT-065–UT-070, UT-074 | IT-018, IT-019, IT-031, IT-038, IT-049 | E2E-046–E2E-049 |
| GatewayErp | Fake único; confirmação ACEITA | UT-058 | IT-015 | E2E-040 |
| TopoLoja | Atalhos de compra no cabeçalho | UT-071 | — | E2E-043–E2E-045 |
| TopoOperador | Topo só de operação | UT-072 | — | E2E-035, E2E-048 |
| CarrosselCategoria / ControleTamanhoLista / BotaoCarregarMais | Superfície S1 | UT-008, UT-009, UT-018, UT-020 | IT-006 | E2E-001–E2E-007, E2E-017–E2E-028 |
| FormularioEntrar / FormularioCadastro | Superfícies S3 e S4 | UT-029–UT-032 | IT-010, IT-011, IT-024, IT-025 | E2E-033–E2E-036 |
| Limites de módulos | Dependências permitidas e CLOSED | UT-075–UT-076 | IT-023 | — |

## Unit Tests

### ConsultaCatalogo, BuscaProduto e Paginacao (Spec: Interfaces principais)

- **UT-001** (happy): `ConsultaCatalogo.listarProdutosVisiveis` com
  `BuscaProduto("", null)` e `Paginacao(1, 10)` devolve até 10 itens
  do conjunto elegível de 30 — **não** uma `Pagina` vazia (quebra do
  MVP 0).
- **UT-002** (happy): `listarProdutosVisiveis` com
  `BuscaProduto("ureia", "Fertilizantes")` e `Paginacao(1, 10)`
  devolve somente itens com `categoria=Fertilizantes` cujo nome
  corresponde a `ureia` (exemplo `prd_01J8F6T8HY3V`,
  `Ureia agrícola 50 kg`, `precoUnitario=198.00`).
- **UT-003** (happy): `BuscaProduto(null, "Todos")` e
  `BuscaProduto(null, null)` listam o conjunto elegível inteiro, sem
  filtrar categoria.
- **UT-004** (error): `BuscaProduto(null, "Defensivos")` lança
  `ExcecaoCatalogo` com código `CATEGORIA_INVALIDA` e mensagem
  “Escolha Todos, Sementes, Fertilizantes ou Correção.”
- **UT-005** (error): `BuscaProduto(null, "Hortaliças")` lança
  `CATEGORIA_INVALIDA` (conjunto fechado: Todos, Sementes,
  Fertilizantes, Correção).
- **UT-006** (error): `Paginacao` / `listarProdutosVisiveis` com
  `tamanhoPagina=24` lança `TAMANHO_PAGINA_INVALIDO` com mensagem
  “Escolha 10, 15, 30 ou 50 produtos por página.” **antes** de
  consultar o repositório (quebra do default 24 do MVP 0; sem shim).
- **UT-007** (error): `tamanhoPagina` igual a `7`, `25` ou `100` lança
  `TAMANHO_PAGINA_INVALIDO` sem consultar o repositório.
- **UT-008** (happy): `Paginacao.PADRAO` é `10`; `listarProdutosVisiveis`
  sem tamanho explícito usa `10` e `pagina=1`.
- **UT-009** (happy): `Paginacao.tamanhosPermitidos()` é
  `{10, 15, 30, 50}`; cada valor produz `Pagina.tamanhoPagina` igual
  ao pedido.
- **UT-010** (boundary): `pagina=1` é o primeiro bloco; `itens` não
  repetem identificadores da página 2 do mesmo recorte.
- **UT-011** (state): o produto regulamentado oculto da fixture nunca
  aparece em `itens` nem incrementa `total`.
- **UT-012** (error): `exigirProdutoPedivel(idTenant, idRegulado)`
  lança `PRODUTO_NAO_ELEGIVEL` com mensagem “Este produto não pode ser
  pedido nesta loja.” sem revelar nome, preço nem categoria.
- **UT-013** (happy): `listarProdutosVisiveis` em `pagina=2` com os
  mesmos `categoria`, `consulta` e `tamanhoPagina=10` devolve o
  próximo bloco sem IDs da página 1.
- **UT-014** (boundary): recorte Todos com 30 elegíveis e
  `tamanhoPagina=10` produz três páginas (`total=30`; páginas 1 e 2
  com 10 itens; página 3 com 10).
- **UT-015** (boundary): `tamanhoPagina=50` no conjunto de 30 devolve
  uma única página com `itens.size()=30`, `total=30`.
- **UT-016** (boundary): `consulta="xyzzy-inexistente"` devolve
  `itens=[]` e `total=0`.
- **UT-017** (boundary): `consulta` só com espaços trata como busca em
  branco e lista o recorte da categoria; texto extremamente longo não
  lança exceção não tratada — ou `itens=[]` ou recusa com orientação
  clara, sem quebrar o serviço.
- **UT-018** (ordering): duas chamadas seguidas com categorias
  diferentes (`Sementes` depois `Fertilizantes`) fazem a segunda
  resposta conter só `Fertilizantes`; a listagem não mistura as duas.
- **UT-019** (happy): item listado expõe `nome`, `categoria`,
  `descricaoCurta`, `unidade`, `precoUnitario`, `disponivel` e
  `urlImagem` (exemplo ureia: `Saco`, `198.00`,
  `/media/products/ureia-50kg.jpg`).
- **UT-020** (boundary): produto ou card de categoria sem imagem
  devolve valor de apresentação seguro (`urlImagem` nulo ou vazio),
  sem URL quebrada obrigatória.
- **UT-021** (error): `obterProdutoVisivel` de produto regulamentado
  ou inexistente resulta em ausência / `PRODUTO_NAO_ELEGIVEL` sem
  corpo do item.
- **UT-022** (boundary): categoria válida sem elegíveis (recorte
  vazio) devolve `itens=[]`, `total=0`.
- **UT-023** (state): `BuscaProduto("ureia", "Sementes")` depois
  `BuscaProduto("ureia", "Fertilizantes")` mantém a consulta `ureia`
  e aplica só a categoria nova.
- **UT-024** (boundary): no conjunto de 30, as categorias presentes
  nos itens listados pertencem só a Sementes, Fertilizantes e
  Correção; Defensivos não surge.

### ConsultaIdentidade e ComandoIdentidade (Spec: Interfaces principais)

- **UT-025** (happy): `ConsultaIdentidade.usuarioAtual()` sem sessão
  devolve `Optional.empty()` — o handler de
  `GET /api/v1/autenticacao/sessao` responde `{ "autenticado": false }`.
- **UT-026** (happy): `usuarioAtual()` com Alfa devolve
  `UsuarioAutenticado` com `email=produtor.alfa@example.com`,
  `papel=PRODUTOR`, sem campo de senha.
- **UT-027** (happy): `usuarioAtual()` com operador devolve
  `email=operador.revenda@example.com` e `papel=OPERADOR_REVENDA`.
- **UT-028** (error): `exigirAutenticado()` sem sessão lança
  `NAO_AUTENTICADO` com mensagem “Entre ou crie uma conta para
  continuar.”
- **UT-029** (happy): `ComandoIdentidade.cadastrar` com nome, e-mail
  novo e senha de fixture cria somente `PRODUTOR` e devolve `nome`,
  `email` e `papeis=["PRODUTOR"]`.
- **UT-030** (error): `cadastrar` com e-mail já usado
  (`produtor.alfa@example.com`) lança `EMAIL_DUPLICADO` com mensagem
  “Este e-mail já está cadastrado. Entre com sua senha ou use outro
  e-mail.” sem expor hash nem senha de fixture.
- **UT-031** (error): `entrar(ComandoEntrada("produtor.alfa@example.com",
  senhaInvalida))` lança `CREDENCIAIS_INVALIDAS` com mensagem
  “E-mail ou senha inválidos.”
- **UT-032** (error): `cadastrar` ou `entrar` com e-mail ou senha
  vazios aponta o campo faltante e não cria sessão.
- **UT-033** (idempotency): dois `entrar` consecutivos com o mesmo
  e-mail fixture e senha de fixture resultam em no máximo uma sessão
  ativa e um único `idUsuario`.
- **UT-034** (state): `entrar` com sessão de produtor já ativa não
  cria segunda conta e permanece autenticado como PRODUTOR.
- **UT-035** (state): encerrar sessão invalida `usuarioAtual()` e não
  apaga `chaveCarrinhoConvidado`.

### ConsultaCarrinho e ServicoCarrinho (Spec: Carrinho de convidado)

- **UT-036** (happy): `ServicoCarrinho.adicionar` de
  `prd_01J8F6T8HY3V` com quantidade `2` produz `precoUnitario=198.00`,
  `totalLinha=396.00` e `total=396.00`.
- **UT-037** (state): `alterarQuantidade` da mesma linha para `3`
  recalcula `totalLinha=594.00` e `total=594.00`.
- **UT-038** (state): `remover` a última linha produz `itens=[]` e
  `total=0.00`.
- **UT-039** (error): quantidade `0`, `-1` ou não inteira lança
  `QUANTIDADE_INVALIDA` com mensagem “Informe uma quantidade inteira
  positiva.”
- **UT-040** (error): adicionar produto com `disponivel=false` lança
  `PRODUTO_INDISPONIVEL` com mensagem “Este produto não está
  disponível.”
- **UT-041** (idempotency): adicionar o mesmo `idProduto` duas vezes
  (quantidades `2` e `1`) consolida uma linha com quantidade `3`.
- **UT-042** (boundary): carrinho com 100 linhas de fixture mantém
  cada quantidade e o total exato.
- **UT-043** (state): alteração interrompida (falha após a leitura)
  preserva o `VisaoCarrinho` confirmado anterior.
- **UT-044** (concurrency): duas alterações simultâneas na mesma
  chave convergem para um único estado coerente, sem linha fantasma.
- **UT-045** (state): o `precoUnitario` da ureia no carrinho de
  convidado é `198.00` e permanece `198.00` após `entrar` como Alfa.
- **UT-046** (error): adicionar o produto regulamentado oculto lança
  `PRODUTO_NAO_ELEGIVEL`.

### ComandoPedido e ConsultaPedido (Spec: Pedido local)

- **UT-047** (happy): `ComandoPedido.criar` com carrinho da ureia
  (2 unidades), propriedade própria Alfa e
  `preferenciaRetirada=DEPOSITO_PRINCIPAL` devolve
  `situacao=RECEBIDO`, `confirmacao=ACEITA` e
  `mensagem=Pedido recebido`.
- **UT-048** (error): `criar` com carrinho vazio lança
  `CARRINHO_VAZIO` com mensagem “Adicione ao menos um produto antes
  do checkout.”
- **UT-049** (error): `criar` sem `idPropriedade` ou sem
  `preferenciaRetirada` lança `DADOS_CHECKOUT_OBRIGATORIOS` com
  mensagem “Escolha uma propriedade e a preferência de retirada.”
- **UT-050** (idempotency): duas chamadas a `criar` com a mesma
  `chaveIdempotencia` produzem o mesmo `idPedido`.
- **UT-051** (state): sessão expirada em `criar` lança
  `NAO_AUTENTICADO` e `ConsultaCarrinho.obter` ainda devolve os itens
  do convidado.
- **UT-052** (error): item do carrinho que deixou de ser elegível
  impede a confirmação com `PRODUTO_NAO_ELEGIVEL`.
- **UT-053** (error): `idPropriedade` de Beta usado por Alfa é
  recusado; Alfa não lista Fazenda Sul.
- **UT-054** (state): alterar o preço de catálogo da ureia depois de
  `criar` não muda `total` nem `precoUnitario` do snapshot do pedido.
- **UT-055** (error): `criar` com papel `OPERADOR_REVENDA` lança
  `ACESSO_NEGADO` com mensagem “Você não tem permissão para este
  recurso.”
- **UT-056** (error): `obterParaProdutor` de Alfa no `idPedido` de
  Beta lança `ACESSO_PEDIDO_NEGADO` com mensagem “Você não pode
  visualizar este pedido.” sem itens.
- **UT-057** (error): `obterParaProdutor` de ID inexistente não
  devolve dados de outro pedido.
- **UT-058** (happy): `GatewayErpSimulado` devolve confirmação
  `ACEITA` para o pedido local já persistido; o mock não cria nem
  apaga o pedido.
- **UT-059** (concurrency): duas confirmações simultâneas do mesmo
  checkout (mesma chave ou mesma sessão de carrinho) produzem um só
  pedido.
- **UT-060** (state): interrupção em `criar` deixa ou o pedido
  persistido reabrível, ou nenhum pedido — sem meio-termo visível.
- **UT-061** (happy): `listarParaProdutor` de Alfa sem pedidos
  devolve `itens=[]` e `total=0`.
- **UT-062** (state): `confirmacao=ACEITA` fica no registro separado
  de `situacao=RECEBIDO`; o produtor vê ambos no detalhe.

### ConsultaPropriedades e ComandoPropriedades (Spec: Propriedades)

- **UT-063** (happy): `ComandoPropriedades.registrar("Fazenda Santa Luzia")`
  como Alfa devolve `ResumoPropriedade` com esse nome e passa a
  aparecer em `listarDoAutenticado()`.
- **UT-064** (error): `listarDoAutenticado()` de Alfa contém Fazenda
  Norte e Sitio Recanto e não contém Fazenda Sul.

### ConsultaRetaguarda (Spec: Retaguarda)

- **UT-065** (happy): `ConsultaRetaguarda.listar(1, 25)` com operador
  inclui `idPedido`, nome do produtor (Produtor Alfa), horário, total
  e status da confirmação.
- **UT-066** (error): `listar` ou `obter` com papel PRODUTOR lança
  `ACESSO_NEGADO`.
- **UT-067** (error): `listar` ou `obter` sem sessão lança
  `NAO_AUTENTICADO`.
- **UT-068** (idempotency): reler o detalhe após confirmação
  `ACEITA` repetida não muda `confirmacao` nem o snapshot.
- **UT-069** (state): pedido de outro `idTenant` não entra em
  `listar` do tenant semeado.
- **UT-070** (happy): `obter` devolve snapshot imutável de itens,
  quantidades, preços, propriedade e retirada.

### Destino pós-Entrar e TopoLoja (Spec: frontend identity / shell)

- **UT-071** (happy): destino de produtor aceita somente origens
  relativas `/catalogo`, `/carrinho`, `/checkout`, `/meus-pedidos`,
  `/pedidos/{id}`, `/retaguarda/pedidos` e
  `/retaguarda/pedidos/{id}`.
- **UT-072** (state): conta com `papeis=["OPERADOR_REVENDA"]` ignora
  `origem` da loja (inclusive `/checkout`) e resolve
  `/retaguarda/pedidos`. Origem absoluta ou desconhecida de produtor
  cai em `/catalogo`.
- **UT-073** (concurrency): duas solicitações de `pagina+1` com os
  mesmos filtros não duplicam `id` no acumulado da listagem.
- **UT-074** (boundary): `ConsultaRetaguarda.listar` sem pedidos no
  tenant devolve `itens=[]` e `total=0`.

### Limites de módulos (Spec: Limites arquiteturais)

- **UT-075** (state): `ApplicationModules.verify()` rejeita importação
  de infraestrutura, entidade JPA, repositório ou DTO de fornecedor
  de outro módulo.
- **UT-076** (state): módulos `catalog` e `cart` permanecem CLOSED
  com as `allowedDependencies` atuais; `order` define `GatewayErp` e
  só `integration` implementa.

## Integration Tests

### Catálogo (quebra MVP 0 → MVP 1)

- **IT-001:** `GET /api/v1/catalogo/produtos?pagina=1&tamanhoPagina=10`
  sem `consulta` devolve HTTP 200 com até 10 itens do conjunto de 30
  e `total=30`; **não** devolve `itens=[]` (quebra do vazio em busca
  em branco do MVP 0). Nenhum item regulamentado.
- **IT-002:** `GET /api/v1/catalogo/produtos?consulta=&pagina=1&tamanhoPagina=10`
  e o mesmo com `consulta` só de espaços listam o recorte elegível,
  não uma página vazia.
- **IT-003:** `GET /api/v1/catalogo/produtos?categoria=Fertilizantes&consulta=ureia&pagina=1&tamanhoPagina=10`
  devolve o formato de `_dx.md` (`prd_01J8F6T8HY3V`,
  `categoria=Fertilizantes`, `precoUnitario=198.00`, `pagina=1`,
  `tamanhoPagina=10`).
- **IT-004:** `GET /api/v1/catalogo/produtos?categoria=Defensivos&pagina=1&tamanhoPagina=10`
  devolve `CATEGORIA_INVALIDA`.
- **IT-005:** `GET /api/v1/catalogo/produtos?pagina=1&tamanhoPagina=24`
  devolve `TAMANHO_PAGINA_INVALIDO` (cliente MVP 0 sem shim).
- **IT-006:** `tamanhoPagina=10`, `15`, `30` e `50` respondem 200 com
  `tamanhoPagina` ecoado; `tamanhoPagina=100` devolve
  `TAMANHO_PAGINA_INVALIDO`.
- **IT-007:** `GET` página 2 com os mesmos `categoria`, `consulta` e
  `tamanhoPagina=10` não repete IDs da página 1; a união das três
  páginas de Todos esgota os 30.
- **IT-008:** produto regulamentado oculto não aparece em listagem;
  `GET /api/v1/catalogo/produtos/{idRegulado}` devolve
  `PRODUTO_NAO_ELEGIVEL` sem nome nem preço.
- **IT-022:** `GET /api/v1/catalogo/produtos/prd_01J8F6T8HY3V`
  devolve o objeto de item da ureia (`nome`, `categoria=Fertilizantes`,
  `precoUnitario=198.00`); ID inexistente devolve
  `PRODUTO_NAO_ELEGIVEL` sem revelar outro produto.
- **IT-032:** `GET /api/v1/catalogo/produtos?categoria=Todos&pagina=1&tamanhoPagina=10`
  lista elegíveis de todas as categorias visíveis.
- **IT-033:** `GET ...&tamanhoPagina=50` no conjunto de 30 devolve
  uma página com 30 itens e sem necessidade de página 2.
- **IT-035:** omitir `tamanhoPagina` usa padrão `10`.
- **IT-041:** `categoria=Correção` (ou recorte fixture vazio) com
  zero elegíveis devolve `itens=[]`, `total=0`.
- **IT-042:** `consulta=xyzzy-inexistente` devolve `itens=[]`,
  `total=0`.
- **IT-043:** `consulta` hostil ou extremamente longa não devolve 5xx.

### Identidade e sessão

- **IT-009:** `GET /api/v1/autenticacao/sessao` sem cookie `sessao`
  devolve `{ "autenticado": false }` e não inclui senha.
- **IT-010:** `POST /api/v1/autenticacao/cadastro` com nome, e-mail
  novo e senha de fixture grava cookie HttpOnly `sessao` e devolve
  `papeis=["PRODUTOR"]` e `email`; o `GET sessao` seguinte ecoa os
  mesmos campos.
- **IT-011:** `POST /api/v1/autenticacao/entrada` com
  `operador.revenda@example.com` e senha de fixture devolve
  `papeis=["OPERADOR_REVENDA"]` e `email=operador.revenda@example.com`;
  o cliente usa esse papel para ir a `/retaguarda/pedidos`.
- **IT-012:** `POST /api/v1/autenticacao/saida` responde `204` e não
  apaga o cookie `chaveCarrinhoConvidado`.
- **IT-024:** `POST /api/v1/autenticacao/cadastro` com
  `produtor.alfa@example.com` devolve `EMAIL_DUPLICADO`.
- **IT-025:** `POST /api/v1/autenticacao/entrada` com senha errada
  devolve `CREDENCIAIS_INVALIDAS`.
- **IT-036:** `GET /api/v1/autenticacao/csrf` devolve `{ "token": ... }`
  e grava cookie `XSRF-TOKEN`; mutações sem `X-XSRF-TOKEN` são
  recusadas.
- **IT-040:** `POST /api/v1/autenticacao/entrada` de Alfa não rejeita
  `origem` — a API não valida origem; o cliente aplica a lista de
  `_dx.md`.
- **IT-045:** após invalidar a sessão, `POST /api/v1/pedidos` devolve
  `NAO_AUTENTICADO` e `GET /api/v1/carrinhos/convidado` ainda lista
  os itens.
- **IT-046:** `POST /api/v1/autenticacao/cadastro` ou `/entrada` com
  corpo sem e-mail ou sem senha não cria sessão e aponta o obrigatório.

### Carrinho

- **IT-013:** `POST /api/v1/carrinhos/convidado/itens` com
  `{ "idProduto": "prd_01J8F6T8HY3V", "quantidade": 2 }` devolve
  `totalLinha=396.00` e `total=396.00` e grava
  `chaveCarrinhoConvidado` HttpOnly (30 dias).
- **IT-014:** `PATCH /api/v1/carrinhos/convidado/itens/{idProduto}`
  e `DELETE /api/v1/carrinhos/convidado/itens/{idProduto}` atualizam
  o total; `GET /api/v1/carrinhos/convidado` ecoa o estado.
- **IT-026:** `POST /api/v1/pedidos` com carrinho vazio devolve
  `CARRINHO_VAZIO`.
- **IT-027:** `POST /api/v1/carrinhos/convidado/itens` com
  `quantidade=0` devolve `QUANTIDADE_INVALIDA`.
- **IT-028:** adicionar produto indisponível devolve
  `PRODUTO_INDISPONIVEL`.
- **IT-029:** `POST /api/v1/pedidos` autenticado como Alfa, com
  carrinho da ureia, sem `idPropriedade` ou sem
  `preferenciaRetirada` devolve `DADOS_CHECKOUT_OBRIGATORIOS`
  (“Escolha uma propriedade e a preferência de retirada.”).
- **IT-034:** após `POST /api/v1/autenticacao/saida`,
  `GET /api/v1/carrinhos/convidado` ainda devolve as linhas do
  convidado.
- **IT-044:** o `precoUnitario` da ureia no carrinho permanece
  `198.00` depois de `POST /entrada` como Alfa.

### Propriedades, pedidos e retaguarda

- **IT-015:** `POST /api/v1/pedidos` com
  `Idempotency-Key`, `idPropriedade` de Alfa e
  `preferenciaRetirada=DEPOSITO_PRINCIPAL` persiste o pedido local
  **antes** da confirmação `ACEITA` do `GatewayErpSimulado` e
  devolve `situacao=RECEBIDO`, `confirmacao=ACEITA`,
  `mensagem=Pedido recebido`.
- **IT-016:** o mesmo `POST /api/v1/pedidos` repetido com a mesma
  `Idempotency-Key` devolve a confirmação original e um só
  `idPedido`.
- **IT-017:** `GET /api/v1/pedidos/{idPedido}` autenticado como Beta
  no pedido de Alfa devolve `ACESSO_PEDIDO_NEGADO` sem itens.
- **IT-018:** `GET /api/v1/retaguarda/pedidos?pagina=1&tamanhoPagina=25`
  como operador lista só pedidos do tenant semeado, com identificador,
  produtor, horário, total e confirmação.
- **IT-019:** o mesmo `GET` como Alfa devolve `403` e
  `ACESSO_NEGADO`.
- **IT-020:** `GET /api/v1/produtor/propriedades` como Alfa contém
  Fazenda Norte e Sitio Recanto e não contém Fazenda Sul.
- **IT-021:** `POST /api/v1/produtor/propriedades` com
  `{ "nome": "Fazenda Santa Luzia" }` como Alfa devolve o resumo e
  passa a listar o item; como operador devolve `ACESSO_NEGADO`.
- **IT-030:** `POST /api/v1/pedidos` como operador devolve
  `ACESSO_NEGADO`.
- **IT-031:** `GET /api/v1/retaguarda/pedidos` e
  `GET /api/v1/retaguarda/pedidos/{idPedido}` sem sessão devolvem
  `NAO_AUTENTICADO`.
- **IT-037:** `GET /api/v1/pedidos?pagina=1&tamanhoPagina=10` como
  Alfa lista só os pedidos de Alfa; o detalhe
  `GET /api/v1/pedidos/{idPedido}` inclui propriedade, retirada,
  total e itens com snapshot de preço.
- **IT-038:** `GET /api/v1/retaguarda/pedidos/{idPedido}` como
  operador devolve o snapshot imutável; nova leitura após refresh
  mostra a confirmação `ACEITA` mais recente.
- **IT-039:** `GET /api/v1/pedidos/{idInexistente}` como Alfa não
  devolve dados de outro pedido.
- **IT-047:** `POST /api/v1/pedidos` com item que deixou de ser
  elegível devolve `PRODUTO_NAO_ELEGIVEL`.
- **IT-048:** `GET /api/v1/pedidos` como Alfa sem pedidos devolve
  lista vazia explícita.
- **IT-049:** `GET /api/v1/retaguarda/pedidos` como operador sem
  pedidos devolve lista vazia explícita.

### Migração e arquitetura

- **IT-023:** Flyway aplica as migrações dos esquemas dos módulos em
  PostgreSQL vazio via Testcontainers e
  `ApplicationModules.verify()` passa.

## End-to-End Tests

Playwright nas rotas de `_dx.md`. Viewport móvel 390 px salvo quando
o caso pede desktop ou tela estreita. Senha de fixture só no harness.

### S1 Catálogo (`/catalogo`)

- **E2E-001:** Abrir `/catalogo` como convidado → carrossel mostra
  Todos, Sementes, Fertilizantes e Correção, cada um com foto de
  demonstração e rótulo → listagem traz até 10 produtos do conjunto
  de 30 (busca em branco **não** deixa a página vazia) → não há
  `bottom-nav` no rodapé.
- **E2E-002:** Em `/catalogo` escolher Fertilizantes → listagem só
  com elegíveis dessa categoria → o card permanece marcado.
- **E2E-003:** Percorrer o carrossel e a listagem → não aparece
  categoria Defensivos nem o produto regulamentado oculto.
- **E2E-004:** Card de categoria sem arquivo de foto → o card
  permanece utilizável, sem imagem quebrada visível.
- **E2E-005:** Escolher categoria sem elegíveis → estado vazio com
  ação de voltar a Todos ou Limpar filtros → acionar restaura o
  conjunto elegível.
- **E2E-006:** Toques rápidos em Sementes e depois Fertilizantes →
  vale Fertilizantes; a lista não mistura as duas.
- **E2E-007:** Viewport 390 px com carrossel que não cabe → desloca
  na horizontal com controle visível além do gesto.
- **E2E-008:** Abrir `/catalogo` como convidado e de novo com sessão
  expirada → carrossel e listagem continuam utilizáveis.
- **E2E-009:** Abrir deep link de produto regulamentado ou oculto →
  acesso negado com `PRODUTO_NAO_ELEGIVEL`; sem nome, preço nem
  categoria do item.
- **E2E-010:** Interromper a troca de categoria (falha de rede
  simulada) → permanece a última listagem bem-sucedida e há como
  tentar de novo.
- **E2E-011:** Com os 30 itens semeados, as categorias visíveis
  continuam só as quatro combinadas; não surge categoria nova.
- **E2E-012:** Em Fertilizantes pesquisar `ureia` no campo da mesma
  página → só elegíveis da categoria cujo nome corresponde.
- **E2E-013:** Com categoria e busca ativas, Limpar filtros →
  categoria volta a Todos, busca vazia, listagem sem recorte.
- **E2E-014:** Digitar texto hostil, só espaços ou texto
  extremamente longo → a loja não quebra; ou não há resultado, ou a
  busca é recusada com orientação clara.
- **E2E-015:** Com Fertilizantes marcado, busca em branco → a
  listagem respeita só Fertilizantes.
- **E2E-016:** Busca sem correspondência → estado vazio com Limpar
  filtros.
- **E2E-017:** Recorte Todos com tamanho 10 → Carregar mais
  acrescenta o segundo bloco sem remover o primeiro; o terceiro
  bloco esgota os 30 e Carregar mais some.
- **E2E-018:** Depois de dois blocos, escolher tamanho 15 (depois
  30 e 50) → a lista recomeça do início com até o novo tamanho; não
  soma o novo tamanho ao que já estava na tela. Trocar a categoria
  com busca `ureia` preenchida mantém a busca.
- **E2E-019:** Trocar a busca com Fertilizantes marcado mantém a
  categoria; acionar Carregar mais duas vezes ao mesmo tempo não
  duplica produto.
- **E2E-020:** Dois textos digitados em sequência rápida (`semente`
  depois `ureia`) → o resultado visível corresponde ao último texto
  estável; não mistura páginas de buscas diferentes.
- **E2E-021:** Abrir detalhe de um produto visível e voltar a
  `/catalogo` → categoria, busca e tamanho permanecem.
- **E2E-022:** Primeira visita a `/catalogo` → Todos, busca vazia,
  tamanho 10.
- **E2E-023:** Controle de tamanho oferece 10, 15, 30 e 50; o
  padrão visível é 10.
- **E2E-024:** Escolher 50 no conjunto de 30 → uma página com todos
  os itens do recorte e sem Carregar mais.
- **E2E-025:** Carregar mais no último bloco parcial → acrescenta
  só o restante e oculta o botão.
- **E2E-026:** Interromper Carregar mais → itens já visíveis
  permanecem e há como tentar de novo.
- **E2E-027:** Forçar tamanho inválido (equivalente a
  `tamanhoPagina=24`) → a loja recusa; permanece o último tamanho
  válido ou o padrão 10; não há listagem com 24.
- **E2E-028:** Recorte vazio → sem Carregar mais; vale o estado
  vazio de E2E-016.

### S2 Carrinho (`/carrinho`)

- **E2E-029:** `/catalogo` → adicionar 2× ureia → `/carrinho` mostra
  item, `198.00`, `396.00` e total do pedido → alterar quantidade e
  remover atualiza o total; duas alterações rápidas convergem sem
  linha fantasma; o preço unitário é o mesmo após Entrar como Alfa.
- **E2E-030:** Sair com itens no carrinho → os itens de convidado
  permanecem → `/checkout` com carrinho vazio explica
  `CARRINHO_VAZIO` (“Adicione ao menos um produto antes do
  checkout.”) e o atalho Checkout no `TopoLoja` permanece visível.
  Interromper uma alteração deixa o último estado confirmado.
- **E2E-031:** Quantidade `0` ou negativa → `QUANTIDADE_INVALIDA`.
  Produto indisponível não entra. Adicionar a ureia de novo junta
  quantidade numa só linha.
- **E2E-032:** Adicionar 100 linhas de fixture → totais corretos e
  legíveis.

### S3 Entrar (`/entrar`) e S4 Criar conta (`/cadastro`)

- **E2E-033:** Convidado com carrinho em `/catalogo` aciona Entrar
  no `TopoLoja` → `/entrar?origem=/catalogo` mostra o formulário e o
  caminho para `/cadastro` → entrar como Alfa com senha de fixture →
  volta a `/catalogo` com sessão ativa e carrinho intacto. Repetir a
  jornada a partir de `/carrinho`, `/checkout` e `/meus-pedidos`.
- **E2E-034:** Em `/entrar`, credenciais inválidas → permanece na
  tela com `CREDENCIAIS_INVALIDAS` e o carrinho não some. Campos
  vazios apontam o que falta. Já autenticado como Alfa ao abrir
  `/entrar` não cria segunda conta. Cancelar/voltar → origem
  intacta, ainda convidado.
- **E2E-035:** Em `/entrar?origem=/checkout`, entrar como
  `operador.revenda@example.com` com senha de fixture → o cliente
  ignora a origem da loja e navega para `/retaguarda/pedidos` com
  `TopoOperador` (Pedidos da revenda, identificação, Sair) e sem
  Catálogo, Carrinho, Checkout nem Pedido.
- **E2E-036:** `/cadastro?origem=/meus-pedidos` cria conta PRODUTOR
  e volta a `/meus-pedidos`. Cadastrar `produtor.alfa@example.com`
  de novo → `EMAIL_DUPLICADO` aponta para Entrar; não há “esqueci a
  senha”. Envio repetido do mesmo cadastro não duplica conta.

### S5 Checkout (`/checkout`)

- **E2E-037:** Carrinho com ureia → `/checkout` sem sessão oferece
  Entrar ou Criar conta no próprio checkout → após Alfa, escolhe
  Fazenda Norte (não vê Fazenda Sul) e `DEPOSITO_PRINCIPAL` →
  confirmar registra o pedido. Sem propriedade, registra
  “Fazenda Santa Luzia” no próprio checkout e confirma.
- **E2E-038:** Confirmar sem propriedade ou retirada →
  `DADOS_CHECKOUT_OBRIGATORIOS`. Voltar preserva o informado.
  Sessão expirada no confirmar → volta à identificação com carrinho
  intacto. Item inelegível → não confirma e explica o ajuste.
- **E2E-039:** Duas confirmações do mesmo checkout → um só pedido;
  interrupção resulta em pedido reabrível ou nenhum pedido.

### S6 Pedido recebido (`/pedidos/{idPedido}`) e S7 Meus pedidos (`/meus-pedidos`)

- **E2E-040:** Confirmação válida → “Pedido recebido” e
  identificador em até um segundo, com selo `ACEITA` → atualizar a
  página não cria outro pedido → reabrir mostra produtos,
  quantidades, total, propriedade, retirada e confirmação.
- **E2E-041:** Convidado abre Pedido no topo → `/meus-pedidos` pede
  Entrar → após Alfa, lista os próprios pedidos (vazia com caminho
  para `/catalogo` se não houver nenhum). Cancelar o Entrar não
  revela lista alheia. Lista com muitos pedidos permanece navegável
  e o detalhe abre um pedido por vez.
- **E2E-042:** Alfa abre `/pedidos/{idDeBeta}` → acesso negado sem
  itens. Endereço de pedido inexistente → não encontrado, sem dados
  de outro pedido.

### S8 TopoLoja

- **E2E-043:** Em `/catalogo`, `/carrinho`, `/checkout` e
  `/meus-pedidos` o cabeçalho mostra Catálogo, Carrinho, Checkout e
  Pedido; não há barra no rodapé. Convidado vê Entrar. Alfa vê
  identificação curta e Sair. Confirmar Sair (inclusive dois toques)
  volta a convidado, mantém o carrinho e mostra Entrar de novo.
- **E2E-044:** Pedido no topo sem conta → `/meus-pedidos` pede
  Entrar e, após sucesso, volta a `/meus-pedidos`. Em 390 px os
  atalhos do topo são utilizáveis por toque e teclado. Alfa em
  `/retaguarda/pedidos` vê `ACESSO_NEGADO` e permanece com
  `TopoLoja`, sem `TopoOperador`.
- **E2E-045:** Atualizar a página em `/catalogo` preserva o papel
  (convidado, produtor ou operador) e o carrinho de convidado.

### S9 / S10 Retaguarda e S11 TopoOperador

- **E2E-046:** Operador em `/retaguarda/pedidos` vê identificador,
  produtor, horário, total e status da confirmação no visual da
  loja; atualizar mostra a confirmação mais recente; pedido de
  outra empresa não aparece; confirmação `ACEITA` repetida não muda
  o resultado.
- **E2E-047:** Abrir `/retaguarda/pedidos/{idPedido}` mostra o
  snapshot imutável de itens e checkout no visual da loja.
- **E2E-048:** `TopoOperador` mostra Pedidos da revenda,
  identificação e Sair — sem Catálogo, Carrinho, Checkout nem
  Pedido. Sair encerra a sessão de operação e a lista some para
  convidado. Operador não usa `/meus-pedidos` como lista própria.
- **E2E-049:** Alfa em `/retaguarda/pedidos` → `ACESSO_NEGADO`.
  Operador sem pedidos → estado vazio explícito. Deep link
  `/retaguarda/pedidos/{id}` sem sessão pede identificação de
  operador e não mostra o snapshot. Entrada falha → permanece fora
  da lista com `CREDENCIAIS_INVALIDAS`.
