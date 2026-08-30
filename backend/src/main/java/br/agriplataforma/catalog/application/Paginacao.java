package br.agriplataforma.catalog.application;

public record Paginacao(int pagina, int tamanhoPagina) {

	public static Paginacao de(Integer pagina, Integer tamanhoPagina) {
		int paginaEfetiva = pagina == null || pagina < 1 ? 1 : pagina;
		int tamanhoEfetivo = tamanhoPagina == null || tamanhoPagina < 1 ? 24 : Math.min(tamanhoPagina, 100);
		return new Paginacao(paginaEfetiva, tamanhoEfetivo);
	}
}
