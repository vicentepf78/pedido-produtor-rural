package br.agriplataforma.catalog.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(schema = "catalog", name = "produto")
public class Produto {

	@Id
	private UUID id;

	private UUID idTenant;

	private String nome;

	private String descricaoCurta;

	private String categoria;

	private String unidade;

	private BigDecimal precoUnitario;

	private boolean disponivel;

	private boolean regulado;

	private String urlImagem;

	protected Produto() {}

	public Produto(
			UUID id,
			UUID idTenant,
			String nome,
			String descricaoCurta,
			String categoria,
			String unidade,
			BigDecimal precoUnitario,
			boolean disponivel,
			boolean regulado,
			String urlImagem) {
		this.id = id;
		this.idTenant = idTenant;
		this.nome = nome;
		this.descricaoCurta = descricaoCurta;
		this.categoria = categoria;
		this.unidade = unidade;
		this.precoUnitario = precoUnitario;
		this.disponivel = disponivel;
		this.regulado = regulado;
		this.urlImagem = urlImagem;
	}

	public UUID id() {
		return id;
	}

	public UUID idTenant() {
		return idTenant;
	}

	public String nome() {
		return nome;
	}

	public String descricaoCurta() {
		return descricaoCurta;
	}

	public String categoria() {
		return categoria;
	}

	public String unidade() {
		return unidade;
	}

	public BigDecimal precoUnitario() {
		return precoUnitario;
	}

	public boolean disponivel() {
		return disponivel;
	}

	public boolean regulado() {
		return regulado;
	}

	public String urlImagem() {
		return urlImagem;
	}
}
