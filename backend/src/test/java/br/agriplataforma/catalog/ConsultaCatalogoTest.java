package br.agriplataforma.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConsultaCatalogoTest {

	static final UUID TENANT = UUID.fromString("11111111-1111-1111-1111-111111111111");
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
	void ut001_consultaEmBrancoListaRecorteElegivel() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		List<Produto> recorte = trintaElegiveis().subList(0, 10);
		when(repositorio.findByIdTenantAndReguladoFalseOrderByNomeAsc(eq(TENANT), any(Pageable.class)))
				.thenReturn(new PageImpl<>(recorte, Pageable.ofSize(10), 30));

		var pagina = servico.listarProdutosVisiveis(new BuscaProduto("", null), Paginacao.de(1, 10));

		assertThat(pagina.itens()).hasSize(10);
		assertThat(pagina.total()).isEqualTo(30);
		assertThat(pagina.tamanhoPagina()).isEqualTo(10);
	}

	@Test
	void ut002_ureiaEmFertilizantes() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		Produto ureia = ureiaContrato();
		when(repositorio.findByIdTenantAndReguladoFalseAndCategoriaAndNomeContainingIgnoreCaseOrderByNomeAsc(
						eq(TENANT), eq("Fertilizantes"), eq("ureia"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(ureia)));

		var pagina = servico.listarProdutosVisiveis(new BuscaProduto("ureia", "Fertilizantes"), Paginacao.de(1, 10));

		assertThat(pagina.itens()).hasSize(1);
		ResumoProduto item = pagina.itens().getFirst();
		assertThat(item.categoria()).isEqualTo("Fertilizantes");
		assertThat(item.nome()).isEqualTo("Ureia agrícola 50 kg");
		assertThat(item.precoUnitario()).isEqualByComparingTo("198.00");
	}

	@Test
	void ut003_todosENuloNaoFiltramCategoria() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseOrderByNomeAsc(eq(TENANT), any(Pageable.class)))
				.thenReturn(new PageImpl<>(trintaElegiveis().subList(0, 10), Pageable.ofSize(10), 30));

		assertThat(servico.listarProdutosVisiveis(new BuscaProduto(null, "Todos"), Paginacao.de(1, 10)).total())
				.isEqualTo(30);
		assertThat(servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(1, 10)).total())
				.isEqualTo(30);
	}

	@Test
	void ut004_defensivosECategoriaInvalida() {
		assertThatThrownBy(() -> servico.listarProdutosVisiveis(new BuscaProduto(null, "Defensivos"), Paginacao.de(1, 10)))
				.isInstanceOf(ExcecaoCatalogo.class)
				.satisfies(excecao -> {
					ExcecaoCatalogo catalogo = (ExcecaoCatalogo) excecao;
					assertThat(catalogo.codigo()).isEqualTo("CATEGORIA_INVALIDA");
					assertThat(catalogo.getMessage())
							.isEqualTo("Escolha Todos, Sementes, Fertilizantes ou Correção.");
				});
		verify(repositorio, never()).findByIdTenantAndReguladoFalseOrderByNomeAsc(any(), any());
	}

	@Test
	void ut005_hortalicasECategoriaInvalida() {
		assertThatThrownBy(() -> servico.listarProdutosVisiveis(new BuscaProduto(null, "Hortaliças"), Paginacao.de(1, 10)))
				.isInstanceOf(ExcecaoCatalogo.class)
				.satisfies(excecao -> assertThat(((ExcecaoCatalogo) excecao).codigo()).isEqualTo("CATEGORIA_INVALIDA"));
	}

	@Test
	void ut006_tamanho24RecusadoAntesDoRepositorio() {
		assertThatThrownBy(() -> servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(1, 24)))
				.isInstanceOf(ExcecaoCatalogo.class)
				.satisfies(excecao -> {
					ExcecaoCatalogo catalogo = (ExcecaoCatalogo) excecao;
					assertThat(catalogo.codigo()).isEqualTo("TAMANHO_PAGINA_INVALIDO");
					assertThat(catalogo.getMessage()).isEqualTo("Escolha 10, 15, 30 ou 50 produtos por página.");
				});
		verify(repositorio, never()).findByIdTenantAndReguladoFalseOrderByNomeAsc(any(), any());
	}

	@ParameterizedTest
	@ValueSource(ints = {7, 25, 100})
	void ut007_tamanhoForaDoConjuntoSemConsultarRepositorio(int tamanho) {
		assertThatThrownBy(() -> servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(1, tamanho)))
				.isInstanceOf(ExcecaoCatalogo.class)
				.satisfies(excecao ->
						assertThat(((ExcecaoCatalogo) excecao).codigo()).isEqualTo("TAMANHO_PAGINA_INVALIDO"));
		verify(repositorio, never()).findByIdTenantAndReguladoFalseOrderByNomeAsc(any(), any());
	}

	@Test
	void ut008_padraoDezEPaginaUm() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseOrderByNomeAsc(eq(TENANT), any(Pageable.class)))
				.thenAnswer(invocacao -> {
					Pageable pageable = invocacao.getArgument(1);
					assertThat(pageable.getPageNumber()).isZero();
					assertThat(pageable.getPageSize()).isEqualTo(10);
					return new PageImpl<>(trintaElegiveis().subList(0, 10), pageable, 30);
				});

		assertThat(Paginacao.PADRAO).isEqualTo(10);
		var pagina = servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(null, null));
		assertThat(pagina.pagina()).isEqualTo(1);
		assertThat(pagina.tamanhoPagina()).isEqualTo(10);
	}

	@Test
	void ut009_tamanhosPermitidosProduzemPaginaEcoada() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		assertThat(Paginacao.tamanhosPermitidos()).isEqualTo(Set.of(10, 15, 30, 50));
		when(repositorio.findByIdTenantAndReguladoFalseOrderByNomeAsc(eq(TENANT), any(Pageable.class)))
				.thenAnswer(invocacao -> {
					Pageable pageable = invocacao.getArgument(1);
					return new PageImpl<>(List.of(), pageable, 0);
				});
		for (int tamanho : List.of(10, 15, 30, 50)) {
			assertThat(servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(1, tamanho))
							.tamanhoPagina())
					.isEqualTo(tamanho);
		}
	}

	@Test
	void ut010_paginaUmNaoRepeteIdsDaPaginaDois() {
		simularTrintaPaginados();
		var primeira = servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(1, 10));
		var segunda = servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(2, 10));
		assertThat(ids(primeira.itens())).doesNotContainAnyElementsOf(ids(segunda.itens()));
	}

	@Test
	void ut011_reguladoNaoEntraEmItensNemTotal() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseOrderByNomeAsc(eq(TENANT), any(Pageable.class)))
				.thenReturn(new PageImpl<>(trintaElegiveis().subList(0, 10), Pageable.ofSize(10), 30));

		var pagina = servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(1, 10));
		assertThat(pagina.total()).isEqualTo(30);
		assertThat(pagina.itens()).noneMatch(item -> item.id().equals(REGULADO));
	}

	@Test
	void ut012_reguladoNaoEPedivelSemRevelarConteudo() {
		Produto regulado = produto(
				REGULADO, "Herbicida glifosato 480 g/L", "Regulado", "Defensivos", "L", "42.00", true, true, null);
		when(repositorio.findByIdAndIdTenant(REGULADO, TENANT)).thenReturn(Optional.of(regulado));

		assertThatThrownBy(() -> servico.exigirProdutoPedivel(TENANT, REGULADO))
				.isInstanceOf(ExcecaoCatalogo.class)
				.satisfies(excecao -> {
					ExcecaoCatalogo catalogo = (ExcecaoCatalogo) excecao;
					assertThat(catalogo.codigo()).isEqualTo("PRODUTO_NAO_ELEGIVEL");
					assertThat(catalogo.getMessage()).isEqualTo("Este produto não pode ser pedido nesta loja.");
					assertThat(catalogo.getMessage()).doesNotContain("glifosato");
					assertThat(catalogo.getMessage()).doesNotContain("42.00");
					assertThat(catalogo.getMessage()).doesNotContain("Defensivos");
				});
	}

	@Test
	void ut013_paginaDoisSemIdsDaPrimeira() {
		simularTrintaPaginados();
		var primeira = servico.listarProdutosVisiveis(new BuscaProduto("ureia", "Fertilizantes"), Paginacao.de(1, 10));
		var segunda = servico.listarProdutosVisiveis(new BuscaProduto("ureia", "Fertilizantes"), Paginacao.de(2, 10));
		assertThat(ids(segunda.itens())).doesNotContainAnyElementsOf(ids(primeira.itens()));
	}

	@Test
	void ut014_trintaItensEmTresPaginasDeDez() {
		simularTrintaPaginados();
		var p1 = servico.listarProdutosVisiveis(new BuscaProduto(null, "Todos"), Paginacao.de(1, 10));
		var p2 = servico.listarProdutosVisiveis(new BuscaProduto(null, "Todos"), Paginacao.de(2, 10));
		var p3 = servico.listarProdutosVisiveis(new BuscaProduto(null, "Todos"), Paginacao.de(3, 10));
		assertThat(p1.total()).isEqualTo(30);
		assertThat(p1.itens()).hasSize(10);
		assertThat(p2.itens()).hasSize(10);
		assertThat(p3.itens()).hasSize(10);
	}

	@Test
	void ut015_tamanho50DevolveOsTrinta() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseOrderByNomeAsc(eq(TENANT), any(Pageable.class)))
				.thenReturn(new PageImpl<>(trintaElegiveis(), Pageable.ofSize(50), 30));

		var pagina = servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(1, 50));
		assertThat(pagina.itens()).hasSize(30);
		assertThat(pagina.total()).isEqualTo(30);
	}

	@Test
	void ut016_consultaInexistenteVazia() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseAndNomeContainingIgnoreCaseOrderByNomeAsc(
						eq(TENANT), eq("xyzzy-inexistente"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of()));

		var pagina = servico.listarProdutosVisiveis(new BuscaProduto("xyzzy-inexistente", null), Paginacao.de(1, 10));
		assertThat(pagina.itens()).isEmpty();
		assertThat(pagina.total()).isZero();
	}

	@Test
	void ut017_espacosListamRecorteETextoLongoNaoQuebra() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseAndCategoriaOrderByNomeAsc(
						eq(TENANT), eq("Sementes"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(produto(
						SEM_IMAGEM,
						"Semente de milho DK697",
						"Híbrido",
						"Sementes",
						"sc 60 kg",
						"890.00",
						true,
						false,
						null))));
		when(repositorio.findByIdTenantAndReguladoFalseAndNomeContainingIgnoreCaseOrderByNomeAsc(
						eq(TENANT), any(), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of()));

		var emBranco = servico.listarProdutosVisiveis(new BuscaProduto("   ", "Sementes"), Paginacao.de(1, 10));
		assertThat(emBranco.itens()).isNotEmpty();
		var longo = servico.listarProdutosVisiveis(new BuscaProduto("x".repeat(4000), null), Paginacao.de(1, 10));
		assertThat(longo.itens()).isEmpty();
	}

	@Test
	void ut018_segundaCategoriaNaoMisturaAPrimeira() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseAndCategoriaOrderByNomeAsc(
						eq(TENANT), eq("Sementes"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(produto(
						SEM_IMAGEM, "Semente", "desc", "Sementes", "Saco", "10.00", true, false, null))));
		when(repositorio.findByIdTenantAndReguladoFalseAndCategoriaOrderByNomeAsc(
						eq(TENANT), eq("Fertilizantes"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(ureiaContrato())));

		servico.listarProdutosVisiveis(new BuscaProduto(null, "Sementes"), Paginacao.de(1, 10));
		var segunda = servico.listarProdutosVisiveis(new BuscaProduto(null, "Fertilizantes"), Paginacao.de(1, 10));
		assertThat(segunda.itens()).allMatch(item -> item.categoria().equals("Fertilizantes"));
	}

	@Test
	void ut019_itemExpoeCamposDoContrato() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseAndNomeContainingIgnoreCaseOrderByNomeAsc(
						eq(TENANT), eq("ureia"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(ureiaContrato())));

		ResumoProduto item = servico.listarProdutosVisiveis(new BuscaProduto("ureia"), Paginacao.de(1, 10))
				.itens()
				.getFirst();
		assertThat(item.nome()).isEqualTo("Ureia agrícola 50 kg");
		assertThat(item.categoria()).isEqualTo("Fertilizantes");
		assertThat(item.descricaoCurta()).isEqualTo("Fonte nitrogenada para cobertura.");
		assertThat(item.unidade()).isEqualTo("Saco");
		assertThat(item.precoUnitario()).isEqualByComparingTo("198.00");
		assertThat(item.disponivel()).isTrue();
		assertThat(item.urlImagem()).isEqualTo("/media/products/ureia-50kg.jpg");
	}

	@Test
	void ut020_imagemAusenteOuInseguraFicaNula() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		Produto semImagem = produto(
				SEM_IMAGEM, "Semente de milho DK697", "Híbrido", "Sementes", "sc 60 kg", "890.00", true, false, null);
		Produto insegura = produto(
				UUID.fromString("10000000-0000-4000-8000-000000000014"),
				"NPK",
				"desc",
				"Fertilizantes",
				"sc 50 kg",
				"215.00",
				true,
				false,
				"javascript:alert(1)");
		when(repositorio.findByIdTenantAndReguladoFalseOrderByNomeAsc(eq(TENANT), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(semImagem, insegura)));

		List<ResumoProduto> itens = servico.listarProdutosVisiveis(new BuscaProduto(null), Paginacao.de(1, 10)).itens();
		assertThat(itens.get(0).urlImagem()).isNull();
		assertThat(itens.get(1).urlImagem()).isNull();
	}

	@Test
	void ut021_reguladoOuInexistenteNaoRevelaItem() {
		when(repositorio.findByIdAndIdTenant(REGULADO, TENANT))
				.thenReturn(Optional.of(produto(
						REGULADO, "Herbicida", "Regulado", "Defensivos", "L", "42.00", true, true, null)));
		when(repositorio.findByIdAndIdTenant(SEM_IMAGEM, TENANT)).thenReturn(Optional.empty());

		assertThat(servico.obterProdutoVisivel(TENANT, REGULADO)).isEmpty();
		assertThat(servico.obterProdutoVisivel(TENANT, SEM_IMAGEM)).isEmpty();
	}

	@Test
	void ut022_categoriaValidaSemElegiveis() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseAndCategoriaOrderByNomeAsc(
						eq(TENANT), eq("Correção"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of()));

		var pagina = servico.listarProdutosVisiveis(new BuscaProduto(null, "Correção"), Paginacao.de(1, 10));
		assertThat(pagina.itens()).isEmpty();
		assertThat(pagina.total()).isZero();
	}

	@Test
	void ut023_consultaUreiaPermaneceAoTrocarCategoria() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseAndCategoriaAndNomeContainingIgnoreCaseOrderByNomeAsc(
						eq(TENANT), eq("Sementes"), eq("ureia"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of()));
		when(repositorio.findByIdTenantAndReguladoFalseAndCategoriaAndNomeContainingIgnoreCaseOrderByNomeAsc(
						eq(TENANT), eq("Fertilizantes"), eq("ureia"), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(ureiaContrato())));

		servico.listarProdutosVisiveis(new BuscaProduto("ureia", "Sementes"), Paginacao.de(1, 10));
		var fertilizantes =
				servico.listarProdutosVisiveis(new BuscaProduto("ureia", "Fertilizantes"), Paginacao.de(1, 10));
		assertThat(fertilizantes.itens()).allMatch(item -> item.categoria().equals("Fertilizantes"));
		assertThat(fertilizantes.itens().getFirst().nome()).containsIgnoringCase("ureia");
	}

	@Test
	void ut024_categoriasListadasSaoSoAsVisiveis() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseOrderByNomeAsc(eq(TENANT), any(Pageable.class)))
				.thenReturn(new PageImpl<>(trintaElegiveis(), Pageable.ofSize(50), 30));

		var pagina = servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(1, 50));
		assertThat(pagina.itens())
				.extracting(ResumoProduto::categoria)
				.containsOnly("Sementes", "Fertilizantes", "Correção");
	}

	@Test
	void ut073_duasPaginasSeguintesNaoDuplicamIds() {
		simularTrintaPaginados();
		var primeira = servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(1, 10));
		var segundaA = servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(2, 10));
		var segundaB = servico.listarProdutosVisiveis(new BuscaProduto(null, null), Paginacao.de(2, 10));
		var acumulado = new java.util.LinkedHashSet<UUID>();
		acumulado.addAll(ids(primeira.itens()));
		acumulado.addAll(ids(segundaA.itens()));
		acumulado.addAll(ids(segundaB.itens()));
		assertThat(acumulado).hasSize(20);
		assertThat(ids(segundaA.itens())).containsExactlyElementsOf(ids(segundaB.itens()));
	}

	private void simularTrintaPaginados() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		when(repositorio.findByIdTenantAndReguladoFalseOrderByNomeAsc(eq(TENANT), any(Pageable.class)))
				.thenAnswer(invocacao -> fatia(invocacao.getArgument(1), trintaElegiveis()));
		when(repositorio.findByIdTenantAndReguladoFalseAndCategoriaAndNomeContainingIgnoreCaseOrderByNomeAsc(
						eq(TENANT), eq("Fertilizantes"), eq("ureia"), any(Pageable.class)))
				.thenAnswer(invocacao -> fatia(invocacao.getArgument(3), trintaElegiveis()));
	}

	private static PageImpl<Produto> fatia(Pageable pageable, List<Produto> todos) {
		int de = pageable.getPageNumber() * pageable.getPageSize();
		int ate = Math.min(de + pageable.getPageSize(), todos.size());
		List<Produto> fatia = de >= todos.size() ? List.of() : todos.subList(de, ate);
		return new PageImpl<>(fatia, pageable, todos.size());
	}

	private static List<UUID> ids(List<ResumoProduto> itens) {
		return itens.stream().map(ResumoProduto::id).collect(Collectors.toList());
	}

	private static Produto ureiaContrato() {
		return produto(
				UREIA,
				"Ureia agrícola 50 kg",
				"Fonte nitrogenada para cobertura.",
				"Fertilizantes",
				"Saco",
				"198.00",
				true,
				false,
				"/media/products/ureia-50kg.jpg");
	}

	private static List<Produto> trintaElegiveis() {
		List<Produto> produtos = new ArrayList<>();
		String[] categorias = {"Sementes", "Fertilizantes", "Correção"};
		for (int i = 0; i < 30; i++) {
			UUID id = UUID.fromString("10000000-0000-4000-8000-" + String.format("%012d", i + 1));
			produtos.add(produto(
					id,
					"Produto " + i,
					"desc",
					categorias[i % 3],
					"Saco",
					"10.00",
					true,
					false,
					null));
		}
		return produtos;
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
