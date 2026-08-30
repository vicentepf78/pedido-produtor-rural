import { createContext, useContext, type ReactNode, useCallback, useEffect, useState } from "react";
import { obterCarrinho, type VisaoCarrinho } from "../features/cart/api";

const vazio: VisaoCarrinho = { itens: [], total: "0.00" };

type ContextoCarrinho = {
  carrinho: VisaoCarrinho;
  confirmar: (proximo: VisaoCarrinho) => void;
  recarregar: () => Promise<void>;
};

const Contexto = createContext<ContextoCarrinho | undefined>(undefined);

export function ProvedorCarrinho({ children }: { children: ReactNode }) {
  const [carrinho, setCarrinho] = useState<VisaoCarrinho>(vazio);

  const recarregar = useCallback(async () => {
    try {
      setCarrinho(await obterCarrinho());
    } catch {
      setCarrinho((atual) => atual);
    }
  }, []);

  useEffect(() => {
    void recarregar();
  }, [recarregar]);

  return (
    <Contexto.Provider value={{ carrinho, confirmar: setCarrinho, recarregar }}>
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
