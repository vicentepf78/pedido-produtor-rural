package br.agriplataforma.cart.infrastructure;

import br.agriplataforma.cart.domain.Carrinho;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioCarrinho extends JpaRepository<Carrinho, UUID> {

	Optional<Carrinho> findByIdTenantAndChaveProprietario(UUID idTenant, String chaveProprietario);
}
