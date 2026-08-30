package br.agriplataforma.cart.infrastructure;

import br.agriplataforma.cart.domain.ItemCarrinho;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioItemCarrinho extends JpaRepository<ItemCarrinho, UUID> {

	List<ItemCarrinho> findByIdCarrinhoOrderByIdAsc(UUID idCarrinho);

	Optional<ItemCarrinho> findByIdCarrinhoAndIdProduto(UUID idCarrinho, UUID idProduto);

	void deleteByIdCarrinhoAndIdProduto(UUID idCarrinho, UUID idProduto);
}
