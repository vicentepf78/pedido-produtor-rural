import { useEffect, useState, type FormEvent } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { useSessao } from "../../estado/ProvedorSessao";
import { isFalhaApi } from "../../infra/http";
import { entrar } from "./api";
import { resolverDestinoAposEntrada } from "./destinoAposEntrada";

type Erros = Partial<Record<"email" | "password" | "form", string>>;

export function PaginaEntrar() {
  const [params] = useSearchParams();
  const origem = params.get("origem");
  const navegar = useNavigate();
  const { sessao, pronta, aplicarAutenticacao } = useSessao();
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erros, setErros] = useState<Erros>({});
  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    if (pronta && sessao.autenticado) {
      navegar(resolverDestinoAposEntrada(origem, sessao.papeis), { replace: true });
    }
  }, [pronta, sessao, origem, navegar]);

  function validar(): Erros {
    const proximo: Erros = {};
    if (!email.trim()) {
      proximo.email = "Informe o e-mail.";
    }
    if (!senha.trim()) {
      proximo.password = "Informe a senha.";
    }
    return proximo;
  }

  async function enviar(evento: FormEvent) {
    evento.preventDefault();
    const proximo = validar();
    if (Object.keys(proximo).length > 0) {
      setErros(proximo);
      if (proximo.email) {
        document.getElementById("email")?.focus();
      } else {
        document.getElementById("password")?.focus();
      }
      return;
    }
    setEnviando(true);
    setErros({});
    try {
      const resposta = await entrar(email.trim(), senha);
      aplicarAutenticacao(resposta);
      navegar(resolverDestinoAposEntrada(origem, resposta.papeis), { replace: true });
    } catch (falha) {
      const mensagem = isFalhaApi(falha) ? falha.message : "E-mail ou senha inválidos.";
      setErros({ form: mensagem, email: mensagem });
      document.getElementById("email")?.focus();
    } finally {
      setEnviando(false);
    }
  }

  const destinoVolta = resolverDestinoAposEntrada(origem, []);

  return (
    <main className="screen-body" data-od-id="s3-entrar">
      <div className="card form-card">
        <h1 data-od-id="auth-title">Entrar</h1>
        <p className="subtitle">Acesse com e-mail e senha para finalizar pedidos.</p>
        <form onSubmit={enviar}>
          <div className="field">
            <label htmlFor="email">E-mail</label>
            <input
              id="email"
              type="email"
              autoComplete="email"
              value={email}
              aria-invalid={Boolean(erros.email)}
              aria-describedby={erros.email ? "email-erro" : undefined}
              onChange={(evento) => setEmail(evento.target.value)}
              data-od-id="input-email"
            />
            {erros.email && (
              <p id="email-erro" className="inline-error" role="alert">
                {erros.email}
              </p>
            )}
          </div>
          <div className="field">
            <label htmlFor="password">Senha</label>
            <input
              id="password"
              type="password"
              autoComplete="current-password"
              value={senha}
              aria-invalid={Boolean(erros.password)}
              aria-describedby={erros.password ? "password-erro" : undefined}
              onChange={(evento) => setSenha(evento.target.value)}
              data-od-id="input-password"
            />
            {erros.password && (
              <p id="password-erro" className="inline-error" role="alert">
                {erros.password}
              </p>
            )}
          </div>
          {erros.form && !erros.email && (
            <p className="inline-error" role="alert">
              {erros.form}
            </p>
          )}
          <button
            type="submit"
            className="btn btn-primary btn-block"
            data-od-id="btn-auth-submit"
            disabled={enviando}
            aria-busy={enviando}
          >
            Entrar
          </button>
        </form>
        <p className="form-link">
          Não tem conta?{" "}
          <Link to={`/cadastro?origem=${destinoVolta}`} data-od-id="link-criar-conta">
            Criar conta
          </Link>
        </p>
        <button type="button" className="btn btn-ghost btn-block" onClick={() => navegar(destinoVolta)}>
          Voltar
        </button>
      </div>
    </main>
  );
}
