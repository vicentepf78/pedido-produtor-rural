import { getJson } from "../../infra/http";

export type ItemPedido = {
  nome: string;
  unidade?: string;
  quantidade: number;
  precoUnitario: string;
  totalLinha: string;
};

export type VisaoPedido = {
  idPedido: string;
  situacao: string;
  confirmacao: string;
  nomePropriedade: string;
  preferenciaRetirada: string;
  total: string;
  criadoEm?: string;
  itens: ItemPedido[];
};

export type ResumoPedido = {
  idPedido: string;
  situacao: string;
  confirmacao: string;
  total: string;
  criadoEm: string;
};

export async function listarPedidos(): Promise<{ itens: ResumoPedido[]; total: number }> {
  return getJson<{ itens: ResumoPedido[]; pagina: number; tamanhoPagina: number; total: number }>(
    "/api/v1/pedidos?pagina=1&tamanhoPagina=10",
  );
}

export async function obterPedido(idPedido: string): Promise<VisaoPedido> {
  return getJson<VisaoPedido>(`/api/v1/pedidos/${idPedido}`);
}
