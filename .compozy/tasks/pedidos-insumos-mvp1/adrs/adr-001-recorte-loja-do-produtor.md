# ADR-001: Recorte do MVP 1 na loja do produtor

## Status

Accepted

## Date

2026-08-30

## Context

O documento de arquitetura descreve um “MVP 1 completo” com backoffice de
catálogo, cadastro de produtor e acompanhamento de ERP. O piloto posterior
ainda inclui Agrofit, preço comercial, aprovação e receita. O pedido desta
versão é melhorar descoberta, visual e login sem puxar esse escopo.

## Decision

O MVP 1 entrega somente a loja do produtor: catálogo, login, carrinho,
checkout, pedido e o visual novo. Não inclui o backoffice maior da
arquitetura nem o piloto.

## Alternatives Considered

### Alternative 1: Loja + backoffice da arquitetura §32

- **Description**: Importar produtos, cadastrar produtor e acompanhar ERP.
- **Pros**: Cobre o “MVP 1 recomendado” do documento de descoberta.
- **Cons**: Mistura operação da revenda com a jornada rápida do produtor.
- **Why rejected**: O usuário recortou explicitamente a loja (Q1 = A).

### Alternative 2: Incluir o piloto comercial

- **Description**: Agrofit, preço, aprovação e receita nesta versão.
- **Pros**: Chega mais perto de um pedido comercial real.
- **Cons**: Inventa regras comerciais, fiscais e regulatórias ainda em aberto.
- **Why rejected**: Fora do pedido e das premissas de agilidade desta versão.

## Consequences

### Positive

- A jornada do produtor permanece o resultado principal.
- Operação, ERP real e catálogo governamental ficam para incrementos seguintes.

### Negative

- Quem espera o “MVP 1” da arquitetura não verá importação nem ERP nesta entrega.

### Risks

- Ampliar o visual da retaguarda pode puxar cadastro de catálogo e ERP.
  Mitigação: o recorte funcional da retaguarda continua só listar e abrir
  pedidos; o visual novo não autoriza importação, preço nem ERP.

## Implementation Notes

Emendada em 2026-08-30 após Q12 = C: a inspeção de pedidos do operador
permanece no escopo funcional do MVP 0 e passa a usar o mesmo visual da
loja. Ver ADR-004.

## References

- `agri_platform_mvp_arquitetura.md` §32–§35
- Grill de produto Q1
