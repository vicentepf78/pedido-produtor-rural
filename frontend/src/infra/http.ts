export type ErroApi = {
  codigo: string;
  mensagem: string;
};

export class FalhaApi extends Error {
  readonly codigo: string;
  readonly status: number;

  constructor(codigo: string, mensagem: string, status: number) {
    super(mensagem);
    this.codigo = codigo;
    this.status = status;
  }
}

let tokenCsrf: string | undefined;

export function invalidarCsrf() {
  tokenCsrf = undefined;
}

async function csrf(): Promise<string> {
  if (tokenCsrf) {
    return tokenCsrf;
  }
  const resposta = await fetch("/api/v1/autenticacao/csrf", { credentials: "include" });
  const corpo = (await resposta.json()) as { token: string };
  tokenCsrf = corpo.token;
  return tokenCsrf;
}

export async function getJson<T>(caminho: string): Promise<T> {
  const resposta = await fetch(caminho, { credentials: "include" });
  return ler(resposta);
}

export async function mutarJson<T>(
  caminho: string,
  metodo: string,
  corpo?: unknown,
  cabecalhos?: Record<string, string>,
): Promise<T> {
  const token = await csrf();
  const resposta = await fetch(caminho, {
    method: metodo,
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      "X-XSRF-TOKEN": token,
      ...cabecalhos,
    },
    body: corpo === undefined ? undefined : JSON.stringify(corpo),
  });
  return ler(resposta);
}

async function ler<T>(resposta: Response): Promise<T> {
  const texto = await resposta.text();
  const json = texto ? (JSON.parse(texto) as T | ErroApi) : ({} as T);
  if (!resposta.ok) {
    const erro = json as ErroApi;
    throw new FalhaApi(erro.codigo ?? "ERRO", erro.mensagem ?? "Não foi possível concluir.", resposta.status);
  }
  return json as T;
}

export function isFalhaApi(erro: unknown): erro is FalhaApi {
  return erro instanceof FalhaApi;
}

export function formatarDinheiro(valor: string | number): string {
  return Number(valor).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

