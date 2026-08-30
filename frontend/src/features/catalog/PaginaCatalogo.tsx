import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useCarrinho } from "../../estado/ProvedorCarrinho";
import { isFalhaApi } from "../../infra/http";
import { obterProduto, listarProdutos, type ItemProduto } from "./api";
import { adicionarItem } from "../cart/api";
import { CartaoProduto } from "./CartaoProduto";
import { aoTeclaAba } from "../../shell/tecladoAbas";

const CATEGORIA_TODOS = "Todos";
const ORDEM_CATEGORIAS = ["Sementes", "Fertilizantes", "Correção"];

function ordenarCategorias(categorias: string[]): string[] {
  const unicas = [...new Set(categorias.filter((c) => c !== "Defensivos"))];
  const preferidas = ORDEM_CATEGORIAS.filter((c) => unicas.includes(c));
  const demais = unicas.filter((c) => !ORDEM_CATEGORIAS.includes(c)).sort();
  return [CATEGORIA_TODOS, ...preferidas, ...demais];
}

export function PaginaCatalogo() {
  const { idProduto } = useParams();
  const navegar = useNavigate();
  const { confirmar } = useCarrinho();
  const [busca, setBusca] = useState("");
  const [categoria, setCategoria] = useState(CATEGORIA_TODOS);
  const [produtos, setProdutos] = useState<ItemProduto[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [erroAdicionar, setErroAdicionar] = useState<string | null>(null);
  const [acessoNegado, setAcessoNegado] = useState(false);
  const [adicionando, setAdicionando] = useState<string | null>(null);
  const [categorias, setCategorias] = useState<string[]>([CATEGORIA_TODOS]);

  useEffect(() => {
    let ativo = true;
    async function carregar() {
      setCarregando(true);
      setErro(null);
      setAcessoNegado(false);
      try {
        if (idProduto) {
          const detalhe = await obterProduto(idProduto);
          if (ativo) {
            setProdutos([detalhe]);
          }
          return;
        }
        const consulta = busca.trim().length === 0 ? undefined : busca.trim();
        const pagina = await listarProdutos(consulta);
        if (ativo) {
          setProdutos(pagina.itens);
          if (consulta === undefined && pagina.itens.length > 0) {
            setCategorias(ordenarCategorias(pagina.itens.map((p) => p.categoria)));
          }
        }
      } catch (falha) {
        if (!ativo) {
          return;
        }
        if (idProduto && isFalhaApi(falha) && falha.codigo === "PRODUTO_NAO_ELEGIVEL") {
          setAcessoNegado(true);
          setProdutos([]);
        } else {
          setErro("Verifique sua conexão e tente novamente.");
        }
      } finally {
        if (ativo) {
          setCarregando(false);
        }
      }
    }
    void carregar();
    return () => {
      ativo = false;
    };
  }, [busca, idProduto]);

  const filtrados = useMemo(() => {
    if (categoria === CATEGORIA_TODOS) {
      return produtos;
    }
    return produtos.filter((p) => p.categoria === categoria);
  }, [produtos, categoria]);

  async function adicionar(produto: ItemProduto) {
    setAdicionando(produto.id);
    setErroAdicionar(null);
    try {
      confirmar(await adicionarItem(produto.id, 1));
    } catch (falha) {
      setErroAdicionar(isFalhaApi(falha) ? falha.message : "Não foi possível adicionar.");
    } finally {
      setAdicionando(null);
    }
  }

  function limparBusca() {
    setBusca("");
    setCategoria(CATEGORIA_TODOS);
    if (idProduto) {
      navegar("/catalogo");
    }
  }

  return (
    <>
      <header className="screen-header" data-od-id="catalog-header">
        <h1>AgroRevenda Centro</h1>
        <p className="inline-info">Insumos para o produtor rural</p>
      </header>
      <main className="screen-body" data-od-id="catalog-body">
        <div className="search-row">
          <input
            type="search"
            aria-label="Buscar por nome"
            placeholder="Buscar por nome"
            value={busca}
            onChange={(evento) => {
              setBusca(evento.target.value);
              if (idProduto) {
                navegar("/catalogo");
              }
            }}
            data-od-id="catalog-search"
          />
        </div>

        <div className="category-tabs" role="tablist" aria-label="Categorias" data-od-id="catalog-categories">
          {categorias.map((cat) => (
            <button
              key={cat}
              type="button"
              role="tab"
              className="cat-tab"
              aria-selected={categoria === cat}
              aria-controls="catalog-painel"
              tabIndex={categoria === cat ? 0 : -1}
              onClick={() => setCategoria(cat)}
              onKeyDown={(evento) => aoTeclaAba(evento, categorias, categoria, setCategoria)}
            >
              {cat}
            </button>
          ))}
        </div>

        <p className="notice" data-od-id="regulated-notice">
          Produtos regulados não aparecem neste catálogo do MVP0.
        </p>

        {carregando && (
          <div data-od-id="catalog-loading" aria-live="polite">
            <div className="skeleton skeleton-card" />
            <div className="skeleton skeleton-card" />
            <div className="skeleton skeleton-card" />
            <p className="inline-info">Carregando produtos…</p>
          </div>
        )}

        {erro && (
          <div className="status-box" data-od-id="catalog-error" role="alert">
            <h2>Não foi possível carregar</h2>
            <p>{erro}</p>
            <button type="button" className="btn btn-ghost" onClick={limparBusca}>
              Tentar novamente
            </button>
          </div>
        )}

        {acessoNegado && (
          <div className="status-box" data-od-id="catalog-denied" role="alert">
            <h2>Acesso negado</h2>
            <p>Este produto está oculto ou você não tem permissão para visualizá-lo.</p>
            <button type="button" className="btn btn-ghost" onClick={() => navegar("/catalogo")}>
              Voltar ao catálogo
            </button>
          </div>
        )}

        {!carregando && !erro && !acessoNegado && filtrados.length === 0 && (
          <div className="status-box" data-od-id="catalog-empty">
            <h2>Nenhum resultado</h2>
            <p>Não encontramos produtos para “{busca || "busca atual"}”.</p>
            <button type="button" className="btn btn-primary" data-od-id="clear-search-btn" onClick={limparBusca}>
              Limpar busca
            </button>
          </div>
        )}

        {erroAdicionar && (
          <p className="inline-error" role="alert" data-od-id="catalog-add-error">
            {erroAdicionar}
          </p>
        )}

        {!carregando && !erro && !acessoNegado && filtrados.length > 0 && (
          <div id="catalog-painel" role="tabpanel" data-od-id="catalog-results">
            {filtrados.map((produto) => (
              <CartaoProduto
                key={produto.id}
                produto={produto}
                adicionando={adicionando === produto.id}
                onAdicionar={adicionar}
              />
            ))}
          </div>
        )}
      </main>
    </>
  );
}
