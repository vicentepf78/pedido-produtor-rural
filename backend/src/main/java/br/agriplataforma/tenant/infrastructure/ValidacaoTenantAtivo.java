package br.agriplataforma.tenant.infrastructure;

import br.agriplataforma.PropriedadesAplicacao;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoTenantAtivo implements ApplicationRunner {

	private final JdbcTemplate jdbc;
	private final PropriedadesAplicacao propriedades;

	public ValidacaoTenantAtivo(JdbcTemplate jdbc, PropriedadesAplicacao propriedades) {
		this.jdbc = jdbc;
		this.propriedades = propriedades;
	}

	@Override
	public void run(ApplicationArguments args) {
		Boolean ativo;
		try {
			ativo = jdbc.queryForObject(
					"SELECT \"ativo\" FROM tenant.\"tenant\" WHERE \"id\" = ?",
					Boolean.class,
					propriedades.idTenantSemeado());
		} catch (EmptyResultDataAccessException excecao) {
			throw new IllegalStateException("Tenant semeado ausente", excecao);
		}
		if (!Boolean.TRUE.equals(ativo)) {
			throw new IllegalStateException("Tenant semeado inativo");
		}
	}
}
