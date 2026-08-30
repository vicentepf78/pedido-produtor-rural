package br.agriplataforma.identity.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(schema = "identity", name = "usuario")
public class Usuario {

	@Id
	private UUID id;

	private UUID idTenant;

	private String nome;

	private String email;

	private String senhaHash;

	private String papel;

	protected Usuario() {}

	public Usuario(UUID id, UUID idTenant, String nome, String email, String senhaHash, String papel) {
		this.id = id;
		this.idTenant = idTenant;
		this.nome = nome;
		this.email = email;
		this.senhaHash = senhaHash;
		this.papel = papel;
	}

	public static Usuario novo(UUID idTenant, String nome, String email, String senhaHash, String papel) {
		return new Usuario(UUID.randomUUID(), idTenant, nome, email, senhaHash, papel);
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

	public String email() {
		return email;
	}

	public String senhaHash() {
		return senhaHash;
	}

	public String papel() {
		return papel;
	}
}
