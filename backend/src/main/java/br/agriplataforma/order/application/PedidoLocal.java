package br.agriplataforma.order.application;

import java.util.UUID;

public record PedidoLocal(UUID idPedido, UUID idTenant) {}
