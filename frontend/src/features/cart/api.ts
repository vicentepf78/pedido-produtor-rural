import { getJson, mutarJson } from "../../infra/http";

export type ItemCarrinho = {
  idProduto: string;
  nome: string;
  quantidade: number;
  precoUnitario: string;
  totalLinha: string;
};

export type VisaoCarrinho = {
  itens: ItemCarrinho[];
  total: string;
};

export async function obterCarrinho(): Promise<VisaoCarrinho> {
  return getJson<VisaoCarrinho>("/api/v1/carrinhos/convidado");
}

export async function adicionarItem(idProduto: string, quantidade: number): Promise<VisaoCarrinho> {
  return mutarJson<VisaoCarrinho>("/api/v1/carrinhos/convidado/itens", "POST", {
    idProduto,
    quantidade,
  });
}

export async function alterarQuantidade(idProduto: string, quantidade: number): Promise<VisaoCarrinho> {
  return mutarJson<VisaoCarrinho>(`/api/v1/carrinhos/convidado/itens/${idProduto}`, "PATCH", {
    quantidade,
  });
}

export async function removerItem(idProduto: string): Promise<VisaoCarrinho> {
  return mutarJson<VisaoCarrinho>(`/api/v1/carrinhos/convidado/itens/${idProduto}`, "DELETE");
}
