package br.agriplataforma.cart.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(schema = "cart", name = "carrinho")
public class Carrinho {

	@Id
	private UUID id;

	private UUID idTenant;

	private String chaveProprietario;

	protected Carrinho() {}

	public Carrinho(UUID id, UUID idTenant, String chaveProprietario) {
		this.id = id;
		this.idTenant = idTenant;
		this.chaveProprietario = chaveProprietario;
	}

	public static Carrinho novo(UUID idTenant, String chaveProprietario) {
		return new Carrinho(UUID.randomUUID(), idTenant, chaveProprietario);
	}

	public UUID id() {
		return id;
	}

	public UUID idTenant() {
		return idTenant;
	}

	public String chaveProprietario() {
		return chaveProprietario;
	}
}
