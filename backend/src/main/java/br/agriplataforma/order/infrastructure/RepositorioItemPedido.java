package br.agriplataforma.order.infrastructure;

import br.agriplataforma.order.domain.ItemPedido;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioItemPedido extends JpaRepository<ItemPedido, UUID> {

	List<ItemPedido> findByIdPedidoOrderByIdAsc(UUID idPedido);
}
