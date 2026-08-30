import { TAMANHOS_PAGINA, type TamanhoPagina } from "./categorias";

type Props = {
  valor: TamanhoPagina;
  onMudar: (tamanho: TamanhoPagina) => void;
};

export function ControleTamanhoLista({ valor, onMudar }: Props) {
  return (
    <div className="page-size" data-od-id="page-size-control">
      <label htmlFor="page-size">Itens por página</label>
      <select
        id="page-size"
        value={valor}
        onChange={(evento) => onMudar(Number(evento.target.value) as TamanhoPagina)}
      >
        {TAMANHOS_PAGINA.map((tamanho) => (
          <option key={tamanho} value={tamanho}>
            {tamanho}
          </option>
        ))}
      </select>
    </div>
  );
}
