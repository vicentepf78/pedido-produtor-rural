package br.agriplataforma.producer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import br.agriplataforma.identity.application.ConsultaIdentidade;
import br.agriplataforma.identity.application.Papel;
import br.agriplataforma.identity.application.UsuarioAutenticado;
import br.agriplataforma.producer.application.ResumoPropriedade;
import br.agriplataforma.producer.application.ServicoProdutor;
import br.agriplataforma.producer.domain.Produtor;
import br.agriplataforma.producer.domain.Propriedade;
import br.agriplataforma.producer.infrastructure.RepositorioProdutor;
import br.agriplataforma.producer.infrastructure.RepositorioPropriedade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicoProdutorTest {

	static final UUID TENANT = UUID.fromString("11111111-1111-1111-1111-111111111111");
	static final UUID ALFA = UUID.fromString("33333333-3333-3333-3333-333333333333");
	static final UUID ID_PRODUTOR = UUID.fromString("44444444-4444-4444-4444-444444444444");
	static final UUID NORTE = UUID.fromString("55555555-5555-5555-5555-555555555555");
	static final UUID RECANTO = UUID.fromString("55555555-5555-5555-5555-555555555556");
	static final UUID SUL = UUID.fromString("66666666-6666-6666-6666-666666666666");

	@Mock
	ConsultaIdentidade consultaIdentidade;

	@Mock
	RepositorioProdutor repositorioProdutor;

	@Mock
	RepositorioPropriedade repositorioPropriedade;

	ServicoProdutor servico;

	@BeforeEach
	void preparar() {
		servico = new ServicoProdutor(consultaIdentidade, repositorioProdutor, repositorioPropriedade);
		when(consultaIdentidade.exigirAutenticado())
				.thenReturn(new UsuarioAutenticado(
						ALFA, TENANT, "Produtor Alfa", "produtor.alfa@example.com", Papel.PRODUTOR));
		when(repositorioProdutor.findByIdTenantAndIdUsuario(TENANT, ALFA))
				.thenReturn(Optional.of(new Produtor(ID_PRODUTOR, TENANT, ALFA)));
	}

	@Test
	void ut063_registrarFazendaSantaLuzia() {
		when(repositorioPropriedade.save(any(Propriedade.class))).thenAnswer(inv -> inv.getArgument(0));
		when(repositorioPropriedade.findByIdTenantAndIdProdutorOrderByNome(TENANT, ID_PRODUTOR))
				.thenReturn(List.of(new Propriedade(
						UUID.randomUUID(), TENANT, ID_PRODUTOR, "Fazenda Santa Luzia")));

		ResumoPropriedade criada = servico.registrar("Fazenda Santa Luzia");
		assertThat(criada.nome()).isEqualTo("Fazenda Santa Luzia");
		assertThat(servico.listarDoAutenticado().stream().map(ResumoPropriedade::nome))
				.contains("Fazenda Santa Luzia");
	}

	@Test
	void ut064_alfaNaoListaFazendaSul() {
		when(repositorioPropriedade.findByIdTenantAndIdProdutorOrderByNome(TENANT, ID_PRODUTOR))
				.thenReturn(List.of(
						new Propriedade(NORTE, TENANT, ID_PRODUTOR, "Fazenda Norte"),
						new Propriedade(RECANTO, TENANT, ID_PRODUTOR, "Sitio Recanto")));

		List<ResumoPropriedade> propriedades = servico.listarDoAutenticado();
		assertThat(propriedades.stream().map(ResumoPropriedade::nome))
				.containsExactly("Fazenda Norte", "Sitio Recanto");
		assertThat(propriedades.stream().map(ResumoPropriedade::id)).doesNotContain(SUL);
	}
}
