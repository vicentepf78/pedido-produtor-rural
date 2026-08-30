# ADR-005: Retorno após Entrar, Pedido visível e tamanho da lista

## Status

Accepted

## Date

2026-08-30

## Context

Depois de fechar convidado + tela de Entrar, faltava o destino pós-login,
o atalho Pedido sem conta, o tamanho da listagem e o que permanece igual
ao MVP 0 (preço, propriedade, recuperação de senha, identificação no topo).

## Decision

- Após Entrar ou Criar conta, o produtor volta à página de origem
  (catálogo, carrinho, checkout ou meus pedidos).
- O atalho Pedido permanece no topo sem conta e pede Entrar para ver
  os pedidos daquela pessoa.
- O conjunto continua com 30 produtos não regulamentados. A lista
  carrega 10 produtos por vez por padrão. O produtor pode escolher 15,
  30 ou 50 por página. “Carregar mais” acrescenta o próximo bloco nesse
  tamanho.
- Esqueci a senha fica de fora.
- Preço unitário igual para convidado e logado.
- Depois de logado, o topo mostra identificação curta (nome ou e-mail) e
  Sair.
- Propriedade rural continua só no checkout.

## Alternatives Considered

### Alternative 1: Sempre ir ao catálogo ou ao checkout após Entrar

- **Description**: Destino fixo.
- **Pros**: Uma regra só.
- **Cons**: Quem entrou no meio do pedido perde o contexto.
- **Why rejected**: O usuário pediu voltar à origem (Q8 = A).

### Alternative 2: Ocultar Pedido até haver conta

- **Description**: Atalho só para logado.
- **Pros**: Não mostra um muro.
- **Cons**: O item some do topo e quebra o mapa mental da loja.
- **Why rejected**: O usuário manteve o atalho visível (Q9 = A).

### Alternative 3: Sem controle de tamanho, só 10 fixos

- **Description**: Sempre 10 por bloco.
- **Pros**: Menos um controle.
- **Cons**: Quem quer ver mais precisa clicar “Carregar mais” várias vezes.
- **Why rejected**: O usuário pediu 10 padrão e opções 15, 30 e 50.

## Consequences

### Positive

- Login não interrompe a compra.
- A listagem escala sem inventar SKUs novos.

### Negative

- Com 30 produtos, a opção 50 mostra o conjunto inteiro de uma vez.
- Pedido sem conta introduz um passo de Entrar fora do checkout.

### Risks

- Tamanho 50 maior que o conjunto atual pode parecer “filtro morto”.
  Mitigação: as quatro opções existem; o conjunto continua 30 até outro
  incremento.

## Implementation Notes

O controle de 10 / 15 / 30 / 50 vale na listagem de produtos da loja,
não na lista da retaguarda, até decisão em contrário.

## References

- Grill de produto Q8–Q11 e Q13–Q15
- ADR-002, ADR-003
