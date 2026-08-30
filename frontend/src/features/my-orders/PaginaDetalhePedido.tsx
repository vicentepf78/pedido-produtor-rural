import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { isFalhaApi } from "../../infra/http";
import { obterPedido, type VisaoPedido } from "./api";
import { DetalhePedido } from "./DetalhePedido";

export function PaginaDetalhePedido() {
  const { idPedido } = useParams();
  const navegar = useNavigate();
  const [pedido, setPedido] = useState<VisaoPedido | null>(null);
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
        const visao = await obterPedido(idPedido);
        if (ativo) {
          setPedido(visao);
        }
      } catch (falha) {
        if (!ativo) {
          return;
        }
        if (isFalhaApi(falha) && falha.codigo === "ACESSO_PEDIDO_NEGADO") {
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
        <button type="button" className="back-link" onClick={() => navegar("/meus-pedidos")} data-od-id="order-detail-back">
          ← Meus pedidos
        </button>
        <h1>Pedido recebido</h1>
      </header>
      {carregando && (
        <main className="screen-body">
          <p className="inline-info">Carregando pedido…</p>
        </main>
      )}
      {negado && (
        <main className="screen-body">
          <div className="status-box" data-od-id="order-denied" role="alert">
            <h2>Acesso negado</h2>
            <p>Você não pode visualizar este pedido.</p>
            <Link className="btn btn-ghost" to="/meus-pedidos">
              Voltar aos pedidos
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
      {pedido && !carregando && !negado && <DetalhePedido pedido={pedido} criadoEm={pedido.criadoEm} />}
    </>
  );
}
