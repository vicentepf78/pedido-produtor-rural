import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useCarrinho } from "../../estado/ProvedorCarrinho";
import { useSessao } from "../../estado/ProvedorSessao";
import { FalhaApi, formatarDinheiro, isFalhaApi } from "../../infra/http";
import {
  cadastrar,
  confirmarPedido,
  criarPropriedade,
  entrar,
  listarPropriedades,
  type ResumoPropriedade,
} from "./api";
import { OPCOES_RETIRADA } from "./opcoes";
import { aoTeclaAba } from "../../shell/tecladoAbas";

const RASCUNHO = "checkout-rascunho";

type Modo = "login" | "register";

type Rascunho = {
  modo: Modo;
  nome: string;
  email: string;
  idPropriedade: string;
  nomePropriedade: string;
  preferenciaRetirada: string;
};

function lerRascunho(): Rascunho {
  try {
    const bruto = sessionStorage.getItem(RASCUNHO);
    if (bruto) {
      return { nomePropriedade: "", ...(JSON.parse(bruto) as Rascunho) };
    }
  } catch {
    /* ignore */
  }
  return { modo: "login", nome: "", email: "", idPropriedade: "", nomePropriedade: "", preferenciaRetirada: "" };
}

type Erros = Partial<Record<"name" | "email" | "password" | "propertyId" | "pickupId" | "form", string>>;

export function PaginaCheckout() {
  const navegar = useNavigate();
  const { carrinho, recarregar } = useCarrinho();
  const { aplicarAutenticacao, encerrar } = useSessao();
  const chaveIdempotencia = useRef(crypto.randomUUID());
  const [rascunho, setRascunho] = useState<Rascunho>(lerRascunho);
  const [senha, setSenha] = useState("");
  const [erros, setErros] = useState<Erros>({});
  const [propriedades, setPropriedades] = useState<ResumoPropriedade[]>([]);
  const [autenticado, setAutenticado] = useState(false);
  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    sessionStorage.setItem(RASCUNHO, JSON.stringify(rascunho));
  }, [rascunho]);

  useEffect(() => {
    let ativo = true;
    listarPropriedades()
      .then((itens) => {
        if (!ativo) {
          return;
        }
        setPropriedades(itens);
        setAutenticado(true);
      })
      .catch((falha) => {
        if (!ativo) {
          return;
        }
        if (isFalhaApi(falha) && falha.status === 401) {
          setAutenticado(false);
          setPropriedades([]);
        }
      });
    return () => {
      ativo = false;
    };
  }, []);

  function atualizar(parcial: Partial<Rascunho>) {
    setRascunho((atual) => ({ ...atual, ...parcial }));
  }

  function validarIdentidade(): Erros {
    const proximo: Erros = {};
    if (!autenticado) {
      if (rascunho.modo === "register" && !rascunho.nome.trim()) {
        proximo.name = "Informe o nome.";
      }
      if (!rascunho.email.trim()) {
        proximo.email = "Informe o e-mail.";
      }
      if (!senha.trim()) {
        proximo.password = "Informe a senha.";
      }
    }
    return proximo;
  }

  function validarCheckout(lista: ResumoPropriedade[]): Erros {
    const proximo: Erros = {};
    if (lista.length > 0) {
      if (!rascunho.idPropriedade) {
        proximo.propertyId = "Selecione uma propriedade.";
      }
    } else if (!rascunho.nomePropriedade.trim()) {
      proximo.propertyId = "Informe o nome da propriedade.";
    }
    if (!rascunho.preferenciaRetirada) {
      proximo.pickupId = "Selecione a preferência de retirada.";
    }
    return proximo;
  }

  function focarPrimeiro(proximo: Erros, temPropriedades: boolean) {
    const ordem: Array<[keyof Erros, string]> = [
      ["name", "auth-name"],
      ["email", "auth-email"],
      ["password", "auth-password"],
      ["propertyId", temPropriedades ? "checkout-property" : "checkout-property-nome"],
      ["pickupId", "checkout-pickup"],
    ];
    for (const [campo, id] of ordem) {
      if (proximo[campo]) {
        document.getElementById(id)?.focus();
        return;
      }
    }
  }

  async function confirmar() {
    const identidade = validarIdentidade();
    if (Object.keys(identidade).length > 0) {
      setErros(identidade);
      focarPrimeiro(identidade, propriedades.length > 0);
      return;
    }
    if (carrinho.itens.length === 0) {
      setErros({ form: "Adicione ao menos um produto antes do checkout." });
      return;
    }
    setEnviando(true);
    try {
      let lista = propriedades;
      if (!autenticado) {
        if (rascunho.modo === "register") {
          aplicarAutenticacao(await cadastrar(rascunho.nome.trim(), rascunho.email.trim(), senha));
        } else {
          aplicarAutenticacao(await entrar(rascunho.email.trim(), senha));
        }
        lista = await listarPropriedades();
        setPropriedades(lista);
        setAutenticado(true);
      }
      const checkout = validarCheckout(lista);
      if (Object.keys(checkout).length > 0) {
        setErros(checkout);
        focarPrimeiro(checkout, lista.length > 0);
        return;
      }
      let idPropriedade = rascunho.idPropriedade;
      if (!idPropriedade && rascunho.nomePropriedade.trim()) {
        const criada = await criarPropriedade(rascunho.nomePropriedade.trim());
        idPropriedade = criada.id;
        setPropriedades([criada]);
        atualizar({ idPropriedade: criada.id });
      }
      const confirmacao = await confirmarPedido(
        idPropriedade,
        rascunho.preferenciaRetirada,
        chaveIdempotencia.current,
      );
      sessionStorage.removeItem(RASCUNHO);
      await recarregar();
      navegar(`/pedidos/${confirmacao.idPedido}`);
    } catch (falha) {
      if (isFalhaApi(falha)) {
        aplicarErroApi(falha);
      } else {
        setErros({ form: "Não foi possível confirmar o pedido." });
      }
    } finally {
      setEnviando(false);
    }
  }

  function aplicarErroApi(falha: FalhaApi) {
    if (falha.codigo === "NAO_AUTENTICADO") {
      setAutenticado(false);
      setErros({ form: falha.message });
      document.getElementById("auth-email")?.focus();
      return;
    }
    if (falha.codigo === "CREDENCIAIS_INVALIDAS" || falha.codigo === "EMAIL_DUPLICADO") {
      setErros({ form: falha.message, email: falha.message });
      document.getElementById("auth-email")?.focus();
      return;
    }
    if (falha.codigo === "DADOS_CHECKOUT_OBRIGATORIOS") {
      const proximo: Erros = { form: falha.message };
      if (!rascunho.idPropriedade) {
        proximo.propertyId = "Selecione uma propriedade.";
      }
      if (!rascunho.preferenciaRetirada) {
        proximo.pickupId = "Selecione a preferência de retirada.";
      }
      setErros(proximo);
      focarPrimeiro(proximo, propriedades.length > 0);
      return;
    }
    if (falha.codigo === "CARRINHO_VAZIO") {
      setErros({ form: falha.message });
      return;
    }
    setErros({ form: falha.message });
  }

  const checkoutDisponivel = carrinho.itens.length > 0 && !enviando;
  const modos = ["login", "register"] as const;

  async function encerrarSessao() {
    await encerrar();
    setAutenticado(false);
    setPropriedades([]);
  }

  return (
    <>
      <header className="screen-header" data-od-id="checkout-header">
        <h1>Checkout</h1>
        {autenticado && (
          <button type="button" className="btn btn-ghost btn-sm" data-od-id="checkout-sair" onClick={() => void encerrarSessao()}>
            Sair
          </button>
        )}
      </header>
      <main className="screen-body" data-od-id="checkout-body">
        {carrinho.itens.length === 0 && (
          <div className="status-box" data-od-id="checkout-empty">
            <h2>Carrinho vazio</h2>
            <p>Adicione ao menos um produto antes do checkout.</p>
            <button type="button" className="btn btn-ghost" onClick={() => navegar("/catalogo")}>
              Ir ao catálogo
            </button>
          </div>
        )}
        <form
          id="checkout-form"
          onSubmit={(evento) => {
            evento.preventDefault();
            void confirmar();
          }}
        >
        <h2 className="section-title">Identificação</h2>
        <div role="tablist" aria-label="Modo de identificação" className="ident-tabs">
          {(
            [
              ["login", "Entrar"],
              ["register", "Criar conta"],
            ] as const
          ).map(([modo, rotulo]) => (
            <button
              key={modo}
              type="button"
              className="cat-tab"
              role="tab"
              aria-selected={rascunho.modo === modo}
              aria-controls="identificacao-painel"
              tabIndex={rascunho.modo === modo ? 0 : -1}
              onClick={() => atualizar({ modo })}
              onKeyDown={(evento) => aoTeclaAba(evento, [...modos], rascunho.modo, (proximo) => atualizar({ modo: proximo as Modo }))}
            >
              {rotulo}
            </button>
          ))}
        </div>
        <div id="identificacao-painel" role="tabpanel">

        {rascunho.modo === "register" && (
          <div className="field">
            <label htmlFor="auth-name">Nome</label>
            <input
              id="auth-name"
              type="text"
              autoComplete="name"
              value={rascunho.nome}
              aria-invalid={Boolean(erros.name)}
              aria-describedby={erros.name ? "auth-name-erro" : undefined}
              onChange={(evento) => atualizar({ nome: evento.target.value })}
              data-od-id="checkout-name"
            />
            {erros.name && (
              <p id="auth-name-erro" className="inline-error" role="alert">
                {erros.name}
              </p>
            )}
          </div>
        )}

        <div className="field">
          <label htmlFor="auth-email">E-mail</label>
          <input
            id="auth-email"
            type="email"
            autoComplete="email"
            value={rascunho.email}
            aria-invalid={Boolean(erros.email)}
            aria-describedby={erros.email ? "auth-email-erro" : undefined}
            onChange={(evento) => atualizar({ email: evento.target.value })}
            data-od-id="checkout-email"
          />
          {erros.email && (
            <p id="auth-email-erro" className="inline-error" role="alert">
              {erros.email}
            </p>
          )}
        </div>

        <div className="field">
          <label htmlFor="auth-password">Senha</label>
          <input
            id="auth-password"
            type="password"
            autoComplete={rascunho.modo === "login" ? "current-password" : "new-password"}
            value={senha}
            aria-invalid={Boolean(erros.password)}
            aria-describedby={erros.password ? "auth-password-erro" : undefined}
            onChange={(evento) => setSenha(evento.target.value)}
            data-od-id="checkout-password"
          />
          {erros.password && (
            <p id="auth-password-erro" className="inline-error" role="alert">
              {erros.password}
            </p>
          )}
        </div>
        </div>

        <h2 className="section-title">Propriedade e retirada</h2>

        <div className="field">
          <label htmlFor="checkout-property">Propriedade</label>
          <select
            id="checkout-property"
            value={rascunho.idPropriedade}
            aria-invalid={Boolean(erros.propertyId) && propriedades.length > 0}
            aria-describedby={erros.propertyId ? "checkout-property-erro" : undefined}
            onChange={(evento) => atualizar({ idPropriedade: evento.target.value })}
            data-od-id="checkout-property"
          >
            <option value="">Selecione…</option>
            {propriedades.map((propriedade) => (
              <option key={propriedade.id} value={propriedade.id}>
                {propriedade.nome}
              </option>
            ))}
          </select>
          {propriedades.length === 0 && (
            <input
              id="checkout-property-nome"
              type="text"
              placeholder="Nome da propriedade"
              value={rascunho.nomePropriedade}
              aria-invalid={Boolean(erros.propertyId)}
              aria-describedby={erros.propertyId ? "checkout-property-erro" : undefined}
              onChange={(evento) => atualizar({ nomePropriedade: evento.target.value })}
              data-od-id="checkout-property-nome"
            />
          )}
          {erros.propertyId && (
            <p id="checkout-property-erro" className="inline-error" role="alert">
              {erros.propertyId}
            </p>
          )}
        </div>

        <div className="field">
          <label htmlFor="checkout-pickup">Preferência de retirada</label>
          <select
            id="checkout-pickup"
            value={rascunho.preferenciaRetirada}
            aria-invalid={Boolean(erros.pickupId)}
            aria-describedby={erros.pickupId ? "checkout-pickup-erro" : undefined}
            onChange={(evento) => atualizar({ preferenciaRetirada: evento.target.value })}
            data-od-id="checkout-pickup"
          >
            <option value="">Selecione…</option>
            {OPCOES_RETIRADA.map((opcao) => (
              <option key={opcao.id} value={opcao.id}>
                {opcao.label}
              </option>
            ))}
          </select>
          {erros.pickupId && (
            <p id="checkout-pickup-erro" className="inline-error" role="alert">
              {erros.pickupId}
            </p>
          )}
        </div>

        {erros.form && (
          <p className="inline-error" role="alert">
            {erros.form}
          </p>
        )}

        <div className="summary-panel" data-od-id="checkout-summary">
          {carrinho.itens.map((linha) => (
            <div key={linha.idProduto} className="summary-row">
              <span>
                {linha.quantidade}× {linha.nome}
              </span>
              <span className="mono">{formatarDinheiro(linha.totalLinha)}</span>
            </div>
          ))}
          <div className="summary-row total">
            <span>Total</span>
            <span className="mono">{formatarDinheiro(carrinho.total)}</span>
          </div>
        </div>
        </form>
      </main>
      <footer className="sticky-footer" data-od-id="checkout-footer">
        <button
          type="submit"
          form="checkout-form"
          className="btn btn-primary btn-block"
          data-od-id="confirm-order-btn"
          disabled={!checkoutDisponivel}
          aria-busy={enviando}
        >
          {enviando ? "Confirmando…" : "Confirmar pedido"}
        </button>
      </footer>
    </>
  );
}
