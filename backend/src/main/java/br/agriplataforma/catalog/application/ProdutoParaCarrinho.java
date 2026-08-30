package br.agriplataforma.catalog.application;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoParaCarrinho(UUID id, String nome, String unidade, BigDecimal precoUnitario) {}
