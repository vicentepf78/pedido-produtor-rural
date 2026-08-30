package br.agriplataforma.order.api;

import br.agriplataforma.identity.application.ConsultaIdentidade;
import br.agriplataforma.order.application.ComandoPedido;
import br.agriplataforma.order.application.ConfirmacaoPedido;
import br.agriplataforma.order.application.CriarPedido;
import br.agriplataforma.order.application.PaginaPedido;
import br.agriplataforma.order.application.ResumoPedido;
import br.agriplataforma.order.application.VisaoPedido;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidosApi {

	static final String COOKIE_CARRINHO = "chaveCarrinhoConvidado";

	private final ComandoPedido comandoPedido;
	private final ConsultaIdentidade consultaIdentidade;

	public PedidosApi(ComandoPedido comandoPedido, ConsultaIdentidade consultaIdentidade) {
		this.comandoPedido = comandoPedido;
		this.consultaIdentidade = consultaIdentidade;
	}

	@PostMapping
	public RespostaConfirmacao criar(
			@RequestBody RequisicaoPedido requisicao,
			@RequestHeader(value = "Idempotency-Key", required = false) String chaveIdempotencia,
			HttpServletRequest pedido) {
		ConfirmacaoPedido confirmacao = comandoPedido.criar(new CriarPedido(
				chaveCarrinho(pedido),
				requisicao.idPropriedade(),
				requisicao.preferenciaRetirada(),
				chaveIdempotencia));
		return new RespostaConfirmacao(
				confirmacao.idPedido(),
				confirmacao.situacao(),
				confirmacao.confirmacao(),
				confirmacao.mensagem());
	}

	@GetMapping("/{idPedido}")
	public RespostaPedido detalhe(@PathVariable UUID idPedido) {
		UUID idUsuario = consultaIdentidade.exigirAutenticado().id();
		return resposta(comandoPedido.obterParaProdutor(idUsuario, idPedido));
	}

	@GetMapping
	public RespostaLista listar(
			@RequestParam(required = false) Integer pagina, @RequestParam(required = false) Integer tamanhoPagina) {
		UUID idUsuario = consultaIdentidade.exigirAutenticado().id();
		int paginaEfetiva = pagina == null ? 1 : pagina;
		int tamanhoEfetivo = tamanhoPagina == null ? 10 : tamanhoPagina;
		PaginaPedido<ResumoPedido> resultado =
				comandoPedido.listarParaProdutor(idUsuario, paginaEfetiva, tamanhoEfetivo);
		return new RespostaLista(
				resultado.itens().stream().map(PedidosApi::resumo).toList(),
				resultado.pagina(),
				resultado.tamanhoPagina(),
				resultado.total());
	}

	private static String chaveCarrinho(HttpServletRequest pedido) {
		Cookie[] cookies = pedido.getCookies();
		if (cookies == null) {
			return "";
		}
		return Arrays.stream(cookies)
				.filter(cookie -> COOKIE_CARRINHO.equals(cookie.getName()))
				.map(Cookie::getValue)
				.filter(valor -> valor != null && !valor.isBlank())
				.findFirst()
				.orElse("");
	}

	private static RespostaPedido resposta(VisaoPedido visao) {
		return new RespostaPedido(
				visao.idPedido(),
				visao.situacao(),
				visao.confirmacao(),
				visao.nomePropriedade(),
				visao.preferenciaRetirada(),
				dinheiro(visao.total()),
				visao.criadoEm() == null ? null : visao.criadoEm().truncatedTo(ChronoUnit.SECONDS).toString(),
				visao.itens().stream()
						.map(item -> new ItemResposta(
								item.nome(),
								item.unidade(),
								item.quantidade(),
								dinheiro(item.precoUnitario()),
								dinheiro(item.totalLinha())))
						.toList());
	}

	private static ItemLista resumo(ResumoPedido pedido) {
		return new ItemLista(
				pedido.idPedido(),
				pedido.situacao(),
				pedido.confirmacao(),
				dinheiro(pedido.total()),
				pedido.criadoEm() == null ? null : pedido.criadoEm().truncatedTo(ChronoUnit.SECONDS).toString());
	}

	private static String dinheiro(BigDecimal valor) {
		return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}

	public record RequisicaoPedido(UUID idPropriedade, String preferenciaRetirada) {}

	public record RespostaConfirmacao(UUID idPedido, String situacao, String confirmacao, String mensagem) {}

	public record RespostaPedido(
			UUID idPedido,
			String situacao,
			String confirmacao,
			String nomePropriedade,
			String preferenciaRetirada,
			String total,
			String criadoEm,
			List<ItemResposta> itens) {}

	public record ItemResposta(
			String nome, String unidade, int quantidade, String precoUnitario, String totalLinha) {}

	public record RespostaLista(List<ItemLista> itens, int pagina, int tamanhoPagina, long total) {}

	public record ItemLista(UUID idPedido, String situacao, String confirmacao, String total, String criadoEm) {}
}
