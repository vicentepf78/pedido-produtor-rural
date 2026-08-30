package br.agriplataforma.catalog.application;

import java.math.BigDecimal;
import java.util.UUID;

public record ResumoProduto(
		UUID id,
		String nome,
		String categoria,
		String descricaoCurta,
		String unidade,
		BigDecimal precoUnitario,
		boolean disponivel,
		String urlImagem) {}
