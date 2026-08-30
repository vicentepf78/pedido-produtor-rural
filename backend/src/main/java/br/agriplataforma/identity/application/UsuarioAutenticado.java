package br.agriplataforma.identity.application;

import java.io.Serializable;
import java.util.UUID;

public record UsuarioAutenticado(UUID id, UUID idTenant, String nome, String email, Papel papel)
		implements Serializable {}
