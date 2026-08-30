package br.agriplataforma.identity.api;

import br.agriplataforma.identity.application.ExcecaoAutenticacao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class TratamentoExcecoesIdentidade {

	@ExceptionHandler(ExcecaoAutenticacao.class)
	ResponseEntity<ErroApi> tratar(ExcecaoAutenticacao excecao) {
		HttpStatus status = switch (excecao.codigo()) {
			case "EMAIL_DUPLICADO" -> HttpStatus.CONFLICT;
			case "NAO_AUTENTICADO" -> HttpStatus.UNAUTHORIZED;
			default -> HttpStatus.UNAUTHORIZED;
		};
		return ResponseEntity.status(status).body(new ErroApi(excecao.codigo(), excecao.getMessage()));
	}
}
