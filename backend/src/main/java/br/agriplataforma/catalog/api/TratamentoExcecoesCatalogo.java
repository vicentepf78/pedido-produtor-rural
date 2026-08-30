package br.agriplataforma.catalog.api;

import br.agriplataforma.catalog.application.ExcecaoCatalogo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class TratamentoExcecoesCatalogo {

	@ExceptionHandler(ExcecaoCatalogo.class)
	ResponseEntity<ErroApi> tratar(ExcecaoCatalogo excecao) {
		HttpStatus status = switch (excecao.codigo()) {
			case "PRODUTO_INDISPONIVEL" -> HttpStatus.CONFLICT;
			case "CATEGORIA_INVALIDA", "TAMANHO_PAGINA_INVALIDO" -> HttpStatus.BAD_REQUEST;
			default -> HttpStatus.NOT_FOUND;
		};
		return ResponseEntity.status(status).body(new ErroApi(excecao.codigo(), excecao.getMessage()));
	}
}
