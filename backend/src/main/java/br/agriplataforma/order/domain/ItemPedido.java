package br.agriplataforma.order.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(schema = "order", name = "itemPedido")
public class ItemPedido {

	@Id
	private UUID id;

	private UUID idPedido;

	private UUID idProduto;

	private String nome;

	private String unidade;

	private int quantidade;

	private BigDecimal precoUnitario;

	private BigDecimal totalLinha;

	protected ItemPedido() {}

	public ItemPedido(
			UUID id,
			UUID idPedido,
			UUID idProduto,
			String nome,
			String unidade,
			int quantidade,
			BigDecimal precoUnitario,
			BigDecimal totalLinha) {
		this.id = id;
		this.idPedido = idPedido;
		this.idProduto = idProduto;
		this.nome = nome;
		this.unidade = unidade;
		this.quantidade = quantidade;
		this.precoUnitario = precoUnitario;
		this.totalLinha = totalLinha;
	}

	public static ItemPedido novo(
			UUID idPedido,
			UUID idProduto,
			String nome,
			String unidade,
			int quantidade,
			BigDecimal precoUnitario,
			BigDecimal totalLinha) {
		return new ItemPedido(
				UUID.randomUUID(), idPedido, idProduto, nome, unidade, quantidade, precoUnitario, totalLinha);
	}

	public UUID id() {
		return id;
	}

	public UUID idPedido() {
		return idPedido;
	}

	public UUID idProduto() {
		return idProduto;
	}

	public String nome() {
		return nome;
	}

	public String unidade() {
		return unidade;
	}

	public int quantidade() {
		return quantidade;
	}

	public BigDecimal precoUnitario() {
		return precoUnitario;
	}

	public BigDecimal totalLinha() {
		return totalLinha;
	}
}
