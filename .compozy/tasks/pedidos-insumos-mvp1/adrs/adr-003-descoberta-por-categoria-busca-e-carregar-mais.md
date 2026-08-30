# ADR-003: Carrossel de categorias, busca composta e Carregar mais

## Status

Accepted

## Date

2026-08-30

## Context

O catálogo do MVP 0 lista categorias em abas e busca por nome, sem
paginação visível. O pedido desta versão é um carrossel no estilo da
referência anexa, listagem contínua e campo de busca. Scroll infinito
puro compete com “Carregar mais” em listas de compra.

## Decision

As categorias visíveis são as mesmas três do conjunto atual (Sementes,
Fertilizantes, Correção), mais o atalho Todos, cada uma com imagem. A
busca por nome permanece. Categoria e busca somam; existe ação explícita
de limpar filtros. A listagem cresce com “Carregar mais”, não com scroll
infinito automático. Defensivos e demais categorias regulamentadas não
entram no carrossel.

## Alternatives Considered

### Alternative 1: Scroll infinito automático

- **Description**: Novos produtos aparecem ao chegar no fim da lista.
- **Pros**: Gesto contínuo, parecido com feed de marketplace.
- **Cons**: Pé da página some; voltar do detalhe e teclado ficam piores.
- **Why rejected**: O usuário escolheu Carregar mais (Q5 = B).

### Alternative 2: Busca ignora a categoria

- **Description**: Digitar nome pesquisa o catálogo inteiro.
- **Pros**: Um campo, um resultado global.
- **Cons**: O recorte do carrossel some sem aviso.
- **Why rejected**: O usuário pediu filtros que somam e limpeza explícita
  (Q6 = C).

### Alternative 3: Mostrar Defensivos no carrossel sem pedido

- **Description**: Card visível e lista vazia ou bloqueada.
- **Pros**: Taxonomia mais parecida com o mercado agro.
- **Cons**: Reabre produto regulado sem regra comercial ou legal.
- **Why rejected**: O usuário manteve as três categorias atuais (Q4 = A).

## Consequences

### Positive

- Descoberta visual sem inventar taxonomia.
- Filtros previsíveis para quem já escolheu uma categoria.

### Negative

- Com o conjunto atual de 30 produtos, “Carregar mais” pode aparecer pouco.
  O controle existe para quando houver mais páginas.

### Risks

- Imagens de categoria sem fonte definida. Mitigação: decidir no grill se
  são figurativas de demonstração ou enviadas pela revenda.

## Implementation Notes

O visual do carrossel e das demais telas da jornada será modelado no
Open Design (catálogo, topo, Entrar, carrinho, checkout e pedido recebido).

## References

- Grill de produto Q4, Q5, Q6 e Q7
- Referência visual anexa (carrossel de cards com foto e rótulo)
