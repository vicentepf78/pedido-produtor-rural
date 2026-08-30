# ADR-002: Navegação como convidado e tela própria de Entrar

## Status

Accepted

## Date

2026-08-30

## Context

O MVP 0 identifica o produtor só no checkout, com e-mail e senha. A
arquitetura do POC mencionou Google. O pedido desta versão é “fazer o
login”, sem fechar sozinho se isso é muralha na frente do catálogo ou
entrada com provedor externo.

## Decision

O produtor navega no catálogo e monta o carrinho sem conta. “Entrar” fica
no topo. Se ainda não estiver identificado, o checkout também pede
entrada. Existe tela própria de Entrar e de Criar conta com e-mail e
senha. Entrar com Google fica de fora desta versão.

## Alternatives Considered

### Alternative 1: Exigir conta antes do catálogo ou do carrinho

- **Description**: Muralha de login na entrada da loja ou ao adicionar item.
- **Pros**: Identidade cedo para histórico e preço futuro.
- **Cons**: Atrasa o pedido e contradiz a premissa de agilidade.
- **Why rejected**: O usuário manteve convidado até precisar (Q2 = A).

### Alternative 2: Login só no checkout, sem tela `/entrar`

- **Description**: Continuar o padrão do MVP 0.
- **Pros**: Menos telas.
- **Cons**: Não entrega “fazer o login neste MVP” de forma visível no topo.
- **Why rejected**: O usuário pediu tela própria (Q3 = A).

### Alternative 3: E-mail, senha e Google nesta versão

- **Description**: Incluir provedor externo agora.
- **Pros**: Menos digitação no celular.
- **Cons**: Consentimento, vínculo de conta e recuperação ainda sem regra.
- **Why rejected**: Não inventar provedor sem decisão; Google fica em aberto
  para um incremento seguinte.

## Consequences

### Positive

- A descoberta continua imediata.
- O produtor reconhece “Entrar” no mesmo lugar das lojas que já usa.

### Negative

- Quem espera Google nesta versão não o terá.

### Risks

- Dois caminhos de entrada (topo e checkout) podem divergir em cópia e
  validação. Mitigação: a mesma regra de conta nos dois pontos.

## Implementation Notes

A coleta de propriedade e a confirmação do pedido continuam depois da
identidade, no checkout. Esta decisão não cria área “minha conta” além de
Entrar / Criar conta / Sair.

## References

- Grill de produto Q2 e Q3
- `.compozy/tasks/pedidos-insumos-mvp0/_spec.md`
