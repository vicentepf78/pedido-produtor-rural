import { getJson, mutarJson } from "../../infra/http";

export { cadastrar, entrar, sair, type RespostaAutenticacao } from "../identity/api";

export type ResumoPropriedade = {
  id: string;
  nome: string;
};

export type ConfirmacaoPedido = {
  idPedido: string;
  situacao: string;
  confirmacao: string;
  mensagem: string;
};

export async function listarPropriedades(): Promise<ResumoPropriedade[]> {
  const pagina = await getJson<{ itens: ResumoPropriedade[] }>("/api/v1/produtor/propriedades");
  return pagina.itens;
}

export async function criarPropriedade(nome: string): Promise<ResumoPropriedade> {
  return mutarJson<ResumoPropriedade>("/api/v1/produtor/propriedades", "POST", { nome });
}

export async function confirmarPedido(
  idPropriedade: string,
  preferenciaRetirada: string,
  chaveIdempotencia: string,
): Promise<ConfirmacaoPedido> {
  return mutarJson<ConfirmacaoPedido>(
    "/api/v1/pedidos",
    "POST",
    { idPropriedade, preferenciaRetirada },
    { "Idempotency-Key": chaveIdempotencia },
  );
}
