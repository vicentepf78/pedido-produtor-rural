# Peer review

## Round 1

Base: `main`. Spec: `.compozy/tasks/pedidos-insumos-mvp0`.
Veredito: **SHIP**.

Lentes: correção (`12fec1ba`), testes (`17a59439`), arquitetura (`6edcdde8`), UX (`3201929f`).

### Remediado

- Chave auto de idempotência passou a ser UUID por requisição (não colapsa o próximo checkout).
- Recuperação de `DataIntegrityViolation` em transação nova; UT-018 cobre o caminho.
- Persistência do pedido exige produto pedível.
- Quantidade inválida não cai mais em `0.5`.
- Cadastro novo registra propriedade no checkout (`POST /api/v1/produtor/propriedades`).
- `catalog` e `cart` CLOSED com `allowedDependencies`.
- `ConsultaPedidoRetaguarda` autoriza operador no módulo dono.
- `esvaziar` saiu de `ConsultaCarrinho` para `ComandoCarrinho`.
- Erro de adicionar no catálogo é inline; carrinho tem loading/falha de restore.
- Checkout é `<form>`, CTA “Confirmando…”, vazio explícito, Sair após identidade.
- S4/S5/S6 com ação de recuperação; 404; skip-link; abas com setas; `aria-describedby`; `prefers-reduced-motion`.
- `_dx.md`: detalhe da retaguarda, `ACESSO_NEGADO`, `Idempotency-Key`, registro de propriedade.
- `make gate` inclui `test-e2e-web`. CSRF dos ITs lê `"token"`. IT-004/IT-008 completos. E2E-005 sem relógio.

### Aceito (padrão existente, sem rewrite)

- Named interface `application` ainda publica serviços (todas as implementações no mesmo pacote).
- Application continua a usar repositórios JPA (padrão do MVP0).
- Matriz HTTP em `identity` (raiz de composição efetiva).
- E2E Playwright permanece contrato de UI com mock; o walk real foi Phase C. O gate agora roda a suíte.
- IT-010 ainda limpa o schema `order` compartilhado.

`make gate` exit 0 após a remediação (Surefire 35, Failsafe 16, Playwright 10).
