package br.agriplataforma.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.agriplataforma.identity.application.ComandoCadastro;
import br.agriplataforma.identity.application.ComandoEntrada;
import br.agriplataforma.identity.application.ExcecaoAutenticacao;
import br.agriplataforma.identity.application.ServicoIdentidade;
import br.agriplataforma.identity.domain.Usuario;
import br.agriplataforma.identity.infrastructure.RepositorioUsuario;
import br.agriplataforma.tenant.application.ConsultaTenant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CadastroAutenticacaoTest {

	private static final UUID TENANT = UUID.fromString("11111111-1111-1111-1111-111111111111");
	private static final String SENHA = "Rural#2026Order";

	@Mock
	RepositorioUsuario repositorio;

	@Mock
	PasswordEncoder senhas;

	@Mock
	ConsultaTenant consultaTenant;

	ServicoIdentidade servico;

	@BeforeEach
	void preparar() {
		when(consultaTenant.idTenantConfigurado()).thenReturn(TENANT);
		servico = new ServicoIdentidade(repositorio, senhas, consultaTenant);
	}

	@Test
	void ut016_cadastroEmailDuplicadoNaoExpoeSenha() {
		when(repositorio.existsByIdTenantAndEmail(TENANT, "joao.silva@example.com")).thenReturn(true);

		assertThatThrownBy(() ->
						servico.cadastrar(new ComandoCadastro("João da Silva", "joao.silva@example.com", SENHA)))
				.isInstanceOf(ExcecaoAutenticacao.class)
				.satisfies(excecao -> {
					ExcecaoAutenticacao autenticacao = (ExcecaoAutenticacao) excecao;
					assertThat(autenticacao.codigo()).isEqualTo("EMAIL_DUPLICADO");
					assertThat(autenticacao.getMessage()).doesNotContain(SENHA);
					assertThat(autenticacao.getMessage()).doesNotContainIgnoringCase("senhaHash");
					assertThat(autenticacao.getMessage()).doesNotContain("$2");
				});

		verify(repositorio, never()).save(any());
		verify(senhas, never()).encode(any());
	}

	@Test
	void ut017_credenciaisInvalidasNaoExpoeSenhaNemHash() {
		when(repositorio.findByIdTenantAndEmail(TENANT, "produtor.alfa@example.com")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> servico.entrar(new ComandoEntrada("produtor.alfa@example.com", SENHA)))
				.isInstanceOf(ExcecaoAutenticacao.class)
				.satisfies(excecao -> {
					ExcecaoAutenticacao autenticacao = (ExcecaoAutenticacao) excecao;
					assertThat(autenticacao.codigo()).isEqualTo("CREDENCIAIS_INVALIDAS");
					assertThat(autenticacao.getMessage()).isEqualTo("E-mail ou senha inválidos.");
					assertThat(autenticacao.getMessage()).doesNotContain(SENHA);
					assertThat(autenticacao.getMessage()).doesNotContain("senhaHash");
				});

		Usuario existente = Usuario.novo(TENANT, "Produtor Alfa", "produtor.alfa@example.com", "$2hash-secreto", "PRODUTOR");
		when(repositorio.findByIdTenantAndEmail(TENANT, "produtor.alfa@example.com"))
				.thenReturn(Optional.of(existente));
		when(senhas.matches(SENHA, "$2hash-secreto")).thenReturn(false);

		assertThatThrownBy(() -> servico.entrar(new ComandoEntrada("produtor.alfa@example.com", SENHA)))
				.isInstanceOf(ExcecaoAutenticacao.class)
				.satisfies(excecao -> {
					assertThat(excecao.getMessage()).doesNotContain(SENHA);
					assertThat(excecao.getMessage()).doesNotContain("$2hash-secreto");
				});
	}
}
