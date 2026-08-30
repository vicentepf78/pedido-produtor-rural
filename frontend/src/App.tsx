import { BrowserRouter, Link, Navigate, Route, Routes } from "react-router-dom";
import { ProvedorCarrinho } from "./estado/ProvedorCarrinho";
import { LayoutApp } from "./shell/LayoutApp";
import { PaginaCatalogo } from "./features/catalog/PaginaCatalogo";
import { PaginaCarrinho } from "./features/cart/PaginaCarrinho";
import { PaginaCheckout } from "./features/checkout/PaginaCheckout";
import { PaginaMeusPedidos } from "./features/my-orders/PaginaMeusPedidos";
import { PaginaDetalhePedido } from "./features/my-orders/PaginaDetalhePedido";
import { PaginaPedidosRetaguarda } from "./features/backoffice-orders/PaginaPedidosRetaguarda";
import { PaginaDetalheRetaguarda } from "./features/backoffice-orders/PaginaDetalheRetaguarda";

export function App() {
  return (
    <BrowserRouter>
      <ProvedorCarrinho>
        <LayoutApp>
          <Routes>
            <Route path="/" element={<Navigate to="/catalogo" replace />} />
            <Route path="/catalogo" element={<PaginaCatalogo />} />
            <Route path="/catalogo/:idProduto" element={<PaginaCatalogo />} />
            <Route path="/carrinho" element={<PaginaCarrinho />} />
            <Route path="/checkout" element={<PaginaCheckout />} />
            <Route path="/pedidos/:idPedido" element={<PaginaDetalhePedido />} />
            <Route path="/meus-pedidos" element={<PaginaMeusPedidos />} />
            <Route path="/retaguarda/pedidos/:idPedido" element={<PaginaDetalheRetaguarda />} />
            <Route path="/retaguarda/pedidos" element={<PaginaPedidosRetaguarda />} />
            <Route
              path="*"
              element={
                <main className="screen-body">
                  <div className="status-box" role="alert">
                    <h1>Página não encontrada</h1>
                    <p>Este endereço não existe nesta loja.</p>
                    <Link className="btn btn-primary" to="/catalogo">
                      Ir ao catálogo
                    </Link>
                  </div>
                </main>
              }
            />
          </Routes>
        </LayoutApp>
      </ProvedorCarrinho>
    </BrowserRouter>
  );
}
