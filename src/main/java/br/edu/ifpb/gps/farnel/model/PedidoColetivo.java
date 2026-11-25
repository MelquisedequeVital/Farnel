package br.edu.ifpb.gps.farnel.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.ColumnDefault;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Marmita de um colaborador dentro do lote coletivo do dia. Os valores da divisão
 * (empresa x colaborador) são gravados no momento do pedido, conforme a subvenção vigente.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class PedidoColetivo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "colaborador_id")
	private Colaborador colaborador;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "prato_id")
	private Prato prato;

	@Column(nullable = false)
	private LocalDate dataEntrega;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal valorTotal;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal valorEmpresa;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal valorColaborador;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusPedido status = StatusPedido.AGENDADO;

	/** Gerado automaticamente a partir de uma recorrência do colaborador. */
	@ColumnDefault("false")
	@Column(nullable = false)
	private boolean recorrente;

}
