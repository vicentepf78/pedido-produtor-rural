import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useSessao } from "../../estado/ProvedorSessao";
import { formatarDinheiro, isFalhaApi } from "../../infra/http";
import { listarPedidos, type ResumoPedido } from "./api";
import { classeConfirmacao, formatarQuando, rotuloConfirmacao, rotuloSituacao } from "./DetalhePedido";

export function PaginaMeusPedidos() {
  const navegar = useNavigate();
  const { sessao, pronta } = useSessao();
  const [pedidos, setPedidos] = useState<ResumoPedido[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    if (!pronta) {
      return;
    }
    if (!sessao.autenticado || !sessao.papeis.includes("PRODUTOR")) {
      setPedidos([]);
      setErro(null);
      setCarregando(false);
      return;
    }
    let ativo = true;
    async function carregar() {
      setCarregando(true);
      setErro(null);
      try {
        const pagina = await listarPedidos();
        if (ativo) {
          setPedidos(pagina.itens);
        }
      } catch (falha) {
        if (!ativo) {
          return;
        }
        if (isFalhaApi(falha) && falha.status === 401) {
          setErro("Entre ou crie uma conta para continuar.");
        } else {
          setErro(isFalhaApi(falha) ? falha.message : "Não foi possível carregar os pedidos.");
        }
      } finally {
        if (ativo) {
          setCarregando(false);
        }
      }
    }
    void carregar();
    return () => {
      ativo = false;
    };
  }, [pronta, sessao]);

  const precisaEntrar = pronta && !sessao.autenticado;
  const operador = sessao.autenticado && sessao.papeis.includes("OPERADOR_REVENDA");

  return (
    <>
      <header className="screen-header" data-od-id="my-orders-header">
        <h1>Meus pedidos</h1>
      </header>
      <main className="screen-body" data-od-id="my-orders-body">
        {precisaEntrar && (
          <section className="empty-state card" data-od-id="s7-pedidos-logged-out">
            <h2>Entre para ver seus pedidos</h2>
            <p>Seus pedidos ficam salvos na sua conta.</p>
            <Link className="btn btn-primary" to="/entrar?origem=/meus-pedidos" data-od-id="btn-pedidos-entrar">
              Entrar
            </Link>
          </section>
        )}
        {operador && (
          <section className="empty-state card" data-od-id="s7-operador">
            <h2>Pedidos da revenda</h2>
            <p>O operador inspeciona os pedidos em Pedidos da revenda.</p>
            <Link className="btn btn-primary" to="/retaguarda/pedidos">
              Pedidos da revenda
            </Link>
          </section>
        )}
        {!precisaEntrar && !operador && carregando && <p className="inline-info">Carregando pedidos…</p>}
        {!precisaEntrar && !operador && erro && (
          <div className="empty-state card" role="alert">
            <h2>Não foi possível carregar</h2>
            <p>{erro}</p>
            <Link className="btn btn-ghost" to="/entrar?origem=/meus-pedidos">
              Entrar
            </Link>
          </div>
        )}
        {!precisaEntrar && !operador && !carregando && !erro && pedidos.length === 0 && (
          <div className="empty-state card" data-od-id="s7-pedidos-empty">
            <h2>Nenhum pedido</h2>
            <p>Seus pedidos confirmados aparecerão aqui.</p>
            <Link className="btn btn-ghost" to="/catalogo">
              Ir ao catálogo
            </Link>
          </div>
        )}
        {!precisaEntrar &&
          !operador &&
          !carregando &&
          !erro &&
          pedidos.map((pedido) => (
            <button
              key={pedido.idPedido}
              type="button"
              className="order-list-item"
              data-od-id={`my-order-${pedido.idPedido}`}
              onClick={() => navegar(`/pedidos/${pedido.idPedido}`)}
            >
              <div className="order-list-top">
                <span className="order-id">{pedido.idPedido}</span>
                <span className="mono">{formatarDinheiro(pedido.total)}</span>
              </div>
              <div className="order-meta">
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
