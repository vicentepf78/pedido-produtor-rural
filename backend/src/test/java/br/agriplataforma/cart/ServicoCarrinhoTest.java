package br.agriplataforma.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.agriplataforma.cart.application.ExcecaoCarrinho;
import br.agriplataforma.cart.application.ServicoCarrinho;
import br.agriplataforma.cart.application.VisaoCarrinho;
import br.agriplataforma.cart.domain.Carrinho;
import br.agriplataforma.cart.domain.ItemCarrinho;
import br.agriplataforma.cart.infrastructure.RepositorioCarrinho;
import br.agriplataforma.cart.infrastructure.RepositorioItemCarrinho;
import br.agriplataforma.catalog.application.ConsultaCatalogo;
import br.agriplataforma.catalog.application.ExcecaoCatalogo;
import br.agriplataforma.catalog.application.ProdutoParaCarrinho;
import br.agriplataforma.catalog.application.ResumoProduto;
import br.agriplataforma.tenant.application.ConsultaTenant;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ServicoCarrinhoTest {

	static final UUID TENANT = UUID.fromString("11111111-1111-1111-1111-111111111111");
	static final UUID AURORA = UUID.fromString("10000000-0000-4000-8000-000000000001");
	static final UUID NPK = UUID.fromString("10000000-0000-4000-8000-000000000014");
	static final String CHAVE = "convidado-abc";

	@Mock
	RepositorioCarrinho repositorioCarrinho;

	@Mock
	RepositorioItemCarrinho repositorioItens;

	@Mock
	ConsultaCatalogo consultaCatalogo;

	@Mock
	ConsultaTenant consultaTenant;

	final Map<UUID, ItemCarrinho> itens = new LinkedHashMap<>();
	Carrinho carrinhoPersistido;

	ServicoCarrinho servico;

	@BeforeEach
	void preparar() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		servico = new ServicoCarrinho(repositorioCarrinho, repositorioItens, consultaCatalogo, consultaTenant);
		when(repositorioCarrinho.findByIdTenantAndChaveProprietario(TENANT, CHAVE))
				.thenAnswer(inv -> Optional.ofNullable(carrinhoPersistido));
		when(repositorioCarrinho.save(any(Carrinho.class))).thenAnswer(inv -> {
			carrinhoPersistido = inv.getArgument(0);
			return carrinhoPersistido;
		});
		when(repositorioItens.findByIdCarrinhoOrderByIdAsc(any())).thenAnswer(inv -> new ArrayList<>(itens.values()));
		when(repositorioItens.findByIdCarrinhoAndIdProduto(any(), any())).thenAnswer(inv -> {
			UUID idProduto = inv.getArgument(1);
			return Optional.ofNullable(itens.get(idProduto));
		});
		when(repositorioItens.save(any(ItemCarrinho.class))).thenAnswer(inv -> {
			ItemCarrinho item = inv.getArgument(0);
			itens.put(item.idProduto(), item);
			return item;
		});
		org.mockito.Mockito.doAnswer((Answer<Void>) inv -> {
					UUID idProduto = inv.getArgument(1);
					itens.remove(idProduto);
					return null;
				})
				.when(repositorioItens)
				.deleteByIdCarrinhoAndIdProduto(any(), any());
	}

	@Test
	void ut036_duasUnidadesCalculamTotalDaLinhaEDoCarrinho() {
		auroraDisponivel();

		VisaoCarrinho visao = servico.adicionar(CHAVE, AURORA, new BigDecimal("2"));

		assertThat(visao.itens()).hasSize(1);
		assertThat(visao.itens().getFirst().quantidade()).isEqualTo(2);
		assertThat(visao.itens().getFirst().precoUnitario()).isEqualByComparingTo("620.00");
		assertThat(visao.itens().getFirst().totalLinha()).isEqualByComparingTo("1240.00");
		assertThat(visao.total()).isEqualByComparingTo("1240.00");
	}

	@Test
	void ut037_alterarQuantidadeRecalculaTotal() {
		auroraDisponivel();
		servico.adicionar(CHAVE, AURORA, new BigDecimal("2"));

		VisaoCarrinho visao = servico.alterarQuantidade(CHAVE, AURORA, new BigDecimal("3"));

		assertThat(visao.itens().getFirst().quantidade()).isEqualTo(3);
		assertThat(visao.itens().getFirst().totalLinha()).isEqualByComparingTo("1860.00");
		assertThat(visao.total()).isEqualByComparingTo("1860.00");
	}

	@Test
	void ut038_removerLinhaFinalProduzCarrinhoVazio() {
		auroraDisponivel();
		servico.adicionar(CHAVE, AURORA, BigDecimal.ONE);

		VisaoCarrinho visao = servico.remover(CHAVE, AURORA);

		assertThat(visao.itens()).isEmpty();
		assertThat(visao.total()).isEqualByComparingTo("0.00");
	}

	@ParameterizedTest
	@ValueSource(strings = {"0", "-1", "1.5"})
	void ut039_quantidadeInvalida(String quantidade) {
		assertThatThrownBy(() -> servico.adicionar(CHAVE, AURORA, new BigDecimal(quantidade)))
				.isInstanceOf(ExcecaoCarrinho.class)
				.satisfies(excecao -> {
					ExcecaoCarrinho carrinho = (ExcecaoCarrinho) excecao;
					assertThat(carrinho.codigo()).isEqualTo("QUANTIDADE_INVALIDA");
					assertThat(carrinho.getMessage()).isEqualTo("Informe uma quantidade inteira positiva.");
				});
		verify(repositorioItens, never()).save(any());
	}

	@Test
	void ut041_mesmoProdutoConsolidaUmaLinha() {
		auroraDisponivel();
		servico.adicionar(CHAVE, AURORA, BigDecimal.ONE);
		VisaoCarrinho visao = servico.adicionar(CHAVE, AURORA, new BigDecimal("2"));

		assertThat(visao.itens()).hasSize(1);
		assertThat(visao.itens().getFirst().quantidade()).isEqualTo(3);
		assertThat(visao.itens().getFirst().totalLinha()).isEqualByComparingTo("1860.00");
	}

	@Test
	void ut042_cemItensMantemQuantidadeETotal() {
		List<UUID> ids = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			UUID id = UUID.fromString("00000000-0000-4000-8000-" + String.format("%012d", i));
			ids.add(id);
			BigDecimal preco = new BigDecimal("1.25");
			when(consultaCatalogo.exigirProdutoPedivel(TENANT, id))
					.thenReturn(new ProdutoParaCarrinho(id, "Produto " + i, "un", preco));
			when(consultaCatalogo.obterProdutoVisivel(TENANT, id))
					.thenReturn(Optional.of(new ResumoProduto(
							id, "Produto " + i, "Sementes", "desc", "un", preco, true, null)));
		}

		for (int i = 0; i < 100; i++) {
			servico.adicionar(CHAVE, ids.get(i), BigDecimal.valueOf((i % 5) + 1));
		}

		VisaoCarrinho visao = servico.obter(CHAVE);
		assertThat(visao.itens()).hasSize(100);
		BigDecimal esperado = BigDecimal.ZERO;
		for (int i = 0; i < 100; i++) {
			int qtd = (i % 5) + 1;
			assertThat(visao.itens().get(i).quantidade()).isEqualTo(qtd);
			esperado = esperado.add(new BigDecimal("1.25").multiply(BigDecimal.valueOf(qtd)));
		}
		assertThat(visao.total()).isEqualByComparingTo(esperado);
	}

	@Test
	void ut043_falhaNaMutacaoPreservaCarrinhoConfirmado() {
		auroraDisponivel();
		servico.adicionar(CHAVE, AURORA, new BigDecimal("2"));
		when(consultaCatalogo.exigirProdutoPedivel(TENANT, NPK))
				.thenThrow(new ExcecaoCatalogo("PRODUTO_INDISPONIVEL", "Este produto não está disponível."));

		assertThatThrownBy(() -> servico.adicionar(CHAVE, NPK, BigDecimal.ONE)).isInstanceOf(ExcecaoCatalogo.class);

		VisaoCarrinho visao = servico.obter(CHAVE);
		assertThat(visao.itens()).hasSize(1);
		assertThat(visao.itens().getFirst().idProduto()).isEqualTo(AURORA);
		assertThat(visao.itens().getFirst().quantidade()).isEqualTo(2);
		assertThat(visao.total()).isEqualByComparingTo("1240.00");
	}

	@Test
	void ut040_produtoIndisponivel() {
		when(consultaCatalogo.exigirProdutoPedivel(TENANT, NPK))
				.thenThrow(new ExcecaoCatalogo("PRODUTO_INDISPONIVEL", "Este produto não está disponível."));
		assertThatThrownBy(() -> servico.adicionar(CHAVE, NPK, BigDecimal.ONE))
				.isInstanceOf(ExcecaoCatalogo.class)
				.satisfies(excecao -> {
					assertThat(((ExcecaoCatalogo) excecao).codigo()).isEqualTo("PRODUTO_INDISPONIVEL");
					assertThat(excecao.getMessage()).isEqualTo("Este produto não está disponível.");
				});
	}

	@Test
	void ut044_duasAlteracoesConvergem() {
		auroraDisponivel();
		servico.adicionar(CHAVE, AURORA, new BigDecimal("2"));
		servico.alterarQuantidade(CHAVE, AURORA, new BigDecimal("3"));
		VisaoCarrinho visao = servico.alterarQuantidade(CHAVE, AURORA, new BigDecimal("1"));
		assertThat(visao.itens()).hasSize(1);
		assertThat(visao.itens().getFirst().quantidade()).isEqualTo(1);
	}

	@Test
	void ut045_precoUreiaPermaneceAposIdentidade() {
		UUID ureia = UUID.fromString("10000000-0000-4000-8000-000000000015");
		when(consultaCatalogo.exigirProdutoPedivel(TENANT, ureia))
				.thenReturn(new ProdutoParaCarrinho(ureia, "Ureia agrícola 50 kg", "Saco", new BigDecimal("198.00")));
		when(consultaCatalogo.obterProdutoVisivel(TENANT, ureia))
				.thenReturn(Optional.of(new ResumoProduto(
						ureia, "Ureia agrícola 50 kg", "Fertilizantes", "desc", "Saco", new BigDecimal("198.00"), true, null)));
		VisaoCarrinho visao = servico.adicionar(CHAVE, ureia, new BigDecimal("2"));
		assertThat(visao.itens().getFirst().precoUnitario()).isEqualByComparingTo("198.00");
		assertThat(visao.total()).isEqualByComparingTo("396.00");
	}

	@Test
	void ut046_reguladoNaoEntraNoCarrinho() {
		UUID regulado = UUID.fromString("10000000-0000-4000-8000-000000000099");
		when(consultaCatalogo.exigirProdutoPedivel(TENANT, regulado))
				.thenThrow(new ExcecaoCatalogo("PRODUTO_NAO_ELEGIVEL", "Este produto não pode ser pedido nesta loja."));
		assertThatThrownBy(() -> servico.adicionar(CHAVE, regulado, BigDecimal.ONE))
				.isInstanceOf(ExcecaoCatalogo.class)
				.satisfies(excecao -> assertThat(((ExcecaoCatalogo) excecao).codigo()).isEqualTo("PRODUTO_NAO_ELEGIVEL"));
	}

	private void auroraDisponivel() {
		when(consultaCatalogo.exigirProdutoPedivel(TENANT, AURORA))
				.thenReturn(new ProdutoParaCarrinho(AURORA, "Semente de milho Aurora 20 kg", "Saco", new BigDecimal("620.00")));
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
	}
}
