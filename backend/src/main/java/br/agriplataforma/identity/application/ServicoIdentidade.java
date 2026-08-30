package br.agriplataforma.identity.application;

import br.agriplataforma.identity.domain.Usuario;
import br.agriplataforma.identity.infrastructure.RepositorioUsuario;
import br.agriplataforma.tenant.application.ConsultaTenant;
import java.util.Locale;
import java.util.Optional;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicoIdentidade implements ComandoIdentidade, ConsultaIdentidade {

	static final String EMAIL_DUPLICADO = "EMAIL_DUPLICADO";

	private final RepositorioUsuario repositorio;
	private final PasswordEncoder senhas;
	private final ConsultaTenant consultaTenant;

	public ServicoIdentidade(
			RepositorioUsuario repositorio, PasswordEncoder senhas, ConsultaTenant consultaTenant) {
		this.repositorio = repositorio;
		this.senhas = senhas;
		this.consultaTenant = consultaTenant;
	}

	@Override
	@Transactional
	public UsuarioAutenticado cadastrar(ComandoCadastro comando) {
		String email = normalizarEmail(comando.email());
		var idTenant = consultaTenant.idTenantConfigurado();
		if (repositorio.existsByIdTenantAndEmail(idTenant, email)) {
			throw new ExcecaoAutenticacao(
					EMAIL_DUPLICADO, "Este e-mail já está cadastrado. Entre com sua senha ou use outro e-mail.");
		}
		Usuario usuario = Usuario.novo(
				idTenant, comando.nome().trim(), email, senhas.encode(comando.senha()), Papel.PRODUTOR.name());
		repositorio.save(usuario);
		return paraSessao(usuario);
	}

	@Override
	@Transactional(readOnly = true)
	public UsuarioAutenticado entrar(ComandoEntrada comando) {
		String email = normalizarEmail(comando.email());
		var idTenant = consultaTenant.idTenantConfigurado();
		Usuario usuario = repositorio
				.findByIdTenantAndEmail(idTenant, email)
				.orElseThrow(ServicoIdentidade::credenciaisInvalidas);
		if (!senhas.matches(comando.senha(), usuario.senhaHash())) {
			throw credenciaisInvalidas();
		}
		return paraSessao(usuario);
	}

	@Override
	public Optional<UsuarioAutenticado> usuarioAtual() {
		Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
		if (autenticacao == null
				|| !autenticacao.isAuthenticated()
				|| autenticacao instanceof AnonymousAuthenticationToken) {
			return Optional.empty();
		}
		if (autenticacao.getPrincipal() instanceof UsuarioAutenticado usuario) {
			return Optional.of(usuario);
		}
		return Optional.empty();
	}

	@Override
	public UsuarioAutenticado exigirAutenticado() {
		return usuarioAtual()
				.orElseThrow(() -> new ExcecaoAutenticacao(
						"NAO_AUTENTICADO", "Entre ou crie uma conta para continuar."));
	}

	private static UsuarioAutenticado paraSessao(Usuario usuario) {
		return new UsuarioAutenticado(
				usuario.id(),
				usuario.idTenant(),
				usuario.nome(),
				usuario.email(),
				Papel.valueOf(usuario.papel()));
	}

	private static String normalizarEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private static ExcecaoAutenticacao credenciaisInvalidas() {
		return new ExcecaoAutenticacao("CREDENCIAIS_INVALIDAS", "E-mail ou senha inválidos.");
	}
}
