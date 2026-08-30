package br.agriplataforma.order.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ResumoPedido(UUID idPedido, String situacao, String confirmacao, BigDecimal total, Instant criadoEm) {}
