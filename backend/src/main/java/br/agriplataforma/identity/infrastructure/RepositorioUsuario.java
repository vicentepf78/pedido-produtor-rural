package br.agriplataforma.identity.infrastructure;

import br.agriplataforma.identity.domain.Usuario;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioUsuario extends JpaRepository<Usuario, UUID> {

	boolean existsByIdTenantAndEmail(UUID idTenant, String email);

	Optional<Usuario> findByIdTenantAndEmail(UUID idTenant, String email);
}
