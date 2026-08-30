import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { PaginaCatalogo } from "./features/catalog/PaginaCatalogo";
import { PaginaCarrinho } from "./features/cart/PaginaCarrinho";
import { PaginaCheckout } from "./features/checkout/PaginaCheckout";
import { PaginaMeusPedidos } from "./features/my-orders/PaginaMeusPedidos";
import { PaginaPedidosRetaguarda } from "./features/backoffice-orders/PaginaPedidosRetaguarda";

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/catalogo" replace />} />
        <Route path="/catalogo" element={<PaginaCatalogo />} />
        <Route path="/carrinho" element={<PaginaCarrinho />} />
        <Route path="/checkout" element={<PaginaCheckout />} />
        <Route path="/meus-pedidos" element={<PaginaMeusPedidos />} />
        <Route path="/retaguarda/pedidos" element={<PaginaPedidosRetaguarda />} />
      </Routes>
    </BrowserRouter>
  );
}
