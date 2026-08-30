package br.agriplataforma.backoffice.api;

import br.agriplataforma.backoffice.application.ExcecaoRetaguarda;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PedidosRetaguardaApi.class)
class TratamentoExcecoesRetaguarda {

	@ExceptionHandler(ExcecaoRetaguarda.class)
	ResponseEntity<ErroApi> tratar(ExcecaoRetaguarda excecao) {
		HttpStatus status = "ACESSO_NEGADO".equals(excecao.codigo()) ? HttpStatus.FORBIDDEN : HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status).body(new ErroApi(excecao.codigo(), excecao.getMessage()));
	}
}
