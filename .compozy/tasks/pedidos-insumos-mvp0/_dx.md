# Experiência do desenvolvedor: pedidos de insumos agrícolas MVP0

Contrato de superfície pública do MVP0. A aplicação de navegador consome estas rotas.

## Caminho principal

```text
1. GET /api/v1/catalogo/produtos?consulta=semente
2. POST /api/v1/carrinhos/convidado/itens
3. POST /api/v1/autenticacao/cadastro
4. POST /api/v1/pedidos
5. GET /api/v1/pedidos/{idPedido}
```

O produtor vê um produto, adiciona-o a um carrinho de visitante, cria uma conta somente
no checkout, confirma um pedido e recebe seu identificador de pedido.

## API

### Listar produtos do catálogo

```http
GET /api/v1/catalogo/produtos?consulta=semente&pagina=1&tamanhoPagina=24
```

```json
{
  "itens": [
    {
      "id": "prd_01J8F6T8HY3V",
      "nome": "Semente de milho Aurora 20 kg",
      "categoria": "Sementes",
      "descricaoCurta": "Cultivar para plantio de verão.",
      "unidade": "Saco",
      "precoUnitario": "620.00",
      "disponivel": true,
      "urlImagem": "/media/products/aurora-milho-20kg.jpg"
    }
  ],
  "pagina": 1,
  "tamanhoPagina": 24,
  "total": 1
}
```

### Adicionar um item ao carrinho de visitante

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
      "nome": "Semente de milho Aurora 20 kg",
      "quantidade": 2,
      "precoUnitario": "620.00",
      "totalLinha": "1240.00"
    }
  ],
  "total": "1240.00"
}
```

### Cadastrar e iniciar uma sessão

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
  "papeis": ["PRODUTOR"]
}
```

O navegador recebe um cookie de sessão seguro. A senha nunca aparece em uma
resposta ou log.

### Confirmar um pedido

```http
POST /api/v1/pedidos
Content-Type: application/json

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

### Visualizar um pedido do produtor

```http
GET /api/v1/pedidos/ord_01J8F7B7B9M4
```

```json
{
  "idPedido": "ord_01J8F7B7B9M4",
  "situacao": "RECEBIDO",
  "confirmacao": "ACEITA",
  "nomePropriedade": "Fazenda Santa Luzia",
  "preferenciaRetirada": "DEPOSITO_PRINCIPAL",
  "total": "1240.00",
  "itens": [
    {
      "nome": "Semente de milho Aurora 20 kg",
      "quantidade": 2,
      "precoUnitario": "620.00",
      "totalLinha": "1240.00"
    }
  ]
}
```

### Listar pedidos do produtor

```http
GET /api/v1/pedidos?pagina=1&tamanhoPagina=10
```

```json
{
  "itens": [
    {
      "idPedido": "ord_01J8F7B7B9M4",
      "situacao": "RECEBIDO",
      "confirmacao": "ACEITA",
      "total": "1240.00",
      "criadoEm": "2026-08-30T13:55:00Z"
    }
  ],
  "pagina": 1,
  "tamanhoPagina": 10,
  "total": 1
}
```

### Listar pedidos para o operador do revendedor

```http
GET /api/v1/retaguarda/pedidos?pagina=1&tamanhoPagina=25
```

```json
{
  "itens": [
    {
      "idPedido": "ord_01J8F7B7B9M4",
      "nomeProdutor": "João da Silva",
      "total": "1240.00",
      "situacao": "RECEBIDO",
      "confirmacao": "ACEITA",
      "criadoEm": "2026-08-30T13:55:00Z"
    }
  ],
  "pagina": 1,
  "tamanhoPagina": 25,
  "total": 1
}
```

## Erros

| Condição | Resposta |
| --- | --- |
| Carrinho não tem itens | `CARRINHO_VAZIO`: “Adicione ao menos um produto antes do checkout.” |
| Produto está indisponível | `PRODUTO_INDISPONIVEL`: “Este produto não está disponível.” |
| Produto não é elegível para o MVP0 | `PRODUTO_NAO_ELEGIVEL`: “Este produto não pode ser pedido no MVP0.” |
| Quantidade inválida | `QUANTIDADE_INVALIDA`: “Informe uma quantidade inteira positiva.” |
| Dados de checkout ausentes | `DADOS_CHECKOUT_OBRIGATORIOS`: “Escolha uma propriedade e a preferência de retirada.” |
| Confirmação duplicada | Retornar a confirmação de pedido existente. |
| Produtor abre o pedido de outro produtor | `ACESSO_PEDIDO_NEGADO`: “Você não pode visualizar este pedido.” |
