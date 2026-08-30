package br.agriplataforma.catalog.application;

import br.agriplataforma.catalog.domain.Produto;
import br.agriplataforma.catalog.infrastructure.RepositorioProduto;
import br.agriplataforma.tenant.application.ConsultaTenant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicoCatalogo implements ConsultaCatalogo {

	static final String PRODUTO_NAO_ELEGIVEL = "PRODUTO_NAO_ELEGIVEL";
	static final String PRODUTO_INDISPONIVEL = "PRODUTO_INDISPONIVEL";
	static final String CATEGORIA_INVALIDA = "CATEGORIA_INVALIDA";
	static final Set<String> CATEGORIAS_VISIVEIS = Set.of("Sementes", "Fertilizantes", "Correção");

	private final RepositorioProduto repositorio;
	private final ConsultaTenant consultaTenant;

	public ServicoCatalogo(RepositorioProduto repositorio, ConsultaTenant consultaTenant) {
		this.repositorio = repositorio;
		this.consultaTenant = consultaTenant;
	}

	@Override
	@Transactional(readOnly = true)
	public Pagina<ResumoProduto> listarProdutosVisiveis(BuscaProduto busca, Paginacao pagina) {
		Paginacao.exigirTamanhoPermitido(pagina.tamanhoPagina());
		String categoria = categoriaFiltro(busca == null ? null : busca.categoria());
		var idTenant = consultaTenant.idTenantConfigurado();
		var pageable = PageRequest.of(pagina.pagina() - 1, pagina.tamanhoPagina());
		String consulta = busca == null ? null : busca.consulta();
		String termo = consulta == null || consulta.isBlank() ? null : consulta.trim();
		Page<Produto> resultado = consultar(idTenant, categoria, termo, pageable);
		return new Pagina<>(
				resultado.getContent().stream().map(ServicoCatalogo::resumo).toList(),
				pagina.pagina(),
				pagina.tamanhoPagina(),
				resultado.getTotalElements());
	}

	@Override
	@Transactional(readOnly = true)
	public ProdutoParaCarrinho exigirProdutoPedivel(UUID idTenant, UUID idProduto) {
		Produto produto = repositorio
				.findByIdAndIdTenant(idProduto, idTenant)
				.orElseThrow(ServicoCatalogo::naoElegivel);
		if (produto.regulado()) {
			throw naoElegivel();
		}
		if (!produto.disponivel()) {
			throw new ExcecaoCatalogo(PRODUTO_INDISPONIVEL, "Este produto não está disponível.");
		}
		return new ProdutoParaCarrinho(produto.id(), produto.nome(), produto.unidade(), produto.precoUnitario());
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ResumoProduto> obterProdutoVisivel(UUID idTenant, UUID idProduto) {
		return repositorio
				.findByIdAndIdTenant(idProduto, idTenant)
				.filter(produto -> !produto.regulado())
				.map(ServicoCatalogo::resumo);
	}

	static ResumoProduto resumo(Produto produto) {
		return new ResumoProduto(
				produto.id(),
				produto.nome(),
				produto.categoria(),
				produto.descricaoCurta(),
				produto.unidade(),
				produto.precoUnitario(),
				produto.disponivel(),
				imagemSegura(produto.urlImagem()));
	}

	static String imagemSegura(String urlImagem) {
		if (urlImagem == null || urlImagem.isBlank()) {
			return null;
		}
		String normalizada = urlImagem.trim();
		if (normalizada.startsWith("/") && !normalizada.startsWith("//")) {
			return normalizada;
		}
		return null;
	}

	private Page<Produto> consultar(UUID idTenant, String categoria, String termo, PageRequest pageable) {
		if (categoria == null && termo == null) {
			return repositorio.findByIdTenantAndReguladoFalseOrderByNomeAsc(idTenant, pageable);
		}
		if (categoria == null) {
			return repositorio.findByIdTenantAndReguladoFalseAndNomeContainingIgnoreCaseOrderByNomeAsc(
					idTenant, termo, pageable);
		}
		if (termo == null) {
			return repositorio.findByIdTenantAndReguladoFalseAndCategoriaOrderByNomeAsc(
					idTenant, categoria, pageable);
		}
		return repositorio.findByIdTenantAndReguladoFalseAndCategoriaAndNomeContainingIgnoreCaseOrderByNomeAsc(
				idTenant, categoria, termo, pageable);
	}

	static String categoriaFiltro(String categoria) {
		if (categoria == null || categoria.isBlank() || "Todos".equals(categoria.trim())) {
			return null;
		}
		String normalizada = categoria.trim();
		if (!CATEGORIAS_VISIVEIS.contains(normalizada)) {
			throw new ExcecaoCatalogo(
					CATEGORIA_INVALIDA, "Escolha Todos, Sementes, Fertilizantes ou Correção.");
		}
		return normalizada;
	}

	private static ExcecaoCatalogo naoElegivel() {
		return new ExcecaoCatalogo(PRODUTO_NAO_ELEGIVEL, "Este produto não pode ser pedido nesta loja.");
	}
}
