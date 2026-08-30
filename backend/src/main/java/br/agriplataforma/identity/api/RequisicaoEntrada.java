package br.agriplataforma.identity.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequisicaoEntrada(@NotBlank @Email String email, @NotBlank String senha) {

	@Override
	public String toString() {
		return "RequisicaoEntrada[email=%s]".formatted(email);
	}
}
