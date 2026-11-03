package br.edu.ifpb.gps.farnel.model;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Assinatura {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusAssinatura status = StatusAssinatura.ATIVA;

	@Column(nullable = false)
	private String diasSemana;

	@Column
	private LocalTime horarioEntrega;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal valor;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "consumidor_id")
	private Consumidor consumidor;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "prato_id")
	private Prato prato;

	@OneToMany(mappedBy = "assinatura")
	private List<Pedido> pedidos = new ArrayList<>();

	public String getHorarioFormatado() {
		return horarioEntrega != null ? horarioEntrega.format(DateTimeFormatter.ofPattern("HH:mm")) : "12:00";
	}

	public String getDiaSemanaAbreviado() {
		if (diasSemana == null) return "Ter";
		String d = diasSemana.toLowerCase();
		if (d.contains("seg")) return "Seg";
		if (d.contains("ter")) return "Ter";
		if (d.contains("qua")) return "Quar";
		if (d.contains("qui")) return "Qui";
		if (d.contains("sex")) return "Sex";
		if (d.contains("sáb") || d.contains("sab")) return "Sab";
		if (d.contains("dom")) return "Dom";
		return diasSemana;
	}

}
