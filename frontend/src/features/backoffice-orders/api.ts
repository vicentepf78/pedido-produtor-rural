import { getJson } from "../../infra/http";

export type ResumoPedidoRetaguarda = {
  idPedido: string;
  nomeProdutor: string;
  total: string;
  situacao: string;
  confirmacao: string;
  criadoEm: string;
};

export type VisaoPedidoRetaguarda = {
  idPedido: string;
  nomeProdutor: string;
  situacao: string;
  confirmacao: string;
  nomePropriedade: string;
  preferenciaRetirada: string;
  total: string;
  criadoEm?: string;
  itens: Array<{
    nome: string;
    unidade?: string;
    quantidade: number;
    precoUnitario: string;
    totalLinha: string;
  }>;
};

export async function listarPedidosRetaguarda(): Promise<{ itens: ResumoPedidoRetaguarda[]; total: number }> {
  return getJson<{ itens: ResumoPedidoRetaguarda[]; pagina: number; tamanhoPagina: number; total: number }>(
    "/api/v1/retaguarda/pedidos?pagina=1&tamanhoPagina=25",
  );
}

export async function obterPedidoRetaguarda(idPedido: string): Promise<VisaoPedidoRetaguarda> {
  return getJson<VisaoPedidoRetaguarda>(`/api/v1/retaguarda/pedidos/${idPedido}`);
}
