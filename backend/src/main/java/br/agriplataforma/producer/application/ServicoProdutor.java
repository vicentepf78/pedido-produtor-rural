package br.agriplataforma.producer.application;

import br.agriplataforma.identity.application.ConsultaIdentidade;
import br.agriplataforma.identity.application.Papel;
import br.agriplataforma.identity.application.UsuarioAutenticado;
import br.agriplataforma.producer.domain.Produtor;
import br.agriplataforma.producer.domain.Propriedade;
import br.agriplataforma.producer.infrastructure.RepositorioProdutor;
import br.agriplataforma.producer.infrastructure.RepositorioPropriedade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicoProdutor implements ConsultaPropriedades, ComandoPropriedades {

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
		Produtor produtor = exigirProdutor();
		return repositorioPropriedade
				.findByIdTenantAndIdProdutorOrderByNome(produtor.idTenant(), produtor.id())
				.stream()
				.map(ServicoProdutor::resumo)
				.toList();
	}

	@Override
	@Transactional
	public Optional<ResumoPropriedade> buscarPropria(UUID idPropriedade) {
		if (idPropriedade == null) {
			return Optional.empty();
		}
		return listarDoAutenticado().stream()
				.filter(propriedade -> propriedade.id().equals(idPropriedade))
				.findFirst();
	}

	@Override
	@Transactional
	public UUID idProdutorDoAutenticado() {
		return exigirProdutor().id();
	}

	@Override
	@Transactional
	public ResumoPropriedade registrar(String nome) {
		String nomeEfetivo = nome == null ? "" : nome.trim();
		if (nomeEfetivo.isEmpty()) {
			throw new ExcecaoProdutor("DADOS_CHECKOUT_OBRIGATORIOS", "Escolha uma propriedade e a preferência de retirada.");
		}
		Produtor produtor = exigirProdutor();
		return resumo(repositorioPropriedade.save(Propriedade.nova(produtor.idTenant(), produtor.id(), nomeEfetivo)));
	}

	private Produtor exigirProdutor() {
		UsuarioAutenticado usuario = consultaIdentidade.exigirAutenticado();
		if (usuario.papel() != Papel.PRODUTOR) {
			throw new AccessDeniedException("Somente o produtor pode listar propriedades.");
		}
		return repositorioProdutor
				.findByIdTenantAndIdUsuario(usuario.idTenant(), usuario.id())
				.orElseGet(() -> repositorioProdutor.save(Produtor.novo(usuario.idTenant(), usuario.id())));
	}

	private static ResumoPropriedade resumo(Propriedade propriedade) {
		return new ResumoPropriedade(propriedade.id(), propriedade.nome());
	}
}
