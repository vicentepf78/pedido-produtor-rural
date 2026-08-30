package br.agriplataforma.identity.infrastructure;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
public class ConfiguracaoSeguranca {

	@Bean
	PasswordEncoder senhas() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain cadeiaSeguranca(HttpSecurity http, Environment ambiente) throws Exception {
		boolean cookieSeguro =
				ambiente.getProperty("server.servlet.session.cookie.secure", Boolean.class, Boolean.TRUE);
		CookieCsrfTokenRepository csrfCookies = CookieCsrfTokenRepository.withHttpOnlyFalse();
		csrfCookies.setCookieCustomizer(cookie -> cookie.secure(cookieSeguro).sameSite("Lax"));
		http.csrf(csrf -> csrf.csrfTokenRepository(csrfCookies))
				.formLogin(form -> form.disable())
				.httpBasic(basic -> basic.disable())
				.logout(logout -> logout.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/actuator/health", "/actuator/health/**")
						.permitAll()
						.requestMatchers(HttpMethod.GET, "/api/v1/autenticacao/csrf")
						.permitAll()
						.requestMatchers(
								HttpMethod.POST,
								"/api/v1/autenticacao/cadastro",
								"/api/v1/autenticacao/entrada",
								"/api/v1/autenticacao/saida")
						.permitAll()
						.requestMatchers("/api/v1/catalogo/**")
						.permitAll()
						.requestMatchers("/api/v1/carrinhos/convidado/**")
						.permitAll()
						.requestMatchers("/api/v1/produtor/**")
						.hasAuthority("PRODUTOR")
						.requestMatchers("/api/v1/pedidos", "/api/v1/pedidos/**")
						.hasAuthority("PRODUTOR")
						.requestMatchers("/api/v1/retaguarda/**")
						.hasAuthority("OPERADOR_REVENDA")
						.anyRequest()
						.authenticated())
				.exceptionHandling(ex -> ex.authenticationEntryPoint(entradaNaoAutenticada())
						.accessDeniedHandler(acessoNegado()));
		return http.build();
	}

	@Bean
	OncePerRequestFilter filtroCookieCsrf() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(
					HttpServletRequest request, HttpServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
				if (token != null) {
					token.getToken();
				}
				chain.doFilter(request, response);
			}
		};
	}

	private static AuthenticationEntryPoint entradaNaoAutenticada() {
		return (request, response, excecao) -> escrever(
				response, 401, "NAO_AUTENTICADO", "Entre ou crie uma conta para continuar.");
	}

	private static AccessDeniedHandler acessoNegado() {
		return (request, response, excecao) -> escrever(
				response, 403, "ACESSO_NEGADO", "Você não tem permissão para este recurso.");
	}

	private static void escrever(HttpServletResponse response, int status, String codigo, String mensagem)
			throws IOException {
		response.setStatus(status);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter()
				.write("{\"codigo\":\"" + codigo + "\",\"mensagem\":\"" + mensagem + "\"}");
	}
}
