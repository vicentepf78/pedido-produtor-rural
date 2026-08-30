package br.agriplataforma.integration.infrastructure;

import br.agriplataforma.order.application.ConfirmacaoErp;
import br.agriplataforma.order.application.GatewayErp;
import br.agriplataforma.order.application.PedidoLocal;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "agriplataforma.gatewayErp", havingValue = "simulado", matchIfMissing = true)
public class GatewayErpSimulado implements GatewayErp {

	@Override
	public ConfirmacaoErp aceitar(PedidoLocal pedido) {
		return new ConfirmacaoErp("ACEITA");
	}
}
