import { useEffect, useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { formatarDinheiro, isFalhaApi } from "../../infra/http";
import { entrarOperador, listarPedidosRetaguarda, type ResumoPedidoRetaguarda } from "./api";
import { classeConfirmacao, formatarQuando, rotuloConfirmacao, rotuloSituacao } from "./DetalhePedidoRetaguarda";

export function PaginaPedidosRetaguarda() {
  const navegar = useNavigate();
  const [pedidos, setPedidos] = useState<ResumoPedidoRetaguarda[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [negado, setNegado] = useState(false);
  const [pedirEntrada, setPedirEntrada] = useState(false);
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erroEntrada, setErroEntrada] = useState<string | null>(null);

  async function carregar() {
    setCarregando(true);
    setErro(null);
    setNegado(false);
    setPedirEntrada(false);
    try {
      const pagina = await listarPedidosRetaguarda();
      setPedidos(pagina.itens);
    } catch (falha) {
      if (isFalhaApi(falha) && falha.status === 401) {
        setPedirEntrada(true);
        setPedidos([]);
      } else if (isFalhaApi(falha) && (falha.status === 403 || falha.codigo === "ACESSO_NEGADO")) {
        setNegado(true);
        setPedidos([]);
      } else {
        setErro(isFalhaApi(falha) ? falha.message : "Não foi possível carregar os pedidos.");
      }
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    void carregar();
  }, []);

  async function entrar(evento: FormEvent) {
    evento.preventDefault();
    setErroEntrada(null);
    try {
      await entrarOperador(email, senha);
      await carregar();
    } catch (falha) {
      setErroEntrada(isFalhaApi(falha) ? falha.message : "E-mail ou senha inválidos.");
    }
  }

  return (
    <>
      <header className="screen-header" data-od-id="backoffice-header">
        <h1>Pedidos do backoffice</h1>
        <p className="inline-info">Área exclusiva do operador</p>
      </header>
      <main className="screen-body" data-od-id="backoffice-body">
        {carregando && <p className="inline-info">Carregando pedidos…</p>}
        {pedirEntrada && !carregando && (
          <form onSubmit={entrar}>
            <div className="field">
              <label htmlFor="bo-email">E-mail</label>
              <input
                id="bo-email"
                type="email"
                autoComplete="username"
                value={email}
                onChange={(evento) => setEmail(evento.target.value)}
              />
            </div>
            <div className="field">
              <label htmlFor="bo-senha">Senha</label>
              <input
                id="bo-senha"
                type="password"
                autoComplete="current-password"
                value={senha}
                onChange={(evento) => setSenha(evento.target.value)}
              />
            </div>
            {erroEntrada && (
              <p className="inline-error" role="alert">
                {erroEntrada}
              </p>
            )}
            <button type="submit" className="btn btn-primary btn-block">
              Entrar
            </button>
          </form>
        )}
        {negado && (
          <div className="status-box" data-od-id="backoffice-denied" role="alert">
            <h2>Acesso negado</h2>
            <p>Somente o operador da revenda pode inspecionar os pedidos da retaguarda.</p>
            <Link className="btn btn-ghost" to="/catalogo">
              Voltar ao catálogo
            </Link>
          </div>
        )}
        {erro && !negado && (
          <div className="status-box" role="alert">
            <h2>Não foi possível carregar</h2>
            <p>{erro}</p>
            <button type="button" className="btn btn-ghost" onClick={() => void carregar()}>
              Tentar novamente
            </button>
          </div>
        )}
        {!carregando && !erro && !negado && !pedirEntrada && pedidos.length === 0 && (
          <div className="status-box" data-od-id="backoffice-empty">
            <h2>Nenhum pedido</h2>
            <p>Nenhum pedido recebido nesta revenda.</p>
          </div>
        )}
        {!carregando &&
          !erro &&
          !negado &&
          !pedirEntrada &&
          pedidos.map((pedido) => (
            <button
              key={pedido.idPedido}
              type="button"
              className="order-list-item"
              data-od-id={`bo-order-${pedido.idPedido}`}
              onClick={() => navegar(`/retaguarda/pedidos/${pedido.idPedido}`)}
            >
              <div className="order-list-top">
                <span className="order-id">{pedido.idPedido}</span>
                <span className="mono">{formatarDinheiro(pedido.total)}</span>
              </div>
              <div className="order-meta">
                <span>{pedido.nomeProdutor}</span>
                <span>{formatarQuando(pedido.criadoEm)}</span>
                <span className="pill neutral">{rotuloSituacao(pedido.situacao)}</span>
                <span className={classeConfirmacao(pedido.confirmacao)}>{rotuloConfirmacao(pedido.confirmacao)}</span>
              </div>
            </button>
          ))}
      </main>
    </>
  );
}
