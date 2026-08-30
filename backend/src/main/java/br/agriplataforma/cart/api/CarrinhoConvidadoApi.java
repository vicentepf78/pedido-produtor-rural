package br.agriplataforma.cart.api;

import br.agriplataforma.cart.application.ServicoCarrinho;
import br.agriplataforma.cart.application.VisaoCarrinho;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carrinhos/convidado")
public class CarrinhoConvidadoApi {

	static final String COOKIE = "chaveCarrinhoConvidado";

	private final ServicoCarrinho servicoCarrinho;
	private final Environment ambiente;

	public CarrinhoConvidadoApi(ServicoCarrinho servicoCarrinho, Environment ambiente) {
		this.servicoCarrinho = servicoCarrinho;
		this.ambiente = ambiente;
	}

	@GetMapping
	public RespostaCarrinho obter(HttpServletRequest requisicao) {
		String chave = chaveExistente(requisicao);
		if (chave == null) {
			return resposta(servicoCarrinho.obter(""));
		}
		return resposta(servicoCarrinho.obter(chave));
	}

	@PostMapping("/itens")
	public RespostaCarrinho adicionar(
			@RequestBody RequisicaoItem requisicao,
			HttpServletRequest pedido,
			HttpServletResponse resposta) {
		String chave = exigirChave(pedido, resposta);
		return resposta(servicoCarrinho.adicionar(chave, requisicao.idProduto(), quantidade(requisicao.quantidade())));
	}

	@PatchMapping("/itens/{idProduto}")
	public RespostaCarrinho alterar(
			@PathVariable UUID idProduto,
			@RequestBody RequisicaoQuantidade requisicao,
			HttpServletRequest pedido,
			HttpServletResponse resposta) {
		String chave = exigirChave(pedido, resposta);
		return resposta(servicoCarrinho.alterarQuantidade(chave, idProduto, quantidade(requisicao.quantidade())));
	}

	@DeleteMapping("/itens/{idProduto}")
	public RespostaCarrinho remover(
			@PathVariable UUID idProduto, HttpServletRequest pedido, HttpServletResponse resposta) {
		String chave = exigirChave(pedido, resposta);
		return resposta(servicoCarrinho.remover(chave, idProduto));
	}

	private String exigirChave(HttpServletRequest pedido, HttpServletResponse resposta) {
		String existente = chaveExistente(pedido);
		if (existente != null) {
			return existente;
		}
		String nova = UUID.randomUUID().toString();
		boolean cookieSeguro =
				ambiente.getProperty("server.servlet.session.cookie.secure", Boolean.class, Boolean.TRUE);
		ResponseCookie cookie = ResponseCookie.from(COOKIE, nova)
				.httpOnly(true)
				.secure(cookieSeguro)
				.sameSite("Lax")
				.path("/")
				.maxAge(java.time.Duration.ofDays(30))
				.build();
		resposta.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		return nova;
	}

	private static String chaveExistente(HttpServletRequest pedido) {
		Cookie[] cookies = pedido.getCookies();
		if (cookies == null) {
			return null;
		}
		return Arrays.stream(cookies)
				.filter(cookie -> COOKIE.equals(cookie.getName()))
				.map(Cookie::getValue)
				.filter(valor -> valor != null && !valor.isBlank())
				.findFirst()
				.orElse(null);
	}

	private static BigDecimal quantidade(Object valor) {
		if (valor == null) {
			return null;
		}
		if (valor instanceof Number numero) {
			return new BigDecimal(numero.toString());
		}
		if (valor instanceof String texto) {
			try {
				return new BigDecimal(texto.trim());
			} catch (NumberFormatException excecao) {
				return new BigDecimal("0.5");
			}
		}
		return new BigDecimal("0.5");
	}

	private static RespostaCarrinho resposta(VisaoCarrinho visao) {
		return new RespostaCarrinho(
				visao.itens().stream()
						.map(item -> new ItemResposta(
								item.idProduto(),
								item.nome(),
								item.quantidade(),
								dinheiro(item.precoUnitario()),
								dinheiro(item.totalLinha())))
						.toList(),
				dinheiro(visao.total()));
	}

	private static String dinheiro(BigDecimal valor) {
		return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}

	public record RequisicaoItem(UUID idProduto, Object quantidade) {}

	public record RequisicaoQuantidade(Object quantidade) {}

	public record RespostaCarrinho(List<ItemResposta> itens, String total) {}

	public record ItemResposta(
			UUID idProduto, String nome, int quantidade, String precoUnitario, String totalLinha) {}
}
