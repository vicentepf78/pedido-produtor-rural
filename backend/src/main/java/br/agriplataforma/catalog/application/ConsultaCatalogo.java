package br.agriplataforma.catalog.application;

import java.util.Optional;
import java.util.UUID;

public interface ConsultaCatalogo {

	Pagina<ResumoProduto> listarProdutosVisiveis(BuscaProduto busca, Paginacao pagina);

	ProdutoParaCarrinho exigirProdutoPedivel(UUID idTenant, UUID idProduto);

	Optional<ResumoProduto> obterProdutoVisivel(UUID idTenant, UUID idProduto);
}
