# Project operating contract

## Authoring posture

Write product and engineering artifacts in Brazilian Portuguese. Do not infer
commercial, regulatory, fiscal, or security rules. Record unresolved decisions in
the feature's Open Questions section.

## Architecture principles

Build a Java 21 Spring Boot modular monolith. Organize code by business module,
then by domain, application, API, and infrastructure. Spring Modulith verifies
module boundaries. A module accesses another module only through its public
application contract, never through an entity, repository, database table, or
vendor DTO.

Use PostgreSQL and Flyway. Each business module owns its schema and migrations.
External systems are behind ports; adapters translate vendor models at the
boundary. Start with one configured tenant and retain tenant isolation as a
future product concern.

Name production classes, methods, attributes, components, database tables, database
fields, API fields, and other domain structures in Brazilian Portuguese. Use
`camelCase` for attributes and fields, including quoted PostgreSQL identifiers such
as `"produtorRural"` and `"saldoNegativo"`. Never enable an ORM naming strategy that
converts those names to `snake_case`.

## Autonomy contracts

Treat product specifications, ADRs, task files, and generated task state as
contracts. Only the prescribed Compozy scripts mutate loop state. Never hand-edit
`state.yaml`. Do not claim a task is verified without fresh command output.

## Security invariants

Keep credentials outside source control. Do not write secrets to logs, fixtures,
documentation, or task memory. Protect authenticated browser sessions with secure
HttpOnly cookies and CSRF defenses. Limit each actor to authorized data and record
auditable domain transitions.

## Workflow rules

Create a specification package under `.compozy/tasks/<slug>/` before implementation.
Keep its Product part implementation-neutral. Maintain a test contract and assign
every test case to exactly one implementation task. Use `make gate` as the
cross-task verification entry point. Commit only when explicitly requested or when
the authorized loop checkpoint owns the commit.
