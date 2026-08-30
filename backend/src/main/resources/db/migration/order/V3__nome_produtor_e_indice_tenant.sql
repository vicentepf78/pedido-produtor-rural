ALTER TABLE "order"."pedido" ADD COLUMN "nomeProdutor" TEXT NOT NULL DEFAULT '';
ALTER TABLE "order"."pedido" ALTER COLUMN "nomeProdutor" DROP DEFAULT;

CREATE INDEX "pedidoTenantIdx" ON "order"."pedido" ("idTenant", "criadoEm" DESC);
