package br.agriplataforma.cart.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(schema = "cart", name = "itemCarrinho")
public class ItemCarrinho {

	@Id
	private UUID id;

	private UUID idCarrinho;

	private UUID idProduto;

	private int quantidade;

	private BigDecimal precoUnitario;

	protected ItemCarrinho() {}

	public ItemCarrinho(UUID id, UUID idCarrinho, UUID idProduto, int quantidade, BigDecimal precoUnitario) {
		this.id = id;
		this.idCarrinho = idCarrinho;
		this.idProduto = idProduto;
		this.quantidade = quantidade;
		this.precoUnitario = precoUnitario;
	}

	public static ItemCarrinho novo(UUID idCarrinho, UUID idProduto, int quantidade, BigDecimal precoUnitario) {
		return new ItemCarrinho(UUID.randomUUID(), idCarrinho, idProduto, quantidade, precoUnitario);
	}

	public UUID id() {
		return id;
	}

	public UUID idCarrinho() {
		return idCarrinho;
	}

	public UUID idProduto() {
		return idProduto;
	}

	public int quantidade() {
		return quantidade;
	}

	public BigDecimal precoUnitario() {
		return precoUnitario;
	}

	public void definirQuantidade(int quantidade, BigDecimal precoUnitario) {
		this.quantidade = quantidade;
		this.precoUnitario = precoUnitario;
	}

	public void somar(int quantidade, BigDecimal precoUnitario) {
		this.quantidade += quantidade;
		this.precoUnitario = precoUnitario;
	}

	public BigDecimal totalLinha() {
		return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
	}
}
