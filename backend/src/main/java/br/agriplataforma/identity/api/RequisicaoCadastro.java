package br.agriplataforma.identity.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequisicaoCadastro(
		@NotBlank String nome, @NotBlank @Email String email, @NotBlank String senha) {

	@Override
	public String toString() {
		return "RequisicaoCadastro[nome=%s, email=%s]".formatted(nome, email);
	}
}
