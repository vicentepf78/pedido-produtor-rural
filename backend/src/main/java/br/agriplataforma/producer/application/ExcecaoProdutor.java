package br.agriplataforma.producer.application;

public class ExcecaoProdutor extends RuntimeException {

	private final String codigo;

	public ExcecaoProdutor(String codigo, String mensagem) {
		super(mensagem);
		this.codigo = codigo;
	}

	public String codigo() {
		return codigo;
	}
}
