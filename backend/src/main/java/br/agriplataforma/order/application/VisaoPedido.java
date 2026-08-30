package br.agriplataforma.order.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record VisaoPedido(
		UUID idPedido,
		String situacao,
		String confirmacao,
		String nomePropriedade,
		String preferenciaRetirada,
		BigDecimal total,
		Instant criadoEm,
		List<ItemVisaoPedido> itens) {

	public record ItemVisaoPedido(
			String nome, String unidade, int quantidade, BigDecimal precoUnitario, BigDecimal totalLinha) {}
}
