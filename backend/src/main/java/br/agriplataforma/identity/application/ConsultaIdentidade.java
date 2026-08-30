package br.agriplataforma.identity.application;

import java.util.Optional;

public interface ConsultaIdentidade {

	Optional<UsuarioAutenticado> usuarioAtual();

	UsuarioAutenticado exigirAutenticado();
}
