package br.agriplataforma.order.api;

import static org.assertj.core.api.Assertions.assertThat;

import br.agriplataforma.ConfiguracaoTestcontainers;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ConfiguracaoTestcontainers.class)
class PedidosIT {

	static final String AURORA = "10000000-0000-4000-8000-000000000001";
	static final String FAZENDA_NORTE = "55555555-5555-5555-5555-555555555555";
	static final String SENHA = "Senha#Fixture2026";

	@LocalServerPort
	int porta;

	private final HttpClient cliente = HttpClient.newHttpClient();

	@Test
	void it006_postDuplicadoComMesmaChaveDevolveConfirmacaoOriginal() throws Exception {
		Sessao sessao = autenticar("produtor.alfa@example.com");
		String cookieCarrinho = adicionarAurora(sessao);
		String chave = UUID.randomUUID().toString();
		String corpo = "{\"idPropriedade\":\"%s\",\"preferenciaRetirada\":\"DEPOSITO_PRINCIPAL\"}"
				.formatted(FAZENDA_NORTE);

		HttpResponse<String> primeira = postPedido(sessao, cookieCarrinho, chave, corpo);
		assertThat(primeira.statusCode()).isEqualTo(200);
		assertThat(primeira.body()).contains("\"mensagem\":\"Pedido recebido\"");
		assertThat(primeira.body()).contains("\"situacao\":\"RECEBIDO\"");
		String idPedido = extrair(primeira.body(), "idPedido");

		HttpResponse<String> segunda = postPedido(sessao, cookieCarrinho, chave, corpo);
		assertThat(segunda.statusCode()).isEqualTo(200);
		assertThat(extrair(segunda.body(), "idPedido")).isEqualTo(idPedido);

		HttpResponse<String> lista = get(sessao, "/api/v1/pedidos?pagina=1&tamanhoPagina=10");
		assertThat(lista.statusCode()).isEqualTo(200);
		assertThat(contarOcorrencias(lista.body(), idPedido)).isEqualTo(1);
	}

	@Test
	void it008_getNegaOutroProdutorERefreshNaoCriaPedido() throws Exception {
		Sessao alfa = autenticar("produtor.alfa@example.com");
		String cookieCarrinho = adicionarAurora(alfa);
		String chave = UUID.randomUUID().toString();
		HttpResponse<String> criado = postPedido(
				alfa,
				cookieCarrinho,
				chave,
				"{\"idPropriedade\":\"%s\",\"preferenciaRetirada\":\"DEPOSITO_PRINCIPAL\"}"
						.formatted(FAZENDA_NORTE));
		assertThat(criado.statusCode()).isEqualTo(200);
		String idPedido = extrair(criado.body(), "idPedido");

		HttpResponse<String> detalhe1 = get(alfa, "/api/v1/pedidos/" + idPedido);
		assertThat(detalhe1.statusCode()).isEqualTo(200);
		assertThat(detalhe1.body()).contains("Fazenda Norte");
		assertThat(detalhe1.body()).contains("\"precoUnitario\":\"620.00\"");

		HttpResponse<String> detalhe2 = get(alfa, "/api/v1/pedidos/" + idPedido);
		assertThat(detalhe2.statusCode()).isEqualTo(200);
		assertThat(extrair(detalhe2.body(), "idPedido")).isEqualTo(idPedido);

		Sessao beta = autenticar("produtor.beta@example.com");
		HttpResponse<String> negado = get(beta, "/api/v1/pedidos/" + idPedido);
		assertThat(negado.statusCode()).isEqualTo(403);
		assertThat(negado.body()).contains("\"codigo\":\"ACESSO_PEDIDO_NEGADO\"");
		assertThat(negado.body()).contains("Você não pode visualizar este pedido.");

		HttpResponse<String> listaBeta = get(beta, "/api/v1/pedidos");
		assertThat(listaBeta.statusCode()).isEqualTo(200);
		assertThat(listaBeta.body()).doesNotContain(idPedido);

		HttpResponse<String> listaAlfa = get(alfa, "/api/v1/pedidos");
		assertThat(contarOcorrencias(listaAlfa.body(), idPedido)).isEqualTo(1);
		HttpResponse<String> refresh = postPedido(
				alfa,
				cookieCarrinho,
				null,
				"{\"idPropriedade\":\"%s\",\"preferenciaRetirada\":\"DEPOSITO_PRINCIPAL\"}"
						.formatted(FAZENDA_NORTE));
		assertThat(refresh.statusCode()).isEqualTo(400);
		assertThat(refresh.body()).contains("\"codigo\":\"CARRINHO_VAZIO\"");
		HttpResponse<String> listaDepois = get(alfa, "/api/v1/pedidos");
		assertThat(contarOcorrencias(listaDepois.body(), idPedido)).isEqualTo(1);
	}

	@Test
	void it004_carrinhoVazioRecusaCheckout() throws Exception {
		Sessao sessao = autenticar("produtor.alfa@example.com");
		HttpResponse<String> vazio = postPedido(
				sessao,
				"chaveCarrinhoConvidado=vazio-it004",
				UUID.randomUUID().toString(),
				"{\"idPropriedade\":\"%s\",\"preferenciaRetirada\":\"DEPOSITO_PRINCIPAL\"}"
						.formatted(FAZENDA_NORTE));
		assertThat(vazio.statusCode()).isEqualTo(400);
		assertThat(vazio.body()).contains("\"codigo\":\"CARRINHO_VAZIO\"");
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
		HttpRequest.Builder builder = HttpRequest.newBuilder(uri("/api/v1/pedidos"))
				.header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
				.header("X-XSRF-TOKEN", csrf.token())
				.header("Cookie", csrf.cookie() + "; " + sessao.cookie() + "; " + cookieCarrinho)
				.POST(HttpRequest.BodyPublishers.ofString(corpo));
		if (chave != null && !chave.isBlank()) {
			builder.header("Idempotency-Key", chave);
		}
		return cliente.send(builder.build(), HttpResponse.BodyHandlers.ofString());
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

	private static int contarOcorrencias(String texto, String trecho) {
		int count = 0;
		int idx = 0;
		while ((idx = texto.indexOf(trecho, idx)) >= 0) {
			count++;
			idx += trecho.length();
		}
		return count;
	}

	private record Sessao(String cookie) {}

	private record Csrf(String token, String cookie) {}
}
