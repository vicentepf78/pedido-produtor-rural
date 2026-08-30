package br.agriplataforma.catalog.api;

import br.agriplataforma.catalog.application.BuscaProduto;
import br.agriplataforma.catalog.application.ConsultaCatalogo;
import br.agriplataforma.catalog.application.ExcecaoCatalogo;
import br.agriplataforma.catalog.application.Paginacao;
import br.agriplataforma.catalog.application.ResumoProduto;
import br.agriplataforma.tenant.application.ConsultaTenant;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalogo/produtos")
public class CatalogoApi {

	private final ConsultaCatalogo consultaCatalogo;
	private final ConsultaTenant consultaTenant;

	public CatalogoApi(ConsultaCatalogo consultaCatalogo, ConsultaTenant consultaTenant) {
		this.consultaCatalogo = consultaCatalogo;
		this.consultaTenant = consultaTenant;
	}

	@GetMapping
	public RespostaPaginaProdutos listar(
			@RequestParam(required = false) String consulta,
			@RequestParam(required = false) String categoria,
			@RequestParam(required = false) Integer pagina,
			@RequestParam(required = false) Integer tamanhoPagina) {
		var resultado = consultaCatalogo.listarProdutosVisiveis(
				new BuscaProduto(consulta, categoria), Paginacao.de(pagina, tamanhoPagina));
		return new RespostaPaginaProdutos(
				resultado.itens().stream().map(CatalogoApi::item).toList(),
				resultado.pagina(),
				resultado.tamanhoPagina(),
				resultado.total());
	}

	@GetMapping("/{idProduto}")
	public ItemProduto detalhe(@PathVariable UUID idProduto) {
		return consultaCatalogo
				.obterProdutoVisivel(consultaTenant.idTenantConfigurado(), idProduto)
				.map(CatalogoApi::item)
				.orElseThrow(() -> new ExcecaoCatalogo(
						"PRODUTO_NAO_ELEGIVEL", "Este produto não pode ser pedido nesta loja."));
	}

	private static ItemProduto item(ResumoProduto produto) {
		return new ItemProduto(
				produto.id(),
				produto.nome(),
				produto.categoria(),
				produto.descricaoCurta(),
				produto.unidade(),
				dinheiro(produto.precoUnitario()),
				produto.disponivel(),
				produto.urlImagem());
	}

	private static String dinheiro(BigDecimal valor) {
		return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}

	public record RespostaPaginaProdutos(List<ItemProduto> itens, int pagina, int tamanhoPagina, long total) {}

	public record ItemProduto(
			UUID id,
			String nome,
			String categoria,
			String descricaoCurta,
			String unidade,
			String precoUnitario,
			boolean disponivel,
			String urlImagem) {}
}
