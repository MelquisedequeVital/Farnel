package br.edu.ifpb.gps.farnel.model;

import java.math.BigDecimal;
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

}
