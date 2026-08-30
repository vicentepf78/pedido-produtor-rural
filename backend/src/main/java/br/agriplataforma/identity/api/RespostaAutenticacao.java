package br.agriplataforma.identity.api;

import java.util.List;
import java.util.UUID;

public record RespostaAutenticacao(UUID idUsuario, String nome, List<String> papeis) {}
