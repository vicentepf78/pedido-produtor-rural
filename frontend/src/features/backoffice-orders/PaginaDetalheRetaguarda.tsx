import { useEffect, useState } from "react";
import { Link, useLocation, useNavigate, useParams } from "react-router-dom";
import { useSessao } from "../../estado/ProvedorSessao";
import { isFalhaApi } from "../../infra/http";
import { obterPedidoRetaguarda, type VisaoPedidoRetaguarda } from "./api";
import { DetalhePedidoRetaguarda } from "./DetalhePedidoRetaguarda";

export function PaginaDetalheRetaguarda() {
  const { idPedido } = useParams();
  const navegar = useNavigate();
  const local = useLocation();
  const { sessao, pronta } = useSessao();
  const [pedido, setPedido] = useState<VisaoPedidoRetaguarda | null>(null);
  const [erro, setErro] = useState<string | null>(null);
  const [negado, setNegado] = useState(false);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    if (!pronta) {
      return;
    }
    if (!sessao.autenticado) {
      setPedido(null);
      setCarregando(false);
      navegar(`/entrar?origem=${encodeURIComponent(local.pathname)}`, { replace: true });
      return;
    }
    let ativo = true;
    async function carregar() {
      if (!idPedido) {
        return;
      }
      setCarregando(true);
      setErro(null);
      setNegado(false);
      setPedido(null);
      try {
        const visao = await obterPedidoRetaguarda(idPedido);
        if (ativo) {
          setPedido(visao);
        }
      } catch (falha) {
        if (!ativo) {
          return;
        }
        if (isFalhaApi(falha) && falha.status === 401) {
          navegar(`/entrar?origem=${encodeURIComponent(local.pathname)}`, { replace: true });
          return;
        }
        if (isFalhaApi(falha) && (falha.status === 403 || falha.codigo === "ACESSO_NEGADO")) {
          setNegado(true);
        } else {
          setErro(isFalhaApi(falha) ? falha.message : "Não foi possível carregar o pedido.");
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
  }, [pronta, sessao, idPedido, local.pathname, navegar]);

  return (
    <>
      <header className="screen-header" data-od-id="order-detail-header">
        <button
          type="button"
          className="back-link"
          onClick={() => navegar("/retaguarda/pedidos")}
          data-od-id="order-detail-back"
        >
          ← Pedidos da revenda
        </button>
        <h1>{pedido?.idPedido ?? "Pedido"}</h1>
      </header>
      {carregando && (
        <main className="screen-body">
          <p className="inline-info">Carregando pedido…</p>
        </main>
      )}
      {negado && (
        <main className="screen-body">
          <div className="empty-state card" data-od-id="backoffice-denied" role="alert">
            <h2>Acesso negado</h2>
            <p>Somente o operador da revenda pode inspecionar os pedidos da retaguarda.</p>
            <Link className="btn btn-ghost" to="/catalogo">
              Voltar ao catálogo
            </Link>
          </div>
        </main>
      )}
      {erro && !negado && (
        <main className="screen-body">
          <div className="empty-state card" role="alert">
            <h2>Não foi possível carregar</h2>
            <p>{erro}</p>
          </div>
        </main>
      )}
      {pedido && !carregando && !negado && <DetalhePedidoRetaguarda pedido={pedido} />}
    </>
  );
}
