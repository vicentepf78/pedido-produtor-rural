import type { ReactNode } from "react";
import { useSessao } from "../estado/ProvedorSessao";
import { TopoLoja } from "./TopoLoja";
import { TopoOperador } from "./TopoOperador";

export function LayoutApp({ children }: { children: ReactNode }) {
  const { sessao } = useSessao();
  const operador = sessao.autenticado && sessao.papeis.includes("OPERADOR_REVENDA");

  return (
    <div className="app-shell" data-od-id="app-shell">
      <a className="skip-link" href="#conteudo-principal">
        Ir ao conteúdo
      </a>
      {operador ? <TopoOperador /> : <TopoLoja />}
      <div id="conteudo-principal">{children}</div>
    </div>
  );
}
