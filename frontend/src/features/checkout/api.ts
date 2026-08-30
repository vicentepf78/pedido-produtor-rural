import { getJson, invalidarCsrf, mutarJson } from "../../infra/http";

export type RespostaAutenticacao = {
  idUsuario: string;
  nome: string;
  papeis: string[];
};

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

export async function cadastrar(nome: string, email: string, senha: string): Promise<RespostaAutenticacao> {
  const resposta = await mutarJson<RespostaAutenticacao>("/api/v1/autenticacao/cadastro", "POST", {
    nome,
    email,
    senha,
  });
  invalidarCsrf();
  return resposta;
}

export async function entrar(email: string, senha: string): Promise<RespostaAutenticacao> {
  const resposta = await mutarJson<RespostaAutenticacao>("/api/v1/autenticacao/entrada", "POST", {
    email,
    senha,
  });
  invalidarCsrf();
  return resposta;
}

export async function listarPropriedades(): Promise<ResumoPropriedade[]> {
  const pagina = await getJson<{ itens: ResumoPropriedade[] }>("/api/v1/produtor/propriedades");
  return pagina.itens;
}

export async function criarPropriedade(nome: string): Promise<ResumoPropriedade> {
  return mutarJson<ResumoPropriedade>("/api/v1/produtor/propriedades", "POST", { nome });
}

export async function sair(): Promise<void> {
  await mutarJson("/api/v1/autenticacao/saida", "POST");
  invalidarCsrf();
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
