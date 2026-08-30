package br.agriplataforma.identity.application;

public record ComandoEntrada(String email, String senha) {

	@Override
	public String toString() {
		return "ComandoEntrada[email=%s]".formatted(email);
	}
}
