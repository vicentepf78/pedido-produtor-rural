# Task Memory: task_03

## Objective Snapshot

Catálogo curado (30 produtos) e carrinho de convidado; UT-001–012, IT-001–004, E2E-001–004; Visual Contract VC-01–07.

## Important Decisions

- IDs de produto na API são UUID (modelo da spec + precedente da identidade), não os ULID de exemplo do `_dx.md`. IT-003 localiza Aurora 20 kg (R$ 620,00) pelo id semeado.
- `GET /api/v1/catalogo/produtos` sem `consulta` lista os visíveis. `consulta` em branco devolve página vazia (UT-003).
- Detalhe: `GET /api/v1/catalogo/produtos/{id}` — regulamentado/ausente → `PRODUTO_NAO_ELEGIVEL`.
- Mutações extras: `GET /api/v1/carrinhos/convidado`, `PATCH` e `DELETE` em `.../itens/{idProduto}`.
- Seed: 30 não regulamentados + 1 regulamentado `10000000-0000-4000-8000-000000000099` só para negação.
- 100 linhas: UT-011 usa 100 produtos sintéticos. E2E-003 usa fixture de 100 linhas (o catálogo tem 30 SKUs).
- IT-004: persistência pelo cookie. Checkout vazio não chama `POST /api/v1/pedidos`; botão desabilitado na S2.
- Aba Defensivos omitida. Sem atalho “Backoffice (operador)”.
- E2E Playwright intercepta `/api` (sem Postgres no `npm test`). IT cobre a API real via Testcontainers.

## Learnings

- `catalog.application` é a única porta de produto para `cart`.
- CSRF continua obrigatório nas mutações públicas do carrinho.
- Mockito strict stubs: fakes compartilhados no `@BeforeEach` do carrinho precisam de `LENIENT`.

## Files / Surfaces

- Backend catalog/cart (domínio, aplicação, API, Flyway V2), `ConfiguracaoSeguranca`.
- Frontend `features/catalog`, `features/cart`, `estilos.css`, `LayoutApp`, Playwright `e2e/`.
- QA: `docs/qa/scenarios/CAT-*.md`, `CART-*.md` (`untested`).

## Errors / Corrections

- Stubbing desnecessário no teste de carrinho → `@MockitoSettings(LENIENT)`.
- Import de `features/catalog/api.ts` corrigido para `../../infra/http`.

## Ready for Next Run

- Bundle VC-01–07 em `evidence/visual/task_03/` (Playwright + Pillow; sem `eng-ui-screenshot`).
- Abas de categoria persistem na busca vazia; ordem Todos → Sementes → Fertilizantes → Correção.
- Walk QA Phase C. Peer-review Phase D.
