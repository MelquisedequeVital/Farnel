package br.edu.ifpb.gps.farnel.model;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Marmita recorrente do colaborador B2B: o mesmo prato toda semana no dia indicado.
 * Um colaborador tem no máximo uma recorrência por dia da semana.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "colaborador_id", "dia_semana" }))
@Getter
@Setter
@NoArgsConstructor
public class RecorrenciaColaborador {

	private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "colaborador_id")
	private Colaborador colaborador;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "prato_id")
	private Prato prato;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DayOfWeek diaSemana;

	/** Ex.: "Segunda-feira". */
	public String getDiaRotulo() {
		return rotulo(diaSemana);
	}

	/** Ex.: "Seg". */
	public String getDiaSigla() {
		return sigla(diaSemana);
	}

	public static String rotulo(DayOfWeek dia) {
		String nome = dia.getDisplayName(TextStyle.FULL, PT_BR);
		return Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
	}

	public static String sigla(DayOfWeek dia) {
		String nome = dia.getDisplayName(TextStyle.SHORT, PT_BR).replace(".", "");
		return Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
	}

}
