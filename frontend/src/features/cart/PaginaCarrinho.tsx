import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCarrinho } from "../../estado/ProvedorCarrinho";
import { formatarDinheiro, isFalhaApi } from "../../infra/http";
import { alterarQuantidade, removerItem } from "./api";

export function PaginaCarrinho() {
  const navegar = useNavigate();
  const { carrinho, carregando, erroRestore, confirmar, recarregar } = useCarrinho();
  const [errosQtd, setErrosQtd] = useState<Record<string, string>>({});
  const [rascunhos, setRascunhos] = useState<Record<string, string>>({});

  useEffect(() => {
    setRascunhos(Object.fromEntries(carrinho.itens.map((item) => [item.idProduto, String(item.quantidade)])));
  }, [carrinho]);

  async function mudarQuantidade(idProduto: string, bruto: string) {
    setRascunhos((atual) => ({ ...atual, [idProduto]: bruto }));
    const parsed = Number(bruto);
    const valida = Number.isInteger(parsed) && parsed > 0;
    if (!valida) {
      setErrosQtd((atual) => ({ ...atual, [idProduto]: "Informe uma quantidade inteira positiva." }));
      return;
    }
    const confirmado = carrinho;
    try {
      confirmar(await alterarQuantidade(idProduto, parsed));
      setErrosQtd((atual) => {
        const proximo = { ...atual };
        delete proximo[idProduto];
        return proximo;
      });
    } catch (falha) {
      confirmar(confirmado);
      setRascunhos((atual) => ({
        ...atual,
        [idProduto]: String(confirmado.itens.find((item) => item.idProduto === idProduto)?.quantidade ?? ""),
      }));
      setErrosQtd((atual) => ({
        ...atual,
        [idProduto]: isFalhaApi(falha) ? falha.message : "Informe uma quantidade inteira positiva.",
      }));
    }
  }

  async function remover(idProduto: string) {
    const confirmado = carrinho;
    try {
      confirmar(await removerItem(idProduto));
    } catch {
      confirmar(confirmado);
    }
  }

  const checkoutBloqueado = carrinho.itens.length === 0 || Object.values(errosQtd).some(Boolean);
  const vazio = !carregando && !erroRestore && carrinho.itens.length === 0;

  return (
    <>
      <header className="screen-header" data-od-id="cart-header">
        <h1>Carrinho</h1>
      </header>
      <main className="screen-body" data-od-id="cart-body">
        {carregando && (
          <p className="inline-info" data-od-id="cart-loading">
            Carregando carrinho…
          </p>
        )}
        {erroRestore && (
          <div className="empty-state card" data-od-id="cart-restore-error" role="alert">
            <h2>Não foi possível restaurar</h2>
            <p>{erroRestore}</p>
            <button type="button" className="btn btn-ghost" onClick={() => void recarregar()}>
              Tentar novamente
            </button>
          </div>
        )}
        {vazio && (
          <section className="empty-state card" data-od-id="s2-carrinho-empty">
            <h2>Carrinho vazio</h2>
            <p>Adicione ao menos um produto antes do checkout.</p>
            <button type="button" className="btn btn-primary" onClick={() => navegar("/catalogo")}>
              Ir ao catálogo
            </button>
          </section>
        )}
        {!vazio && !carregando && !erroRestore && (
          <section className="card cart-card" data-od-id="s2-carrinho">
            {carrinho.itens.map((linha) => (
              <div key={linha.idProduto} className="cart-line" data-od-id={`cart-line-${linha.idProduto}`}>
                <div className="cart-line-info">
                  <h2>{linha.nome}</h2>
                  <p>
                    Unitário: <span className="mono">{formatarDinheiro(linha.precoUnitario)}</span>
                  </p>
                  <button
                    type="button"
                    className="btn btn-ghost btn-sm"
                    data-od-id={`remove-${linha.idProduto}`}
                    onClick={() => void remover(linha.idProduto)}
                  >
                    Remover
                  </button>
                </div>
                <div className="qty-row">
                  <label htmlFor={`qty-${linha.idProduto}`}>Qtd</label>
                  <input
                    id={`qty-${linha.idProduto}`}
                    type="number"
                    inputMode="numeric"
                    min="1"
                    step="1"
                    value={rascunhos[linha.idProduto] ?? String(linha.quantidade)}
                    aria-invalid={Boolean(errosQtd[linha.idProduto])}
                    aria-describedby={errosQtd[linha.idProduto] ? `qty-err-${linha.idProduto}` : undefined}
                    onChange={(evento) => void mudarQuantidade(linha.idProduto, evento.target.value)}
                    data-od-id={`qty-input-${linha.idProduto}`}
                  />
                  <span className="line-total">{formatarDinheiro(linha.totalLinha)}</span>
                </div>
                {errosQtd[linha.idProduto] && (
                  <p id={`qty-err-${linha.idProduto}`} className="inline-error" role="alert">
                    {errosQtd[linha.idProduto]}
                  </p>
                )}
              </div>
            ))}
            <div className="cart-summary" data-od-id="cart-summary">
              <div className="summary-row total">
                <span>Total do pedido</span>
                <span className="mono">{formatarDinheiro(carrinho.total)}</span>
              </div>
            </div>
          </section>
        )}
      </main>
      <footer className="sticky-footer" data-od-id="cart-footer">
        <button
          type="button"
          className="btn btn-primary btn-block"
          data-od-id="cart-checkout-btn"
          disabled={checkoutBloqueado}
          onClick={() => navegar("/checkout")}
        >
          Ir para checkout
        </button>
      </footer>
    </>
  );
}
