package br.agriplataforma.producer.infrastructure;

import br.agriplataforma.producer.domain.Propriedade;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioPropriedade extends JpaRepository<Propriedade, UUID> {

	List<Propriedade> findByIdTenantAndIdProdutorOrderByNome(UUID idTenant, UUID idProdutor);
}
