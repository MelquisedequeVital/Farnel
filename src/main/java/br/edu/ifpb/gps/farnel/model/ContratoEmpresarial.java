package br.edu.ifpb.gps.farnel.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Contrato coletivo de refeições de uma empresa contratante (uma por empresa). */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ContratoEmpresarial {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "empresa_id", unique = true)
	private Empresa empresa;

	@Column(nullable = false)
	private LocalDate dataInicio;

	private LocalDate dataFim;

	/** Quantidade máxima de colaboradores que podem usar o benefício. */
	@Column(nullable = false)
	private Integer limiteColaboradores;

	/** Horário previsto de chegada do lote coletivo no endereço da empresa. */
	@Column(nullable = false)
	private LocalTime horarioEntrega;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusContrato status = StatusContrato.ATIVO;

	public String getNumero() {
		return id == null ? "—" : String.format("B2B-%04d", id);
	}

}
