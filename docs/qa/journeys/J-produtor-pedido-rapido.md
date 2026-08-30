# J-produtor-pedido-rapido

Hipótese do ADR-001: o produtor conclui um pedido de produto não regulamentado
com identificação tardia e confirmação imediata.

```mermaid
flowchart TD
    E[Entrada: / ou /catalogo] --> A[Navega ou pesquisa o catálogo]
    A -->|resultados| B[Vê nome, preço, unidade e imagem ou placeholder]
    A -->|busca vazia ou sem correspondência| A1[Estado vazio + Limpar busca]
    A1 --> A
    A -->|item indisponível| A2[Visível sem ação de compra]
    A -->|link direto a regulamentado| A3[PRODUTO_NAO_ELEGIVEL sem revelar conteúdo]
    A2 --> A
    B --> C[Adiciona quantidade positiva ao carrinho]
    C -->|quantidade inválida| C1[QUANTIDADE_INVALIDA + carrinho anterior intacto]
    C1 --> C
    C --> D[Ajusta ou remove no /carrinho]
    D -->|carrinho vazio| D1[Checkout bloqueado: CARRINHO_VAZIO]
    D1 -->|abandona a aba| X1[Abandono: volta depois; cookie chaveCarrinhoConvidado]
    X1 -->|retoma| D
    D --> F[Inicia /checkout]
    F --> G[Entra ou cria conta]
    G -->|EMAIL_DUPLICADO ou CREDENCIAIS_INVALIDAS| G1[Erro em foco; dados e carrinho preservados]
    G1 --> G
    G -->|sessão expirada| G2[NAO_AUTENTICADO; carrinho de convidado intacto]
    G2 --> G
    G --> H[Escolhe propriedade própria e retirada]
    H -->|campo ausente| H1[DADOS_CHECKOUT_OBRIGATORIOS]
    H1 --> H
    H --> I[Confirma POST /api/v1/pedidos com Idempotency-Key]
    I --> J[Efeito: pedido local persistido RECEBIDO]
    J --> K[Efeito: GatewayErpSimulado devolve ACEITA sem mutar o snapshot]
    K --> L[Tela: Pedido recebido + identificador em até 1s]
    I -->|duplo envio mesma chave| I2[Um só pedido; mesma confirmação]
    I2 --> L
    L --> M[Reabre /meus-pedidos e /pedidos/id]
    M -->|pedido de outro produtor| M1[ACESSO_PEDIDO_NEGADO]
    M --> N[True end: mesmo id, itens, preços, propriedade, retirada e ACEITA no reload]
```

```yaml
journey:
  id: J-produtor-pedido-rapido
  name: Concluir pedido rápido
  value_statement: "O produtor recebe um pedido local com identificador e confirmação ACEITA sem depender de ERP real."
  personas: [Produtor rural, Produtora no campo, Produtor após falha]
  entry_points:
    - url: /
      origin: direct
    - url: /catalogo
      origin: in-app-nav
    - url: GET /api/v1/catalogo/produtos
      origin: in-app-nav
    - url: POST /api/v1/pedidos
      origin: in-app-nav
  actions:
    - step: 1
      verb: Abre a loja e encontra um produto disponível
      expected_observable: Nome, preço, unidade e imagem ou placeholder em até 3s
    - step: 2
      verb: Adiciona e ajusta quantidades no carrinho
      expected_observable: Totais de linha e pedido batem com o preço unitário atual
    - step: 3
      verb: Identifica-se só no checkout
      expected_observable: Cookie sessao HttpOnly; propriedades só as próprias
    - step: 4
      verb: Escolhe propriedade e retirada e confirma
      expected_observable: Pedido recebido e identificador visíveis
    - step: 5
      verb: Reabre o pedido em Meus pedidos
      expected_observable: Snapshot imutável igual ao da confirmação
  goal:
    observable: Pedido recebido com o mesmo identificador na confirmação e em Meus pedidos
    side_effects: [record-created, mock-erp-aceita]
  true_end_state: Reload de /pedidos/{id} e /meus-pedidos mostra o mesmo snapshot; confirmacao ACEITA; o mock não alterou itens nem total
  exit:
    natural: Detalhe do pedido ou lista Meus pedidos
  abandonment:
    - at_step: 2
      how: Fecha a aba com itens no carrinho, sem se identificar
      resume: Volta pelo mesmo navegador; chaveCarrinhoConvidado restaura o último estado confirmado
    - at_step: 3
      how: Erra a senha ou deixa a sessão expirar
      resume: Volta à identificação sem perder o carrinho
  crosses: [catalog, cart, identity, producer, order, integration]
```
