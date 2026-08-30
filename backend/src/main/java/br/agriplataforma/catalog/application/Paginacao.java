package br.agriplataforma.catalog.application;

import java.util.Set;

public record Paginacao(int pagina, int tamanhoPagina) {

	public static final int PADRAO = 10;

	public static Set<Integer> tamanhosPermitidos() {
		return Set.of(10, 15, 30, 50);
	}

	public static Paginacao de(Integer pagina, Integer tamanhoPagina) {
		int paginaEfetiva = pagina == null || pagina < 1 ? 1 : pagina;
		int tamanhoEfetivo = tamanhoPagina == null ? PADRAO : tamanhoPagina;
		exigirTamanhoPermitido(tamanhoEfetivo);
		return new Paginacao(paginaEfetiva, tamanhoEfetivo);
	}

	static void exigirTamanhoPermitido(int tamanhoPagina) {
		if (!tamanhosPermitidos().contains(tamanhoPagina)) {
			throw new ExcecaoCatalogo(
					"TAMANHO_PAGINA_INVALIDO", "Escolha 10, 15, 30 ou 50 produtos por página.");
		}
	}
}
