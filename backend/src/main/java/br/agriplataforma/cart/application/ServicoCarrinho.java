package br.agriplataforma.cart.application;

import br.agriplataforma.cart.domain.Carrinho;
import br.agriplataforma.cart.domain.ItemCarrinho;
import br.agriplataforma.cart.infrastructure.RepositorioCarrinho;
import br.agriplataforma.cart.infrastructure.RepositorioItemCarrinho;
import br.agriplataforma.catalog.application.ConsultaCatalogo;
import br.agriplataforma.catalog.application.ProdutoParaCarrinho;
import br.agriplataforma.tenant.application.ConsultaTenant;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicoCarrinho implements ConsultaCarrinho, ComandoCarrinho {

	private final RepositorioCarrinho repositorioCarrinho;
	private final RepositorioItemCarrinho repositorioItens;
	private final ConsultaCatalogo consultaCatalogo;
	private final ConsultaTenant consultaTenant;

	public ServicoCarrinho(
			RepositorioCarrinho repositorioCarrinho,
			RepositorioItemCarrinho repositorioItens,
			ConsultaCatalogo consultaCatalogo,
			ConsultaTenant consultaTenant) {
		this.repositorioCarrinho = repositorioCarrinho;
		this.repositorioItens = repositorioItens;
		this.consultaCatalogo = consultaCatalogo;
		this.consultaTenant = consultaTenant;
	}

	@Override
	@Transactional(readOnly = true)
	public VisaoCarrinho obter(String chaveProprietario) {
		return repositorioCarrinho
				.findByIdTenantAndChaveProprietario(consultaTenant.idTenantConfigurado(), chaveProprietario)
				.map(this::visao)
				.orElseGet(ServicoCarrinho::vazio);
	}

	@Transactional
	public VisaoCarrinho adicionar(String chaveProprietario, UUID idProduto, BigDecimal quantidadeBruta) {
		int quantidade = Quantidade.exigirInteiraPositiva(quantidadeBruta);
		Carrinho carrinho = obterOuCriar(chaveProprietario);
		ProdutoParaCarrinho produto = consultaCatalogo.exigirProdutoPedivel(carrinho.idTenant(), idProduto);
		repositorioItens
				.findByIdCarrinhoAndIdProduto(carrinho.id(), idProduto)
				.ifPresentOrElse(
						item -> {
							item.somar(quantidade, produto.precoUnitario());
							repositorioItens.save(item);
						},
						() -> repositorioItens.save(
								ItemCarrinho.novo(carrinho.id(), idProduto, quantidade, produto.precoUnitario())));
		return visao(carrinho);
	}

	@Transactional
	public VisaoCarrinho alterarQuantidade(String chaveProprietario, UUID idProduto, BigDecimal quantidadeBruta) {
		int quantidade = Quantidade.exigirInteiraPositiva(quantidadeBruta);
		Carrinho carrinho = exigirCarrinho(chaveProprietario);
		ItemCarrinho item = repositorioItens
				.findByIdCarrinhoAndIdProduto(carrinho.id(), idProduto)
				.orElseThrow(() -> new ExcecaoCarrinho(
						"PRODUTO_NAO_ELEGIVEL", "Este produto não pode ser pedido no MVP0."));
		ProdutoParaCarrinho produto = consultaCatalogo.exigirProdutoPedivel(carrinho.idTenant(), idProduto);
		item.definirQuantidade(quantidade, produto.precoUnitario());
		repositorioItens.save(item);
		return visao(carrinho);
	}

	@Override
	@Transactional
	public void esvaziar(String chaveProprietario) {
		repositorioCarrinho
				.findByIdTenantAndChaveProprietario(consultaTenant.idTenantConfigurado(), chaveProprietario)
				.ifPresent(carrinho -> repositorioItens.deleteByIdCarrinho(carrinho.id()));
	}

	@Transactional
	public VisaoCarrinho remover(String chaveProprietario, UUID idProduto) {
		Carrinho carrinho = exigirCarrinho(chaveProprietario);
		repositorioItens.deleteByIdCarrinhoAndIdProduto(carrinho.id(), idProduto);
		return visao(carrinho);
	}

	private Carrinho obterOuCriar(String chaveProprietario) {
		var idTenant = consultaTenant.idTenantConfigurado();
		return repositorioCarrinho
				.findByIdTenantAndChaveProprietario(idTenant, chaveProprietario)
				.orElseGet(() -> repositorioCarrinho.save(Carrinho.novo(idTenant, chaveProprietario)));
	}

	private Carrinho exigirCarrinho(String chaveProprietario) {
		return repositorioCarrinho
				.findByIdTenantAndChaveProprietario(consultaTenant.idTenantConfigurado(), chaveProprietario)
				.orElseGet(() -> obterOuCriar(chaveProprietario));
	}

	private VisaoCarrinho visao(Carrinho carrinho) {
		List<ItemCarrinho> linhas = repositorioItens.findByIdCarrinhoOrderByIdAsc(carrinho.id());
		List<VisaoCarrinho.ItemVisaoCarrinho> itens = new ArrayList<>();
		BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		for (ItemCarrinho linha : linhas) {
			String nome = consultaCatalogo
					.obterProdutoVisivel(carrinho.idTenant(), linha.idProduto())
					.map(produto -> produto.nome())
					.orElse("Produto");
			BigDecimal totalLinha = linha.totalLinha().setScale(2, RoundingMode.HALF_UP);
			itens.add(new VisaoCarrinho.ItemVisaoCarrinho(
					linha.idProduto(),
					nome,
					linha.quantidade(),
					linha.precoUnitario().setScale(2, RoundingMode.HALF_UP),
					totalLinha));
			total = total.add(totalLinha);
		}
		return new VisaoCarrinho(List.copyOf(itens), total);
	}

	private static VisaoCarrinho vazio() {
		return new VisaoCarrinho(List.of(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
	}
}
