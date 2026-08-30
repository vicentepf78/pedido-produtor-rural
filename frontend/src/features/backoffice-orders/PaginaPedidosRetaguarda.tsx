import { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useSessao } from "../../estado/ProvedorSessao";
import { formatarDinheiro, isFalhaApi } from "../../infra/http";
import { listarPedidosRetaguarda, type ResumoPedidoRetaguarda } from "./api";
import { classeConfirmacao, formatarQuando, rotuloConfirmacao, rotuloSituacao } from "./DetalhePedidoRetaguarda";

export function PaginaPedidosRetaguarda() {
  const navegar = useNavigate();
  const local = useLocation();
  const { sessao, pronta } = useSessao();
  const [pedidos, setPedidos] = useState<ResumoPedidoRetaguarda[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [negado, setNegado] = useState(false);

  async function carregar() {
    setCarregando(true);
    setErro(null);
    setNegado(false);
    try {
      const pagina = await listarPedidosRetaguarda();
      setPedidos(pagina.itens);
    } catch (falha) {
      if (isFalhaApi(falha) && falha.status === 401) {
        setPedidos([]);
        navegar(`/entrar?origem=${encodeURIComponent(local.pathname)}`, { replace: true });
        return;
      }
      if (isFalhaApi(falha) && (falha.status === 403 || falha.codigo === "ACESSO_NEGADO")) {
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
    if (!pronta) {
      return;
    }
    if (!sessao.autenticado) {
      setPedidos([]);
      setCarregando(false);
      navegar(`/entrar?origem=${encodeURIComponent(local.pathname)}`, { replace: true });
      return;
    }
    void carregar();
  }, [pronta, sessao, local.pathname, navegar]);

  return (
    <>
      <header className="screen-header" data-od-id="backoffice-header">
        <h1>Pedidos da revenda</h1>
      </header>
      <main className="screen-body" data-od-id="backoffice-body">
        {carregando && <p className="inline-info">Carregando pedidos…</p>}
        {negado && (
          <div className="empty-state card" data-od-id="backoffice-denied" role="alert">
            <h2>Acesso negado</h2>
            <p>Somente o operador da revenda pode inspecionar os pedidos da retaguarda.</p>
            <Link className="btn btn-ghost" to="/catalogo">
              Voltar ao catálogo
            </Link>
          </div>
        )}
        {erro && !negado && (
          <div className="empty-state card" role="alert">
            <h2>Não foi possível carregar</h2>
            <p>{erro}</p>
            <button type="button" className="btn btn-ghost" onClick={() => void carregar()}>
              Tentar novamente
            </button>
          </div>
        )}
        {!carregando && !erro && !negado && pedidos.length === 0 && sessao.autenticado && (
          <div className="empty-state card" data-od-id="backoffice-empty">
            <h2>Nenhum pedido</h2>
            <p>Nenhum pedido recebido nesta revenda.</p>
          </div>
        )}
        {!carregando && !erro && !negado && pedidos.length > 0 && (
          <section className="card table-card" data-od-id="s8-retaguarda-lista">
            <table className="backoffice-table" data-od-id="orders-table">
              <thead>
                <tr>
                  <th>Pedido</th>
                  <th>Produtor</th>
                  <th>Horário</th>
                  <th>Total</th>
                  <th>Confirmação</th>
                </tr>
              </thead>
              <tbody>
                {pedidos.map((pedido) => (
                  <tr
                    key={pedido.idPedido}
                    data-od-id={`bo-order-${pedido.idPedido}`}
                    onClick={() => navegar(`/retaguarda/pedidos/${pedido.idPedido}`)}
                  >
                    <td className="order-id">{pedido.idPedido}</td>
                    <td>{pedido.nomeProdutor}</td>
                    <td>{formatarQuando(pedido.criadoEm)}</td>
                    <td className="mono">{formatarDinheiro(pedido.total)}</td>
                    <td>
                      <span className="pill neutral">{rotuloSituacao(pedido.situacao)}</span>{" "}
                      <span className={classeConfirmacao(pedido.confirmacao)}>
                        {rotuloConfirmacao(pedido.confirmacao)}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </section>
        )}
      </main>
    </>
  );
}
