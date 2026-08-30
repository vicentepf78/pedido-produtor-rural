package br.agriplataforma.identity.api;

import br.agriplataforma.identity.application.ComandoCadastro;
import br.agriplataforma.identity.application.ComandoEntrada;
import br.agriplataforma.identity.application.ComandoIdentidade;
import br.agriplataforma.identity.application.UsuarioAutenticado;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/autenticacao")
public class AutenticacaoApi {

	private final ComandoIdentidade comandoIdentidade;

	public AutenticacaoApi(ComandoIdentidade comandoIdentidade) {
		this.comandoIdentidade = comandoIdentidade;
	}

	@GetMapping("/csrf")
	public Map<String, String> csrf(CsrfToken token) {
		return Map.of("token", token.getToken());
	}

	@PostMapping("/cadastro")
	public RespostaAutenticacao cadastrar(
			@Valid @RequestBody RequisicaoCadastro requisicao,
			HttpServletRequest pedido,
			HttpServletResponse resposta) {
		UsuarioAutenticado usuario =
				comandoIdentidade.cadastrar(new ComandoCadastro(requisicao.nome(), requisicao.email(), requisicao.senha()));
		SessaoHttp.iniciar(usuario, pedido, resposta);
		return resposta(usuario);
	}

	@PostMapping("/entrada")
	public RespostaAutenticacao entrar(
			@Valid @RequestBody RequisicaoEntrada requisicao,
			HttpServletRequest pedido,
			HttpServletResponse resposta) {
		UsuarioAutenticado usuario =
				comandoIdentidade.entrar(new ComandoEntrada(requisicao.email(), requisicao.senha()));
		SessaoHttp.iniciar(usuario, pedido, resposta);
		return resposta(usuario);
	}

	@PostMapping("/saida")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void sair(HttpServletRequest pedido, HttpServletResponse resposta) {
		SessaoHttp.encerrar(pedido, resposta);
	}

	private static RespostaAutenticacao resposta(UsuarioAutenticado usuario) {
		return new RespostaAutenticacao(usuario.id(), usuario.nome(), List.of(usuario.papel().name()));
	}
}
