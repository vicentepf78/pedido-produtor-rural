package br.agriplataforma.cart.application;

public interface ConsultaCarrinho {

	VisaoCarrinho obter(String chaveProprietario);

	void esvaziar(String chaveProprietario);
}
