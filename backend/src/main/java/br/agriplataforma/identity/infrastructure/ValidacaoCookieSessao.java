package br.agriplataforma.identity.infrastructure;

import java.util.Arrays;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
class ValidacaoCookieSessao implements ApplicationRunner {

	private final Environment ambiente;

	ValidacaoCookieSessao(Environment ambiente) {
		this.ambiente = ambiente;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (Arrays.asList(ambiente.getActiveProfiles()).contains("local")) {
			return;
		}
		Boolean httpOnly = ambiente.getProperty("server.servlet.session.cookie.http-only", Boolean.class);
		Boolean seguro = ambiente.getProperty("server.servlet.session.cookie.secure", Boolean.class);
		if (!Boolean.TRUE.equals(httpOnly) || !Boolean.TRUE.equals(seguro)) {
			throw new IllegalStateException(
					"Cookie de sessão deve ser HttpOnly e Secure fora do perfil local");
		}
	}
}
