package br.agriplataforma.identity.application;

public class ExcecaoAutenticacao extends RuntimeException {

	private final String codigo;

	public ExcecaoAutenticacao(String codigo, String mensagem) {
		super(mensagem);
		this.codigo = codigo;
	}

	public String codigo() {
		return codigo;
	}
}
