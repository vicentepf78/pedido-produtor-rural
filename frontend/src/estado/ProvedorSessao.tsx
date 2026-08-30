import { createContext, useCallback, useContext, useEffect, useRef, useState, type ReactNode } from "react";
import { obterSessao, sair, type RespostaAutenticacao, type Sessao } from "../features/identity/api";

type ContextoSessao = {
  sessao: Sessao;
  pronta: boolean;
  aplicarAutenticacao: (resposta: RespostaAutenticacao) => void;
  encerrar: () => Promise<void>;
  recarregar: () => Promise<void>;
};

const Contexto = createContext<ContextoSessao | undefined>(undefined);

export function ProvedorSessao({ children }: { children: ReactNode }) {
  const [sessao, setSessao] = useState<Sessao>({ autenticado: false });
  const [pronta, setPronta] = useState(false);
  const saindo = useRef(false);

  const recarregar = useCallback(async () => {
    try {
      setSessao(await obterSessao());
    } catch {
      setSessao({ autenticado: false });
    } finally {
      setPronta(true);
    }
  }, []);

  useEffect(() => {
    void recarregar();
  }, [recarregar]);

  function aplicarAutenticacao(resposta: RespostaAutenticacao) {
    setSessao({
      autenticado: true,
      idUsuario: resposta.idUsuario,
      nome: resposta.nome,
      email: resposta.email,
      papeis: resposta.papeis,
    });
  }

  async function encerrar() {
    if (saindo.current) {
      return;
    }
    saindo.current = true;
    try {
      await sair();
      setSessao({ autenticado: false });
    } finally {
      saindo.current = false;
    }
  }

  return (
    <Contexto.Provider value={{ sessao, pronta, aplicarAutenticacao, encerrar, recarregar }}>
      {children}
    </Contexto.Provider>
  );
}

export function useSessao() {
  const ctx = useContext(Contexto);
  if (!ctx) {
    throw new Error("ProvedorSessao ausente");
  }
  return ctx;
}
