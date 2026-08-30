package br.agriplataforma.producer.api;

import br.agriplataforma.producer.application.ExcecaoProdutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class TratamentoExcecoesProdutor {

	@ExceptionHandler(ExcecaoProdutor.class)
	ResponseEntity<ErroApi> tratar(ExcecaoProdutor excecao) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErroApi(excecao.codigo(), excecao.getMessage()));
	}
}
