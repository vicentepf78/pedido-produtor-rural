package arquitetura.ut026.violacao.carrinho.application;

import arquitetura.ut026.violacao.catalogo.infrastructure.RepositorioCatalogo;

public class ServicoCarrinhoIlegal {

	private final RepositorioCatalogo repositorio;

	public ServicoCarrinhoIlegal(RepositorioCatalogo repositorio) {
		this.repositorio = repositorio;
	}
}
