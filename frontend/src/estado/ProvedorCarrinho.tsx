import { createContext, useContext, type ReactNode, useCallback, useEffect, useState } from "react";
import { obterCarrinho, type VisaoCarrinho } from "../features/cart/api";

const vazio: VisaoCarrinho = { itens: [], total: "0.00" };

type ContextoCarrinho = {
  carrinho: VisaoCarrinho;
  carregando: boolean;
  erroRestore: string | null;
  confirmar: (proximo: VisaoCarrinho) => void;
  recarregar: () => Promise<void>;
};

const Contexto = createContext<ContextoCarrinho | undefined>(undefined);

export function ProvedorCarrinho({ children }: { children: ReactNode }) {
  const [carrinho, setCarrinho] = useState<VisaoCarrinho>(vazio);
  const [carregando, setCarregando] = useState(true);
  const [erroRestore, setErroRestore] = useState<string | null>(null);

  const recarregar = useCallback(async () => {
    setCarregando(true);
    setErroRestore(null);
    try {
      setCarrinho(await obterCarrinho());
    } catch {
      setErroRestore("Não foi possível restaurar o carrinho. Mantemos o último estado confirmado.");
    } finally {
      setCarregando(false);
    }
  }, []);

  useEffect(() => {
    void recarregar();
  }, [recarregar]);

  return (
    <Contexto.Provider value={{ carrinho, carregando, erroRestore, confirmar: setCarrinho, recarregar }}>
      {children}
    </Contexto.Provider>
  );
}

export function useCarrinho() {
  const ctx = useContext(Contexto);
  if (!ctx) {
    throw new Error("ProvedorCarrinho ausente");
  }
  return ctx;
}
