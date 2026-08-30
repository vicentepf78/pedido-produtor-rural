package br.agriplataforma;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
class FiltroCorrelacaoRequisicao extends OncePerRequestFilter {

	static final String CHAVE = "idCorrelacao";

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String idCorrelacao = request.getHeader("X-Correlation-Id");
		if (idCorrelacao == null || idCorrelacao.isBlank()) {
			idCorrelacao = UUID.randomUUID().toString();
		}
		MDC.put(CHAVE, idCorrelacao);
		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(CHAVE);
		}
	}
}
