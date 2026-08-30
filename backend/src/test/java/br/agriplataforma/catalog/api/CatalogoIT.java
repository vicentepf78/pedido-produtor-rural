package br.agriplataforma.catalog.api;

import static org.assertj.core.api.Assertions.assertThat;

import br.agriplataforma.ConfiguracaoTestcontainers;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ConfiguracaoTestcontainers.class)
class CatalogoIT {

	static final String REGULADO = "10000000-0000-4000-8000-000000000099";
	static final String SEM_IMAGEM = "10000000-0000-4000-8000-000000000002";
	static final String UREIA = "10000000-0000-4000-8000-000000000013";
	static final Pattern ID_ITEM = Pattern.compile("\"id\":\"([^\"]+)\"");

	@LocalServerPort
	int porta;

	private final HttpClient cliente = HttpClient.newHttpClient();

	@Test
	void it001_listagemSemConsultaDevolveRecorte() throws Exception {
		HttpResponse<String> resposta = get("/api/v1/catalogo/produtos?pagina=1&tamanhoPagina=10");
		assertThat(resposta.statusCode()).isEqualTo(200);
		assertThat(ids(resposta.body())).hasSize(10);
		assertThat(campoLong(resposta.body(), "total")).isEqualTo(30);
		assertThat(resposta.body()).doesNotContain("Herbicida glifosato");
		assertThat(resposta.body()).doesNotContain(REGULADO);
	}

	@Test
	void it002_consultaEmBrancoListaRecorte() throws Exception {
		HttpResponse<String> vazia = get("/api/v1/catalogo/produtos?consulta=&pagina=1&tamanhoPagina=10");
		HttpResponse<String> espacos = get("/api/v1/catalogo/produtos?consulta=%20%20&pagina=1&tamanhoPagina=10");
		assertThat(campoLong(vazia.body(), "total")).isEqualTo(30);
		assertThat(campoLong(espacos.body(), "total")).isEqualTo(30);
	}

	@Test
	void it003_fertilizantesEUreia() throws Exception {
		HttpResponse<String> resposta =
				get("/api/v1/catalogo/produtos?categoria=Fertilizantes&consulta=ureia&pagina=1&tamanhoPagina=10");
		assertThat(resposta.statusCode()).isEqualTo(200);
		assertThat(campoLong(resposta.body(), "pagina")).isEqualTo(1);
		assertThat(campoLong(resposta.body(), "tamanhoPagina")).isEqualTo(10);
		assertThat(resposta.body()).contains("Fertilizantes");
		assertThat(resposta.body()).containsIgnoringCase("ureia");
	}

	@Test
	void it004_defensivosRecusado() throws Exception {
		HttpResponse<String> resposta = get("/api/v1/catalogo/produtos?categoria=Defensivos&pagina=1&tamanhoPagina=10");
		assertThat(resposta.statusCode()).isEqualTo(400);
		assertThat(resposta.body()).contains("\"codigo\":\"CATEGORIA_INVALIDA\"");
	}

	@Test
	void it005_tamanho24Recusado() throws Exception {
		HttpResponse<String> resposta = get("/api/v1/catalogo/produtos?pagina=1&tamanhoPagina=24");
		assertThat(resposta.statusCode()).isEqualTo(400);
		assertThat(resposta.body()).contains("\"codigo\":\"TAMANHO_PAGINA_INVALIDO\"");
	}

	@Test
	void it006_tamanhosPermitidosE100Recusado() throws Exception {
		for (int tamanho : new int[] {10, 15, 30, 50}) {
			HttpResponse<String> ok = get("/api/v1/catalogo/produtos?pagina=1&tamanhoPagina=" + tamanho);
			assertThat(ok.statusCode()).isEqualTo(200);
			assertThat(campoLong(ok.body(), "tamanhoPagina")).isEqualTo(tamanho);
		}
		HttpResponse<String> recusado = get("/api/v1/catalogo/produtos?pagina=1&tamanhoPagina=100");
		assertThat(recusado.body()).contains("TAMANHO_PAGINA_INVALIDO");
	}

	@Test
	void it007_paginaDoisNaoRepeteEUniaoEsgotaTrinta() throws Exception {
		List<String> p1 = ids(get("/api/v1/catalogo/produtos?pagina=1&tamanhoPagina=10").body());
		List<String> p2 = ids(get("/api/v1/catalogo/produtos?pagina=2&tamanhoPagina=10").body());
		List<String> p3 = ids(get("/api/v1/catalogo/produtos?pagina=3&tamanhoPagina=10").body());
		Set<String> todos = new HashSet<>();
		todos.addAll(p1);
		todos.addAll(p2);
		todos.addAll(p3);
		assertThat(todos).hasSize(30);
		assertThat(p2).doesNotContainAnyElementsOf(p1);
	}

	@Test
	void it008_reguladoOcultoNoDetalhe() throws Exception {
		HttpResponse<String> detalhe = get("/api/v1/catalogo/produtos/" + REGULADO);
		assertThat(detalhe.statusCode()).isEqualTo(404);
		assertThat(detalhe.body()).contains("\"codigo\":\"PRODUTO_NAO_ELEGIVEL\"");
		assertThat(detalhe.body()).doesNotContain("glifosato");
		assertThat(get("/api/v1/catalogo/produtos?pagina=1&tamanhoPagina=50").body()).doesNotContain(REGULADO);
	}

	@Test
	void it022_detalheUreiaEInexistente() throws Exception {
		HttpResponse<String> ureia = get("/api/v1/catalogo/produtos/" + UREIA);
		assertThat(ureia.statusCode()).isEqualTo(200);
		assertThat(ureia.body()).contains("Ureia 45% N");
		assertThat(ureia.body()).contains("\"categoria\":\"Fertilizantes\"");
		HttpResponse<String> inexistente = get("/api/v1/catalogo/produtos/10000000-0000-4000-8000-0000000000aa");
		assertThat(inexistente.body()).contains("PRODUTO_NAO_ELEGIVEL");
		assertThat(inexistente.body()).doesNotContain("Ureia 45% N");
	}

	@Test
	void it032_categoriaTodos() throws Exception {
		HttpResponse<String> resposta = get("/api/v1/catalogo/produtos?categoria=Todos&pagina=1&tamanhoPagina=10");
		assertThat(campoLong(resposta.body(), "total")).isEqualTo(30);
	}

	@Test
	void it033_tamanho50UmaPagina() throws Exception {
		String corpo = get("/api/v1/catalogo/produtos?pagina=1&tamanhoPagina=50").body();
		assertThat(ids(corpo)).hasSize(30);
		assertThat(campoLong(corpo, "total")).isEqualTo(30);
	}

	@Test
	void it035_omitirTamanhoUsaDez() throws Exception {
		String corpo = get("/api/v1/catalogo/produtos?pagina=1").body();
		assertThat(campoLong(corpo, "tamanhoPagina")).isEqualTo(10);
		assertThat(ids(corpo)).hasSize(10);
	}

	@Test
	void it041_recorteVazio() throws Exception {
		String corpo = get("/api/v1/catalogo/produtos?categoria=Correção&consulta=xyzzy-inexistente&pagina=1&tamanhoPagina=10")
				.body();
		assertThat(ids(corpo)).isEmpty();
		assertThat(campoLong(corpo, "total")).isZero();
	}

	@Test
	void it042_consultaInexistente() throws Exception {
		String corpo = get("/api/v1/catalogo/produtos?consulta=xyzzy-inexistente&pagina=1&tamanhoPagina=10").body();
		assertThat(ids(corpo)).isEmpty();
		assertThat(campoLong(corpo, "total")).isZero();
	}

	@Test
	void it043_consultaHostilNaoDevolve5xx() throws Exception {
		HttpResponse<String> resposta =
				get("/api/v1/catalogo/produtos?consulta=" + "x".repeat(2000) + "&pagina=1&tamanhoPagina=10");
		assertThat(resposta.statusCode()).isLessThan(500);
	}

	@Test
	void it008_imagemAusenteSegura() throws Exception {
		HttpResponse<String> semImagem = get("/api/v1/catalogo/produtos/" + SEM_IMAGEM);
		assertThat(semImagem.statusCode()).isEqualTo(200);
		assertThat(semImagem.body()).contains("\"urlImagem\":null");
		assertThat(semImagem.body()).doesNotContain("javascript:");
	}

	private HttpResponse<String> get(String caminho) throws Exception {
		return cliente.send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + porta + caminho))
						.GET()
						.build(),
				HttpResponse.BodyHandlers.ofString());
	}

	private static long campoLong(String json, String campo) {
		Matcher matcher = Pattern.compile("\"" + campo + "\":(\\d+)").matcher(json);
		assertThat(matcher.find()).as(campo).isTrue();
		return Long.parseLong(matcher.group(1));
	}

	private static List<String> ids(String json) {
		int inicioItens = json.indexOf("\"itens\":");
		String recorte = inicioItens < 0 ? json : json.substring(inicioItens);
		List<String> ids = new ArrayList<>();
		Matcher matcher = ID_ITEM.matcher(recorte);
		while (matcher.find()) {
			ids.add(matcher.group(1));
		}
		return ids;
	}
}
