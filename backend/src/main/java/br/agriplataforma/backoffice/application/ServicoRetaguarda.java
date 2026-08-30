package br.agriplataforma.backoffice.application;

import br.agriplataforma.identity.application.ConsultaIdentidade;
import br.agriplataforma.identity.application.Papel;
import br.agriplataforma.identity.application.UsuarioAutenticado;
import br.agriplataforma.order.application.ConsultaPedidoRetaguarda;
import br.agriplataforma.order.application.PaginaPedido;
import br.agriplataforma.order.application.ResumoPedidoRetaguarda;
import br.agriplataforma.order.application.VisaoPedidoRetaguarda;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ServicoRetaguarda implements ConsultaRetaguarda {

	static final String ACESSO_NEGADO = "ACESSO_NEGADO";
	static final String MSG_NEGADO = "Somente o operador da revenda pode inspecionar os pedidos da retaguarda.";

	private final ConsultaIdentidade consultaIdentidade;
	private final ConsultaPedidoRetaguarda consultaPedidoRetaguarda;

	public ServicoRetaguarda(
			ConsultaIdentidade consultaIdentidade, ConsultaPedidoRetaguarda consultaPedidoRetaguarda) {
		this.consultaIdentidade = consultaIdentidade;
		this.consultaPedidoRetaguarda = consultaPedidoRetaguarda;
	}

	@Override
	public PaginaPedido<ResumoPedidoRetaguarda> listar(int pagina, int tamanhoPagina) {
		UsuarioAutenticado operador = exigirOperador();
		return consultaPedidoRetaguarda.listarPorTenant(operador.idTenant(), pagina, tamanhoPagina);
	}

	@Override
	public VisaoPedidoRetaguarda obter(UUID idPedido) {
		UsuarioAutenticado operador = exigirOperador();
		return consultaPedidoRetaguarda.obterPorTenant(operador.idTenant(), idPedido);
	}

	private UsuarioAutenticado exigirOperador() {
		UsuarioAutenticado usuario = consultaIdentidade.exigirAutenticado();
		if (usuario.papel() != Papel.OPERADOR_REVENDA) {
			throw new ExcecaoRetaguarda(ACESSO_NEGADO, MSG_NEGADO);
		}
		return usuario;
	}
}
