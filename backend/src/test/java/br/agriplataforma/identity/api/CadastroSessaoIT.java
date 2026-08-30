package br.agriplataforma.identity.api;

import static org.assertj.core.api.Assertions.assertThat;

import br.agriplataforma.ConfiguracaoTestcontainers;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ConfiguracaoTestcontainers.class)
class CadastroSessaoIT {

	private static final String SENHA_FIXTURE = "Senha#Fixture2026";

	@LocalServerPort
	int porta;

	private final HttpClient cliente = HttpClient.newHttpClient();

	@Test
	void it005_cadastroDefineCookieSeguro() throws Exception {
		HttpResponse<String> cadastro = postJson(
				"/api/v1/autenticacao/cadastro",
				"""
				{"nome":"Produtor Gama","email":"produtor.gama@example.com","senha":"Rural#2026Order"}
				""");

		assertThat(cadastro.statusCode()).isEqualTo(200);
		assertThat(cadastro.body()).contains("\"nome\":\"Produtor Gama\"");
		assertThat(cadastro.body()).contains("\"PRODUTOR\"");
		assertThat(cadastro.body()).contains("idUsuario");
		assertThat(cadastro.body()).doesNotContain("Rural#2026Order");
		assertThat(cadastro.body()).doesNotContain("senhaHash");
		assertThat(cadastro.body()).doesNotContain("\"senha\"");

		String setCookieSessao = cookieSessao(cadastro.headers().allValues("Set-Cookie"));
		assertThat(setCookieSessao).containsIgnoringCase("HttpOnly");
		assertThat(setCookieSessao).containsIgnoringCase("Secure");

		HttpResponse<String> propriedades = cliente.send(
				HttpRequest.newBuilder(uri("/api/v1/produtor/propriedades"))
						.header("Cookie", "sessao=" + valorCookie(setCookieSessao))
						.GET()
						.build(),
				HttpResponse.BodyHandlers.ofString());
		assertThat(propriedades.statusCode()).isEqualTo(200);
		assertThat(propriedades.body()).contains("\"itens\":[]");

		Csrf csrfDepois = csrf();
		HttpResponse<String> criada = cliente.send(
				HttpRequest.newBuilder(uri("/api/v1/produtor/propriedades"))
						.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.header("X-XSRF-TOKEN", csrfDepois.token())
						.header("Cookie", csrfDepois.cookie() + "; sessao=" + valorCookie(setCookieSessao))
						.POST(HttpRequest.BodyPublishers.ofString("{\"nome\":\"Sitio Novo Gama\"}"))
						.build(),
				HttpResponse.BodyHandlers.ofString());
		assertThat(criada.statusCode()).isEqualTo(200);
		assertThat(criada.body()).contains("Sitio Novo Gama");
		assertThat(criada.body()).contains("\"id\"");
	}

	@Test
	void it005_produtorSelecionaSomentePropriedadesProprias() throws Exception {
		String corpoAlfa = propriedadesAposEntrada("produtor.alfa@example.com");
		assertThat(corpoAlfa).contains("Fazenda Norte");
		assertThat(corpoAlfa).contains("Sitio Recanto");
		assertThat(corpoAlfa).doesNotContain("Fazenda Sul");

		String corpoBeta = propriedadesAposEntrada("produtor.beta@example.com");
		assertThat(corpoBeta).contains("Fazenda Sul");
		assertThat(corpoBeta).doesNotContain("Fazenda Norte");
		assertThat(corpoBeta).doesNotContain("Sitio Recanto");
	}

	private String propriedadesAposEntrada(String email) throws Exception {
		HttpResponse<String> entrada = postJson(
				"/api/v1/autenticacao/entrada",
				"{\"email\":\"%s\",\"senha\":\"%s\"}".formatted(email, SENHA_FIXTURE));
		assertThat(entrada.statusCode()).isEqualTo(200);
		String setCookieSessao = cookieSessao(entrada.headers().allValues("Set-Cookie"));
		assertThat(setCookieSessao).containsIgnoringCase("HttpOnly");

		HttpResponse<String> propriedades = cliente.send(
				HttpRequest.newBuilder(uri("/api/v1/produtor/propriedades"))
						.header("Cookie", "sessao=" + valorCookie(setCookieSessao))
						.GET()
						.build(),
				HttpResponse.BodyHandlers.ofString());
		assertThat(propriedades.statusCode()).isEqualTo(200);
		return propriedades.body();
	}

	private HttpResponse<String> postJson(String caminho, String json) throws Exception {
		Csrf csrf = csrf();
		return cliente.send(
				HttpRequest.newBuilder(uri(caminho))
						.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.header("X-XSRF-TOKEN", csrf.token())
						.header("Cookie", csrf.cookie())
						.POST(HttpRequest.BodyPublishers.ofString(json))
						.build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private Csrf csrf() throws Exception {
		HttpResponse<String> resposta = cliente.send(
				HttpRequest.newBuilder(uri("/api/v1/autenticacao/csrf")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertThat(resposta.statusCode()).isEqualTo(200);
		String corpo = resposta.body();
		String token = br.agriplataforma.ApoioCsrf.token(corpo);
		String cookie = resposta.headers().allValues("Set-Cookie").stream()
				.filter(valor -> valor.startsWith("XSRF-TOKEN="))
				.map(valor -> valor.split(";", 2)[0])
				.findFirst()
				.orElseThrow(() -> new AssertionError("Cookie XSRF-TOKEN ausente"));
		return new Csrf(token, cookie);
	}

	private URI uri(String caminho) {
		return URI.create("http://127.0.0.1:" + porta + caminho);
	}

	private static String cookieSessao(List<String> setCookies) {
		return setCookies.stream()
				.filter(cookie -> cookie.startsWith("sessao="))
				.findFirst()
				.orElseThrow(() -> new AssertionError("Set-Cookie sessao ausente: " + setCookies));
	}

	private static String valorCookie(String setCookie) {
		String primeiro = setCookie.split(";", 2)[0];
		return primeiro.substring(primeiro.indexOf('=') + 1);
	}

	private record Csrf(String token, String cookie) {}
}
