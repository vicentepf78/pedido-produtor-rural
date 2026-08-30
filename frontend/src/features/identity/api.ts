import { getJson, invalidarCsrf, mutarJson } from "../../infra/http";

export type RespostaAutenticacao = {
  idUsuario: string;
  nome: string;
  email: string;
  papeis: string[];
};

export type SessaoConvidado = {
  autenticado: false;
};

export type SessaoAutenticada = {
  autenticado: true;
  idUsuario: string;
  nome: string;
  email: string;
  papeis: string[];
};

export type Sessao = SessaoConvidado | SessaoAutenticada;

export async function obterSessao(): Promise<Sessao> {
  return getJson<Sessao>("/api/v1/autenticacao/sessao");
}

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

export async function sair(): Promise<void> {
  await mutarJson("/api/v1/autenticacao/saida", "POST");
  invalidarCsrf();
}
