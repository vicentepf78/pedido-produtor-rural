package br.agriplataforma.backoffice.application;

import br.agriplataforma.order.application.PaginaPedido;
import br.agriplataforma.order.application.ResumoPedidoRetaguarda;
import br.agriplataforma.order.application.VisaoPedidoRetaguarda;
import java.util.UUID;

public interface ConsultaRetaguarda {

	PaginaPedido<ResumoPedidoRetaguarda> listar(int pagina, int tamanhoPagina);

	VisaoPedidoRetaguarda obter(UUID idPedido);
}
