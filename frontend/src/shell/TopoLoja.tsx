import { useRef, useState } from "react";
import { NavLink, useLocation, useNavigate } from "react-router-dom";
import { useCarrinho } from "../estado/ProvedorCarrinho";
import { useSessao } from "../estado/ProvedorSessao";
import { destinoPermitido } from "../features/identity/destinoAposEntrada";

export function TopoLoja() {
  const { carrinho } = useCarrinho();
  const { sessao, encerrar } = useSessao();
  const local = useLocation();
  const navegar = useNavigate();
  const quantidade = carrinho.itens.reduce((soma, item) => soma + item.quantidade, 0);
  const [confirmarSaida, setConfirmarSaida] = useState(false);
  const saidaArmada = useRef(false);

  const origem = destinoPermitido(`${local.pathname}`);

  async function aoSair() {
    if (!saidaArmada.current) {
      saidaArmada.current = true;
      setConfirmarSaida(true);
      return;
    }
    await encerrar();
    saidaArmada.current = false;
    setConfirmarSaida(false);
  }

  const identificacao = sessao.autenticado ? sessao.nome.trim() || sessao.email : "";

  return (
    <header className="top-bar" data-od-id="producer-top-bar">
      <div className="top-bar-inner">
        <div className="brand-mark" data-od-id="brand">
          Insumos Agro
        </div>
        <nav className="nav-links" aria-label="Navegação principal">
          <NavLink to="/catalogo" className="nav-link" data-od-id="nav-catalogo">
            Catálogo
          </NavLink>
          <NavLink to="/carrinho" className="nav-link" data-od-id="nav-carrinho">
            Carrinho
            {quantidade > 0 && (
              <span className="nav-badge" aria-label={`${quantidade} itens`}>
                {quantidade}
              </span>
            )}
          </NavLink>
          <NavLink to="/checkout" className="nav-link" data-od-id="nav-checkout">
            Checkout
          </NavLink>
          <NavLink to="/meus-pedidos" className="nav-link" data-od-id="nav-pedidos">
            Pedido
          </NavLink>
        </nav>
        <div className="user-area" data-od-id="user-area">
          {sessao.autenticado ? (
            <>
              <span data-od-id="user-name">{identificacao}</span>
              <button type="button" onClick={() => void aoSair()} data-od-id="btn-sair">
                {confirmarSaida ? "Confirmar saída" : "Sair"}
              </button>
            </>
          ) : (
            <button
              type="button"
              data-od-id="btn-entrar"
              onClick={() => navegar(`/entrar?origem=${origem}`)}
            >
              Entrar
            </button>
          )}
        </div>
      </div>
    </header>
  );
}
