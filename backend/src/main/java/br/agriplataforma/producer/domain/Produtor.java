package br.agriplataforma.producer.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(schema = "producer", name = "produtor")
public class Produtor {

	@Id
	private UUID id;

	private UUID idTenant;

	private UUID idUsuario;

	protected Produtor() {}

	public Produtor(UUID id, UUID idTenant, UUID idUsuario) {
		this.id = id;
		this.idTenant = idTenant;
		this.idUsuario = idUsuario;
	}

	public static Produtor novo(UUID idTenant, UUID idUsuario) {
		return new Produtor(UUID.randomUUID(), idTenant, idUsuario);
	}

	public UUID id() {
		return id;
	}

	public UUID idTenant() {
		return idTenant;
	}

	public UUID idUsuario() {
		return idUsuario;
	}
}
