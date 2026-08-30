package br.agriplataforma.producer.api;

import br.agriplataforma.producer.application.ComandoPropriedades;
import br.agriplataforma.producer.application.ConsultaPropriedades;
import br.agriplataforma.producer.application.ResumoPropriedade;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/produtor/propriedades")
public class PropriedadeApi {

	private final ConsultaPropriedades consultaPropriedades;
	private final ComandoPropriedades comandoPropriedades;

	public PropriedadeApi(ConsultaPropriedades consultaPropriedades, ComandoPropriedades comandoPropriedades) {
		this.consultaPropriedades = consultaPropriedades;
		this.comandoPropriedades = comandoPropriedades;
	}

	@GetMapping
	public RespostaListaPropriedades listar() {
		return new RespostaListaPropriedades(consultaPropriedades.listarDoAutenticado());
	}

	@PostMapping
	public ResumoPropriedade registrar(@RequestBody RequisicaoPropriedade requisicao) {
		return comandoPropriedades.registrar(requisicao.nome());
	}

	public record RespostaListaPropriedades(List<ResumoPropriedade> itens) {}

	public record RequisicaoPropriedade(String nome) {}
}
