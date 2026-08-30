# ADR-006: Topo do operador, mídia de demonstração e checkout vazio

## Status

Accepted

## Date

2026-08-30

## Context

Com a retaguarda no mesmo visual da loja, faltava o mapa do topo do
operador, a origem das fotos do carrossel, se o design inclui a
retaguarda e o que o atalho Checkout faz sem itens.

## Decision

- O operador vê um topo próprio: pedidos da revenda, identificação e
  Sair. Não vê Catálogo, Carrinho, Checkout nem Pedido do produtor.
- Os cards do carrossel usam fotos de demonstração no conjunto
  selecionado. Não são mídia oficial da revenda e não há envio de foto
  pela operação nesta versão.
- O workspace de design desta versão inclui lista e detalhe da
  retaguarda no mesmo pacote da loja.
- Checkout no topo com carrinho vazio abre o checkout e explica que é
  preciso ter itens. O atalho não some.

## Alternatives Considered

### Alternative 1: Mesmo topo da loja com atalho extra

- **Description**: Operador navega com Catálogo e Carrinho visíveis.
- **Pros**: Um único cabeçalho.
- **Cons**: Ação diária errada (checkout de produtor).
- **Why rejected**: O usuário escolheu topo próprio (Q16 = A).

### Alternative 2: Cards sem foto

- **Description**: Só rótulo no formato de card.
- **Pros**: Sem mídia fictícia.
- **Cons**: Afasta da referência visual acordada.
- **Why rejected**: O usuário pediu fotos de demonstração (Q17 = A).

### Alternative 3: Checkout vazio manda ao carrinho

- **Description**: Atalho Checkout vira carrinho quando não há itens.
- **Pros**: Evita uma tela de bloqueio.
- **Cons**: Dois destinos para o mesmo atalho.
- **Why rejected**: O usuário manteve o checkout com mensagem (Q19 = A).

## Consequences

### Positive

- Papéis não se misturam no topo.
- O carrossel pode parecer uma loja real sem inventar upload.

### Negative

- Fotos de demonstração podem ser lidas como catálogo oficial.

### Risks

- Confundir mídia de demonstração com acervo da revenda. Mitigação:
  Non-Goal de envio de mídia e regra explícita de demonstração.

## Implementation Notes

A tela Entrar da loja e a identificação do operador ainda podem
compartilhar a mesma regra de e-mail e senha; o destino de uma conta de
operador que usa a Entrar da loja permanece em Open Questions.

## References

- Grill de produto Q16–Q19
- ADR-004
