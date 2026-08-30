package br.agriplataforma.cart.api;

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
class CarrinhoConvidadoIT {

	static final String AURORA = "10000000-0000-4000-8000-000000000001";
	static final String UREIA = "10000000-0000-4000-8000-000000000013";
	static final String REGULADO = "10000000-0000-4000-8000-000000000099";

	@LocalServerPort
	int porta;

	private final HttpClient cliente = HttpClient.newBuilder().build();

	@Test
	void it003_adicionarItemDevolveLinhaETotalDoContratoDx() throws Exception {
		HttpResponse<String> resposta = postJson(
				"/api/v1/carrinhos/convidado/itens",
				"{\"idProduto\":\"%s\",\"quantidade\":2}".formatted(AURORA),
				null);

		assertThat(resposta.statusCode()).isEqualTo(200);
		assertThat(resposta.body()).contains("\"nome\":\"Semente de milho Aurora 20 kg\"");
		assertThat(resposta.body()).contains("\"quantidade\":2");
		assertThat(resposta.body()).contains("\"precoUnitario\":\"620.00\"");
		assertThat(resposta.body()).contains("\"totalLinha\":\"1240.00\"");
		assertThat(resposta.body()).contains("\"total\":\"1240.00\"");
		assertThat(cookieCarrinho(resposta.headers().allValues("Set-Cookie"))).isNotBlank();
	}

	@Test
	void it003_quantidadeInvalidaEProdutosRecusados() throws Exception {
		HttpResponse<String> zero = postJson(
				"/api/v1/carrinhos/convidado/itens",
				"{\"idProduto\":\"%s\",\"quantidade\":0}".formatted(AURORA),
				null);
		assertThat(zero.statusCode()).isEqualTo(400);
		assertThat(zero.body()).contains("\"codigo\":\"QUANTIDADE_INVALIDA\"");

		HttpResponse<String> fracao = postJson(
				"/api/v1/carrinhos/convidado/itens",
				"{\"idProduto\":\"%s\",\"quantidade\":1.5}".formatted(AURORA),
				null);
		assertThat(fracao.statusCode()).isEqualTo(400);
		assertThat(fracao.body()).contains("QUANTIDADE_INVALIDA");

		HttpResponse<String> ureia = postJson(
				"/api/v1/carrinhos/convidado/itens",
				"{\"idProduto\":\"%s\",\"quantidade\":1}".formatted(UREIA),
				null);
		assertThat(ureia.statusCode()).isEqualTo(409);
		assertThat(ureia.body()).contains("PRODUTO_INDISPONIVEL");

		HttpResponse<String> regulado = postJson(
				"/api/v1/carrinhos/convidado/itens",
				"{\"idProduto\":\"%s\",\"quantidade\":1}".formatted(REGULADO),
				null);
		assertThat(regulado.statusCode()).isEqualTo(404);
		assertThat(regulado.body()).contains("PRODUTO_NAO_ELEGIVEL");
	}

	@Test
	void it004_carrinhoPersisteAposRestaurarSessaoECarrinhoVazioNaoLiberaCheckout() throws Exception {
		HttpResponse<String> vazio = cliente.send(
				HttpRequest.newBuilder(uri("/api/v1/carrinhos/convidado")).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		assertThat(vazio.statusCode()).isEqualTo(200);
		assertThat(vazio.body()).contains("\"itens\":[]");
		assertThat(vazio.body()).contains("\"total\":\"0.00\"");

		HttpResponse<String> adicao = postJson(
				"/api/v1/carrinhos/convidado/itens",
				"{\"idProduto\":\"%s\",\"quantidade\":2}".formatted(AURORA),
				null);
		assertThat(adicao.statusCode()).isEqualTo(200);
		String chave = valorCookie(cookieCarrinho(adicao.headers().allValues("Set-Cookie")));

		HttpClient outroNavegador = HttpClient.newHttpClient();
		HttpResponse<String> restaurado = outroNavegador.send(
				HttpRequest.newBuilder(uri("/api/v1/carrinhos/convidado"))
						.header("Cookie", "chaveCarrinhoConvidado=" + chave)
						.GET()
						.build(),
				HttpResponse.BodyHandlers.ofString());
		assertThat(restaurado.statusCode()).isEqualTo(200);
		assertThat(restaurado.body()).contains("Semente de milho Aurora 20 kg");
		assertThat(restaurado.body()).contains("\"total\":\"1240.00\"");
		assertThat(restaurado.headers().allValues("Set-Cookie"))
				.noneMatch(cookie -> cookie.toLowerCase().contains("chavecarrinhoconvidado=")
						&& cookie.toLowerCase().contains("max-age=0"));
	}

	private HttpResponse<String> postJson(String caminho, String json, String cookieCarrinho) throws Exception {
		Csrf csrf = csrf();
		String cookie = csrf.cookie();
		if (cookieCarrinho != null) {
			cookie = cookie + "; " + cookieCarrinho;
		}
		return cliente.send(
				HttpRequest.newBuilder(uri(caminho))
						.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
						.header("X-XSRF-TOKEN", csrf.token())
						.header("Cookie", cookie)
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

	private static String cookieCarrinho(List<String> setCookies) {
		return setCookies.stream()
				.filter(cookie -> cookie.startsWith("chaveCarrinhoConvidado="))
				.findFirst()
				.orElseThrow(() -> new AssertionError("Set-Cookie chaveCarrinhoConvidado ausente: " + setCookies));
	}

	private static String valorCookie(String setCookie) {
		String primeiro = setCookie.split(";", 2)[0];
		return primeiro.substring(primeiro.indexOf('=') + 1);
	}

	private record Csrf(String token, String cookie) {}
}
