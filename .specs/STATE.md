# Project state

## Decisions

| ID | Decision | Status | Rationale |
| --- | --- | --- | --- |
| AD-001 | Build the MVP as a modular monolith with Spring Boot, React, and PostgreSQL. | active | Start simple and preserve domain boundaries for later evolution. |
| AD-002 | Start with one pilot revenda or cooperative in Paraná. | active | Validate the product with a single commercial and regulatory context. |
| AD-003 | Use Agrofit daily CSV open data for the initial regulatory catalog load. | active | It is a national, official, free source for registered crop-protection products. |
| AD-004 | Do not integrate an ERP or message broker in the MVP. | active | The pilot validates the commercial journey before external operational integrations. |
| AD-005 | Use Spring Modulith, module-owned PostgreSQL schemas, and React feature modules. | active | Makes boundaries verifiable without introducing distributed-system overhead. |
| AD-006 | Use Maven, Vite + React + TypeScript, Flyway, and unit, integration, and critical E2E tests. | active | Establishes a repeatable baseline for delivery. |
| AD-007 | Use secure HttpOnly cookies for web authentication. | active | Avoids exposing session credentials to browser JavaScript. |
| AD-008 | Use Cloudinary Free behind a media port for the pilot. | active | Supports image/video delivery and preserves provider portability. |
| AD-009 | Use direct WhatsApp Cloud API integration and e-mail notifications. | active | Avoids BSP markup while keeping official messaging support. |
| AD-010 | Preserve the last valid Agrofit data when daily refresh fails; alert via e-mail, WhatsApp, and monitoring screen. | active | A bad external file must not remove the usable catalog. |

## Handoff

The PRD, technical specification, and task plan are complete and ready for execution.
Start with T01 in `.specs/features/pedidos-insumos-mvp/tasks.md`.
