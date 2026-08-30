import { getJson } from "../../infra/http";

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

export async function listarProdutos(consulta?: string): Promise<PaginaProdutos> {
  const params = new URLSearchParams({ pagina: "1", tamanhoPagina: "30" });
  if (consulta !== undefined) {
    params.set("consulta", consulta);
  }
  return getJson<PaginaProdutos>(`/api/v1/catalogo/produtos?${params.toString()}`);
}

export async function obterProduto(id: string): Promise<ItemProduto> {
  return getJson<ItemProduto>(`/api/v1/catalogo/produtos/${id}`);
}
