package br.agriplataforma.producer.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(schema = "producer", name = "propriedade")
public class Propriedade {

	@Id
	private UUID id;

	private UUID idTenant;

	private UUID idProdutor;

	private String nome;

	protected Propriedade() {}

	public Propriedade(UUID id, UUID idTenant, UUID idProdutor, String nome) {
		this.id = id;
		this.idTenant = idTenant;
		this.idProdutor = idProdutor;
		this.nome = nome;
	}

	public UUID id() {
		return id;
	}

	public UUID idTenant() {
		return idTenant;
	}

	public UUID idProdutor() {
		return idProdutor;
	}

	public String nome() {
		return nome;
	}
}
