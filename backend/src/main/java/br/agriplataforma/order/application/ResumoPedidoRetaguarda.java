package br.agriplataforma.order.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ResumoPedidoRetaguarda(
		UUID idPedido,
		String nomeProdutor,
		BigDecimal total,
		String situacao,
		String confirmacao,
		Instant criadoEm) {}
