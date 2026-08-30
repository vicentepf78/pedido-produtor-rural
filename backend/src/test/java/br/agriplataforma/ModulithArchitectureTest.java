package br.agriplataforma;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import arquitetura.ut026.violacao.AplicacaoVioladora;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.core.Violations;

class ModulithArchitectureTest {

	@Test
	void ut075_rejeitaImportacaoDeInfraestruturaDeOutroModulo() {
		ApplicationModules violacao =
				ApplicationModules.of(AplicacaoVioladora.class, ImportOption.Predefined.ONLY_INCLUDE_TESTS);
		assertThatThrownBy(violacao::verify)
				.isInstanceOf(Violations.class)
				.hasMessageContaining("infrastructure");
	}

	@Test
	void ut076_limitesDosModulosDeProducaoSaoValidos() {
		ApplicationModules.of(AgriPlatformApplication.class).verify();
	}
}
