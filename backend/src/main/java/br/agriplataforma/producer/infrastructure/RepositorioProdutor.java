package br.agriplataforma.producer.infrastructure;

import br.agriplataforma.producer.domain.Produtor;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioProdutor extends JpaRepository<Produtor, UUID> {

	Optional<Produtor> findByIdTenantAndIdUsuario(UUID idTenant, UUID idUsuario);
}
