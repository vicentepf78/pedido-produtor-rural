# Especificação de testes: pedidos de insumos agrícolas do MVP0

Contrato de teste canônico para o MVP0. Complementar a `_spec.md`.

## Estratégia

- Usar JUnit 5 para testes de domínio/aplicação, testes de integração PostgreSQL para
  migrações e comportamento da API, testes de arquitetura Spring Modulith e
  Playwright para jornadas de navegador em viewport móvel.
- Semear as mesmas 30 fixtures de produtos não regulamentados por meio de migrações em
  todos os ambientes de teste.
- Usar o Mock ERP determinístico somente na porta de integração. Isolar o estado do
  banco de dados para testes paralelos e nunca alterar configurações de todo o
  processo.

## Matriz de cobertura

| Fonte | Comportamento | Unit | Integration | E2E |
| --- | --- | --- | --- | --- |
| US-001 | Descoberta de produtos | UT-001–UT-004 | IT-001 | E2E-001 |
| US-001.EC-1–EC-4 | Catálogo vazio, mídia, disponibilidade, acesso | UT-003–UT-005 | IT-002 | E2E-002 |
| US-002 | Totais e alterações do carrinho | UT-006–UT-008 | IT-003 | E2E-003 |
| US-002.EC-1–EC-5 | Carrinho inválido, vazio, repetido, escala, interrupção | UT-009–UT-012 | IT-004 | E2E-004 |
| US-003 | Conta tardia e checkout | UT-013–UT-015 | IT-005 | E2E-005 |
| US-003.EC-1–EC-5 | Credenciais, campos, duplicidade, sessão, navegação para trás | UT-016–UT-019 | IT-006 | E2E-006 |
| US-004 | Confirmar e revisitar pedido | UT-020–UT-022 | IT-007 | E2E-007 |
| US-004.EC-1–EC-2 | Acesso e atualização | UT-023 | IT-008 | E2E-008 |
| US-005 | Visualização de pedido pelo operador | UT-024 | IT-009 | E2E-009 |
| US-005.EC-1–EC-4 | Acesso do operador, vazio, repetição, atualidade | UT-025 | IT-010 | E2E-010 |
| Limites de módulos | Somente dependências permitidas | UT-026 | IT-011 | — |

## Testes unitários

### Catálogo e carrinho

- **UT-001** (sucesso): `ConsultaCatalogo.listarProdutosVisiveis` retorna o produto
  selecionado disponível correspondente com descrição curta, unidade, preço e imagem.
- **UT-002** (erro): `ConsultaCatalogo.exigirProdutoPedivel` rejeita um produto
  regulamentado com `PRODUTO_NAO_ELEGIVEL`.
- **UT-003** (limite): uma pesquisa vazia ou sem correspondência retorna um resultado
  vazio.
- **UT-004** (estado): um produto indisponível é listado como indisponível, mas não
  pode ser adicionado a um carrinho.
- **UT-005** (limite): um produto sem imagem retorna um valor de apresentação seguro.
- **UT-006** (sucesso): adicionar duas unidades calcula o total da linha e do
  carrinho a partir do preço unitário.
- **UT-007** (estado): alterar a quantidade de uma linha recalcula o total.
- **UT-008** (estado): remover a linha final produz um carrinho vazio.
- **UT-009** (erro): quantidades zero, negativas e não inteiras retornam
  `QUANTIDADE_INVALIDA`.
- **UT-010** (idempotência): adicionar o mesmo produto duas vezes consolida uma linha
  de carrinho.
- **UT-011** (limite): um carrinho com 100 itens mantém cada quantidade e o total
  exato.
- **UT-012** (estado): uma alteração de carrinho interrompida preserva o carrinho
  confirmado anterior.

### Identidade, produtor e pedidos

- **UT-013** (sucesso): o checkout aceita um produtor autenticado com uma propriedade
  própria e preferência de retirada.
- **UT-014** (erro): checkout sem itens no carrinho retorna `CARRINHO_VAZIO`.
- **UT-015** (erro): checkout sem propriedade ou preferência de retirada retorna
  `DADOS_CHECKOUT_OBRIGATORIOS`.
- **UT-016** (erro): o cadastro de e-mail duplicado é rejeitado sem expor dados de
  senha.
- **UT-017** (erro): credenciais inválidas fazem a autenticação falhar.
- **UT-018** (concorrência): duas confirmações com uma chave de idempotência produzem
  um identificador de pedido.
- **UT-019** (ordenação): uma sessão expirada exige autenticação enquanto mantém o
  carrinho de convidado.
- **UT-020** (sucesso): uma confirmação válida cria um snapshot imutável de pedido com
  propriedade, preferência de retirada, preços dos itens, quantidades e total.
- **UT-021** (sucesso): a confirmação do Mock ERP é aceita para um pedido local
  persistido.
- **UT-022** (estado): alterar o preço do catálogo após a confirmação não altera o
  total do pedido.
- **UT-023** (permissão): um produtor não pode ler o pedido de outro produtor.

### Backoffice e arquitetura

- **UT-024** (sucesso): o resumo do operador da revenda inclui pedido, produtor,
  total, horário de criação e confirmação aceita.
- **UT-025** (permissão): o papel de produtor não pode obter a lista de pedidos de
  backoffice.
- **UT-026** (estado): a verificação Spring Modulith rejeita uma importação de
  infraestrutura de outro módulo.

## Testes de integração

### Catálogo, carrinho, checkout e pedidos

- **IT-001:** `GET /api/v1/catalogo/produtos?consulta=semente` retorna somente produtos
  visíveis delimitados ao tenant semeado.
- **IT-002:** O acesso direto a um produto regulamentado oculto é negado, enquanto
  uma imagem ausente não produz uma resposta de API quebrada.
- **IT-003:** `POST /api/v1/carrinhos/convidado/itens` retorna a linha de carrinho e o total
  exatos para a requisição documentada em `_dx.md`.
- **IT-004:** o carrinho de convidado persiste após uma restauração de sessão do
  navegador e rejeita checkout vazio.
- **IT-005:** o cadastro define um cookie seguro de sessão e permite a um produtor
  selecionar somente propriedades próprias.
- **IT-006:** POST de pedido duplicado com a mesma chave de idempotência retorna a
  confirmação original.
- **IT-007:** `POST /api/v1/pedidos` persiste o pedido antes de receber a confirmação
  aceita do Mock ERP.
- **IT-008:** `GET /api/v1/pedidos/{idPedido}` nega um produtor autenticado diferente e
  não cria um pedido após a atualização da página.
- **IT-009:** `GET /api/v1/retaguarda/pedidos` retorna somente os resumos de pedido do
  tenant configurado.
- **IT-010:** o backoffice retorna um resultado vazio explícito e rejeita acesso de
  produtor.
- **IT-011:** migrações são aplicadas em um banco de dados PostgreSQL vazio e a
  verificação de limites do Spring Modulith é aprovada.

## Testes end-to-end

### Jornada móvel do produtor

- **E2E-001:** Catálogo móvel → pesquisar “semente” → detalhes do produto mostram
  descrição curta → adicionar ao carrinho.
- **E2E-002:** Catálogo móvel → estados de pesquisa indisponível/vazia mostram
  recuperação clara; um link direto regulamentado não pode ser pedido.
- **E2E-003:** Carrinho móvel → alterar quantidade → remover linha → total é
  atualizado; adicionar 100 linhas de fixture sem perder uma linha.
- **E2E-004:** Carrinho vazio → checkout é bloqueado; recarregar um carrinho de
  convidado o preserva até a confirmação bem-sucedida.
- **E2E-005:** Carrinho → criar conta → selecionar propriedade e retirada → confirmar
  → ver “Pedido recebido” e identificador em até um segundo.
- **E2E-006:** Checkout → conta inválida ou propriedade/retirada ausente → ver erro em
  foco e manter os dados inseridos no carrinho.
- **E2E-007:** Confirmação → Meus pedidos → abrir o pedido criado → ver item, preço,
  propriedade, retirada e confirmação aceita imutáveis.
- **E2E-008:** Entrar como outro produtor → link direto para o pedido criado → ver
  acesso negado.
- **E2E-009:** Entrar como operador da revenda → lista de backoffice → abrir detalhes
  do pedido.
- **E2E-010:** Entrar como operador da revenda sem pedidos → ver estado vazio
  explícito; entrar como produtor → rota de backoffice é negada.
