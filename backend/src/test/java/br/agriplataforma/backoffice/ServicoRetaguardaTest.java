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
import br.agriplataforma.order.application.VisaoPedido;
import br.agriplataforma.order.application.VisaoPedidoRetaguarda;
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
	void ut065_resumoDoOperadorIncluiPedidoProdutorTotalCriacaoEConfirmacaoAceita() {
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
	void ut066_papelProdutorNaoObtemListaDaRetaguarda() {
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

	@Test
	void ut067_semSessaoNaoLista() {
		when(consultaIdentidade.exigirAutenticado())
				.thenThrow(new br.agriplataforma.identity.application.ExcecaoAutenticacao(
						"NAO_AUTENTICADO", "Entre ou crie uma conta para continuar."));
		assertThatThrownBy(() -> servico.listar(1, 25))
				.isInstanceOf(br.agriplataforma.identity.application.ExcecaoAutenticacao.class);
	}

	@Test
	void ut068_relerDetalheNaoMudaConfirmacao() {
		operadorAutenticado();
		Instant criado = Instant.parse("2026-08-30T13:55:00Z");
		when(consultaPedidoRetaguarda.obterPorTenant(eq(TENANT), eq(PEDIDO)))
				.thenReturn(new VisaoPedidoRetaguarda(
						PEDIDO,
						"Produtor Alfa",
						"RECEBIDO",
						"ACEITA",
						"Fazenda Norte",
						"DEPOSITO_PRINCIPAL",
						new BigDecimal("1240.00"),
						criado,
						List.of()));
		var primeira = servico.obter(PEDIDO);
		var segunda = servico.obter(PEDIDO);
		assertThat(segunda.confirmacao()).isEqualTo(primeira.confirmacao());
		assertThat(segunda.total()).isEqualByComparingTo(primeira.total());
	}

	@Test
	void ut069_outroTenantNaoAparece() {
		operadorAutenticado();
		when(consultaPedidoRetaguarda.listarPorTenant(eq(TENANT), eq(1), eq(25)))
				.thenReturn(new PaginaPedido<>(List.of(), 1, 25, 0));
		assertThat(servico.listar(1, 25).itens()).isEmpty();
	}

	@Test
	void ut070_obterDevolveSnapshot() {
		operadorAutenticado();
		when(consultaPedidoRetaguarda.obterPorTenant(eq(TENANT), eq(PEDIDO)))
				.thenReturn(new VisaoPedidoRetaguarda(
						PEDIDO,
						"Produtor Alfa",
						"RECEBIDO",
						"ACEITA",
						"Fazenda Norte",
						"DEPOSITO_PRINCIPAL",
						new BigDecimal("1240.00"),
						Instant.parse("2026-08-30T13:55:00Z"),
						List.of(new VisaoPedido.ItemVisaoPedido(
								"Semente de milho Aurora 20 kg",
								"Saco",
								2,
								new BigDecimal("620.00"),
								new BigDecimal("1240.00")))));
		var visao = servico.obter(PEDIDO);
		assertThat(visao.itens()).hasSize(1);
		assertThat(visao.nomePropriedade()).isEqualTo("Fazenda Norte");
		assertThat(visao.preferenciaRetirada()).isEqualTo("DEPOSITO_PRINCIPAL");
	}

	@Test
	void ut074_listaVazia() {
		operadorAutenticado();
		when(consultaPedidoRetaguarda.listarPorTenant(eq(TENANT), eq(1), eq(25)))
				.thenReturn(new PaginaPedido<>(List.of(), 1, 25, 0));
		var pagina = servico.listar(1, 25);
		assertThat(pagina.itens()).isEmpty();
		assertThat(pagina.total()).isZero();
	}

	private void operadorAutenticado() {
		when(consultaIdentidade.exigirAutenticado())
				.thenReturn(new UsuarioAutenticado(
						OPERADOR, TENANT, "Operador Demonstracao", "operador.revenda@example.com", Papel.OPERADOR_REVENDA));
	}
}
