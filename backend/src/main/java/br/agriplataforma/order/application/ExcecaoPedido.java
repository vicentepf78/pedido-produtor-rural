package br.agriplataforma.order.application;

public class ExcecaoPedido extends RuntimeException {

	private final String codigo;

	public ExcecaoPedido(String codigo, String mensagem) {
		super(mensagem);
		this.codigo = codigo;
	}

	public String codigo() {
		return codigo;
	}
}
