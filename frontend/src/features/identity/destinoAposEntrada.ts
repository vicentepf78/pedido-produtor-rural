const ORIGENS_EXATAS = new Set([
  "/catalogo",
  "/carrinho",
  "/checkout",
  "/meus-pedidos",
  "/retaguarda/pedidos",
]);

const ORIGEM_PEDIDO = /^\/pedidos\/[A-Za-z0-9_-]+$/;
const ORIGEM_RETAGUARDA_DETALHE = /^\/retaguarda\/pedidos\/[A-Za-z0-9_-]+$/;

export function destinoPermitido(origem: string | null | undefined): string {
  if (!origem) {
    return "/catalogo";
  }
  if (
    origem.includes("://") ||
    origem.startsWith("//") ||
    origem.includes("\\") ||
    !origem.startsWith("/") ||
    origem.includes("?") ||
    origem.includes("#") ||
    origem.includes("..")
  ) {
    return "/catalogo";
  }
  if (ORIGENS_EXATAS.has(origem) || ORIGEM_PEDIDO.test(origem) || ORIGEM_RETAGUARDA_DETALHE.test(origem)) {
    return origem;
  }
  return "/catalogo";
}

export function resolverDestinoAposEntrada(origem: string | null | undefined, papeis: readonly string[]): string {
  if (papeis.includes("OPERADOR_REVENDA")) {
    return "/retaguarda/pedidos";
  }
  return destinoPermitido(origem);
}
