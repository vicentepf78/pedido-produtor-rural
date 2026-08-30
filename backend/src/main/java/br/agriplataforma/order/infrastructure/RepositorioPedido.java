package br.agriplataforma.order.infrastructure;

import br.agriplataforma.order.domain.Pedido;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioPedido extends JpaRepository<Pedido, UUID> {

	Optional<Pedido> findByIdTenantAndIdProdutorAndChaveIdempotencia(
			UUID idTenant, UUID idProdutor, String chaveIdempotencia);

	Page<Pedido> findByIdTenantAndIdProdutor(UUID idTenant, UUID idProdutor, Pageable pagina);
}
