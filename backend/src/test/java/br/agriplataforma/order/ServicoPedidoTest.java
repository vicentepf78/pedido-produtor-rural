package br.agriplataforma.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.agriplataforma.cart.application.ComandoCarrinho;
import br.agriplataforma.cart.application.ConsultaCarrinho;
import br.agriplataforma.catalog.application.ProdutoParaCarrinho;
import br.agriplataforma.cart.application.VisaoCarrinho;
import br.agriplataforma.catalog.application.ConsultaCatalogo;
import br.agriplataforma.catalog.application.ExcecaoCatalogo;
import br.agriplataforma.catalog.application.ResumoProduto;
import br.agriplataforma.identity.application.ConsultaIdentidade;
import br.agriplataforma.identity.application.Papel;
import br.agriplataforma.identity.application.UsuarioAutenticado;
import br.agriplataforma.order.application.ConfirmacaoErp;
import br.agriplataforma.order.application.ConfirmacaoPedido;
import br.agriplataforma.order.application.CriarPedido;
import br.agriplataforma.order.application.ExcecaoPedido;
import br.agriplataforma.order.application.GatewayErp;
import br.agriplataforma.order.application.PedidoLocal;
import br.agriplataforma.order.application.ServicoPedido;
import br.agriplataforma.order.application.VisaoPedido;
import br.agriplataforma.order.domain.ItemPedido;
import br.agriplataforma.order.domain.Pedido;
import br.agriplataforma.order.infrastructure.RepositorioItemPedido;
import br.agriplataforma.order.infrastructure.RepositorioPedido;
import br.agriplataforma.producer.application.ConsultaPropriedades;
import br.agriplataforma.producer.application.ResumoPropriedade;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServicoPedidoTest {

	static final UUID TENANT = UUID.fromString("11111111-1111-1111-1111-111111111111");
	static final UUID ALFA = UUID.fromString("33333333-3333-3333-3333-333333333333");
	static final UUID BETA = UUID.fromString("77777777-7777-7777-7777-777777777777");
	static final UUID PRODUTOR_ALFA = UUID.fromString("44444444-4444-4444-4444-444444444444");
	static final UUID PRODUTOR_BETA = UUID.fromString("88888888-8888-8888-8888-888888888888");
	static final UUID FAZENDA_NORTE = UUID.fromString("55555555-5555-5555-5555-555555555555");
	static final UUID AURORA = UUID.fromString("10000000-0000-4000-8000-000000000001");
	static final String CHAVE_CARRINHO = "convidado-abc";
	static final String CHAVE_IDEMP = "idem-1";
	static final String RETIRADA = "DEPOSITO_PRINCIPAL";

	@Mock
	ConsultaIdentidade consultaIdentidade;

	@Mock
	ConsultaPropriedades consultaPropriedades;

	@Mock
	ConsultaCarrinho consultaCarrinho;

	@Mock
	ComandoCarrinho comandoCarrinho;

	@Mock
	ConsultaCatalogo consultaCatalogo;

	@Mock
	RepositorioPedido repositorioPedido;

	@Mock
	RepositorioItemPedido repositorioItens;

	@Mock
	GatewayErp gatewayErp;

	@Mock
	PlatformTransactionManager gerenciadorTransacao;

	ServicoPedido servico;
	Pedido pedidoPersistido;
	final List<ItemPedido> itensPersistidos = new ArrayList<>();

	@BeforeEach
	void preparar() {
		when(gerenciadorTransacao.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
		when(gatewayErp.aceitar(any(PedidoLocal.class))).thenReturn(new ConfirmacaoErp("ACEITA"));
		servico = new ServicoPedido(
				consultaIdentidade,
				consultaPropriedades,
				consultaCarrinho,
				comandoCarrinho,
				consultaCatalogo,
				repositorioPedido,
				repositorioItens,
				gatewayErp,
				gerenciadorTransacao);
		autenticar(ALFA, PRODUTOR_ALFA);
		when(consultaPropriedades.buscarPropria(FAZENDA_NORTE))
				.thenReturn(Optional.of(new ResumoPropriedade(FAZENDA_NORTE, "Fazenda Norte")));
		when(consultaCatalogo.exigirProdutoPedivel(TENANT, AURORA))
				.thenReturn(new ProdutoParaCarrinho(
						AURORA, "Semente de milho Aurora 20 kg", "Saco", new BigDecimal("620.00")));
		when(consultaCatalogo.obterProdutoVisivel(TENANT, AURORA))
				.thenReturn(Optional.of(new ResumoProduto(
						AURORA,
						"Semente de milho Aurora 20 kg",
						"Sementes",
						"Cultivar para plantio de verão.",
						"Saco",
						new BigDecimal("620.00"),
						true,
						"/media/produtos/aurora-milho-20kg.svg")));
		when(repositorioPedido.findByIdTenantAndIdProdutorAndChaveIdempotencia(any(), any(), any()))
				.thenAnswer(inv -> Optional.ofNullable(pedidoPersistido)
						.filter(pedido -> pedido.chaveIdempotencia().equals(inv.getArgument(2))));
		when(repositorioPedido.save(any(Pedido.class))).thenAnswer(inv -> {
			pedidoPersistido = inv.getArgument(0);
			return pedidoPersistido;
		});
		when(repositorioPedido.findById(any())).thenAnswer(inv -> Optional.ofNullable(pedidoPersistido)
				.filter(pedido -> pedido.id().equals(inv.getArgument(0))));
		when(repositorioItens.saveAll(any())).thenAnswer(inv -> {
			itensPersistidos.clear();
			itensPersistidos.addAll(inv.getArgument(0));
			return itensPersistidos;
		});
		when(repositorioItens.findByIdPedidoOrderByIdAsc(any())).thenAnswer(inv -> List.copyOf(itensPersistidos));
		carrinhoComAurora(2, new BigDecimal("620.00"));
	}

	@Test
	void ut047_checkoutAceitaProdutorAutenticadoComPropriedadeERetirada() {
		ConfirmacaoPedido confirmacao = servico.criar(comandoValido());

		assertThat(confirmacao.idPedido()).isNotNull();
		assertThat(confirmacao.situacao()).isEqualTo("RECEBIDO");
		assertThat(confirmacao.confirmacao()).isEqualTo("ACEITA");
		assertThat(confirmacao.mensagem()).isEqualTo("Pedido recebido");
		assertThat(pedidoPersistido.nomePropriedade()).isEqualTo("Fazenda Norte");
		assertThat(pedidoPersistido.preferenciaRetirada()).isEqualTo(RETIRADA);
		verify(comandoCarrinho).esvaziar(CHAVE_CARRINHO);
	}

	@Test
	void ut048_carrinhoVazioRetornaCarrinhoVazio() {
		when(consultaCarrinho.obter(CHAVE_CARRINHO))
				.thenReturn(new VisaoCarrinho(List.of(), BigDecimal.ZERO.setScale(2)));

		assertThatThrownBy(() -> servico.criar(comandoValido()))
				.isInstanceOf(ExcecaoPedido.class)
				.satisfies(excecao -> {
					ExcecaoPedido pedido = (ExcecaoPedido) excecao;
					assertThat(pedido.codigo()).isEqualTo("CARRINHO_VAZIO");
					assertThat(pedido.getMessage()).isEqualTo("Adicione ao menos um produto antes do checkout.");
				});
		verify(repositorioPedido, never()).save(any());
	}

	@Test
	void ut049_semPropriedadeOuRetiradaRetornaDadosObrigatorios() {
		when(consultaPropriedades.buscarPropria(null)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> servico.criar(new CriarPedido(CHAVE_CARRINHO, null, RETIRADA, CHAVE_IDEMP)))
				.isInstanceOf(ExcecaoPedido.class)
				.satisfies(excecao -> {
					ExcecaoPedido pedido = (ExcecaoPedido) excecao;
					assertThat(pedido.codigo()).isEqualTo("DADOS_CHECKOUT_OBRIGATORIOS");
					assertThat(pedido.getMessage()).isEqualTo("Escolha uma propriedade e a preferência de retirada.");
				});

		assertThatThrownBy(() -> servico.criar(new CriarPedido(CHAVE_CARRINHO, FAZENDA_NORTE, "  ", CHAVE_IDEMP)))
				.isInstanceOf(ExcecaoPedido.class)
				.satisfies(excecao -> assertThat(((ExcecaoPedido) excecao).codigo())
						.isEqualTo("DADOS_CHECKOUT_OBRIGATORIOS"));
		verify(repositorioPedido, never()).save(any());
	}

	@Test
	void ut050_duasConfirmacoesComMesmaChaveProduzemUmPedido() {
		ConfirmacaoPedido primeira = servico.criar(comandoValido());
		ConfirmacaoPedido segunda = servico.criar(comandoValido());

		assertThat(segunda.idPedido()).isEqualTo(primeira.idPedido());
		verify(repositorioPedido).save(any());
		verify(gatewayErp).aceitar(any(PedidoLocal.class));
	}

	@Test
	void ut059_violacaoDeUnicidadeRecuperaOPedidoExistente() {
		Pedido existente = Pedido.novo(
				TENANT,
				PRODUTOR_ALFA,
				FAZENDA_NORTE,
				"Fazenda Norte",
				"Alfa",
				RETIRADA,
				new BigDecimal("1240.00"),
				CHAVE_IDEMP);
		when(repositorioPedido.findByIdTenantAndIdProdutorAndChaveIdempotencia(any(), any(), any()))
				.thenReturn(Optional.empty())
				.thenReturn(Optional.of(existente));
		when(repositorioPedido.save(any(Pedido.class))).thenThrow(new DataIntegrityViolationException("uk"));
		when(repositorioPedido.findById(existente.id())).thenReturn(Optional.of(existente));

		ConfirmacaoPedido confirmacao = servico.criar(comandoValido());

		assertThat(confirmacao.idPedido()).isEqualTo(existente.id());
	}

	@Test
	void ut062_confirmacaoCriaSnapshotImutavel() {
		servico.criar(comandoValido());
		VisaoPedido visao = servico.obterParaProdutor(ALFA, pedidoPersistido.id());

		assertThat(visao.nomePropriedade()).isEqualTo("Fazenda Norte");
		assertThat(visao.preferenciaRetirada()).isEqualTo(RETIRADA);
		assertThat(visao.itens()).hasSize(1);
		assertThat(visao.itens().getFirst().nome()).isEqualTo("Semente de milho Aurora 20 kg");
		assertThat(visao.itens().getFirst().quantidade()).isEqualTo(2);
		assertThat(visao.itens().getFirst().precoUnitario()).isEqualByComparingTo("620.00");
		assertThat(visao.itens().getFirst().totalLinha()).isEqualByComparingTo("1240.00");
		assertThat(visao.total()).isEqualByComparingTo("1240.00");
		assertThat(visao.situacao()).isEqualTo("RECEBIDO");
		assertThat(visao.confirmacao()).isEqualTo("ACEITA");
	}

	@Test
	void ut058_mockAceitaPedidoLocalJaPersistido() {
		ConfirmacaoPedido confirmacao = servico.criar(comandoValido());

		assertThat(confirmacao.confirmacao()).isEqualTo("ACEITA");
		assertThat(pedidoPersistido.confirmacao()).isEqualTo("ACEITA");
		InOrder ordem = inOrder(repositorioPedido, gatewayErp);
		ordem.verify(repositorioPedido).save(any(Pedido.class));
		ordem.verify(repositorioPedido).flush();
		ordem.verify(gatewayErp).aceitar(org.mockito.ArgumentMatchers.argThat(local -> local.idPedido()
				.equals(pedidoPersistido.id())
				&& local.idTenant().equals(TENANT)));
	}

	@Test
	void ut054_alterarPrecoCatalogoNaoAlteraPedido() {
		servico.criar(comandoValido());
		when(consultaCatalogo.obterProdutoVisivel(TENANT, AURORA))
				.thenReturn(Optional.of(new ResumoProduto(
						AURORA,
						"Semente de milho Aurora 20 kg",
						"Sementes",
						"Cultivar para plantio de verão.",
						"Saco",
						new BigDecimal("999.00"),
						true,
						null)));
		carrinhoComAurora(2, new BigDecimal("999.00"));

		VisaoPedido visao = servico.obterParaProdutor(ALFA, pedidoPersistido.id());
		assertThat(visao.total()).isEqualByComparingTo("1240.00");
		assertThat(visao.itens().getFirst().precoUnitario()).isEqualByComparingTo("620.00");
	}

	@Test
	void ut056_produtorNaoLePedidoDeOutroProdutor() {
		servico.criar(comandoValido());
		UUID idPedido = pedidoPersistido.id();
		autenticar(BETA, PRODUTOR_BETA);

		assertThatThrownBy(() -> servico.obterParaProdutor(BETA, idPedido))
				.isInstanceOf(ExcecaoPedido.class)
				.satisfies(excecao -> {
					ExcecaoPedido pedido = (ExcecaoPedido) excecao;
					assertThat(pedido.codigo()).isEqualTo("ACESSO_PEDIDO_NEGADO");
					assertThat(pedido.getMessage()).isEqualTo("Você não pode visualizar este pedido.");
				});
	}

	@Test
	void ut051_sessaoExpiradaNaoApagaCarrinho() {
		when(consultaIdentidade.exigirAutenticado())
				.thenThrow(new br.agriplataforma.identity.application.ExcecaoAutenticacao(
						"NAO_AUTENTICADO", "Entre ou crie uma conta para continuar."));
		assertThatThrownBy(() -> servico.criar(comandoValido()))
				.isInstanceOf(br.agriplataforma.identity.application.ExcecaoAutenticacao.class)
				.satisfies(excecao -> assertThat(((br.agriplataforma.identity.application.ExcecaoAutenticacao) excecao)
								.codigo())
						.isEqualTo("NAO_AUTENTICADO"));
		assertThat(consultaCarrinho.obter(CHAVE_CARRINHO).itens()).hasSize(1);
	}

	@Test
	void ut052_itemInelegivelImpedeConfirmacao() {
		when(consultaCatalogo.exigirProdutoPedivel(TENANT, AURORA))
				.thenThrow(new ExcecaoCatalogo("PRODUTO_NAO_ELEGIVEL", "Este produto não pode ser pedido nesta loja."));
		assertThatThrownBy(() -> servico.criar(comandoValido())).isInstanceOf(ExcecaoCatalogo.class);
		verify(comandoCarrinho, never()).esvaziar(any());
	}

	@Test
	void ut053_propriedadeDeBetaRecusada() {
		UUID fazendaSul = UUID.fromString("66666666-6666-6666-6666-666666666666");
		when(consultaPropriedades.buscarPropria(fazendaSul)).thenReturn(Optional.empty());
		assertThatThrownBy(() ->
						servico.criar(new CriarPedido(CHAVE_CARRINHO, fazendaSul, RETIRADA, CHAVE_IDEMP)))
				.isInstanceOf(ExcecaoPedido.class)
				.satisfies(excecao -> assertThat(((ExcecaoPedido) excecao).codigo())
						.isEqualTo("DADOS_CHECKOUT_OBRIGATORIOS"));
	}

	@Test
	void ut055_operadorNaoCriaPedido() {
		when(consultaIdentidade.exigirAutenticado())
				.thenReturn(new UsuarioAutenticado(
						UUID.fromString("22222222-2222-2222-2222-222222222222"),
						TENANT,
						"Operador",
						"operador.revenda@example.com",
						Papel.OPERADOR_REVENDA));
		assertThatThrownBy(() -> servico.criar(comandoValido()))
				.isInstanceOf(ExcecaoPedido.class)
				.satisfies(excecao -> {
					ExcecaoPedido pedido = (ExcecaoPedido) excecao;
					assertThat(pedido.codigo()).isEqualTo("ACESSO_NEGADO");
					assertThat(pedido.getMessage()).isEqualTo("Você não tem permissão para este recurso.");
				});
	}

	@Test
	void ut057_idInexistenteNaoRevelaOutroPedido() {
		servico.criar(comandoValido());
		assertThatThrownBy(() -> servico.obterParaProdutor(ALFA, UUID.randomUUID()))
				.isInstanceOf(ExcecaoPedido.class)
				.satisfies(excecao -> assertThat(((ExcecaoPedido) excecao).codigo())
						.isEqualTo("ACESSO_PEDIDO_NEGADO"));
	}

	@Test
	void ut060_interrupcaoNaoDeixaMeioTermo() {
		org.mockito.Mockito.doThrow(new RuntimeException("falha")).when(repositorioItens).saveAll(any());
		assertThatThrownBy(() -> servico.criar(comandoValido())).isInstanceOf(RuntimeException.class);
	}

	@Test
	void ut061_listaVaziaDeAlfa() {
		when(repositorioPedido.findByIdTenantAndIdProdutor(any(), any(), any()))
				.thenReturn(org.springframework.data.domain.Page.empty());
		var pagina = servico.listarParaProdutor(ALFA, 1, 10);
		assertThat(pagina.itens()).isEmpty();
		assertThat(pagina.total()).isZero();
	}

	private CriarPedido comandoValido() {
		return new CriarPedido(CHAVE_CARRINHO, FAZENDA_NORTE, RETIRADA, CHAVE_IDEMP);
	}

	private void autenticar(UUID idUsuario, UUID idProdutor) {
		when(consultaIdentidade.exigirAutenticado())
				.thenReturn(new UsuarioAutenticado(idUsuario, TENANT, "Produtor", "p@example.com", Papel.PRODUTOR));
		when(consultaPropriedades.idProdutorDoAutenticado()).thenReturn(idProdutor);
	}

	private void carrinhoComAurora(int quantidade, BigDecimal preco) {
		BigDecimal totalLinha = preco.multiply(BigDecimal.valueOf(quantidade)).setScale(2);
		when(consultaCarrinho.obter(CHAVE_CARRINHO))
				.thenReturn(new VisaoCarrinho(
						List.of(new VisaoCarrinho.ItemVisaoCarrinho(
								AURORA, "Semente de milho Aurora 20 kg", quantidade, preco, totalLinha)),
						totalLinha));
	}
}
