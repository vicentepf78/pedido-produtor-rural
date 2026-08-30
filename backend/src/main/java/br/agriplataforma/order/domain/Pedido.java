package br.agriplataforma.order.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(schema = "order", name = "pedido")
public class Pedido {

	@Id
	private UUID id;

	private UUID idTenant;

	private UUID idProdutor;

	private UUID idPropriedade;

	private String nomePropriedade;

	private String preferenciaRetirada;

	private String situacao;

	private String confirmacao;

	private BigDecimal total;

	private String chaveIdempotencia;

	private Instant criadoEm;

	protected Pedido() {}

	public Pedido(
			UUID id,
			UUID idTenant,
			UUID idProdutor,
			UUID idPropriedade,
			String nomePropriedade,
			String preferenciaRetirada,
			String situacao,
			String confirmacao,
			BigDecimal total,
			String chaveIdempotencia,
			Instant criadoEm) {
		this.id = id;
		this.idTenant = idTenant;
		this.idProdutor = idProdutor;
		this.idPropriedade = idPropriedade;
		this.nomePropriedade = nomePropriedade;
		this.preferenciaRetirada = preferenciaRetirada;
		this.situacao = situacao;
		this.confirmacao = confirmacao;
		this.total = total;
		this.chaveIdempotencia = chaveIdempotencia;
		this.criadoEm = criadoEm;
	}

	public static Pedido novo(
			UUID idTenant,
			UUID idProdutor,
			UUID idPropriedade,
			String nomePropriedade,
			String preferenciaRetirada,
			BigDecimal total,
			String chaveIdempotencia) {
		return new Pedido(
				UUID.randomUUID(),
				idTenant,
				idProdutor,
				idPropriedade,
				nomePropriedade,
				preferenciaRetirada,
				"RECEBIDO",
				"PENDENTE",
				total,
				chaveIdempotencia,
				Instant.now());
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

	public UUID idPropriedade() {
		return idPropriedade;
	}

	public String nomePropriedade() {
		return nomePropriedade;
	}

	public String preferenciaRetirada() {
		return preferenciaRetirada;
	}

	public String situacao() {
		return situacao;
	}

	public String confirmacao() {
		return confirmacao;
	}

	public BigDecimal total() {
		return total;
	}

	public String chaveIdempotencia() {
		return chaveIdempotencia;
	}

	public Instant criadoEm() {
		return criadoEm;
	}
}
