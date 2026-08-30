package br.agriplataforma.identity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.agriplataforma.identity.api.AutenticacaoApi;
import br.agriplataforma.identity.application.ComandoIdentidade;
import br.agriplataforma.identity.application.ConsultaIdentidade;
import br.agriplataforma.identity.application.Papel;
import br.agriplataforma.identity.application.UsuarioAutenticado;
import br.agriplataforma.identity.infrastructure.ConfiguracaoSeguranca;
import br.agriplataforma.producer.api.PropriedadeApi;
import br.agriplataforma.producer.application.ComandoPropriedades;
import br.agriplataforma.producer.application.ConsultaPropriedades;
import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = {AutenticacaoApi.class, PropriedadeApi.class})
@Import(ConfiguracaoSeguranca.class)
@TestPropertySource(
		properties = "spring.autoconfigure.exclude=org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration")
class SessaoExpiradaTest {

	private static final String CHAVE_CARRINHO = "chaveCarrinhoConvidado";

	@Autowired
	MockMvc mvc;

	@MockitoBean
	ComandoIdentidade comandoIdentidade;

	@MockitoBean
	ConsultaIdentidade consultaIdentidade;

	@MockitoBean
	ConsultaPropriedades consultaPropriedades;

	@MockitoBean
	ComandoPropriedades comandoPropriedades;

	@Test
	void ut019_sessaoExpiradaExigeAutenticacaoEPreservaCarrinhoConvidado() throws Exception {
		MockHttpSession sessao = sessaoAutenticada();
		sessao.invalidate();

		MvcResult resultado = mvc.perform(get("/api/v1/produtor/propriedades")
						.session(sessao)
						.cookie(new Cookie(CHAVE_CARRINHO, "convidado-abc")))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.codigo").value("NAO_AUTENTICADO"))
				.andReturn();

		assertThat(resultado.getResponse().getHeaders("Set-Cookie"))
				.noneMatch(SessaoExpiradaTest::apagaCarrinhoConvidado);
		assertThat(resultado.getResponse().getContentAsString()).doesNotContain("senha");
	}

	@Test
	void ut019_saidaNaoApagaCookieDoCarrinhoConvidado() throws Exception {
		MvcResult resultado = mvc.perform(post("/api/v1/autenticacao/saida")
						.with(csrf())
						.session(sessaoAutenticada())
						.cookie(new Cookie(CHAVE_CARRINHO, "convidado-abc")))
				.andExpect(status().isNoContent())
				.andReturn();

		assertThat(resultado.getResponse().getHeaders("Set-Cookie"))
				.noneMatch(SessaoExpiradaTest::apagaCarrinhoConvidado);
	}

	private static MockHttpSession sessaoAutenticada() {
		var usuario = new UsuarioAutenticado(
				UUID.fromString("33333333-3333-3333-3333-333333333333"),
				UUID.fromString("11111111-1111-1111-1111-111111111111"),
				"Produtor Alfa",
				"produtor.alfa@example.com",
				Papel.PRODUTOR);
		var autenticacao = new UsernamePasswordAuthenticationToken(
				usuario, null, List.of(new SimpleGrantedAuthority("PRODUTOR")));
		MockHttpSession sessao = new MockHttpSession();
		sessao.setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				new SecurityContextImpl(autenticacao));
		return sessao;
	}

	private static boolean apagaCarrinhoConvidado(String setCookie) {
		String normalizado = setCookie.toLowerCase();
		return normalizado.startsWith(CHAVE_CARRINHO.toLowerCase() + "=")
				&& (normalizado.contains("max-age=0") || normalizado.contains("max-age=0"));
	}
}
