package br.agriplataforma.order.application;

import br.agriplataforma.cart.application.ConsultaCarrinho;
import br.agriplataforma.cart.application.VisaoCarrinho;
import br.agriplataforma.catalog.application.ConsultaCatalogo;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicoPedido implements ComandoPedido {

	static final String MENSAGEM_RECEBIDO = "Pedido recebido";
	static final String DADOS_CHECKOUT = "DADOS_CHECKOUT_OBRIGATORIOS";
	static final String MSG_DADOS = "Escolha uma propriedade e a preferência de retirada.";

	private final ConsultaIdentidade consultaIdentidade;
	private final ConsultaPropriedades consultaPropriedades;
	private final ConsultaCarrinho consultaCarrinho;
	private final ConsultaCatalogo consultaCatalogo;
	private final RepositorioPedido repositorioPedido;
	private final RepositorioItemPedido repositorioItens;

	public ServicoPedido(
			ConsultaIdentidade consultaIdentidade,
			ConsultaPropriedades consultaPropriedades,
			ConsultaCarrinho consultaCarrinho,
			ConsultaCatalogo consultaCatalogo,
			RepositorioPedido repositorioPedido,
			RepositorioItemPedido repositorioItens) {
		this.consultaIdentidade = consultaIdentidade;
		this.consultaPropriedades = consultaPropriedades;
		this.consultaCarrinho = consultaCarrinho;
		this.consultaCatalogo = consultaCatalogo;
		this.repositorioPedido = repositorioPedido;
		this.repositorioItens = repositorioItens;
	}

	@Override
	@Transactional
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
		String chave = chaveEfetiva(comando, usuario, idProdutor);
		return repositorioPedido
				.findByIdTenantAndIdProdutorAndChaveIdempotencia(usuario.idTenant(), idProdutor, chave)
				.map(ServicoPedido::confirmacao)
				.orElseGet(() -> persistirNovo(comando, usuario, idProdutor, propriedade, retirada, chave));
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

	private ConfirmacaoPedido persistirNovo(
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
				retirada,
				total,
				chave);
		try {
			repositorioPedido.save(pedido);
			repositorioPedido.flush();
			List<ItemPedido> linhas = new ArrayList<>();
			for (VisaoCarrinho.ItemVisaoCarrinho item : carrinho.itens()) {
				String unidade = consultaCatalogo
						.obterProdutoVisivel(usuario.idTenant(), item.idProduto())
						.map(produto -> produto.unidade() == null ? "" : produto.unidade())
						.orElse("");
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
			consultaCarrinho.esvaziar(comando.chaveProprietario());
			return confirmacao(pedido);
		} catch (DataIntegrityViolationException duplicado) {
			return repositorioPedido
					.findByIdTenantAndIdProdutorAndChaveIdempotencia(usuario.idTenant(), idProdutor, chave)
					.map(ServicoPedido::confirmacao)
					.orElseThrow(() -> duplicado);
		}
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
			throw new ExcecaoPedido("ACESSO_PEDIDO_NEGADO", "Você não pode visualizar este pedido.");
		}
		return usuario;
	}

	private static ConfirmacaoPedido confirmacao(Pedido pedido) {
		return new ConfirmacaoPedido(pedido.id(), pedido.situacao(), pedido.confirmacao(), MENSAGEM_RECEBIDO);
	}

	private static String chaveEfetiva(CriarPedido comando, UsuarioAutenticado usuario, UUID idProdutor) {
		if (comando.chaveIdempotencia() != null && !comando.chaveIdempotencia().isBlank()) {
			return comando.chaveIdempotencia().trim();
		}
		return "auto:" + usuario.id() + ":" + idProdutor + ":" + comando.chaveProprietario() + ":"
				+ comando.idPropriedade() + ":" + comando.preferenciaRetirada();
	}

	private static ExcecaoPedido acessoNegado() {
		return new ExcecaoPedido("ACESSO_PEDIDO_NEGADO", "Você não pode visualizar este pedido.");
	}
}
