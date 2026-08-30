package br.agriplataforma.producer.application;

import br.agriplataforma.identity.application.ConsultaIdentidade;
import br.agriplataforma.identity.application.Papel;
import br.agriplataforma.identity.application.UsuarioAutenticado;
import br.agriplataforma.producer.domain.Produtor;
import br.agriplataforma.producer.domain.Propriedade;
import br.agriplataforma.producer.infrastructure.RepositorioProdutor;
import br.agriplataforma.producer.infrastructure.RepositorioPropriedade;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicoProdutor implements ConsultaPropriedades {

	private final ConsultaIdentidade consultaIdentidade;
	private final RepositorioProdutor repositorioProdutor;
	private final RepositorioPropriedade repositorioPropriedade;

	public ServicoProdutor(
			ConsultaIdentidade consultaIdentidade,
			RepositorioProdutor repositorioProdutor,
			RepositorioPropriedade repositorioPropriedade) {
		this.consultaIdentidade = consultaIdentidade;
		this.repositorioProdutor = repositorioProdutor;
		this.repositorioPropriedade = repositorioPropriedade;
	}

	@Override
	@Transactional
	public List<ResumoPropriedade> listarDoAutenticado() {
		UsuarioAutenticado usuario = consultaIdentidade.exigirAutenticado();
		if (usuario.papel() != Papel.PRODUTOR) {
			throw new AccessDeniedException("Somente o produtor pode listar propriedades.");
		}
		Produtor produtor = repositorioProdutor
				.findByIdTenantAndIdUsuario(usuario.idTenant(), usuario.id())
				.orElseGet(() -> repositorioProdutor.save(Produtor.novo(usuario.idTenant(), usuario.id())));
		return repositorioPropriedade
				.findByIdTenantAndIdProdutorOrderByNome(usuario.idTenant(), produtor.id())
				.stream()
				.map(ServicoProdutor::resumo)
				.toList();
	}

	private static ResumoPropriedade resumo(Propriedade propriedade) {
		return new ResumoPropriedade(propriedade.id(), propriedade.nome());
	}
}
