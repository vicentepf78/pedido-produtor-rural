# Histórias de usuário: pedidos de insumos agrícolas MVP0

Catálogo canônico de comportamentos do MVP0. Complementar a `_spec.md`.

## Personas

- **Produtor rural** — Precisa montar rapidamente um pequeno pedido inicial sem
  treinamento no sistema de produtos.
- **Operador do revendedor** — Precisa ver os pedidos recebidos e se a confirmação
  downstream simulada foi bem-sucedida.

## Índice de histórias

| ID | Área funcional | Persona | História |
| --- | --- | --- | --- |
| US-001 | Catálogo | Produtor rural | Encontra um produto não regulado disponível. |
| US-002 | Carrinho | Produtor rural | Monta e ajusta um pedido antes da identificação. |
| US-003 | Checkout | Produtor rural | Identifica-se, escolhe uma propriedade e confirma um pedido. |
| US-004 | Pedido | Produtor rural | Recebe uma confirmação clara e pode visualizar o pedido. |
| US-005 | Backoffice | Operador do revendedor | Vê os pedidos recebidos e a confirmação downstream. |

## Catálogo

### US-001: Encontrar um produto

**Como** produtor rural, **quero** navegar e pesquisar no catálogo curado,
**para que** eu possa escolher um insumo sem assistência.

Critérios de aceitação:

- AC-1: Dado um produto disponível, quando navego em sua categoria ou pesquiso seu nome,
  então vejo seu nome, imagem, preço unitário e unidade de compra.
- AC-2: Dado um produto regulado, quando navego ou pesquiso, então não o vejo
  no MVP0.

Casos de borda:

- EC-1: Pesquisa em branco ou sem correspondência → exibir um estado vazio com uma forma de limpar a pesquisa.
- EC-2: Imagem de produto ausente → exibir o produto sem uma imagem quebrada.
- EC-3: Produto indisponível → ele não pode ser adicionado ao carrinho.
- EC-4: Navegação direta para um produto oculto → negar acesso sem revelar dados.

## Carrinho

### US-002: Montar um pedido

**Como** produtor rural, **quero** adicionar, alterar e remover produtos no carrinho,
**para que** eu possa preparar um pedido antes de compartilhar dados pessoais.

Critérios de aceitação:

- AC-1: Dado um produto visível, quando adiciono uma quantidade positiva, então o carrinho
  exibe o item, preço unitário, total da linha e total do pedido.
- AC-2: Dado um item no meu carrinho, quando altero sua quantidade ou o removo, então
  o total do pedido reflete os itens atuais.

Casos de borda:

- EC-1: Quantidade zero, negativa ou não numérica → rejeitá-la com orientação clara.
- EC-2: Carrinho vazio → o checkout fica indisponível e explica o motivo.
- EC-3: Ação repetida de adicionar → consolidar o item em vez de duplicá-lo.
- EC-4: Cem itens no carrinho → manter quantidades e totais corretos.
- EC-5: Interrupção de conexão ao alterar o carrinho → preservar o último
  estado confirmado do carrinho.

## Checkout

### US-003: Confirmar um pedido

**Como** produtor rural, **quero** me identificar somente ao finalizar a compra e
escolher uma propriedade e uma preferência de retirada, **para que** eu conclua o pedido rapidamente.

Critérios de aceitação:

- AC-1: Dado um produtor não autenticado com itens no carrinho, quando inicio o checkout,
  então posso entrar ou criar uma conta com e-mail e senha.
- AC-2: Dado um produtor identificado, quando seleciono uma propriedade e uma preferência
  de retirada e confirmo, então o sistema registra um pedido local.

Casos de borda:

- EC-1: Credenciais inválidas ou e-mail duplicado → impedir a confirmação e explicar
  como recuperar.
- EC-2: Propriedade ou preferência de retirada ausente → identificar o campo obrigatório.
- EC-3: Duas tentativas de confirmação → criar somente um pedido para o mesmo envio.
- EC-4: Sessão de entrada expirada → retornar à identificação preservando o carrinho.
- EC-5: Navegação para trás → mantém os dados do checkout que o produtor já informou.

## Pedido

### US-004: Ver confirmação do pedido

**Como** produtor rural, **quero** uma confirmação imediata e compreensível,
**para que** eu saiba que o revendedor recebeu meu pedido.

Critérios de aceitação:

- AC-1: Dado um checkout válido, quando confirmo, então vejo um identificador de pedido e
  “Order received” em até um segundo.
- AC-2: Dado um pedido confirmado, quando o visualizo posteriormente, então vejo seus produtos,
  quantidades, total, propriedade, preferência de retirada e confirmação atual.

Casos de borda:

- EC-1: Produtor tenta abrir o pedido de outro produtor → negar acesso.
- EC-2: Atualização repetida da página → não cria outro pedido.

## Backoffice

### US-005: Inspecionar pedidos recebidos

**Como** operador do revendedor, **quero** listar os pedidos recebidos e suas
confirmações, **para que** eu possa acompanhar o fluxo da prova de conceito.

Critérios de aceitação:

- AC-1: Dado o acesso do revendedor, quando abro a lista de pedidos, então vejo o
  identificador do pedido, produtor, horário de criação, total e status da confirmação.
- AC-2: Dado um pedido, quando abro seus detalhes, então vejo seu snapshot imutável de itens e
  checkout.

Casos de borda:

- EC-1: Conta de produtor abre o backoffice → negar acesso.
- EC-2: Nenhum pedido recebido → exibir um estado vazio explícito.
- EC-3: Uma confirmação repetida → não altera um resultado bem-sucedido.
- EC-4: Visualização de lista desatualizada → atualizar retorna a confirmação mais recente conhecida.
