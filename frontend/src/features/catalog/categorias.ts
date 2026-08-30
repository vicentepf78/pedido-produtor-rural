export const CATEGORIA_TODOS = "Todos";

export const CATEGORIAS_CARROSSEL = ["Todos", "Sementes", "Fertilizantes", "Correção"] as const;

export type CategoriaCarrossel = (typeof CATEGORIAS_CARROSSEL)[number];

export const TAMANHOS_PAGINA = [10, 15, 30, 50] as const;

export type TamanhoPagina = (typeof TAMANHOS_PAGINA)[number];

export const TAMANHO_PAGINA_PADRAO: TamanhoPagina = 10;

export const ATRASO_BUSCA_MS = 300;

export type ItemCategoria = {
  id: CategoriaCarrossel;
  rotulo: string;
  slug: string;
  urlImagem: string;
};

export const ITENS_CARROSSEL: ItemCategoria[] = [
  { id: "Todos", rotulo: "Todos", slug: "todos", urlImagem: "/media/categorias/todos.svg" },
  { id: "Sementes", rotulo: "Sementes", slug: "sementes", urlImagem: "/media/categorias/sementes.svg" },
  {
    id: "Fertilizantes",
    rotulo: "Fertilizantes",
    slug: "fertilizantes",
    urlImagem: "/media/categorias/fertilizantes.svg",
  },
  { id: "Correção", rotulo: "Correção", slug: "correcao", urlImagem: "/media/categorias/correcao.svg" },
];

export function ehTamanhoPagina(valor: number): valor is TamanhoPagina {
  return (TAMANHOS_PAGINA as readonly number[]).includes(valor);
}

export function parseTamanhoPagina(bruto: string | null, fallback: TamanhoPagina = TAMANHO_PAGINA_PADRAO): TamanhoPagina {
  if (bruto === null || bruto.trim() === "") {
    return fallback;
  }
  const numero = Number(bruto);
  return ehTamanhoPagina(numero) ? numero : fallback;
}

export function parseCategoria(bruto: string | null): CategoriaCarrossel {
  if (!bruto || bruto === CATEGORIA_TODOS) {
    return CATEGORIA_TODOS;
  }
  return (CATEGORIAS_CARROSSEL as readonly string[]).includes(bruto) ? (bruto as CategoriaCarrossel) : CATEGORIA_TODOS;
}

export function ehAbortado(erro: unknown): boolean {
  return erro instanceof DOMException && erro.name === "AbortError";
}
