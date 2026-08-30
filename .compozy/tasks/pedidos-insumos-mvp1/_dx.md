# Experiência do desenvolvedor: pedidos de insumos agrícolas MVP 1

Contrato de superfície pública do MVP 1. A aplicação de navegador consome
estas rotas. Complementa e altera o contrato do MVP 0: o cliente não deve
assumir listagem sem `categoria`, tamanho 24 nem busca em branco vazia.

## Caminho principal

```text
1. GET /api/v1/autenticacao/csrf
2. GET /api/v1/autenticacao/sessao
3. GET /api/v1/catalogo/produtos?pagina=1&tamanhoPagina=10
4. GET /api/v1/catalogo/produtos?categoria=Fertilizantes&consulta=ureia&pagina=1&tamanhoPagina=10
5. POST /api/v1/carrinhos/convidado/itens
6. POST /api/v1/autenticacao/entrada
7. GET /api/v1/autenticacao/sessao
8. POST /api/v1/pedidos
9. GET /api/v1/pedidos/{idPedido}
```

O produtor vê o catálogo sem conta, recorta por categoria e nome, carrega
o próximo bloco, adiciona ao carrinho, entra pelo topo, volta à origem e
confirma o pedido.

## Rotas do navegador

| Caminho | Quem usa | Nota |
| --- | --- | --- |
| `/catalogo` | Convidado ou produtor | Carrossel, busca, tamanho, Carregar mais |
| `/carrinho` | Convidado ou produtor | |
| `/checkout` | Convidado ou produtor | Vazio explica falta de itens |
| `/entrar?origem=/checkout` | Convidado | `origem` só caminho relativo permitido |
| `/cadastro?origem=/meus-pedidos` | Convidado | Cria papel PRODUTOR |
| `/meus-pedidos` | Produtor; convidado vê Entrar | |
| `/pedidos/{idPedido}` | Produtor dono | |
| `/retaguarda/pedidos` | Operador | Destino após Entrar com papel operador |
| `/retaguarda/pedidos/{idPedido}` | Operador | |

`origem` permitida: `/catalogo`, `/carrinho`, `/checkout`, `/meus-pedidos`,
`/pedidos/{id}`, `/retaguarda/pedidos` e `/retaguarda/pedidos/{id}`.
Qualquer outro valor cai em `/catalogo`. Conta OPERADOR_REVENDA ignora
`origem` da loja e vai a `/retaguarda/pedidos`.

## API

### Sessão atual

```http
GET /api/v1/autenticacao/sessao
```

Convidado:

```json
{ "autenticado": false }
```

Produtor:

```json
{
  "autenticado": true,
  "idUsuario": "usr_01J8F6XJ9D2Q",
  "nome": "João da Silva",
  "email": "joao.silva@example.com",
  "papeis": ["PRODUTOR"]
}
```

Operador: o mesmo formato com `"papeis": ["OPERADOR_REVENDA"]`.
Não exige autenticação. Senha nunca aparece.

### Obter token CSRF

```http
GET /api/v1/autenticacao/csrf
```

```json
{ "token": "csrf-token" }
```

O cookie `XSRF-TOKEN` é gravado. Mutações enviam `X-XSRF-TOKEN`.

### Cadastrar e iniciar sessão

```http
POST /api/v1/autenticacao/cadastro
Content-Type: application/json

{
  "nome": "João da Silva",
  "email": "joao.silva@example.com",
  "senha": "Rural#2026Order"
}
```

```json
{
  "idUsuario": "usr_01J8F6XJ9D2Q",
  "nome": "João da Silva",
  "email": "joao.silva@example.com",
  "papeis": ["PRODUTOR"]
}
```

Cookie de sessão `sessao` (HttpOnly). Cadastro pela loja cria somente
PRODUTOR.

### Entrar

```http
POST /api/v1/autenticacao/entrada
Content-Type: application/json

{
  "email": "operador.revenda@example.com",
  "senha": "Rural#2026Order"
}
```

```json
{
  "idUsuario": "usr_01J8F6OP7REV",
  "nome": "Operador Demonstracao",
  "email": "operador.revenda@example.com",
  "papeis": ["OPERADOR_REVENDA"]
}
```

O cliente envia o operador a `/retaguarda/pedidos`. Produtor volta a
`origem`.

### Encerrar sessão

```http
POST /api/v1/autenticacao/saida
```

Resposta `204`. Não apaga `chaveCarrinhoConvidado`.

### Listar produtos

```http
GET /api/v1/catalogo/produtos?categoria=Fertilizantes&consulta=ureia&pagina=1&tamanhoPagina=10
```

```json
{
  "itens": [
    {
      "id": "prd_01J8F6T8HY3V",
      "nome": "Ureia agrícola 50 kg",
      "categoria": "Fertilizantes",
      "descricaoCurta": "Fonte nitrogenada para cobertura.",
      "unidade": "Saco",
      "precoUnitario": "198.00",
      "disponivel": true,
      "urlImagem": "/media/products/ureia-50kg.jpg"
    }
  ],
  "pagina": 1,
  "tamanhoPagina": 10,
  "total": 1
}
```

Regras:

- `consulta` ausente ou em branco: lista o recorte da categoria, não
  uma página vazia.
- `categoria` ausente, em branco ou `Todos`: todos os elegíveis.
- `categoria` aceita `Sementes`, `Fertilizantes` ou `Correção`. Outro
  valor: `CATEGORIA_INVALIDA`.
- `pagina` começa em 1.
- `tamanhoPagina` padrão `10`. Aceitos: `10`, `15`, `30`, `50`. Outro
  valor: `TAMANHO_PAGINA_INVALIDO`.
- Produtos regulamentados nunca entram em `itens` nem em `total`.
- Carregar mais é `pagina+1` com os mesmos `categoria`, `consulta` e
  `tamanhoPagina`. Itens não se repetem entre páginas.

### Detalhe do produto

```http
GET /api/v1/catalogo/produtos/{idProduto}
```

Mesmo objeto de item. Regulamentado ou inexistente: `PRODUTO_NAO_ELEGIVEL`
sem revelar o conteúdo.

### Carrinho de convidado

```http
POST /api/v1/carrinhos/convidado/itens
Content-Type: application/json

{
  "idProduto": "prd_01J8F6T8HY3V",
  "quantidade": 2
}
```

```json
{
  "itens": [
    {
      "idProduto": "prd_01J8F6T8HY3V",
      "nome": "Ureia agrícola 50 kg",
      "quantidade": 2,
      "precoUnitario": "198.00",
      "totalLinha": "396.00"
    }
  ],
  "total": "396.00"
}
```

Também: `GET /api/v1/carrinhos/convidado`,
`PATCH /api/v1/carrinhos/convidado/itens/{idProduto}`,
`DELETE /api/v1/carrinhos/convidado/itens/{idProduto}`.
Cookie `chaveCarrinhoConvidado` (HttpOnly, 30 dias).

### Propriedades

```http
GET /api/v1/produtor/propriedades
```

```json
{
  "itens": [
    { "id": "prop_01J8F71VTX5R", "nome": "Fazenda Santa Luzia" }
  ]
}
```

```http
POST /api/v1/produtor/propriedades
Content-Type: application/json

{ "nome": "Fazenda Santa Luzia" }
```

Somente PRODUTOR.

### Confirmar pedido

```http
POST /api/v1/pedidos
Content-Type: application/json
Idempotency-Key: 9f3e2c1a-4b5d-6e7f-8a9b-0c1d2e3f4a5b

{
  "idPropriedade": "prop_01J8F71VTX5R",
  "preferenciaRetirada": "DEPOSITO_PRINCIPAL"
}
```

```json
{
  "idPedido": "ord_01J8F7B7B9M4",
  "situacao": "RECEBIDO",
  "confirmacao": "ACEITA",
  "mensagem": "Pedido recebido"
}
```

Exige PRODUTOR. Operador recebe `ACESSO_NEGADO`.

### Pedidos do produtor

```http
GET /api/v1/pedidos?pagina=1&tamanhoPagina=10
GET /api/v1/pedidos/ord_01J8F7B7B9M4
```

O detalhe inclui propriedade, retirada, total e itens com snapshot de
preço. Outro produtor: `ACESSO_PEDIDO_NEGADO`.

### Retaguarda

```http
GET /api/v1/retaguarda/pedidos?pagina=1&tamanhoPagina=25
GET /api/v1/retaguarda/pedidos/{idPedido}
```

Exige OPERADOR_REVENDA. Produtor: `403` `ACESSO_NEGADO`.

## Erros

| Condição | Resposta |
| --- | --- |
| Carrinho sem itens | `CARRINHO_VAZIO`: “Adicione ao menos um produto antes do checkout.” |
| Produto indisponível | `PRODUTO_INDISPONIVEL`: “Este produto não está disponível.” |
| Produto não elegível | `PRODUTO_NAO_ELEGIVEL`: “Este produto não pode ser pedido nesta loja.” |
| Quantidade inválida | `QUANTIDADE_INVALIDA`: “Informe uma quantidade inteira positiva.” |
| Checkout incompleto | `DADOS_CHECKOUT_OBRIGATORIOS`: “Escolha uma propriedade e a preferência de retirada.” |
| Confirmação duplicada | Devolve a confirmação já existente. |
| Pedido de outro produtor | `ACESSO_PEDIDO_NEGADO`: “Você não pode visualizar este pedido.” |
| E-mail duplicado | `EMAIL_DUPLICADO`: “Este e-mail já está cadastrado. Entre com sua senha ou use outro e-mail.” |
| Credenciais inválidas | `CREDENCIAIS_INVALIDAS`: “E-mail ou senha inválidos.” |
| Sessão ausente | `NAO_AUTENTICADO`: “Entre ou crie uma conta para continuar.” |
| Papel sem permissão | `ACESSO_NEGADO`: “Você não tem permissão para este recurso.” |
| Categoria inválida | `CATEGORIA_INVALIDA`: “Escolha Todos, Sementes, Fertilizantes ou Correção.” |
| Tamanho de página inválido | `TAMANHO_PAGINA_INVALIDO`: “Escolha 10, 15, 30 ou 50 produtos por página.” |
| Origem de retorno inválida | O cliente ignora e usa `/catalogo`; a API de autenticação não precisa rejeitar. |
