package br.agriplataforma.backoffice.application;

public class ExcecaoRetaguarda extends RuntimeException {

	private final String codigo;

	public ExcecaoRetaguarda(String codigo, String mensagem) {
		super(mensagem);
		this.codigo = codigo;
	}

	public String codigo() {
		return codigo;
	}
}
