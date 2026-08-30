package br.agriplataforma.order.api;

import br.agriplataforma.order.application.ExcecaoPedido;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class TratamentoExcecoesPedido {

	@ExceptionHandler(ExcecaoPedido.class)
	ResponseEntity<ErroApi> tratar(ExcecaoPedido excecao) {
		HttpStatus status = switch (excecao.codigo()) {
			case "ACESSO_PEDIDO_NEGADO" -> HttpStatus.FORBIDDEN;
			default -> HttpStatus.BAD_REQUEST;
		};
		return ResponseEntity.status(status).body(new ErroApi(excecao.codigo(), excecao.getMessage()));
	}
}
