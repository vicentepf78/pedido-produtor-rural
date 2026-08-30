package br.agriplataforma.order.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.agriplataforma.ConfiguracaoTestcontainers;
import br.agriplataforma.order.application.ConfirmacaoErp;
import br.agriplataforma.order.application.GatewayErp;
import br.agriplataforma.order.application.PedidoLocal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ConfiguracaoTestcontainers.class)
class ConfirmacaoErpIT {

	static final String AURORA = "10000000-0000-4000-8000-000000000001";
	static final String FAZENDA_NORTE = "55555555-5555-5555-5555-555555555555";
	static final String SENHA = "Senha#Fixture2026";

	@LocalServerPort
	int porta;

	@Autowired
	JdbcTemplate jdbc;

	@MockitoBean
	GatewayErp gatewayErp;

	private final HttpClient cliente = HttpClient.newHttpClient();

	@Test
	void it007_postPersistePedidoAntesDaConfirmacaoAceita() throws Exception {
		AtomicInteger presentesNaAceite = new AtomicInteger();
		when(gatewayErp.aceitar(any(PedidoLocal.class))).thenAnswer(invocacao -> {
			PedidoLocal local = invocacao.getArgument(0);
			Integer presentes = jdbc.queryForObject(
					"SELECT COUNT(*) FROM \"order\".\"pedido\" WHERE \"id\" = ?", Integer.class, local.idPedido());
			presentesNaAceite.set(presentes == null ? 0 : presentes);
			return new ConfirmacaoErp("ACEITA");
		});

		Sessao sessao = autenticar("produtor.alfa@example.com");
		String cookieCarrinho = adicionarAurora(sessao);
		HttpResponse<String> criado = postPedido(
				sessao,
				cookieCarrinho,
				UUID.randomUUID().toString(),
				"{\"idPropriedade\":\"%s\",\"preferenciaRetirada\":\"DEPOSITO_PRINCIPAL\"}"
						.formatted(FAZENDA_NORTE));

		assertThat(criado.statusCode()).isEqualTo(200);
		assertThat(criado.body()).contains("\"confirmacao\":\"ACEITA\"");
		assertThat(presentesNaAceite.get()).isEqualTo(1);
		String idPedido = extrair(criado.body(), "idPedido");
		Integer persistidos = jdbc.queryForObject(
				"SELECT COUNT(*) FROM \"order\".\"pedido\" WHERE \"id\" = ?", Integer.class, UUID.fromString(idPedido));
		assertThat(persistidos).isEqualTo(1);
		verify(gatewayErp).aceitar(any(PedidoLocal.class));
	}

	@Test
	void it007_pedidoLocalPermaneceSeAdaptadorForSubstituidoPorFalha() throws Exception {
		when(gatewayErp.aceitar(any(PedidoLocal.class))).thenThrow(new IllegalStateException("adaptador substituido"));

		Sessao sessao = autenticar("produtor.alfa@example.com");
		String cookieCarrinho = adicionarAurora(sessao);
		HttpResponse<String> criado = postPedido(
				sessao,
				cookieCarrinho,
				UUID.randomUUID().toString(),
				"{\"idPropriedade\":\"%s\",\"preferenciaRetirada\":\"DEPOSITO_PRINCIPAL\"}"
						.formatted(FAZENDA_NORTE));

		assertThat(criado.statusCode()).isEqualTo(200);
		assertThat(criado.body()).contains("\"mensagem\":\"Pedido recebido\"");
		String idPedido = extrair(criado.body(), "idPedido");
		Integer persistidos = jdbc.queryForObject(
				"SELECT COUNT(*) FROM \"order\".\"pedido\" WHERE \"id\" = ?", Integer.class, UUID.fromString(idPedido));
		assertThat(persistidos).isEqualTo(1);
		verify(gatewayErp).aceitar(any(PedidoLocal.class));
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
