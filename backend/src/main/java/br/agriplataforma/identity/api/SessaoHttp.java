package br.agriplataforma.identity.api;

import br.agriplataforma.identity.application.UsuarioAutenticado;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

final class SessaoHttp {

	private static final SecurityContextRepository REPOSITORIO = new HttpSessionSecurityContextRepository();

	private SessaoHttp() {}

	static void iniciar(UsuarioAutenticado usuario, HttpServletRequest requisicao, HttpServletResponse resposta) {
		var autenticacao = new UsernamePasswordAuthenticationToken(
				usuario, null, List.of(new SimpleGrantedAuthority(usuario.papel().name())));
		SecurityContext contexto = SecurityContextHolder.createEmptyContext();
		contexto.setAuthentication(autenticacao);
		SecurityContextHolder.setContext(contexto);
		requisicao.getSession(true);
		REPOSITORIO.saveContext(contexto, requisicao, resposta);
	}

	static void encerrar(HttpServletRequest requisicao, HttpServletResponse resposta) {
		SecurityContextHolder.clearContext();
		HttpSession sessao = requisicao.getSession(false);
		if (sessao != null) {
			sessao.invalidate();
		}
		REPOSITORIO.saveContext(SecurityContextHolder.createEmptyContext(), requisicao, resposta);
	}
}
