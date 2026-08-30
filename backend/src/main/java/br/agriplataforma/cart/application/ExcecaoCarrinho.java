package br.agriplataforma.cart.application;

public class ExcecaoCarrinho extends RuntimeException {

	private final String codigo;

	public ExcecaoCarrinho(String codigo, String mensagem) {
		super(mensagem);
		this.codigo = codigo;
	}

	public String codigo() {
		return codigo;
	}
}
