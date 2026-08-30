CREATE TABLE tenant."tenant" (
  "id" UUID NOT NULL,
  "nome" TEXT NOT NULL,
  "ativo" BOOLEAN NOT NULL,
  CONSTRAINT "tenantPk" PRIMARY KEY ("id")
);

INSERT INTO tenant."tenant" ("id", "nome", "ativo")
VALUES ('11111111-1111-1111-1111-111111111111', 'Revenda demonstracao MVP0', TRUE);
