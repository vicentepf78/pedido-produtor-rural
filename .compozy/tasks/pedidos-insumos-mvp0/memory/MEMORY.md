# Workflow Memory: pedidos-insumos-mvp0

## Current State

- `task_01`–`task_05` concluídas. Próxima: Phase C `qa-report` (`task_06`).
- Frontend pinado em Vite 6 (Node do host é 20.17).

## Shared Decisions

- Pacote Java-base: `br.agriplataforma`.
- Spring Boot 4.1.1 + Spring Modulith 2.1.1.
- Módulos não-OPEN; pacote `application` é named interface. `domain`/`api`/`infrastructure` internos.
- Flyway por módulo via `FlywayMigrationStrategy` (um schema + history table por módulo). Em Boot 4 a classe está em `org.springframework.boot.flyway.autoconfigure`.
- Tenant semeado: `11111111-1111-1111-1111-111111111111` (`Revenda demonstracao MVP0`), tabela `tenant."tenant"`.
- ORM: `PhysicalNamingStrategyStandardImpl` + `globally_quoted_identifiers`; `ddl-auto: none`.
- Desenvolvimento nesta branch: `mvp-0`.
- Wireframes em `references/mvp0-pedidos-insumos.html`; não há tarefa de desenho.
- Cookie de sessão: `sessao` (HttpOnly + Secure fora do perfil `local`). CSRF: `GET /api/v1/autenticacao/csrf` + cookie `XSRF-TOKEN` + header `X-XSRF-TOKEN`.
- Superfície de identidade: `POST /api/v1/autenticacao/cadastro`, `POST /api/v1/autenticacao/entrada`, `POST /api/v1/autenticacao/saida`. Propriedades: `GET /api/v1/produtor/propriedades`.
- Papéis: `PRODUTOR` e `OPERADOR_REVENDA`. Fixtures fictícias: `operador.revenda@example.com`, `produtor.alfa@example.com` (Fazenda Norte, Sitio Recanto), `produtor.beta@example.com` (Fazenda Sul). Senha só no código de teste.
- Identidade não deve apagar o cookie `chaveCarrinhoConvidado`. Catálogo e carrinho de convidado são `permitAll` em `ConfiguracaoSeguranca`.
- `UserDetailsServiceAutoConfiguration` está excluída (evita senha gerada em log).
- IDs de produto na API são UUID. Catálogo sem `consulta` lista visíveis; `consulta` em branco devolve vazio.
- Cookie de convidado: `chaveCarrinhoConvidado` (HttpOnly, SameSite=Lax, Secure fora de `local`, Max-Age 30d), gravado na primeira mutação do carrinho.
- Seed: 30 produtos não regulamentados + 1 regulamentado `10000000-0000-4000-8000-000000000099` (fora da listagem). Aurora: `10000000-0000-4000-8000-000000000001` a R$ 620,00.
- Detalhe: `GET /api/v1/catalogo/produtos/{id}`. Mutações extras do carrinho: GET/PATCH/DELETE em `/api/v1/carrinhos/convidado`.
- Pedido: `POST /api/v1/pedidos` + `GET /api/v1/pedidos` e `GET /api/v1/pedidos/{id}` (papel `PRODUTOR`). Header `Idempotency-Key`. Após persistir, `GatewayErpSimulado` aceita e `confirmacao` passa a `ACEITA`.
- `GatewayErp` vive em `order.application`; a única implementação MVP0 é `integration.infrastructure.GatewayErpSimulado` (`agriplataforma.gatewayErp=simulado`). O mock não cria/altera/apaga pedido.
- Retaguarda: `GET /api/v1/retaguarda/pedidos` e detalhe `GET /api/v1/retaguarda/pedidos/{id}` exigem `OPERADOR_REVENDA`. Produtor recebe `ACESSO_NEGADO`. Snapshot inclui `nomeProdutor`.
- Frontend S6 em `features/backoffice-orders`; rota `/retaguarda/pedidos`. Sem atalho Backoffice na nav do produtor.
- Módulo `order` lê o carrinho só pela porta `application` (não o inverso) para não fechar ciclo Modulith.

## Shared Learnings

- `spring-modulith-starter-jpa` omitido na fundação para não criar registry de eventos fora de Flyway.
- Testes Modulith de rejeição precisam de `ImportOption.Predefined.ONLY_INCLUDE_TESTS`; o default ignora `src/test`.
- Testcontainers 2 usa `org.testcontainers.postgresql.PostgreSQLContainer`.
- `@WebMvcTest` não herda o exclude de `AgriPlatformApplication`; precisa `spring.autoconfigure.exclude` no teste.
- O remoto `vicentepf78/pedido-produtor-rural` estava vazio no bootstrap.
- O app desktop CompozyOS não está instalado; acompanhamento pela UI web do daemon.
- O dock Tasks do CompozyOS não lista `task_NN.md`.
- `eng-ui-screenshot` ausente: bundles visuais via Playwright (`VISUAL_TASK03=1` / `VISUAL_TASK04=1` / `VISUAL_TASK05=1`) + Pillow; revisão humana dos pares.
- Abas do catálogo vêm do catálogo sem filtro e não desaparecem na busca vazia.

## Open Risks

- Nenhum bloqueio de toolchain (Java 21, Maven, Docker e Node disponíveis).

## Open Questions

- Nenhuma no bootstrap.

## Handoffs

- `task_06`: plano de QA; mock ERP e retaguarda já existem. Não reabrir checkout/Meus pedidos do produtor. Walk dos cenários `INT-mock-erp-aceita`, `ORD-backoffice-lista`, `ORD-backoffice-vazio-ou-negado` fica na Phase C.
