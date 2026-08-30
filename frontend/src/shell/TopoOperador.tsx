import { useRef, useState } from "react";
import { NavLink } from "react-router-dom";
import { useSessao } from "../estado/ProvedorSessao";

export function TopoOperador() {
  const { sessao, encerrar } = useSessao();
  const [confirmarSaida, setConfirmarSaida] = useState(false);
  const saidaArmada = useRef(false);
  const identificacao = sessao.autenticado ? sessao.nome.trim() || sessao.email : "";

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

  return (
    <header className="top-bar" data-od-id="operator-top-bar">
      <div className="top-bar-inner">
        <div className="brand-mark" data-od-id="brand">
          Retaguarda — Revenda
        </div>
        <nav className="nav-links" aria-label="Navegação da retaguarda">
          <NavLink to="/retaguarda/pedidos" className="nav-link" data-od-id="nav-pedidos-revenda">
            Pedidos da revenda
          </NavLink>
        </nav>
        <div className="user-area" data-od-id="user-area">
          {identificacao && <span data-od-id="user-name">{identificacao}</span>}
          <button type="button" onClick={() => void aoSair()} data-od-id="btn-sair">
            {confirmarSaida ? "Confirmar saída" : "Sair"}
          </button>
        </div>
      </div>
    </header>
  );
}
