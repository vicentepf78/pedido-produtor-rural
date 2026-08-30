CREATE TABLE identity."usuario" (
  "id" UUID NOT NULL,
  "idTenant" UUID NOT NULL,
  "nome" TEXT NOT NULL,
  "email" TEXT NOT NULL,
  "senhaHash" TEXT NOT NULL,
  "papel" TEXT NOT NULL,
  CONSTRAINT "usuarioPk" PRIMARY KEY ("id"),
  CONSTRAINT "usuarioEmailUk" UNIQUE ("idTenant", "email")
);

INSERT INTO identity."usuario" ("id", "idTenant", "nome", "email", "senhaHash", "papel")
VALUES
  (
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'Operador Demonstracao',
    'operador.revenda@example.com',
    '$2b$10$I5YovWHfe5ZFT/8AFTbLfuleAirBVh/SwPXdjTRMb04BMFB1rMlXK',
    'OPERADOR_REVENDA'
  ),
  (
    '33333333-3333-3333-3333-333333333333',
    '11111111-1111-1111-1111-111111111111',
    'Produtor Alfa',
    'produtor.alfa@example.com',
    '$2b$10$I5YovWHfe5ZFT/8AFTbLfuleAirBVh/SwPXdjTRMb04BMFB1rMlXK',
    'PRODUTOR'
  ),
  (
    '77777777-7777-7777-7777-777777777777',
    '11111111-1111-1111-1111-111111111111',
    'Produtor Beta',
    'produtor.beta@example.com',
    '$2b$10$I5YovWHfe5ZFT/8AFTbLfuleAirBVh/SwPXdjTRMb04BMFB1rMlXK',
    'PRODUTOR'
  );
