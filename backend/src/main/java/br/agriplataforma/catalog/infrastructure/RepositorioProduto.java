package br.agriplataforma.catalog.infrastructure;

import br.agriplataforma.catalog.domain.Produto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioProduto extends JpaRepository<Produto, UUID> {

	Page<Produto> findByIdTenantAndReguladoFalseOrderByNomeAsc(UUID idTenant, Pageable pageable);

	Page<Produto> findByIdTenantAndReguladoFalseAndNomeContainingIgnoreCaseOrderByNomeAsc(
			UUID idTenant, String nome, Pageable pageable);

	Optional<Produto> findByIdAndIdTenant(UUID id, UUID idTenant);
}
