import { NavLink } from "react-router-dom";
import type { ReactNode } from "react";
import { useCarrinho } from "../estado/ProvedorCarrinho";

export function LayoutApp({ children }: { children: ReactNode }) {
  const { carrinho } = useCarrinho();
  const quantidade = carrinho.itens.reduce((soma, item) => soma + item.quantidade, 0);

  return (
    <div className="app-shell" data-od-id="app-shell">
      {children}
      <nav className="bottom-nav" aria-label="Navegação principal" data-od-id="bottom-nav">
        <NavLink
          to="/catalogo"
          className="nav-btn"
          data-od-id="nav-catalog"
        >
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
    </div>
  );
}
