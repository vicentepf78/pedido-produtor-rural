package br.agriplataforma.order.application;

import java.util.UUID;

public interface ComandoPedido {

	ConfirmacaoPedido criar(CriarPedido comando);

	VisaoPedido obterParaProdutor(UUID idUsuario, UUID idPedido);

	PaginaPedido<ResumoPedido> listarParaProdutor(UUID idUsuario, int pagina, int tamanhoPagina);
}
