# L-008: Schema changes are migrations

Every persistent model change uses a reviewed, append-only Flyway migration. Startup
code must not reconcile, create, or alter business schemas.
