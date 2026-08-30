CREATE TABLE producer."produtor" (
  "id" UUID NOT NULL,
  "idTenant" UUID NOT NULL,
  "idUsuario" UUID NOT NULL,
  CONSTRAINT "produtorPk" PRIMARY KEY ("id"),
  CONSTRAINT "produtorUsuarioUk" UNIQUE ("idTenant", "idUsuario")
);

CREATE TABLE producer."propriedade" (
  "id" UUID NOT NULL,
  "idTenant" UUID NOT NULL,
  "idProdutor" UUID NOT NULL,
  "nome" TEXT NOT NULL,
  CONSTRAINT "propriedadePk" PRIMARY KEY ("id"),
  CONSTRAINT "propriedadeProdutorFk" FOREIGN KEY ("idProdutor") REFERENCES producer."produtor" ("id")
);

INSERT INTO producer."produtor" ("id", "idTenant", "idUsuario")
VALUES
  (
    '44444444-4444-4444-4444-444444444444',
    '11111111-1111-1111-1111-111111111111',
    '33333333-3333-3333-3333-333333333333'
  ),
  (
    '88888888-8888-8888-8888-888888888888',
    '11111111-1111-1111-1111-111111111111',
    '77777777-7777-7777-7777-777777777777'
  );

INSERT INTO producer."propriedade" ("id", "idTenant", "idProdutor", "nome")
VALUES
  (
    '55555555-5555-5555-5555-555555555555',
    '11111111-1111-1111-1111-111111111111',
    '44444444-4444-4444-4444-444444444444',
    'Fazenda Norte'
  ),
  (
    '66666666-6666-6666-6666-666666666666',
    '11111111-1111-1111-1111-111111111111',
    '44444444-4444-4444-4444-444444444444',
    'Sitio Recanto'
  ),
  (
    '99999999-9999-9999-9999-999999999999',
    '11111111-1111-1111-1111-111111111111',
    '88888888-8888-8888-8888-888888888888',
    'Fazenda Sul'
  );
