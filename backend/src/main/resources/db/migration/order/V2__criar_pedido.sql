CREATE TABLE "order"."pedido" (
  "id" UUID NOT NULL,
  "idTenant" UUID NOT NULL,
  "idProdutor" UUID NOT NULL,
  "idPropriedade" UUID NOT NULL,
  "nomePropriedade" TEXT NOT NULL,
  "preferenciaRetirada" TEXT NOT NULL,
  "situacao" TEXT NOT NULL,
  "confirmacao" TEXT NOT NULL,
  "total" NUMERIC(12,2) NOT NULL,
  "chaveIdempotencia" TEXT NOT NULL,
  "criadoEm" TIMESTAMPTZ NOT NULL,
  CONSTRAINT "pedidoPk" PRIMARY KEY ("id"),
  CONSTRAINT "pedidoIdempotenciaUk" UNIQUE ("idTenant", "idProdutor", "chaveIdempotencia"),
  CONSTRAINT "pedidoTotalCk" CHECK ("total" >= 0)
);

CREATE TABLE "order"."itemPedido" (
  "id" UUID NOT NULL,
  "idPedido" UUID NOT NULL,
  "idProduto" UUID NOT NULL,
  "nome" TEXT NOT NULL,
  "unidade" TEXT NOT NULL,
  "quantidade" INTEGER NOT NULL,
  "precoUnitario" NUMERIC(12,2) NOT NULL,
  "totalLinha" NUMERIC(12,2) NOT NULL,
  CONSTRAINT "itemPedidoPk" PRIMARY KEY ("id"),
  CONSTRAINT "itemPedidoFk" FOREIGN KEY ("idPedido") REFERENCES "order"."pedido" ("id"),
  CONSTRAINT "itemPedidoQtdCk" CHECK ("quantidade" > 0)
);

CREATE INDEX "pedidoProdutorIdx" ON "order"."pedido" ("idTenant", "idProdutor", "criadoEm" DESC);
