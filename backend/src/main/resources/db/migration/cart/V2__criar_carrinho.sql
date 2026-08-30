CREATE TABLE cart."carrinho" (
  "id" UUID NOT NULL,
  "idTenant" UUID NOT NULL,
  "chaveProprietario" TEXT NOT NULL,
  CONSTRAINT "carrinhoPk" PRIMARY KEY ("id"),
  CONSTRAINT "carrinhoChaveUk" UNIQUE ("idTenant", "chaveProprietario")
);

CREATE TABLE cart."itemCarrinho" (
  "id" UUID NOT NULL,
  "idCarrinho" UUID NOT NULL,
  "idProduto" UUID NOT NULL,
  "quantidade" INTEGER NOT NULL,
  "precoUnitario" NUMERIC(12,2) NOT NULL,
  CONSTRAINT "itemCarrinhoPk" PRIMARY KEY ("id"),
  CONSTRAINT "itemCarrinhoFk" FOREIGN KEY ("idCarrinho") REFERENCES cart."carrinho" ("id"),
  CONSTRAINT "itemCarrinhoProdutoUk" UNIQUE ("idCarrinho", "idProduto"),
  CONSTRAINT "itemCarrinhoQtdCk" CHECK ("quantidade" > 0)
);
