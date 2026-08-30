---
status: pending
title: Fundação modular, banco e verificação de arquitetura
type: infra
complexity: high
---

# Task 1: Fundação modular, banco e verificação de arquitetura

## Overview

Entrega o monólito modular Java 21 Spring Boot, o esqueleto React (Vite +
TypeScript) e o PostgreSQL com Flyway por módulo, para que as fatias de
negócio tenham um lugar estável para viver. Sem esta base, nenhum módulo
consegue persistir, publicar API ou provar limites de arquitetura.

<critical>
- ALWAYS READ `_spec.md` and its catalogs (`_user_stories.md`, `_dx.md`, `_uiux.md` when present, `_tests.md`) before starting
- REFERENCE `_spec.md` Part II for implementation details — do not duplicate here
- FOCUS ON "WHAT" — describe what needs to be accomplished, not how
- MINIMIZE CODE — show code only to illustrate current structure or problem areas
- TESTS REQUIRED — implement every test case assigned in ## Tests
</critical>

<requirements>
- MUST criar `backend/` como aplicação Java 21 Spring Boot com Spring Modulith e
  Maven, pacote-base `br.agriplataforma`.
- MUST criar as raízes vazias dos módulos `tenant`, `identity`, `producer`,
  `catalog`, `cart`, `order`, `integration` e `backoffice`, cada uma com
  pacotes `domain`, `application`, `api` e `infrastructure`.
- MUST fazer cada módulo ser proprietário do próprio schema PostgreSQL e das
  próprias migrações Flyway; a inicialização NÃO DEVE reconciliar, criar ou
  alterar schema de negócio fora de migrações append-only.
- MUST desativar qualquer naming strategy ORM que converta identificadores para
  `snake_case`; campos e colunas permanecem `camelCase` com identificadores
  PostgreSQL quoted (SD-012).
- MUST expor verificação de integridade da aplicação e falhar a subida quando
  faltar conexão de banco, tenant semeado ou configuração obrigatória.
- MUST criar `frontend/` (Vite + TypeScript + React) com rotas vazias das
  cinco superfícies do `_spec.md`, sem duplicar regras de negócio.
- MUST expandir o `Makefile` com `test`, `test-integration` e `test-e2e-web`;
  `make gate` continua a validar a spec e passa a invocar a suíte automática
  quando o código existir.
- MUST isolar estado de teste (Testcontainers ou equivalente PostgreSQL
  dedicado); testes paralelos NÃO DEVEM mutar configuração de processo sem
  restauração.
- SHOULD publicar `.env.example` e `docker-compose.yml` somente com valores
  locais não secretos.
- MUST NOT puxar módulos ou fluxos do MVP piloto (`pricing`, `fulfillment`,
  `compliance`, Agrofit, notificações).
</requirements>

## Subtasks

- [ ] 1.1 Criar o projeto Maven Java 21 com Spring Boot, Spring Modulith, JPA,
      Flyway e Testcontainers.
- [ ] 1.2 Declarar os oito módulos de negócio e a raiz de composição da
      aplicação.
- [ ] 1.3 Criar schemas e migrações Flyway por módulo, incluindo seed do
      tenant ativo único.
- [ ] 1.4 Congelar a estratégia de nomes `camelCase` quoted no PostgreSQL e
      nas entidades.
- [ ] 1.5 Expor health, validação de configuração e logs estruturados sem
      dados pessoais.
- [ ] 1.6 Criar o esqueleto React/Vite com as rotas das superfícies S1–S6.
- [ ] 1.7 Adicionar alvos `test`, `test-integration` e `test-e2e-web` ao
      Makefile e ligá-los ao `gate`.
- [ ] 1.8 Implementar UT-026 e IT-011 cobrindo limites Modulith e migração
      em banco vazio.

## Implementation Details

O repositório é greenfield: não existem `pom.xml`, `src/` nem `frontend/`.
`.gitignore` já reserva `backend/target/` e `frontend/node_modules/`. Seguir
`_spec.md` Parte II (Arquitetura, Limites, Sequenciamento item 2) e
`CLAUDE.md`. Classes, métodos, atributos, tabelas e campos de produção em
português brasileiro.

### Relevant Files

- `CLAUDE.md` — monólito modular, Flyway por módulo, SD de nomenclatura.
- `.compozy/tasks/pedidos-insumos-mvp0/_spec.md` — módulos, schemas, invariantes.
- `agri_platform_mvp_arquitetura.md` — justificativa do monólito e Mock ERP.
- `docs/_memory/standing_directives.md` — SD-001, SD-006, SD-007, SD-012.
- `Makefile` — hoje só `gate`/`check-spec`; deve ganhar alvos de teste.
- `.gitignore` — já ignora `backend/target/` e `frontend/`.

### Dependent Files

- `backend/pom.xml` — projeto Maven a criar.
- `backend/src/main/java/br/agriplataforma/AgriPlatformApplication.java` — raiz de composição.
- `backend/src/main/resources/application.yml` — banco, sessão, perfil mock.
- `backend/src/main/resources/db/migration/{tenant,identity,producer,catalog,cart,order,integration,backoffice}/` — migrações por módulo.
- `backend/src/test/java/br/agriplataforma/ModulithArchitectureTest.java` — UT-026 / IT-011.
- `frontend/package.json`, `frontend/vite.config.ts`, `frontend/src/main.tsx` — esqueleto web.
- `frontend/src/features/{catalog,cart,checkout,my-orders,backoffice-orders}/` — pastas das superfícies.
- `docker-compose.yml`, `.env.example` — PostgreSQL local sem segredos.
- `Makefile` — alvos de verificação da aplicação.

### Related ADRs

- [ADR-001: Validar pedido rápido com produtos não regulamentados](adrs/adr-001-fast-non-regulated-order-proof.md)
  — a fundação deve caber nesta prova, não no piloto completo.

### Web/Docs Impact

- `web/`: none — checked surfaces: `web/`, `web/src/systems/`; reason: o produto
  usa `frontend/`, não o SPA Compozy.
- `packages/site`: none — checked surfaces: `packages/site/content/`; reason:
  este repositório não publica docs de runtime Compozy.
- QA impact: none — no user-visible behavior change. Esta tarefa só sobe
  esqueleto, health e migrações; nenhuma jornada de produtor ou operador fica
  utilizável.

### Extensibility / Agent Manageability / Config Lifecycle

- Extensibility: none — checked surfaces: manifests, hooks, skills, MCP,
  registries; reason: MVP0 é aplicação de produto independente. `GatewayErp`
  permanece porta vazia até a task_05.
- Agent manageability: health HTTP da aplicação e logs estruturados; nenhuma
  CLI/UDS. Rotas de `_dx.md` ainda não são implementadas.
- Config lifecycle: none for `config.toml` — checked surfaces: `config.toml`;
  reason: configuração via `application.yml` e variáveis de ambiente
  (`SPRING_DATASOURCE_*`, perfil ativo). Validação falha a subida se faltar
  valor obrigatório. Nenhum segredo versionado.

## Deliverables

- `backend/` inicializável com PostgreSQL local e Flyway por módulo.
- `frontend/` esqueleto com rotas das cinco superfícies.
- Teste Modulith rejeitando importação de infraestrutura cruzada.
- Migrações aplicadas em banco PostgreSQL vazio.
- `make test`, `make test-integration` e `make gate` executáveis.
- Every test case assigned in `## Tests` implemented and passing **(REQUIRED)**

## Tests

Cases assigned from `_tests.md`, the test contract — read each ID's full definition there before writing tests.

- [ ] UT-026 — a verificação Spring Modulith rejeita importação de
      infraestrutura de outro módulo.
- [ ] IT-011 — migrações aplicam em PostgreSQL vazio e a verificação de
      limites Modulith é aprovada.

Cobrir também, dentro desses casos ou como asserções irmãs no mesmo teste de
arquitetura: schemas por módulo presentes, identificadores quoted em
`camelCase`, e falha de subida sem datasource. Citar isolamento de estado
(L-002) e migrações append-only (L-008).

## Success Criteria

- Every assigned test case implemented and passing
- `make test-integration` aplica Flyway em banco vazio e passa
- Um módulo não consegue importar entidade, repositório ou adapter de outro
- Frontend e backend sobem localmente com a configuração documentada em
  `.env.example`
