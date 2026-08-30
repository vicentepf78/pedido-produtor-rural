package br.agriplataforma.catalog.application;

public class ExcecaoCatalogo extends RuntimeException {

	private final String codigo;

	public ExcecaoCatalogo(String codigo, String mensagem) {
		super(mensagem);
		this.codigo = codigo;
	}

	public String codigo() {
		return codigo;
	}
}
