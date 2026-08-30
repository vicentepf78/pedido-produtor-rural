# Workflow Memory: pedidos-insumos-mvp0

## Current State

- `task_01` e `task_02` concluídas. Próxima: `task_03`.
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
- Identidade não deve apagar o cookie `chaveCarrinhoConvidado`. Catálogo/carrinho (task_03) precisam de `permitAll` em `ConfiguracaoSeguranca`.
- `UserDetailsServiceAutoConfiguration` está excluída (evita senha gerada em log).

## Shared Learnings

- `spring-modulith-starter-jpa` omitido na fundação para não criar registry de eventos fora de Flyway.
- Testes Modulith de rejeição precisam de `ImportOption.Predefined.ONLY_INCLUDE_TESTS`; o default ignora `src/test`.
- Testcontainers 2 usa `org.testcontainers.postgresql.PostgreSQLContainer`.
- `@WebMvcTest` não herda o exclude de `AgriPlatformApplication`; precisa `spring.autoconfigure.exclude` no teste.
- O remoto `vicentepf78/pedido-produtor-rural` estava vazio no bootstrap.
- O app desktop CompozyOS não está instalado; acompanhamento pela UI web do daemon.
- O dock Tasks do CompozyOS não lista `task_NN.md`.

## Open Risks

- Nenhum bloqueio de toolchain (Java 21, Maven, Docker e Node disponíveis).

## Open Questions

- Nenhuma no bootstrap.

## Handoffs

- Próxima ação: `task_03` (catálogo e carrinho). Liberar rotas públicas e reutilizar `chaveCarrinhoConvidado` sem o identity apagá-lo.
