import { getJson } from "../../infra/http";
import { CATEGORIA_TODOS, TAMANHO_PAGINA_PADRAO, type CategoriaCarrossel, type TamanhoPagina } from "./categorias";

export type ItemProduto = {
  id: string;
  nome: string;
  categoria: string;
  descricaoCurta: string;
  unidade: string;
  precoUnitario: string;
  disponivel: boolean;
  urlImagem: string | null;
};

export type PaginaProdutos = {
  itens: ItemProduto[];
  pagina: number;
  tamanhoPagina: number;
  total: number;
};

export type ConsultaCatalogo = {
  consulta?: string;
  categoria?: CategoriaCarrossel;
  pagina?: number;
  tamanhoPagina?: TamanhoPagina;
};

export async function listarProdutos(
  consulta: ConsultaCatalogo = {},
  signal?: AbortSignal,
): Promise<PaginaProdutos> {
  const params = new URLSearchParams({
    pagina: String(consulta.pagina ?? 1),
    tamanhoPagina: String(consulta.tamanhoPagina ?? TAMANHO_PAGINA_PADRAO),
  });
  const termo = consulta.consulta?.trim();
  if (termo) {
    params.set("consulta", termo);
  }
  if (consulta.categoria && consulta.categoria !== CATEGORIA_TODOS) {
    params.set("categoria", consulta.categoria);
  }
  return getJson<PaginaProdutos>(`/api/v1/catalogo/produtos?${params.toString()}`, { signal });
}

export async function obterProduto(id: string, signal?: AbortSignal): Promise<ItemProduto> {
  return getJson<ItemProduto>(`/api/v1/catalogo/produtos/${id}`, { signal });
}
