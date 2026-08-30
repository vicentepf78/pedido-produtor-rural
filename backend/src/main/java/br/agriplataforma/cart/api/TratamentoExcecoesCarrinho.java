package br.agriplataforma.cart.api;

import br.agriplataforma.cart.application.ExcecaoCarrinho;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class TratamentoExcecoesCarrinho {

	@ExceptionHandler(ExcecaoCarrinho.class)
	ResponseEntity<ErroApi> tratar(ExcecaoCarrinho excecao) {
		HttpStatus status = switch (excecao.codigo()) {
			case "QUANTIDADE_INVALIDA" -> HttpStatus.BAD_REQUEST;
			case "PRODUTO_INDISPONIVEL" -> HttpStatus.CONFLICT;
			default -> HttpStatus.NOT_FOUND;
		};
		return ResponseEntity.status(status).body(new ErroApi(excecao.codigo(), excecao.getMessage()));
	}
}
