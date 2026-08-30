package br.agriplataforma.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.agriplataforma.identity.application.ComandoCadastro;
import br.agriplataforma.identity.application.ComandoEntrada;
import br.agriplataforma.identity.application.ExcecaoAutenticacao;
import br.agriplataforma.identity.application.Papel;
import br.agriplataforma.identity.application.ServicoIdentidade;
import br.agriplataforma.identity.application.UsuarioAutenticado;
import br.agriplataforma.identity.domain.Usuario;
import br.agriplataforma.identity.infrastructure.RepositorioUsuario;
import br.agriplataforma.tenant.application.ConsultaTenant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CadastroAutenticacaoTest {

	private static final UUID TENANT = UUID.fromString("11111111-1111-1111-1111-111111111111");
	private static final UUID ALFA = UUID.fromString("33333333-3333-3333-3333-333333333333");
	private static final UUID OPERADOR = UUID.fromString("44444444-4444-4444-4444-444444444444");
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
		SecurityContextHolder.clearContext();
	}

	@Test
	void ut025_usuarioAtualSemSessaoVazio() {
		assertThat(servico.usuarioAtual()).isEmpty();
	}

	@Test
	void ut026_usuarioAtualAlfa() {
		var usuario = new UsuarioAutenticado(ALFA, TENANT, "Produtor Alfa", "produtor.alfa@example.com", Papel.PRODUTOR);
		autenticar(usuario);

		assertThat(servico.usuarioAtual()).contains(usuario);
		assertThat(servico.usuarioAtual().orElseThrow().email()).isEqualTo("produtor.alfa@example.com");
		assertThat(servico.usuarioAtual().orElseThrow().papel()).isEqualTo(Papel.PRODUTOR);
		assertThat(usuario.toString()).doesNotContain(SENHA);
	}

	@Test
	void ut027_usuarioAtualOperador() {
		var usuario = new UsuarioAutenticado(
				OPERADOR, TENANT, "Operador Demonstracao", "operador.revenda@example.com", Papel.OPERADOR_REVENDA);
		autenticar(usuario);

		assertThat(servico.usuarioAtual().orElseThrow().email()).isEqualTo("operador.revenda@example.com");
		assertThat(servico.usuarioAtual().orElseThrow().papel()).isEqualTo(Papel.OPERADOR_REVENDA);
	}

	@Test
	void ut028_exigirAutenticadoSemSessao() {
		assertThatThrownBy(servico::exigirAutenticado)
				.isInstanceOf(ExcecaoAutenticacao.class)
				.satisfies(excecao -> {
					ExcecaoAutenticacao autenticacao = (ExcecaoAutenticacao) excecao;
					assertThat(autenticacao.codigo()).isEqualTo("NAO_AUTENTICADO");
					assertThat(autenticacao.getMessage()).isEqualTo("Entre ou crie uma conta para continuar.");
				});
	}

	@Test
	void ut029_cadastroCriaSomenteProdutor() {
		when(repositorio.existsByIdTenantAndEmail(TENANT, "joao.silva@example.com")).thenReturn(false);
		when(senhas.encode(SENHA)).thenReturn("$2hash");
		when(repositorio.save(any(Usuario.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

		UsuarioAutenticado criado =
				servico.cadastrar(new ComandoCadastro("João da Silva", "joao.silva@example.com", SENHA));

		assertThat(criado.papel()).isEqualTo(Papel.PRODUTOR);
		assertThat(criado.email()).isEqualTo("joao.silva@example.com");
		assertThat(criado.nome()).isEqualTo("João da Silva");
		ArgumentCaptor<Usuario> salvo = ArgumentCaptor.forClass(Usuario.class);
		verify(repositorio).save(salvo.capture());
		assertThat(salvo.getValue().papel()).isEqualTo("PRODUTOR");
	}

	@Test
	void ut030_emailDuplicadoNaoExpoeSenha() {
		when(repositorio.existsByIdTenantAndEmail(TENANT, "produtor.alfa@example.com")).thenReturn(true);

		assertThatThrownBy(() ->
						servico.cadastrar(new ComandoCadastro("Alfa", "produtor.alfa@example.com", SENHA)))
				.isInstanceOf(ExcecaoAutenticacao.class)
				.satisfies(excecao -> {
					ExcecaoAutenticacao autenticacao = (ExcecaoAutenticacao) excecao;
					assertThat(autenticacao.codigo()).isEqualTo("EMAIL_DUPLICADO");
					assertThat(autenticacao.getMessage())
							.isEqualTo("Este e-mail já está cadastrado. Entre com sua senha ou use outro e-mail.");
					assertThat(autenticacao.getMessage()).doesNotContain(SENHA);
					assertThat(autenticacao.getMessage()).doesNotContain("$2");
				});
		verify(repositorio, never()).save(any());
	}

	@Test
	void ut031_credenciaisInvalidas() {
		when(repositorio.findByIdTenantAndEmail(TENANT, "produtor.alfa@example.com")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> servico.entrar(new ComandoEntrada("produtor.alfa@example.com", "senha-errada")))
				.isInstanceOf(ExcecaoAutenticacao.class)
				.satisfies(excecao -> {
					ExcecaoAutenticacao autenticacao = (ExcecaoAutenticacao) excecao;
					assertThat(autenticacao.codigo()).isEqualTo("CREDENCIAIS_INVALIDAS");
					assertThat(autenticacao.getMessage()).isEqualTo("E-mail ou senha inválidos.");
				});
	}

	@Test
	void ut032_camposVaziosNaoCriamSessao() {
		assertThatThrownBy(() -> servico.cadastrar(new ComandoCadastro("João", "", SENHA)))
				.isInstanceOf(ExcecaoAutenticacao.class)
				.satisfies(excecao -> assertThat(excecao.getMessage()).containsIgnoringCase("e-mail"));
		assertThatThrownBy(() -> servico.entrar(new ComandoEntrada("produtor.alfa@example.com", " ")))
				.isInstanceOf(ExcecaoAutenticacao.class)
				.satisfies(excecao -> assertThat(excecao.getMessage()).containsIgnoringCase("senha"));
		verify(repositorio, never()).save(any());
	}

	@Test
	void ut033_doisEntrarConsecutivosMesmoUsuario() {
		Usuario existente = Usuario.novo(TENANT, "Produtor Alfa", "produtor.alfa@example.com", "$2hash", "PRODUTOR");
		when(repositorio.findByIdTenantAndEmail(TENANT, "produtor.alfa@example.com")).thenReturn(Optional.of(existente));
		when(senhas.matches(SENHA, "$2hash")).thenReturn(true);

		UsuarioAutenticado primeiro = servico.entrar(new ComandoEntrada("produtor.alfa@example.com", SENHA));
		UsuarioAutenticado segundo = servico.entrar(new ComandoEntrada("produtor.alfa@example.com", SENHA));

		assertThat(primeiro.id()).isEqualTo(segundo.id());
		assertThat(primeiro.email()).isEqualTo("produtor.alfa@example.com");
	}

	@Test
	void ut034_entrarComSessaoAtivaPermaneceProdutor() {
		Usuario existente = Usuario.novo(TENANT, "Produtor Alfa", "produtor.alfa@example.com", "$2hash", "PRODUTOR");
		when(repositorio.findByIdTenantAndEmail(TENANT, "produtor.alfa@example.com")).thenReturn(Optional.of(existente));
		when(senhas.matches(SENHA, "$2hash")).thenReturn(true);
		autenticar(new UsuarioAutenticado(existente.id(), TENANT, existente.nome(), existente.email(), Papel.PRODUTOR));

		UsuarioAutenticado deNovo = servico.entrar(new ComandoEntrada("produtor.alfa@example.com", SENHA));
		assertThat(deNovo.papel()).isEqualTo(Papel.PRODUTOR);
		verify(repositorio, never()).save(any());
		verify(repositorio, times(1)).findByIdTenantAndEmail(TENANT, "produtor.alfa@example.com");
	}

	private static void autenticar(UsuarioAutenticado usuario) {
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(usuario, null, java.util.List.of()));
	}
}
