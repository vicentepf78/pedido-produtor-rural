package br.agriplataforma.order.application;

import java.util.UUID;

public interface ConsultaPedidoRetaguarda {

	PaginaPedido<ResumoPedidoRetaguarda> listarPorTenant(UUID idTenant, int pagina, int tamanhoPagina);

	VisaoPedidoRetaguarda obterPorTenant(UUID idTenant, UUID idPedido);
}
