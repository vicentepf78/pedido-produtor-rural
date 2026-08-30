import { useEffect, useState } from "react";
import { formatarDinheiro } from "../../infra/http";
import type { ItemProduto } from "./api";

type Props = {
  produto: ItemProduto;
  onAdicionar: (produto: ItemProduto) => void;
  adicionando?: boolean;
};

export function CartaoProduto({ produto, onAdicionar, adicionando }: Props) {
  const [imagemQuebrada, setImagemQuebrada] = useState(false);
  useEffect(() => {
    setImagemQuebrada(false);
  }, [produto.urlImagem]);

  const temImagem = Boolean(produto.urlImagem) && !imagemQuebrada;

  return (
    <article
      className={`product-card${produto.disponivel ? "" : " unavailable"}`}
      data-od-id={`product-card-${produto.id}`}
    >
      <div className={`product-thumb${temImagem ? " has-image" : ""}`} aria-hidden="true">
        {temImagem ? (
          <img src={produto.urlImagem ?? ""} alt="" onError={() => setImagemQuebrada(true)} />
        ) : (
          "Sem imagem"
        )}
      </div>
      <div className="product-meta">
        <h2>{produto.nome}</h2>
        <p>{produto.descricaoCurta}</p>
        <div className="price-row">
          <span className="unit-price">{formatarDinheiro(produto.precoUnitario)}</span>
          <span className="unit-label">/ {produto.unidade}</span>
        </div>
        {produto.disponivel ? (
          <button
            type="button"
            className="btn btn-primary btn-sm"
            data-od-id={`add-btn-${produto.id}`}
            disabled={adicionando}
            onClick={() => onAdicionar(produto)}
          >
            Adicionar
          </button>
        ) : (
          <>
            <p className="inline-error" role="status">
              Este produto não está disponível.
            </p>
            <button type="button" className="btn btn-sm" disabled aria-disabled="true">
              Indisponível
            </button>
          </>
        )}
      </div>
    </article>
  );
}
