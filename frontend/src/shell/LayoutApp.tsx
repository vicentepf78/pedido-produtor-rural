import { NavLink, useLocation } from "react-router-dom";
import type { ReactNode } from "react";
import { useCarrinho } from "../estado/ProvedorCarrinho";

export function LayoutApp({ children }: { children: ReactNode }) {
  const { carrinho } = useCarrinho();
  const local = useLocation();
  const quantidade = carrinho.itens.reduce((soma, item) => soma + item.quantidade, 0);
  const ocultarNav = local.pathname.startsWith("/pedidos/") || local.pathname.startsWith("/retaguarda/");

  return (
    <div className="app-shell" data-od-id="app-shell">
      <a className="skip-link" href="#conteudo-principal">
        Ir ao conteúdo
      </a>
      <div id="conteudo-principal">{children}</div>
      {!ocultarNav && (
        <nav className="bottom-nav" aria-label="Navegação principal" data-od-id="bottom-nav">
          <NavLink to="/catalogo" className="nav-btn" data-od-id="nav-catalog">
            Catálogo
          </NavLink>
          <NavLink to="/carrinho" className="nav-btn" data-od-id="nav-cart">
            Carrinho
            {quantidade > 0 && (
              <span className="badge-count" aria-label={`${quantidade} itens`}>
                {quantidade}
              </span>
            )}
          </NavLink>
          <NavLink to="/checkout" className="nav-btn" data-od-id="nav-checkout">
            Checkout
          </NavLink>
          <NavLink to="/meus-pedidos" className="nav-btn" data-od-id="nav-my-orders">
            Pedidos
          </NavLink>
        </nav>
      )}
    </div>
  );
}
