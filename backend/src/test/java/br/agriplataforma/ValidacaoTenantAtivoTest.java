package br.agriplataforma;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.agriplataforma.tenant.infrastructure.ValidacaoTenantAtivo;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

class ValidacaoTenantAtivoTest {

	@Test
	void falhaQuandoTenantSemeadoAusente() {
		JdbcTemplate jdbc = mock(JdbcTemplate.class);
		when(jdbc.queryForObject(any(String.class), eq(Boolean.class), any()))
				.thenThrow(new EmptyResultDataAccessException(1));
		ValidacaoTenantAtivo validacao = new ValidacaoTenantAtivo(jdbc, propriedades());
		assertThatThrownBy(() -> validacao.run(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("ausente");
	}

	@Test
	void falhaQuandoTenantSemeadoInativo() {
		JdbcTemplate jdbc = mock(JdbcTemplate.class);
		when(jdbc.queryForObject(any(String.class), eq(Boolean.class), any())).thenReturn(false);
		ValidacaoTenantAtivo validacao = new ValidacaoTenantAtivo(jdbc, propriedades());
		assertThatThrownBy(() -> validacao.run(null))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("inativo");
	}

	private static PropriedadesAplicacao propriedades() {
		return new PropriedadesAplicacao(UUID.fromString("11111111-1111-1111-1111-111111111111"));
	}
}
