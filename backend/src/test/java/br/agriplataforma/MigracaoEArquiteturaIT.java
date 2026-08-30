package br.agriplataforma;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.modulith.core.ApplicationModules;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ConfiguracaoTestcontainers.class)
class MigracaoEArquiteturaIT {

	private static final List<String> SCHEMAS = List.of(
			"tenant", "identity", "producer", "catalog", "cart", "order", "integration", "backoffice");

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	Environment environment;

	@LocalServerPort
	int porta;

	@Test
	void it023_migracoesEmPostgresVazioELimitesModulith() throws Exception {
		ApplicationModules.of(AgriPlatformApplication.class).verify();

		for (String schema : SCHEMAS) {
			Integer presente = jdbc.queryForObject(
					"SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = ?",
					Integer.class,
					schema);
			assertThat(presente).as("schema %s", schema).isEqualTo(1);
		}

		List<String> colunas = jdbc.queryForList(
				"SELECT column_name FROM information_schema.columns WHERE table_schema = 'tenant' AND table_name = 'tenant'",
				String.class);
		assertThat(colunas).containsExactlyInAnyOrder("id", "nome", "ativo");

		String sqlTenant = new String(
				MigracaoEArquiteturaIT.class
						.getResourceAsStream("/db/migration/tenant/V1__criar_schema_tenant.sql")
						.readAllBytes());
		assertThat(sqlTenant).contains("\"id\"").contains("\"nome\"").contains("\"ativo\"");
		assertThat(sqlTenant).doesNotContain("id_tenant");

		assertThat(environment.getProperty("spring.jpa.hibernate.naming.physical-strategy"))
				.isEqualTo("org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
		assertThat(environment.getProperty("spring.jpa.hibernate.ddl-auto")).isEqualTo("none");

		Integer tenantsAtivos = jdbc.queryForObject(
				"SELECT COUNT(*) FROM tenant.\"tenant\" WHERE \"ativo\" = TRUE", Integer.class);
		assertThat(tenantsAtivos).isEqualTo(1);

		HttpResponse<String> saude = HttpClient.newHttpClient()
				.send(
						HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + porta + "/actuator/health"))
								.GET()
								.build(),
						HttpResponse.BodyHandlers.ofString());
		assertThat(saude.statusCode()).isEqualTo(200);
	}

	@Test
	void falhaSubidaSemConexaoDeBanco() {
		assertThatThrownBy(() -> new SpringApplicationBuilder(AgriPlatformApplication.class)
						.web(WebApplicationType.NONE)
						.run(
								"--spring.datasource.url=jdbc:postgresql://127.0.0.1:1/agriplataforma",
								"--spring.datasource.username=agri",
								"--spring.datasource.password=agri",
								"--spring.flyway.enabled=false"))
				.isInstanceOf(Exception.class);
	}
}
