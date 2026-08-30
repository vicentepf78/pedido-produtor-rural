package br.agriplataforma.order.application;

import java.util.UUID;

public record CriarPedido(
		String chaveProprietario, UUID idPropriedade, String preferenciaRetirada, String chaveIdempotencia) {}
