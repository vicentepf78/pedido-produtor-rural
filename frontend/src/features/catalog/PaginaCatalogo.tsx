import { useEffect, useRef, useState } from "react";
import { Link, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useCarrinho } from "../../estado/ProvedorCarrinho";
import { isFalhaApi } from "../../infra/http";
import { adicionarItem } from "../cart/api";
import { obterProduto, listarProdutos, type ItemProduto } from "./api";
import { BotaoCarregarMais } from "./BotaoCarregarMais";
import { CartaoProduto } from "./CartaoProduto";
import { CarrosselCategoria } from "./CarrosselCategoria";
import { ControleTamanhoLista } from "./ControleTamanhoLista";
import {
  ATRASO_BUSCA_MS,
  CATEGORIA_TODOS,
  ehAbortado,
  ehTamanhoPagina,
  parseCategoria,
  parseTamanhoPagina,
  TAMANHO_PAGINA_PADRAO,
  type CategoriaCarrossel,
  type TamanhoPagina,
} from "./categorias";

function queryCatalogo(categoria: CategoriaCarrossel, consulta: string, tamanho: TamanhoPagina): string {
  const params = new URLSearchParams();
  if (categoria !== CATEGORIA_TODOS) {
    params.set("categoria", categoria);
  }
  if (consulta) {
    params.set("consulta", consulta);
  }
  if (tamanho !== TAMANHO_PAGINA_PADRAO) {
    params.set("tamanhoPagina", String(tamanho));
  }
  const texto = params.toString();
  return texto ? `?${texto}` : "";
}

export function PaginaCatalogo() {
  const { idProduto } = useParams();
  const [params, setParams] = useSearchParams();
  const navegar = useNavigate();
  const { confirmar } = useCarrinho();

  const categoria = parseCategoria(params.get("categoria"));
  const consultaUrl = (params.get("consulta") ?? "").trim();
  const tamanhoPagina = parseTamanhoPagina(params.get("tamanhoPagina"));

  const [busca, setBusca] = useState(() => params.get("consulta") ?? "");
  const consultaParam = params.get("consulta") ?? "";
  const [produtos, setProdutos] = useState<ItemProduto[]>([]);
  const [total, setTotal] = useState(0);
  const [paginaAtual, setPaginaAtual] = useState(1);
  const [carregando, setCarregando] = useState(true);
  const [carregandoMais, setCarregandoMais] = useState(false);
  const [erro, setErro] = useState<string | null>(null);
  const [erroAdicionar, setErroAdicionar] = useState<string | null>(null);
  const [acessoNegado, setAcessoNegado] = useState(false);
  const [adicionando, setAdicionando] = useState<string | null>(null);
  const [tentativa, setTentativa] = useState(0);
  const [origemErro, setOrigemErro] = useState<"lista" | "mais" | null>(null);
  const geracao = useRef(0);
  const carregandoMaisRef = useRef(false);

  useEffect(() => {
    setBusca(consultaParam);
  }, [consultaParam]);

  useEffect(() => {
    const bruto = params.get("tamanhoPagina");
    const categoriaBruta = params.get("categoria");
    let sujo = false;
    const proximo = new URLSearchParams(params);
    if (proximo.has("pagina")) {
      proximo.delete("pagina");
      sujo = true;
    }
    if (bruto !== null && !ehTamanhoPagina(Number(bruto))) {
      proximo.delete("tamanhoPagina");
      sujo = true;
    }
    if (categoriaBruta && parseCategoria(categoriaBruta) === CATEGORIA_TODOS && categoriaBruta !== CATEGORIA_TODOS) {
      proximo.delete("categoria");
      sujo = true;
    }
    if (sujo) {
      setParams(proximo, { replace: true });
    }
  }, [params, setParams]);

  useEffect(() => {
    const id = window.setTimeout(() => {
      const estavel = busca.trim();
      if (estavel === consultaUrl) {
        return;
      }
      aplicarFiltros(categoria, estavel, tamanhoPagina);
    }, ATRASO_BUSCA_MS);
    return () => window.clearTimeout(id);
  }, [busca, categoria, consultaUrl, tamanhoPagina]);

  function aplicarFiltros(proximaCategoria: CategoriaCarrossel, proximaConsulta: string, proximoTamanho: TamanhoPagina) {
    const destino = `/catalogo${queryCatalogo(proximaCategoria, proximaConsulta, proximoTamanho)}`;
    if (idProduto) {
      navegar(destino);
      return;
    }
    setParams(new URLSearchParams(queryCatalogo(proximaCategoria, proximaConsulta, proximoTamanho).replace(/^\?/, "")), {
      replace: true,
    });
  }

  useEffect(() => {
    const esta = ++geracao.current;
    const ac = new AbortController();
    async function carregar() {
      setErro(null);
      setOrigemErro(null);
      setAcessoNegado(false);
      setCarregando(true);
      try {
        if (idProduto) {
          const detalhe = await obterProduto(idProduto, ac.signal);
          if (esta !== geracao.current) {
            return;
          }
          setProdutos([detalhe]);
          setTotal(1);
          setPaginaAtual(1);
          return;
        }
        const pagina = await listarProdutos(
          { consulta: consultaUrl, categoria, pagina: 1, tamanhoPagina },
          ac.signal,
        );
        if (esta !== geracao.current) {
          return;
        }
        setProdutos(pagina.itens);
        setTotal(pagina.total);
        setPaginaAtual(1);
      } catch (falha) {
        if (esta !== geracao.current || ehAbortado(falha)) {
          return;
        }
        if (idProduto && isFalhaApi(falha) && falha.codigo === "PRODUTO_NAO_ELEGIVEL") {
          setAcessoNegado(true);
          setProdutos([]);
          setTotal(0);
        } else {
          setOrigemErro("lista");
          setErro("Verifique sua conexão e tente novamente.");
        }
      } finally {
        if (esta === geracao.current) {
          setCarregando(false);
        }
      }
    }
    void carregar();
    return () => {
      ac.abort();
    };
  }, [categoria, consultaUrl, tamanhoPagina, idProduto, tentativa]);

  async function carregarMais() {
    if (carregandoMaisRef.current || produtos.length >= total) {
      return;
    }
    carregandoMaisRef.current = true;
    setCarregandoMais(true);
    setErro(null);
    setOrigemErro(null);
    const proxima = paginaAtual + 1;
    try {
      const pagina = await listarProdutos({
        consulta: consultaUrl,
        categoria,
        pagina: proxima,
        tamanhoPagina,
      });
      setProdutos((atuais) => {
        const vistos = new Set(atuais.map((item) => item.id));
        return [...atuais, ...pagina.itens.filter((item) => !vistos.has(item.id))];
      });
      setPaginaAtual(proxima);
      setTotal(pagina.total);
    } catch (falha) {
      if (!ehAbortado(falha)) {
        setOrigemErro("mais");
        setErro("Verifique sua conexão e tente novamente.");
      }
    } finally {
      carregandoMaisRef.current = false;
      setCarregandoMais(false);
    }
  }

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

  function limparFiltros() {
    setBusca("");
    aplicarFiltros(CATEGORIA_TODOS, "", TAMANHO_PAGINA_PADRAO);
  }

  function tentarDeNovo() {
    setErro(null);
    if (origemErro === "mais") {
      void carregarMais();
      return;
    }
    setTentativa((n) => n + 1);
  }

  const filtrosAtivos = categoria !== CATEGORIA_TODOS || consultaUrl.length > 0 || busca.trim().length > 0;
  const queryAtual = queryCatalogo(categoria, consultaUrl, tamanhoPagina);
  const temMais = !idProduto && produtos.length > 0 && produtos.length < total;
  const mostrarEsqueleto = carregando && produtos.length === 0 && !acessoNegado;
  const vazio = !carregando && !erro && !acessoNegado && produtos.length === 0;

  return (
    <>
      <header className="screen-header" data-od-id="catalog-header">
        <h1>AgroRevenda Centro</h1>
        <p className="inline-info">Insumos para o produtor rural</p>
      </header>
      <main className="screen-body" data-od-id="catalog-body">
        {!idProduto && (
          <>
            <CarrosselCategoria
              categoria={categoria}
              onEscolher={(proxima) => aplicarFiltros(proxima, busca.trim(), tamanhoPagina)}
            />
            <div className="toolbar" data-od-id="catalog-toolbar">
              <div className="search-row search-field" data-od-id="search-field">
                <input
                  type="search"
                  aria-label="Buscar por nome"
                  placeholder="Buscar por nome"
                  value={busca}
                  onChange={(evento) => setBusca(evento.target.value)}
                  data-od-id="catalog-search"
                />
              </div>
              <ControleTamanhoLista
                valor={tamanhoPagina}
                onMudar={(proximo) => aplicarFiltros(categoria, busca.trim(), proximo)}
              />
            </div>
            {filtrosAtivos && !vazio && !acessoNegado && (
              <div className="filter-actions">
                <button type="button" className="btn btn-ghost" data-od-id="btn-limpar-filtros" onClick={limparFiltros}>
                  Limpar filtros
                </button>
              </div>
            )}
          </>
        )}

        {mostrarEsqueleto && (
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
            <button type="button" className="btn btn-ghost" onClick={tentarDeNovo}>
              Tentar novamente
            </button>
          </div>
        )}

        {acessoNegado && (
          <div className="status-box" data-od-id="catalog-denied" role="alert">
            <h2>Acesso negado</h2>
            <p>Este produto não pode ser pedido nesta loja.</p>
            <Link to={`/catalogo${queryAtual}`} className="btn btn-ghost">
              Voltar ao catálogo
            </Link>
          </div>
        )}

        {vazio && (
          <div className="status-box" data-od-id="catalog-empty">
            <h2>Nenhum resultado</h2>
            <p>Não encontramos produtos para este recorte.</p>
            <button type="button" className="btn btn-primary" data-od-id="btn-limpar-filtros" onClick={limparFiltros}>
              Limpar filtros
            </button>
          </div>
        )}

        {erroAdicionar && (
          <p className="inline-error" role="alert" data-od-id="catalog-add-error">
            {erroAdicionar}
          </p>
        )}

        {!acessoNegado && produtos.length > 0 && (
          <div id="catalog-painel" data-od-id="catalog-results">
            {idProduto && (
              <p className="filter-actions">
                <Link to={`/catalogo${queryAtual}`} className="btn btn-ghost">
                  Voltar ao catálogo
                </Link>
              </p>
            )}
            <div className={idProduto ? undefined : "product-grid"} data-od-id="product-grid">
              {produtos.map((produto) => (
                <CartaoProduto
                  key={produto.id}
                  produto={produto}
                  adicionando={adicionando === produto.id}
                  onAdicionar={adicionar}
                  hrefDetalhe={
                    idProduto ? undefined : `/catalogo/${produto.id}${queryCatalogo(categoria, busca.trim() || consultaUrl, tamanhoPagina)}`
                  }
                />
              ))}
            </div>
            <BotaoCarregarMais visivel={temMais} carregando={carregandoMais} onCarregar={() => void carregarMais()} />
          </div>
        )}
      </main>
    </>
  );
}
