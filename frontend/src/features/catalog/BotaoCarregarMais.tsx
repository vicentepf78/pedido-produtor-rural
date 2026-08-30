type Props = {
  visivel: boolean;
  carregando: boolean;
  onCarregar: () => void;
};

export function BotaoCarregarMais({ visivel, carregando, onCarregar }: Props) {
  if (!visivel) {
    return null;
  }
  return (
    <div className="load-more-wrap">
      <button
        type="button"
        className="btn btn-ghost"
        data-od-id="btn-carregar-mais"
        disabled={carregando}
        onClick={onCarregar}
      >
        {carregando ? "Carregando…" : "Carregar mais"}
      </button>
    </div>
  );
}
