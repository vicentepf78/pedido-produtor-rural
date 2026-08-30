package br.agriplataforma.identity.application;

public interface ComandoIdentidade {

	UsuarioAutenticado cadastrar(ComandoCadastro comando);

	UsuarioAutenticado entrar(ComandoEntrada comando);
}
