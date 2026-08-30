package br.agriplataforma.order.application;

import br.agriplataforma.cart.application.ComandoCarrinho;
import br.agriplataforma.cart.application.ConsultaCarrinho;
import br.agriplataforma.cart.application.VisaoCarrinho;
import br.agriplataforma.catalog.application.ConsultaCatalogo;
import br.agriplataforma.catalog.application.ProdutoParaCarrinho;
import br.agriplataforma.identity.application.ConsultaIdentidade;
import br.agriplataforma.identity.application.Papel;
import br.agriplataforma.identity.application.UsuarioAutenticado;
import br.agriplataforma.order.domain.ItemPedido;
import br.agriplataforma.order.domain.Pedido;
import br.agriplataforma.order.infrastructure.RepositorioItemPedido;
import br.agriplataforma.order.infrastructure.RepositorioPedido;
import br.agriplataforma.producer.application.ConsultaPropriedades;
import br.agriplataforma.producer.application.ResumoPropriedade;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class ServicoPedido implements ComandoPedido, ConsultaPedidoRetaguarda {

	static final String MENSAGEM_RECEBIDO = "Pedido recebido";
	static final String DADOS_CHECKOUT = "DADOS_CHECKOUT_OBRIGATORIOS";
	static final String MSG_DADOS = "Escolha uma propriedade e a preferência de retirada.";
	static final String ACEITA = "ACEITA";

	private static final Logger LOG = LoggerFactory.getLogger(ServicoPedido.class);

	private final ConsultaIdentidade consultaIdentidade;
	private final ConsultaPropriedades consultaPropriedades;
	private final ConsultaCarrinho consultaCarrinho;
	private final ComandoCarrinho comandoCarrinho;
	private final ConsultaCatalogo consultaCatalogo;
	private final RepositorioPedido repositorioPedido;
	private final RepositorioItemPedido repositorioItens;
	private final GatewayErp gatewayErp;
	private final TransactionTemplate transacao;

	public ServicoPedido(
			ConsultaIdentidade consultaIdentidade,
			ConsultaPropriedades consultaPropriedades,
			ConsultaCarrinho consultaCarrinho,
			ComandoCarrinho comandoCarrinho,
			ConsultaCatalogo consultaCatalogo,
			RepositorioPedido repositorioPedido,
			RepositorioItemPedido repositorioItens,
			GatewayErp gatewayErp,
			PlatformTransactionManager gerenciadorTransacao) {
		this.consultaIdentidade = consultaIdentidade;
		this.consultaPropriedades = consultaPropriedades;
		this.consultaCarrinho = consultaCarrinho;
		this.comandoCarrinho = comandoCarrinho;
		this.consultaCatalogo = consultaCatalogo;
		this.repositorioPedido = repositorioPedido;
		this.repositorioItens = repositorioItens;
		this.gatewayErp = gatewayErp;
		this.transacao = new TransactionTemplate(gerenciadorTransacao);
	}

	@Override
	public ConfirmacaoPedido criar(CriarPedido comando) {
		UsuarioAutenticado usuario = exigirProdutor();
		ResumoPropriedade propriedade = consultaPropriedades
				.buscarPropria(comando.idPropriedade())
				.orElseThrow(() -> new ExcecaoPedido(DADOS_CHECKOUT, MSG_DADOS));
		String retirada = comando.preferenciaRetirada() == null ? "" : comando.preferenciaRetirada().trim();
		if (retirada.isEmpty()) {
			throw new ExcecaoPedido(DADOS_CHECKOUT, MSG_DADOS);
		}
		UUID idProdutor = consultaPropriedades.idProdutorDoAutenticado();
		String chave = chaveEfetiva(comando);
		Pedido existente = transacao.execute(status -> repositorioPedido
				.findByIdTenantAndIdProdutorAndChaveIdempotencia(usuario.idTenant(), idProdutor, chave)
				.orElse(null));
		if (existente != null) {
			aceitarSePendente(existente);
			return confirmacao(recarregar(existente.id()));
		}
		Pedido novo;
		try {
			novo = transacao.execute(
					status -> persistirNovo(comando, usuario, idProdutor, propriedade, retirada, chave));
		} catch (DataIntegrityViolationException duplicado) {
			novo = transacao.execute(status -> repositorioPedido
					.findByIdTenantAndIdProdutorAndChaveIdempotencia(usuario.idTenant(), idProdutor, chave)
					.orElseThrow(() -> duplicado));
		}
		aceitarSePendente(novo);
		return confirmacao(recarregar(novo.id()));
	}

	@Override
	@Transactional(readOnly = true)
	public VisaoPedido obterParaProdutor(UUID idUsuario, UUID idPedido) {
		UsuarioAutenticado usuario = exigirProdutor();
		if (!usuario.id().equals(idUsuario)) {
			throw acessoNegado();
		}
		UUID idProdutor = consultaPropriedades.idProdutorDoAutenticado();
		Pedido pedido = repositorioPedido.findById(idPedido).orElseThrow(ServicoPedido::acessoNegado);
		if (!pedido.idTenant().equals(usuario.idTenant()) || !pedido.idProdutor().equals(idProdutor)) {
			throw acessoNegado();
		}
		return visao(pedido);
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaPedido<ResumoPedido> listarParaProdutor(UUID idUsuario, int pagina, int tamanhoPagina) {
		UsuarioAutenticado usuario = exigirProdutor();
		if (!usuario.id().equals(idUsuario)) {
			throw acessoNegado();
		}
		int paginaEfetiva = pagina < 1 ? 1 : pagina;
		int tamanhoEfetivo = tamanhoPagina < 1 ? 10 : Math.min(tamanhoPagina, 100);
		UUID idProdutor = consultaPropriedades.idProdutorDoAutenticado();
		Page<Pedido> resultado = repositorioPedido.findByIdTenantAndIdProdutor(
				usuario.idTenant(),
				idProdutor,
				PageRequest.of(paginaEfetiva - 1, tamanhoEfetivo, Sort.by(Sort.Direction.DESC, "criadoEm")));
		List<ResumoPedido> itens = resultado.getContent().stream()
				.map(pedido -> new ResumoPedido(
						pedido.id(),
						pedido.situacao(),
						pedido.confirmacao(),
						pedido.total().setScale(2, RoundingMode.HALF_UP),
						pedido.criadoEm()))
				.toList();
		return new PaginaPedido<>(itens, paginaEfetiva, tamanhoEfetivo, resultado.getTotalElements());
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaPedido<ResumoPedidoRetaguarda> listarPorTenant(UUID idTenant, int pagina, int tamanhoPagina) {
		exigirOperador(idTenant);
		int paginaEfetiva = pagina < 1 ? 1 : pagina;
		int tamanhoEfetivo = tamanhoPagina < 1 ? 25 : Math.min(tamanhoPagina, 100);
		Page<Pedido> resultado = repositorioPedido.findByIdTenant(
				idTenant, PageRequest.of(paginaEfetiva - 1, tamanhoEfetivo, Sort.by(Sort.Direction.DESC, "criadoEm")));
		List<ResumoPedidoRetaguarda> itens = resultado.getContent().stream()
				.map(pedido -> new ResumoPedidoRetaguarda(
						pedido.id(),
						pedido.nomeProdutor(),
						pedido.total().setScale(2, RoundingMode.HALF_UP),
						pedido.situacao(),
						pedido.confirmacao(),
						pedido.criadoEm()))
				.toList();
		return new PaginaPedido<>(itens, paginaEfetiva, tamanhoEfetivo, resultado.getTotalElements());
	}

	@Override
	@Transactional(readOnly = true)
	public VisaoPedidoRetaguarda obterPorTenant(UUID idTenant, UUID idPedido) {
		exigirOperador(idTenant);
		Pedido pedido = repositorioPedido
				.findByIdAndIdTenant(idPedido, idTenant)
				.orElseThrow(() -> new ExcecaoPedido("PEDIDO_NAO_ENCONTRADO", "Pedido não encontrado."));
		VisaoPedido visao = visao(pedido);
		return new VisaoPedidoRetaguarda(
				visao.idPedido(),
				pedido.nomeProdutor(),
				visao.situacao(),
				visao.confirmacao(),
				visao.nomePropriedade(),
				visao.preferenciaRetirada(),
				visao.total(),
				visao.criadoEm(),
				visao.itens());
	}

	private Pedido persistirNovo(
			CriarPedido comando,
			UsuarioAutenticado usuario,
			UUID idProdutor,
			ResumoPropriedade propriedade,
			String retirada,
			String chave) {
		VisaoCarrinho carrinho = consultaCarrinho.obter(comando.chaveProprietario());
		if (carrinho.itens().isEmpty()) {
			throw new ExcecaoPedido("CARRINHO_VAZIO", "Adicione ao menos um produto antes do checkout.");
		}
		BigDecimal total = carrinho.total().setScale(2, RoundingMode.HALF_UP);
		Pedido pedido = Pedido.novo(
				usuario.idTenant(),
				idProdutor,
				propriedade.id(),
				propriedade.nome(),
				usuario.nome(),
				retirada,
				total,
				chave);
		repositorioPedido.save(pedido);
		repositorioPedido.flush();
		List<ItemPedido> linhas = new ArrayList<>();
		for (VisaoCarrinho.ItemVisaoCarrinho item : carrinho.itens()) {
			ProdutoParaCarrinho produto = consultaCatalogo.exigirProdutoPedivel(usuario.idTenant(), item.idProduto());
			String unidade = produto.unidade() == null ? "" : produto.unidade();
			linhas.add(ItemPedido.novo(
					pedido.id(),
					item.idProduto(),
					item.nome(),
					unidade,
					item.quantidade(),
					item.precoUnitario().setScale(2, RoundingMode.HALF_UP),
					item.totalLinha().setScale(2, RoundingMode.HALF_UP)));
		}
		repositorioItens.saveAll(linhas);
		comandoCarrinho.esvaziar(comando.chaveProprietario());
		return pedido;
	}

	private void aceitarSePendente(Pedido pedido) {
		if (pedido == null || ACEITA.equals(pedido.confirmacao())) {
			return;
		}
		try {
			ConfirmacaoErp resultado = gatewayErp.aceitar(new PedidoLocal(pedido.id(), pedido.idTenant()));
			if (resultado != null && ACEITA.equals(resultado.codigo())) {
				transacao.executeWithoutResult(status -> {
					Pedido persistido = repositorioPedido.findById(pedido.id()).orElse(null);
					if (persistido == null) {
						return;
					}
					persistido.registrarConfirmacaoAceita();
					LOG.info("confirmacao_aceita idPedido={} idTenant={}", persistido.id(), persistido.idTenant());
				});
			}
		} catch (RuntimeException ignorada) {
			LOG.info("confirmacao_pendente idPedido={} idTenant={}", pedido.id(), pedido.idTenant());
		}
	}

	private Pedido recarregar(UUID idPedido) {
		return transacao.execute(status -> repositorioPedido.findById(idPedido).orElseThrow());
	}

	private VisaoPedido visao(Pedido pedido) {
		List<VisaoPedido.ItemVisaoPedido> itens = repositorioItens.findByIdPedidoOrderByIdAsc(pedido.id()).stream()
				.map(item -> new VisaoPedido.ItemVisaoPedido(
						item.nome(),
						item.unidade(),
						item.quantidade(),
						item.precoUnitario().setScale(2, RoundingMode.HALF_UP),
						item.totalLinha().setScale(2, RoundingMode.HALF_UP)))
				.toList();
		return new VisaoPedido(
				pedido.id(),
				pedido.situacao(),
				pedido.confirmacao(),
				pedido.nomePropriedade(),
				pedido.preferenciaRetirada(),
				pedido.total().setScale(2, RoundingMode.HALF_UP),
				pedido.criadoEm(),
				itens);
	}

	private UsuarioAutenticado exigirProdutor() {
		UsuarioAutenticado usuario = consultaIdentidade.exigirAutenticado();
		if (usuario.papel() != Papel.PRODUTOR) {
			throw new ExcecaoPedido("ACESSO_NEGADO", "Você não tem permissão para este recurso.");
		}
		return usuario;
	}

	private void exigirOperador(UUID idTenant) {
		UsuarioAutenticado usuario = consultaIdentidade.exigirAutenticado();
		if (usuario.papel() != Papel.OPERADOR_REVENDA || !usuario.idTenant().equals(idTenant)) {
			throw new ExcecaoPedido("ACESSO_NEGADO", "Você não tem permissão para este recurso.");
		}
	}

	private static ConfirmacaoPedido confirmacao(Pedido pedido) {
		return new ConfirmacaoPedido(pedido.id(), pedido.situacao(), pedido.confirmacao(), MENSAGEM_RECEBIDO);
	}

	private static String chaveEfetiva(CriarPedido comando) {
		if (comando.chaveIdempotencia() != null && !comando.chaveIdempotencia().isBlank()) {
			return comando.chaveIdempotencia().trim();
		}
		return UUID.randomUUID().toString();
	}

	private static ExcecaoPedido acessoNegado() {
		return new ExcecaoPedido("ACESSO_PEDIDO_NEGADO", "Você não pode visualizar este pedido.");
	}
}
