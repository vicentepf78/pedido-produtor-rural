package br.agriplataforma.order.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record VisaoPedidoRetaguarda(
		UUID idPedido,
		String nomeProdutor,
		String situacao,
		String confirmacao,
		String nomePropriedade,
		String preferenciaRetirada,
		BigDecimal total,
		Instant criadoEm,
		List<VisaoPedido.ItemVisaoPedido> itens) {}
