package br.agriplataforma.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import br.agriplataforma.catalog.application.BuscaProduto;
import br.agriplataforma.catalog.application.ExcecaoCatalogo;
import br.agriplataforma.catalog.application.Paginacao;
import br.agriplataforma.catalog.application.ProdutoParaCarrinho;
import br.agriplataforma.catalog.application.ResumoProduto;
import br.agriplataforma.catalog.application.ServicoCatalogo;
import br.agriplataforma.catalog.domain.Produto;
import br.agriplataforma.catalog.infrastructure.RepositorioProduto;
import br.agriplataforma.tenant.application.ConsultaTenant;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ConsultaCatalogoTest {

	static final UUID TENANT = UUID.fromString("11111111-1111-1111-1111-111111111111");
	static final UUID AURORA = UUID.fromString("10000000-0000-4000-8000-000000000001");
	static final UUID UREIA = UUID.fromString("10000000-0000-4000-8000-000000000013");
	static final UUID REGULADO = UUID.fromString("10000000-0000-4000-8000-000000000099");
	static final UUID SEM_IMAGEM = UUID.fromString("10000000-0000-4000-8000-000000000002");

	@Mock
	RepositorioProduto repositorio;

	@Mock
	ConsultaTenant consultaTenant;

	ServicoCatalogo servico;

	@BeforeEach
	void preparar() {
		servico = new ServicoCatalogo(repositorio, consultaTenant);
	}

	@Test
	void ut001_listaProdutoDisponivelComDescricaoUnidadePrecoEImagem() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		Produto aurora = produto(
				AURORA,
				"Semente de milho Aurora 20 kg",
				"Cultivar para plantio de verão.",
				"Sementes",
				"Saco",
				"620.00",
				true,
				false,
				"/media/produtos/aurora-milho-20kg.svg");
		when(repositorio.findByIdTenantAndReguladoFalseAndNomeContainingIgnoreCaseOrderByNomeAsc(
						eq(TENANT), eq("semente"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(aurora)));

		var pagina = servico.listarProdutosVisiveis(new BuscaProduto("semente"), Paginacao.de(1, 24));

		assertThat(pagina.total()).isEqualTo(1);
		ResumoProduto item = pagina.itens().getFirst();
		assertThat(item.nome()).isEqualTo("Semente de milho Aurora 20 kg");
		assertThat(item.descricaoCurta()).isEqualTo("Cultivar para plantio de verão.");
		assertThat(item.unidade()).isEqualTo("Saco");
		assertThat(item.precoUnitario()).isEqualByComparingTo("620.00");
		assertThat(item.urlImagem()).isEqualTo("/media/produtos/aurora-milho-20kg.svg");
		assertThat(item.disponivel()).isTrue();
	}

	@Test
	void ut002_produtoReguladoNaoEPedivel() {
		when(repositorio.findByIdAndIdTenant(REGULADO, TENANT))
				.thenReturn(Optional.of(produto(
						REGULADO,
						"Herbicida glifosato 480 g/L",
						"Regulado",
						"Defensivos",
						"L",
						"42.00",
						true,
						true,
						null)));

		assertThatThrownBy(() -> servico.exigirProdutoPedivel(TENANT, REGULADO))
				.isInstanceOf(ExcecaoCatalogo.class)
				.satisfies(excecao -> {
					ExcecaoCatalogo catalogo = (ExcecaoCatalogo) excecao;
					assertThat(catalogo.codigo()).isEqualTo("PRODUTO_NAO_ELEGIVEL");
					assertThat(catalogo.getMessage()).isEqualTo("Este produto não pode ser pedido no MVP0.");
				});
	}

	@Test
	void ut003_pesquisaVaziaOuSemCorrespondenciaRetornaVazio() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseAndNomeContainingIgnoreCaseOrderByNomeAsc(
						eq(TENANT), eq("xyzzy"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of()));

		assertThat(servico.listarProdutosVisiveis(new BuscaProduto("   "), Paginacao.de(1, 24))
						.itens())
				.isEmpty();
		assertThat(servico.listarProdutosVisiveis(new BuscaProduto(""), Paginacao.de(1, 24))
						.total())
				.isZero();
		assertThat(servico.listarProdutosVisiveis(new BuscaProduto("xyzzy"), Paginacao.de(1, 24))
						.itens())
				.isEmpty();
	}

	@Test
	void ut004_indisponivelApareceNaListaMasNaoEntraNoCarrinho() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		Produto ureia = produto(
				UREIA,
				"Ureia 45% N",
				"Adubo nitrogenado granulado.",
				"Fertilizantes",
				"sc 50 kg",
				"178.00",
				false,
				false,
				null);
		when(repositorio.findByIdTenantAndReguladoFalseOrderByNomeAsc(eq(TENANT), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(ureia)));
		when(repositorio.findByIdAndIdTenant(UREIA, TENANT)).thenReturn(Optional.of(ureia));

		ResumoProduto listado = servico.listarProdutosVisiveis(new BuscaProduto(null), Paginacao.de(1, 24))
				.itens()
				.getFirst();
		assertThat(listado.disponivel()).isFalse();
		assertThat(listado.nome()).isEqualTo("Ureia 45% N");

		assertThatThrownBy(() -> servico.exigirProdutoPedivel(TENANT, UREIA))
				.isInstanceOf(ExcecaoCatalogo.class)
				.satisfies(excecao ->
						assertThat(((ExcecaoCatalogo) excecao).codigo()).isEqualTo("PRODUTO_INDISPONIVEL"));
	}

	@Test
	void ut005_produtoSemImagemDevolveValorSeguro() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		Produto semImagem = produto(
				SEM_IMAGEM,
				"Semente de milho DK697",
				"Híbrido adaptado ao cerrado, alto vigor.",
				"Sementes",
				"sc 60 kg",
				"890.00",
				true,
				false,
				null);
		Produto insegura = produto(
				UUID.fromString("10000000-0000-4000-8000-000000000014"),
				"Fertilizante NPK 20-05-20",
				"Formulação balanceada para cobertura.",
				"Fertilizantes",
				"sc 50 kg",
				"215.00",
				true,
				false,
				"javascript:alert(1)");
		when(repositorio.findByIdTenantAndReguladoFalseOrderByNomeAsc(eq(TENANT), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(semImagem, insegura)));

		List<ResumoProduto> itens = servico.listarProdutosVisiveis(new BuscaProduto(null), Paginacao.de(1, 24))
				.itens();
		assertThat(itens.get(0).urlImagem()).isNull();
		assertThat(itens.get(1).urlImagem()).isNull();
	}

	@Test
	void produtoPedivelRetornaDadosParaOCarrinho() {
		when(repositorio.findByIdAndIdTenant(AURORA, TENANT))
				.thenReturn(Optional.of(produto(
						AURORA,
						"Semente de milho Aurora 20 kg",
						"Cultivar para plantio de verão.",
						"Sementes",
						"Saco",
						"620.00",
						true,
						false,
						"/media/produtos/aurora-milho-20kg.svg")));

		ProdutoParaCarrinho produto = servico.exigirProdutoPedivel(TENANT, AURORA);
		assertThat(produto.nome()).isEqualTo("Semente de milho Aurora 20 kg");
		assertThat(produto.precoUnitario()).isEqualByComparingTo("620.00");
	}

	private static Produto produto(
			UUID id,
			String nome,
			String descricao,
			String categoria,
			String unidade,
			String preco,
			boolean disponivel,
			boolean regulado,
			String urlImagem) {
		return new Produto(
				id, TENANT, nome, descricao, categoria, unidade, new BigDecimal(preco), disponivel, regulado, urlImagem);
	}
}
