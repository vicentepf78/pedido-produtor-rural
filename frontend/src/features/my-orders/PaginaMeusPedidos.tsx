import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { formatarDinheiro, isFalhaApi } from "../../infra/http";
import { listarPedidos, type ResumoPedido } from "./api";
import { classeConfirmacao, formatarQuando, rotuloConfirmacao, rotuloSituacao } from "./DetalhePedido";

export function PaginaMeusPedidos() {
  const navegar = useNavigate();
  const [pedidos, setPedidos] = useState<ResumoPedido[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
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
  }, []);

  return (
    <>
      <header className="screen-header" data-od-id="my-orders-header">
        <h1>Meus pedidos</h1>
      </header>
      <main className="screen-body" data-od-id="my-orders-body">
        {carregando && <p className="inline-info">Carregando pedidos…</p>}
        {erro && (
          <div className="status-box" role="alert">
            <h2>Não foi possível carregar</h2>
            <p>{erro}</p>
            <button type="button" className="btn btn-ghost" onClick={() => navegar("/checkout")}>
              Ir ao checkout
            </button>
          </div>
        )}
        {!carregando && !erro && pedidos.length === 0 && (
          <div className="status-box">
            <h2>Nenhum pedido</h2>
            <p>Seus pedidos confirmados aparecerão aqui.</p>
          </div>
        )}
        {!carregando &&
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
