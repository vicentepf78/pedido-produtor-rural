package br.agriplataforma.order.application;

import java.util.UUID;

public record ConfirmacaoPedido(UUID idPedido, String situacao, String confirmacao, String mensagem) {}
