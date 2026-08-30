# ADR-004: Retaguarda no mesmo visual da loja

## Status

Accepted

## Date

2026-08-30

## Context

O ADR-001 recortou o MVP 1 na loja do produtor e deixou a inspeção de
pedidos do MVP 0 sem redesenho. Na segunda rodada o usuário pediu o mesmo
visual da loja na retaguarda (Q12 = C), sem reabrir importação de
catálogo, cadastro operacional de produtor nem ERP.

## Decision

A lista e o detalhe de pedidos do operador usam o mesmo visual da loja.
A capacidade permanece a do MVP 0: inspecionar pedidos recebidos e a
confirmação registrada. Não entram manutenção de catálogo, mídia,
notificação nem cadastro operacional de produtores.

## Alternatives Considered

### Alternative 1: Retaguarda intacta, sem visual novo

- **Description**: Continuar as telas do MVP 0.
- **Pros**: Menor escopo de interface.
- **Cons**: Jornada partida entre loja nova e operação antiga.
- **Why rejected**: O usuário escolheu o mesmo visual (Q12 = C).

### Alternative 2: Fora deste MVP

- **Description**: Não mexer na retaguarda.
- **Pros**: Foco só no produtor.
- **Cons**: O operador ficaria na interface antiga enquanto a loja muda.
- **Why rejected**: Recusado em Q12.

## Consequences

### Positive

- Produtor e operador reconhecem o mesmo produto.
- O recorte funcional da operação não cresce.

### Negative

- O Open Design precisa incluir lista e detalhe da retaguarda, além da
  jornada do produtor.

### Risks

- Tratar “mesmo visual” como licença para o backoffice da arquitetura §32.
  Mitigação: esta decisão cobre só apresentação das telas já existentes.

## Implementation Notes

O topo do operador (o que aparece no lugar de Catálogo / Carrinho /
Checkout / Pedido) ainda não está decidido. Ver grill seguinte.

## References

- ADR-001
- Grill de produto Q12
