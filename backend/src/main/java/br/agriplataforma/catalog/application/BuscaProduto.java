package br.agriplataforma.catalog.application;

public record BuscaProduto(String consulta, String categoria) {

	public BuscaProduto(String consulta) {
		this(consulta, null);
	}
}
