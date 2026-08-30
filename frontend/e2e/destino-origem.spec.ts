import { expect } from "@playwright/test";
import { resolverDestinoAposEntrada } from "../src/features/identity/destinoAposEntrada";
import { test } from "./helpers/apiMock";

const ORIGENS_PRODUTOR = [
  "/catalogo",
  "/carrinho",
  "/checkout",
  "/meus-pedidos",
  "/pedidos/ord-demo",
  "/retaguarda/pedidos",
  "/retaguarda/pedidos/ord-demo",
];

test.describe("Destino pós-Entrar", () => {
  test("UT-071 destino de produtor aceita somente origens relativas da whitelist", () => {
    for (const origem of ORIGENS_PRODUTOR) {
      expect(resolverDestinoAposEntrada(origem, ["PRODUTOR"]), origem).toBe(origem);
    }
  });

  test("UT-072 operador ignora origem da loja; absoluta ou desconhecida cai em /catalogo", () => {
    expect(resolverDestinoAposEntrada("/checkout", ["OPERADOR_REVENDA"])).toBe("/retaguarda/pedidos");
    expect(resolverDestinoAposEntrada("/catalogo", ["OPERADOR_REVENDA"])).toBe("/retaguarda/pedidos");
    expect(resolverDestinoAposEntrada("/meus-pedidos", ["OPERADOR_REVENDA"])).toBe("/retaguarda/pedidos");
    expect(resolverDestinoAposEntrada("https://evil.example/phishing", ["PRODUTOR"])).toBe("/catalogo");
    expect(resolverDestinoAposEntrada("//evil.example/x", ["PRODUTOR"])).toBe("/catalogo");
    expect(resolverDestinoAposEntrada("/admin", ["PRODUTOR"])).toBe("/catalogo");
    expect(resolverDestinoAposEntrada("/entrar", ["PRODUTOR"])).toBe("/catalogo");
    expect(resolverDestinoAposEntrada(undefined, ["PRODUTOR"])).toBe("/catalogo");
  });
});
