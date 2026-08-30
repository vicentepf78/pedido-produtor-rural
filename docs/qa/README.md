# Quality assurance workspace

This directory stores the living QA contract for Compozy task loops.

- Area codes: `AUTH` (identity), `CAT` (catalog), `CART` (cart),
  `CHK` (checkout), `ORD` (orders), and `INT` (integration).
- MVP0 entry points will be the producer catalog, cart, checkout, and the
  backoffice order list. The development commands are defined by the
  application-scaffolding task.
- `personas.md` defines test participants.
- `journeys/` contains end-to-end user journeys.
- `scenarios/` contains executable or manual scenario definitions.
- `charters/` contains exploratory test charters.
- `bugs/` stores the bug registry.
- `reports/` stores dated QA execution reports.
- `automation-backlog/` records scenarios to automate later.
- `templates/` contains project QA templates.

Do not commit generated screenshots, recordings, credentials, or production data.
