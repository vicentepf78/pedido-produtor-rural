# ADR-007: Conta de operador na Entrar da loja vai à retaguarda

## Status

Accepted

## Date

2026-08-30

## Context

A Entrar da loja usa e-mail e senha. Uma conta de operador pode ser
digitada nesse mesmo formulário. Deixar o destino em aberto geraria
topo de compra para quem opera a revenda, ou um beco sem mensagem.

## Decision

Se a conta autenticada for de operador, o destino após Entrar ou Criar
conta (quando aplicável) é a retaguarda — a lista de pedidos da
revenda — e o topo passa a ser o de operação. Não se recusa o login
nem se deixa o operador no catálogo.

## Alternatives Considered

### Alternative 1: Recusar a conta de operador na Entrar da loja

- **Description**: Mensagem de papel incompatível.
- **Pros**: Separação rígida de portas.
- **Cons**: Operador que abre a loja fica sem caminho claro.
- **Why rejected**: O usuário pediu enviar à retaguarda.

### Alternative 2: Tratar como produtor

- **Description**: Destino pela origem (Q8) mesmo para operador.
- **Pros**: Uma regra de retorno só.
- **Cons**: Operador cai no checkout ou no catálogo com o topo errado.
- **Why rejected**: Contradiz o topo próprio da US-009.

## Consequences

### Positive

- Um único formulário de e-mail e senha; o papel decide o destino.
- O operador não precisa decorar um endereço separado na primeira vez.

### Negative

- Criar conta pela loja continua sendo jornada de produtor; operador
  não se auto-cadastra aqui (cadastro operacional continua fora).

### Risks

- Confundir “enviar à retaguarda” com permitir que o operador compre.
  Mitigação: após o login de operador, o topo e as rotas são os da
  US-009; catálogo de compra não é o destino.

## Implementation Notes

Criar conta na loja cria papel de produtor. Só contas já operador
disparam este desvio.

## References

- Confirmação da Parte I em 2026-08-30
- ADR-002, ADR-006
