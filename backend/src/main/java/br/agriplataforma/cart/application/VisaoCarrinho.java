package br.agriplataforma.cart.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record VisaoCarrinho(List<ItemVisaoCarrinho> itens, BigDecimal total) {

	public record ItemVisaoCarrinho(
			UUID idProduto, String nome, int quantidade, BigDecimal precoUnitario, BigDecimal totalLinha) {}
}
