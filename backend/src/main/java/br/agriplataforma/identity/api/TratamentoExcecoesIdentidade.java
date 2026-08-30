package br.agriplataforma.identity.api;

import br.agriplataforma.identity.application.ExcecaoAutenticacao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class TratamentoExcecoesIdentidade {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ErroApi> tratarValidacao(MethodArgumentNotValidException excecao) {
		String campo = excecao.getBindingResult().getFieldErrors().stream()
				.map(erro -> erro.getField())
				.findFirst()
				.orElse("campo");
		String mensagem = switch (campo) {
			case "email" -> "Informe o e-mail.";
			case "senha" -> "Informe a senha.";
			case "nome" -> "Informe o nome.";
			default -> "Informe o " + campo + ".";
		};
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroApi("DADOS_OBRIGATORIOS", mensagem));
	}

	@ExceptionHandler(ExcecaoAutenticacao.class)
	ResponseEntity<ErroApi> tratar(ExcecaoAutenticacao excecao) {
		HttpStatus status = switch (excecao.codigo()) {
			case "EMAIL_DUPLICADO" -> HttpStatus.CONFLICT;
			case "DADOS_OBRIGATORIOS" -> HttpStatus.BAD_REQUEST;
			case "NAO_AUTENTICADO" -> HttpStatus.UNAUTHORIZED;
			default -> HttpStatus.UNAUTHORIZED;
		};
		return ResponseEntity.status(status).body(new ErroApi(excecao.codigo(), excecao.getMessage()));
	}
}
