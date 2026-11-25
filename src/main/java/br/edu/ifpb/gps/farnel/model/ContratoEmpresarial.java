package br.edu.ifpb.gps.farnel.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

	// ---------- RF-B2B-02: Subvenção de Refeições ----------

	/** Porcentagem do valor da refeição paga pela empresa (0 a 100). */
	private Integer percentualSubsidio = 0;

	/** Valor máximo que a empresa paga por refeição (teto do subsídio). */
	@Column(precision = 10, scale = 2)
	private BigDecimal tetoSubsidio = BigDecimal.ZERO;

	/** Divide o preço de uma refeição entre a parte da empresa e a do colaborador. */
	public DivisaoRefeicao dividir(BigDecimal preco) {
		int percentual = percentualSubsidio == null ? 0 : percentualSubsidio;
		BigDecimal teto = tetoSubsidio == null ? BigDecimal.ZERO : tetoSubsidio;
		BigDecimal pelaPorcentagem = preco.multiply(BigDecimal.valueOf(percentual))
				.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
		BigDecimal parteEmpresa = pelaPorcentagem.min(teto);
		boolean limitadoPeloTeto = pelaPorcentagem.compareTo(teto) > 0;
		return new DivisaoRefeicao(preco, parteEmpresa, preco.subtract(parteEmpresa), limitadoPeloTeto);
	}

	/**
	 * Resultado da divisão: {@code limitadoPeloTeto} indica que o subsídio parou no teto
	 * e o colaborador paga o excedente.
	 */
	public record DivisaoRefeicao(BigDecimal total, BigDecimal empresa, BigDecimal colaborador,
			boolean limitadoPeloTeto) {
	}

	public String getNumero() {
		return id == null ? "—" : String.format("B2B-%04d", id);
	}

}
