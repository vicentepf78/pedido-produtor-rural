package br.agriplataforma;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PropriedadesAplicacao.class)
class ConfiguracaoAplicacao {

	static final String[] MODULOS = {
			"tenant", "identity", "producer", "catalog", "cart", "order", "integration", "backoffice"
	};

	@Bean
	FlywayMigrationStrategy estrategiaFlywayPorModulo(DataSource dataSource) {
		return unused -> {
			for (String modulo : MODULOS) {
				Flyway.configure()
						.dataSource(dataSource)
						.schemas(modulo)
						.defaultSchema(modulo)
						.createSchemas(true)
						.locations("classpath:db/migration/" + modulo)
						.load()
						.migrate();
			}
		};
	}
}
