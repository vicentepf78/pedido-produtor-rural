package br.agriplataforma.tenant.application;

import br.agriplataforma.PropriedadesAplicacao;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ServicoTenant implements ConsultaTenant {

	private final PropriedadesAplicacao propriedades;

	public ServicoTenant(PropriedadesAplicacao propriedades) {
		this.propriedades = propriedades;
	}

	@Override
	public UUID idTenantConfigurado() {
		return propriedades.idTenantSemeado();
	}
}
