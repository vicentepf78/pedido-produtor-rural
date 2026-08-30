# Task Memory: task_01

## Objective Snapshot

Fundação modular Java 21 + React, PostgreSQL/Flyway por módulo, testes UT-026 e IT-011.

## Important Decisions

- Spring Boot 4.1.1 + Spring Modulith 2.1.1.
- Módulos default (não OPEN); `application` é named interface.
- Flyway por módulo com `FlywayMigrationStrategy`; schema `order` quoted.
- Só a tabela `tenant` + seed nesta fatia; demais entidades na task_02+.
- Rotas frontend vazias (`/catalogo`, `/carrinho`, `/checkout`, `/meus-pedidos`, `/retaguarda/pedidos`).
- Fixture UT-026 em `arquitetura.ut026.violacao` com `ONLY_INCLUDE_TESTS`.
- Auto-commit desabilitado; status da task permanece `in_progress`.

## Learnings

- `FlywayMigrationStrategy` mudou de pacote no Boot 4.
- `TestRestTemplate` saiu do starter webmvc-test; IT-011 usa `java.net.http.HttpClient`.
- Vite 7 exige Node ≥ 20.19; o host tem 20.17 — frontend pinado em Vite 6.

## Files / Surfaces

- `backend/` (pom, 8 módulos, Flyway, testes)
- `frontend/` (Vite/React)
- `Makefile`, `docker-compose.yml`, `.env.example`

## Errors / Corrections

- Compile: import Flyway e TestRestTemplate do Boot 4.
- UT-026: `ApplicationModules.of` não via classes de teste até `ONLY_INCLUDE_TESTS`.

## Ready for Next Run

- `cy-final-verify` fresco: `make test` 4/0 e `make test-integration` 2/0, exit 0.
- QA walk adiado para Phase C. Peer-review adiado para Phase D.
- Checkpoint do orquestrador fecha esta fatia.
