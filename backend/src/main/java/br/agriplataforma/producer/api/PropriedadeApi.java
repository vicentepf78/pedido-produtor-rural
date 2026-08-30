package br.agriplataforma.producer.api;

import br.agriplataforma.producer.application.ConsultaPropriedades;
import br.agriplataforma.producer.application.ResumoPropriedade;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/produtor/propriedades")
public class PropriedadeApi {

	private final ConsultaPropriedades consultaPropriedades;

	public PropriedadeApi(ConsultaPropriedades consultaPropriedades) {
		this.consultaPropriedades = consultaPropriedades;
	}

	@GetMapping
	public RespostaListaPropriedades listar() {
		return new RespostaListaPropriedades(consultaPropriedades.listarDoAutenticado());
	}

	public record RespostaListaPropriedades(List<ResumoPropriedade> itens) {}
}
