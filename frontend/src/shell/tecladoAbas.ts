import type { KeyboardEvent } from "react";

export function aoTeclaAba(
  evento: KeyboardEvent<HTMLButtonElement>,
  opcoes: string[],
  atual: string,
  escolher: (proximo: string) => void,
) {
  const indice = opcoes.indexOf(atual);
  if (indice < 0) {
    return;
  }
  if (evento.key === "ArrowRight" || evento.key === "ArrowDown") {
    evento.preventDefault();
    escolher(opcoes[(indice + 1) % opcoes.length]);
  } else if (evento.key === "ArrowLeft" || evento.key === "ArrowUp") {
    evento.preventDefault();
    escolher(opcoes[(indice - 1 + opcoes.length) % opcoes.length]);
  } else if (evento.key === "Home") {
    evento.preventDefault();
    escolher(opcoes[0]);
  } else if (evento.key === "End") {
    evento.preventDefault();
    escolher(opcoes[opcoes.length - 1]);
  }
}
