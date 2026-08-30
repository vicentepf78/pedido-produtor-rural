package br.agriplataforma.catalog.api;

import static org.assertj.core.api.Assertions.assertThat;

import br.agriplataforma.ConfiguracaoTestcontainers;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

	@LocalServerPort
	int porta;

	private final HttpClient cliente = HttpClient.newHttpClient();

	@Test
	void it001_listaSomenteVisiveisDoTenantSemeadoParaConsultaSemente() throws Exception {
		HttpResponse<String> resposta = get("/api/v1/catalogo/produtos?consulta=semente&pagina=1&tamanhoPagina=24");

		assertThat(resposta.statusCode()).isEqualTo(200);
		assertThat(resposta.body()).contains("Semente de milho Aurora 20 kg");
		assertThat(resposta.body()).contains("\"descricaoCurta\"");
		assertThat(resposta.body()).contains("\"unidade\"");
		assertThat(resposta.body()).contains("\"precoUnitario\"");
		assertThat(resposta.body()).doesNotContain("Herbicida glifosato");
		assertThat(resposta.body()).doesNotContain("glifosato");
		assertThat(resposta.body()).doesNotContain("Defensivos");
		assertThat(resposta.body()).contains("\"pagina\":1");
	}

	@Test
	void it002_produtoReguladoOcultoEImagemAusenteNaoQuebraApi() throws Exception {
		HttpResponse<String> regulado = get("/api/v1/catalogo/produtos/" + REGULADO);
		assertThat(regulado.statusCode()).isEqualTo(404);
		assertThat(regulado.body()).contains("\"codigo\":\"PRODUTO_NAO_ELEGIVEL\"");
		assertThat(regulado.body()).doesNotContain("glifosato 480");

		HttpResponse<String> listagem = get("/api/v1/catalogo/produtos?consulta=semente");
		assertThat(listagem.body()).doesNotContain(REGULADO);

		HttpResponse<String> semImagem = get("/api/v1/catalogo/produtos/" + SEM_IMAGEM);
		assertThat(semImagem.statusCode()).isEqualTo(200);
		assertThat(semImagem.body()).contains("Semente de milho DK697");
		assertThat(semImagem.body()).contains("\"urlImagem\":null");
		assertThat(semImagem.body()).doesNotContain("javascript:");
		assertThat(semImagem.body()).doesNotContain("undefined");
	}

	private HttpResponse<String> get(String caminho) throws Exception {
		return cliente.send(
				HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + porta + caminho))
						.GET()
						.build(),
				HttpResponse.BodyHandlers.ofString());
	}
}
