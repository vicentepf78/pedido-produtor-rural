package br.agriplataforma.backoffice.api;

import static org.assertj.core.api.Assertions.assertThat;

import br.agriplataforma.ConfiguracaoTestcontainers;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ConfiguracaoTestcontainers.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RetaguardaIT {

	static final String AURORA = "10000000-0000-4000-8000-000000000001";
	static final String FAZENDA_NORTE = "55555555-5555-5555-5555-555555555555";
	static final String SENHA = "Senha#Fixture2026";

	@LocalServerPort
	int porta;

	@Autowired
	JdbcTemplate jdbc;

	private final HttpClient cliente = HttpClient.newHttpClient();

	@Test
	@Order(2)
	void it009_listaRetaguardaDevolveSomenteResumosDoTenantConfigurado() throws Exception {
		Sessao alfa = autenticar("produtor.alfa@example.com");
		String cookieCarrinho = adicionarAurora(alfa);
		HttpResponse<String> criado = postPedido(
				alfa,
				cookieCarrinho,
				UUID.randomUUID().toString(),
				"{\"idPropriedade\":\"%s\",\"preferenciaRetirada\":\"DEPOSITO_PRINCIPAL\"}"
						.formatted(FAZENDA_NORTE));
		assertThat(criado.statusCode()).isEqualTo(200);
		assertThat(criado.body()).contains("\"confirmacao\":\"ACEITA\"");
		String idPedido = extrair(criado.body(), "idPedido");

		Sessao operador = autenticar("operador.revenda@example.com");
		HttpResponse<String> lista = get(operador, "/api/v1/retaguarda/pedidos?pagina=1&tamanhoPagina=25");
		assertThat(lista.statusCode()).isEqualTo(200);
		assertThat(lista.body()).contains("\"idPedido\":\"" + idPedido + "\"");
		assertThat(lista.body()).contains("\"nomeProdutor\":\"Produtor Alfa\"");
		assertThat(lista.body()).contains("\"total\":\"1240.00\"");
		assertThat(lista.body()).contains("\"confirmacao\":\"ACEITA\"");
		assertThat(lista.body()).contains("\"situacao\":\"RECEBIDO\"");
		assertThat(lista.body()).contains("\"criadoEm\"");

		HttpResponse<String> detalhe = get(operador, "/api/v1/retaguarda/pedidos/" + idPedido);
		assertThat(detalhe.statusCode()).isEqualTo(200);
		assertThat(detalhe.body()).contains("\"nomeProdutor\":\"Produtor Alfa\"");
		assertThat(detalhe.body()).contains("Fazenda Norte");
		assertThat(detalhe.body()).contains("\"precoUnitario\":\"620.00\"");
	}

	@Test
	@Order(1)
	void it010_retaguardaVaziaExplicitaERecusaProdutor() throws Exception {
		jdbc.update("DELETE FROM \"order\".\"itemPedido\"");
		jdbc.update("DELETE FROM \"order\".\"pedido\"");
		Sessao operador = autenticar("operador.revenda@example.com");
		HttpResponse<String> vazia = get(operador, "/api/v1/retaguarda/pedidos?pagina=1&tamanhoPagina=25");
		assertThat(vazia.statusCode()).isEqualTo(200);
		assertThat(vazia.body()).contains("\"itens\":[]");
		assertThat(vazia.body()).contains("\"total\":0");

		Sessao alfa = autenticar("produtor.alfa@example.com");
		HttpResponse<String> negado = get(alfa, "/api/v1/retaguarda/pedidos");
		assertThat(negado.statusCode()).isEqualTo(403);
		assertThat(negado.body()).contains("\"codigo\":\"ACESSO_NEGADO\"");
		assertThat(negado.body()).doesNotContain("\"itens\"");
		assertThat(negado.body()).doesNotContain("Produtor Alfa");
	}

	private String adicionarAurora(Sessao sessao) throws Exception {
		Csrf csrf = csrf(sessao.cookie());
		HttpResponse<String> adicao = cliente.send(
				HttpRequest.newBuilder(uri("/api/v1/carrinhos/convidado/itens"))
						.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.header("X-XSRF-TOKEN", csrf.token())
						.header("Cookie", csrf.cookie() + "; " + sessao.cookie())
						.POST(HttpRequest.BodyPublishers.ofString(
								"{\"idProduto\":\"%s\",\"quantidade\":2}".formatted(AURORA)))
						.build(),
				HttpResponse.BodyHandlers.ofString());
		assertThat(adicao.statusCode()).isEqualTo(200);
		return cookieNome(adicao.headers().allValues("Set-Cookie"), "chaveCarrinhoConvidado");
	}

	private HttpResponse<String> postPedido(Sessao sessao, String cookieCarrinho, String chave, String corpo)
			throws Exception {
		Csrf csrf = csrf(sessao.cookie() + "; " + cookieCarrinho);
		return cliente.send(
				HttpRequest.newBuilder(uri("/api/v1/pedidos"))
						.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.header("X-XSRF-TOKEN", csrf.token())
						.header("Idempotency-Key", chave)
						.header("Cookie", csrf.cookie() + "; " + sessao.cookie() + "; " + cookieCarrinho)
						.POST(HttpRequest.BodyPublishers.ofString(corpo))
						.build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> get(Sessao sessao, String caminho) throws Exception {
		return cliente.send(
				HttpRequest.newBuilder(uri(caminho)).header("Cookie", sessao.cookie()).GET().build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private Sessao autenticar(String email) throws Exception {
		Csrf csrf = csrf(null);
		HttpResponse<String> entrada = cliente.send(
				HttpRequest.newBuilder(uri("/api/v1/autenticacao/entrada"))
						.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.header("X-XSRF-TOKEN", csrf.token())
						.header("Cookie", csrf.cookie())
						.POST(HttpRequest.BodyPublishers.ofString(
								"{\"email\":\"%s\",\"senha\":\"%s\"}".formatted(email, SENHA)))
						.build(),
				HttpResponse.BodyHandlers.ofString());
		assertThat(entrada.statusCode()).isEqualTo(200);
		return new Sessao(cookieNome(entrada.headers().allValues("Set-Cookie"), "sessao"));
	}

	private Csrf csrf(String cookiesExtras) throws Exception {
		HttpRequest.Builder builder = HttpRequest.newBuilder(uri("/api/v1/autenticacao/csrf")).GET();
		if (cookiesExtras != null) {
			builder.header("Cookie", cookiesExtras);
		}
		HttpResponse<String> resposta = cliente.send(builder.build(), HttpResponse.BodyHandlers.ofString());
		assertThat(resposta.statusCode()).isEqualTo(200);
		String corpo = resposta.body();
		String token = br.agriplataforma.ApoioCsrf.token(corpo);
		String cookie = resposta.headers().allValues("Set-Cookie").stream()
				.filter(valor -> valor.startsWith("XSRF-TOKEN="))
				.map(valor -> valor.split(";", 2)[0])
				.findFirst()
				.orElseGet(() -> cookiesExtras == null
						? ""
						: java.util.Arrays.stream(cookiesExtras.split(";"))
								.map(String::trim)
								.filter(valor -> valor.startsWith("XSRF-TOKEN="))
								.findFirst()
								.orElseThrow(() -> new AssertionError("Cookie XSRF-TOKEN ausente")));
		return new Csrf(token, cookie);
	}

	private URI uri(String caminho) {
		return URI.create("http://127.0.0.1:" + porta + caminho);
	}

	private static String cookieNome(List<String> setCookies, String nome) {
		return setCookies.stream()
				.filter(cookie -> cookie.startsWith(nome + "="))
				.map(cookie -> cookie.split(";", 2)[0])
				.findFirst()
				.orElseThrow(() -> new AssertionError("Set-Cookie " + nome + " ausente: " + setCookies));
	}

	private static String extrair(String json, String campo) {
		String chave = "\"" + campo + "\":\"";
		int inicio = json.indexOf(chave);
		if (inicio < 0) {
			throw new AssertionError("Campo " + campo + " ausente em " + json);
		}
		int de = inicio + chave.length();
		return json.substring(de, json.indexOf('"', de));
	}

	private record Sessao(String cookie) {}

	private record Csrf(String token, String cookie) {}
}
