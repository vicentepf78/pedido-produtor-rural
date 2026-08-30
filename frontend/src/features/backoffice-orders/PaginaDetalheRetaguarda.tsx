import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { isFalhaApi } from "../../infra/http";
import { obterPedidoRetaguarda, type VisaoPedidoRetaguarda } from "./api";
import { DetalhePedidoRetaguarda } from "./DetalhePedidoRetaguarda";

export function PaginaDetalheRetaguarda() {
  const { idPedido } = useParams();
  const navegar = useNavigate();
  const [pedido, setPedido] = useState<VisaoPedidoRetaguarda | null>(null);
  const [erro, setErro] = useState<string | null>(null);
  const [negado, setNegado] = useState(false);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    let ativo = true;
    async function carregar() {
      if (!idPedido) {
        return;
      }
      setCarregando(true);
      setErro(null);
      setNegado(false);
      try {
        const visao = await obterPedidoRetaguarda(idPedido);
        if (ativo) {
          setPedido(visao);
        }
      } catch (falha) {
        if (!ativo) {
          return;
        }
        if (isFalhaApi(falha) && (falha.status === 403 || falha.codigo === "ACESSO_NEGADO")) {
          setNegado(true);
        } else if (isFalhaApi(falha) && falha.status === 401) {
          setErro("Entre ou crie uma conta para continuar.");
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
  }, [idPedido]);

  return (
    <>
      <header className="screen-header" data-od-id="order-detail-header">
        <button
          type="button"
          className="back-link"
          onClick={() => navegar("/retaguarda/pedidos")}
          data-od-id="order-detail-back"
        >
          ← Pedidos do backoffice
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
          <div className="status-box" data-od-id="backoffice-denied" role="alert">
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
          <div className="status-box" role="alert">
            <h2>Não foi possível carregar</h2>
            <p>{erro}</p>
          </div>
        </main>
      )}
      {pedido && !carregando && !negado && <DetalhePedidoRetaguarda pedido={pedido} />}
    </>
  );
}
