package br.agriplataforma.identity.application;

public record ComandoCadastro(String nome, String email, String senha) {

	@Override
	public String toString() {
		return "ComandoCadastro[nome=%s, email=%s]".formatted(nome, email);
	}
}
