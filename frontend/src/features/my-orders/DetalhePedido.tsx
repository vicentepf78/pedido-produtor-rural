import { formatarDinheiro } from "../../infra/http";
import { OPCOES_RETIRADA } from "../checkout/opcoes";
import type { VisaoPedido } from "./api";

function rotuloSituacao(situacao: string) {
  if (situacao === "RECEBIDO") {
    return "Recebido";
  }
  return situacao;
}

function rotuloConfirmacao(confirmacao: string) {
  if (confirmacao === "ACEITA") {
    return "Aceita";
  }
  if (confirmacao === "PENDENTE") {
    return "Pendente";
  }
  return confirmacao;
}

function classeConfirmacao(confirmacao: string) {
  return confirmacao === "ACEITA" ? "pill success" : "pill warn";
}

function rotuloRetirada(codigo: string) {
  return OPCOES_RETIRADA.find((opcao) => opcao.id === codigo)?.label ?? codigo;
}

function formatarQuando(iso?: string) {
  if (!iso) {
    return "—";
  }
  const data = new Date(iso);
  if (Number.isNaN(data.getTime())) {
    return iso;
  }
  return data.toLocaleString("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
}

export function DetalhePedido({ pedido, criadoEm }: { pedido: VisaoPedido; criadoEm?: string }) {
  return (
    <main className="screen-body" data-od-id="order-detail-body">
      <div
        className="status-box status-box-solid"
        data-od-id="confirmation-success"
        style={{ marginBottom: "1rem" }}
      >
        <h2>Pedido recebido</h2>
        <p>Obrigado! Seu pedido foi registrado.</p>
      </div>

      <dl className="detail-grid" data-od-id="order-meta-grid">
        <div className="detail-row">
          <dt>Identificador</dt>
          <dd>{pedido.idPedido}</dd>
        </div>
        <div className="detail-row">
          <dt>Situação</dt>
          <dd>
            <span className="pill neutral">{rotuloSituacao(pedido.situacao)}</span>
          </dd>
        </div>
        <div className="detail-row">
          <dt>Confirmação</dt>
          <dd>
            <span className={classeConfirmacao(pedido.confirmacao)}>{rotuloConfirmacao(pedido.confirmacao)}</span>
          </dd>
        </div>
        <div className="detail-row">
          <dt>Data/hora</dt>
          <dd>{formatarQuando(criadoEm)}</dd>
        </div>
        <div className="detail-row">
          <dt>Propriedade</dt>
          <dd className="detail-wrap">{pedido.nomePropriedade}</dd>
        </div>
        <div className="detail-row">
          <dt>Retirada</dt>
          <dd className="detail-wrap">{rotuloRetirada(pedido.preferenciaRetirada)}</dd>
        </div>
      </dl>

      <h2 className="section-title">Itens</h2>
      {pedido.itens.map((linha, indice) => (
        <div key={`${linha.nome}-${indice}`} className="cart-line" data-od-id={`detail-line-${indice}`}>
          <h2 style={{ margin: "0 0 0.375rem", fontSize: "0.9375rem" }}>{linha.nome}</h2>
          <div className="summary-row">
            <span>
              {linha.quantidade} × {formatarDinheiro(linha.precoUnitario)}
              {linha.unidade ? ` / ${linha.unidade}` : ""}
            </span>
            <span className="mono">{formatarDinheiro(linha.totalLinha)}</span>
          </div>
        </div>
      ))}

      <div className="summary-panel" data-od-id="order-detail-total">
        <div className="summary-row total">
          <span>Total do pedido</span>
          <span className="mono">{formatarDinheiro(pedido.total)}</span>
        </div>
      </div>
    </main>
  );
}

export { formatarQuando, rotuloConfirmacao, rotuloSituacao, classeConfirmacao };
