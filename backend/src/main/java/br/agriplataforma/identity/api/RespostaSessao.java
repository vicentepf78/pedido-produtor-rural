package br.agriplataforma.identity.api;

import br.agriplataforma.identity.application.UsuarioAutenticado;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespostaSessao(
		boolean autenticado, UUID idUsuario, String nome, String email, List<String> papeis) {

	public static RespostaSessao convidada() {
		return new RespostaSessao(false, null, null, null, null);
	}

	public static RespostaSessao autenticada(UsuarioAutenticado usuario) {
		return new RespostaSessao(
				true, usuario.id(), usuario.nome(), usuario.email(), List.of(usuario.papel().name()));
	}
}
