package br.agriplataforma.backoffice.api;

import br.agriplataforma.backoffice.application.ConsultaRetaguarda;
import br.agriplataforma.order.application.PaginaPedido;
import br.agriplataforma.order.application.ResumoPedidoRetaguarda;
import br.agriplataforma.order.application.VisaoPedidoRetaguarda;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/retaguarda/pedidos")
public class PedidosRetaguardaApi {

	private final ConsultaRetaguarda consultaRetaguarda;

	public PedidosRetaguardaApi(ConsultaRetaguarda consultaRetaguarda) {
		this.consultaRetaguarda = consultaRetaguarda;
	}

	@GetMapping
	public RespostaLista listar(
			@RequestParam(required = false) Integer pagina, @RequestParam(required = false) Integer tamanhoPagina) {
		int paginaEfetiva = pagina == null ? 1 : pagina;
		int tamanhoEfetivo = tamanhoPagina == null ? 25 : tamanhoPagina;
		PaginaPedido<ResumoPedidoRetaguarda> resultado = consultaRetaguarda.listar(paginaEfetiva, tamanhoEfetivo);
		return new RespostaLista(
				resultado.itens().stream().map(PedidosRetaguardaApi::resumo).toList(),
				resultado.pagina(),
				resultado.tamanhoPagina(),
				resultado.total());
	}

	@GetMapping("/{idPedido}")
	public RespostaPedido detalhe(@PathVariable UUID idPedido) {
		return resposta(consultaRetaguarda.obter(idPedido));
	}

	private static ItemLista resumo(ResumoPedidoRetaguarda pedido) {
		return new ItemLista(
				pedido.idPedido(),
				pedido.nomeProdutor(),
				dinheiro(pedido.total()),
				pedido.situacao(),
				pedido.confirmacao(),
				pedido.criadoEm() == null ? null : pedido.criadoEm().truncatedTo(ChronoUnit.SECONDS).toString());
	}

	private static RespostaPedido resposta(VisaoPedidoRetaguarda visao) {
		return new RespostaPedido(
				visao.idPedido(),
				visao.nomeProdutor(),
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

	private static String dinheiro(BigDecimal valor) {
		return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}

	public record RespostaLista(List<ItemLista> itens, int pagina, int tamanhoPagina, long total) {}

	public record ItemLista(
			UUID idPedido,
			String nomeProdutor,
			String total,
			String situacao,
			String confirmacao,
			String criadoEm) {}

	public record RespostaPedido(
			UUID idPedido,
			String nomeProdutor,
			String situacao,
			String confirmacao,
			String nomePropriedade,
			String preferenciaRetirada,
			String total,
			String criadoEm,
			List<ItemResposta> itens) {}

	public record ItemResposta(
			String nome, String unidade, int quantidade, String precoUnitario, String totalLinha) {}
}
