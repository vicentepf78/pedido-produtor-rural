import { useRef, useState } from "react";
import { ITENS_CARROSSEL, type CategoriaCarrossel } from "./categorias";

type Props = {
  categoria: CategoriaCarrossel;
  onEscolher: (categoria: CategoriaCarrossel) => void;
};

export function CarrosselCategoria({ categoria, onEscolher }: Props) {
  const trilho = useRef<HTMLDivElement>(null);
  const [quebradas, setQuebradas] = useState<Record<string, boolean>>({});

  function deslocar(delta: number) {
    trilho.current?.scrollBy({ left: delta, behavior: "smooth" });
  }

  return (
    <div className="carousel-wrap" data-od-id="category-carousel-wrap">
      <button
        type="button"
        className="carousel-arrow"
        aria-label="Rolar categorias para esquerda"
        data-od-id="carousel-prev"
        onClick={() => deslocar(-120)}
      >
        ‹
      </button>
      <div ref={trilho} className="category-carousel" role="list" aria-label="Categorias">
        {ITENS_CARROSSEL.map((item) => {
          const selecionado = categoria === item.id;
          const semFoto = Boolean(quebradas[item.id]);
          return (
            <button
              key={item.id}
              type="button"
              role="listitem"
              className={`category-card${selecionado ? " selected" : ""}`}
              aria-pressed={selecionado}
              data-od-id={`category-${item.slug}`}
              onClick={() => onEscolher(item.id)}
            >
              <span className={`cat-thumb${semFoto ? " cat-thumb-faltando" : ""}`} aria-hidden="true">
                {!semFoto && (
                  <img src={item.urlImagem} alt="" onError={() => setQuebradas((atual) => ({ ...atual, [item.id]: true }))} />
                )}
              </span>
              <span>{item.rotulo}</span>
            </button>
          );
        })}
      </div>
      <button
        type="button"
        className="carousel-arrow carousel-arrow-next"
        aria-label="Rolar categorias para direita"
        data-od-id="carousel-next"
        onClick={() => deslocar(120)}
      >
        ›
      </button>
    </div>
  );
}
