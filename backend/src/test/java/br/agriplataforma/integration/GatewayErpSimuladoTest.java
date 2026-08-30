package br.agriplataforma.integration;

import static org.assertj.core.api.Assertions.assertThat;

import br.agriplataforma.integration.infrastructure.GatewayErpSimulado;
import br.agriplataforma.order.application.ConfirmacaoErp;
import br.agriplataforma.order.application.PedidoLocal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GatewayErpSimuladoTest {

	@Test
	void ut058_aceitaPedidoLocalSemMutarRegistro() {
		GatewayErpSimulado gateway = new GatewayErpSimulado();
		UUID idPedido = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
		UUID idTenant = UUID.fromString("11111111-1111-1111-1111-111111111111");

		ConfirmacaoErp resultado = gateway.aceitar(new PedidoLocal(idPedido, idTenant));

		assertThat(resultado.codigo()).isEqualTo("ACEITA");
	}
}
