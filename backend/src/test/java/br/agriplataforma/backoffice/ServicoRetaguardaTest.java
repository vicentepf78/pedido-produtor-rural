package br.agriplataforma.backoffice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.agriplataforma.backoffice.application.ExcecaoRetaguarda;
import br.agriplataforma.backoffice.application.ServicoRetaguarda;
import br.agriplataforma.identity.application.ConsultaIdentidade;
import br.agriplataforma.identity.application.Papel;
import br.agriplataforma.identity.application.UsuarioAutenticado;
import br.agriplataforma.order.application.ConsultaPedidoRetaguarda;
import br.agriplataforma.order.application.PaginaPedido;
import br.agriplataforma.order.application.ResumoPedidoRetaguarda;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicoRetaguardaTest {

	static final UUID TENANT = UUID.fromString("11111111-1111-1111-1111-111111111111");
	static final UUID OPERADOR = UUID.fromString("22222222-2222-2222-2222-222222222222");
	static final UUID PRODUTOR = UUID.fromString("33333333-3333-3333-3333-333333333333");
	static final UUID PEDIDO = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

	@Mock
	ConsultaIdentidade consultaIdentidade;

	@Mock
	ConsultaPedidoRetaguarda consultaPedidoRetaguarda;

	ServicoRetaguarda servico;

	@BeforeEach
	void preparar() {
		servico = new ServicoRetaguarda(consultaIdentidade, consultaPedidoRetaguarda);
	}

	@Test
	void ut024_resumoDoOperadorIncluiPedidoProdutorTotalCriacaoEConfirmacaoAceita() {
		when(consultaIdentidade.exigirAutenticado())
				.thenReturn(new UsuarioAutenticado(
						OPERADOR, TENANT, "Operador Demonstracao", "operador.revenda@example.com", Papel.OPERADOR_REVENDA));
		Instant criado = Instant.parse("2026-08-30T13:55:00Z");
		when(consultaPedidoRetaguarda.listarPorTenant(eq(TENANT), eq(1), eq(25)))
				.thenReturn(new PaginaPedido<>(
						List.of(new ResumoPedidoRetaguarda(
								PEDIDO,
								"Produtor Alfa",
								new BigDecimal("1240.00"),
								"RECEBIDO",
								"ACEITA",
								criado)),
						1,
						25,
						1));

		PaginaPedido<ResumoPedidoRetaguarda> pagina = servico.listar(1, 25);

		assertThat(pagina.itens()).hasSize(1);
		ResumoPedidoRetaguarda resumo = pagina.itens().getFirst();
		assertThat(resumo.idPedido()).isEqualTo(PEDIDO);
		assertThat(resumo.nomeProdutor()).isEqualTo("Produtor Alfa");
		assertThat(resumo.total()).isEqualByComparingTo("1240.00");
		assertThat(resumo.criadoEm()).isEqualTo(criado);
		assertThat(resumo.confirmacao()).isEqualTo("ACEITA");
		assertThat(resumo.situacao()).isEqualTo("RECEBIDO");
	}

	@Test
	void ut025_papelProdutorNaoObtemListaDaRetaguarda() {
		when(consultaIdentidade.exigirAutenticado())
				.thenReturn(new UsuarioAutenticado(
						PRODUTOR, TENANT, "Produtor Alfa", "produtor.alfa@example.com", Papel.PRODUTOR));

		assertThatThrownBy(() -> servico.listar(1, 25))
				.isInstanceOf(ExcecaoRetaguarda.class)
				.satisfies(excecao -> {
					ExcecaoRetaguarda retaguarda = (ExcecaoRetaguarda) excecao;
					assertThat(retaguarda.codigo()).isEqualTo("ACESSO_NEGADO");
					assertThat(retaguarda.getMessage())
							.isEqualTo("Somente o operador da revenda pode inspecionar os pedidos da retaguarda.");
				});
		verify(consultaPedidoRetaguarda, never()).listarPorTenant(any(), anyInt(), anyInt());
	}
}
