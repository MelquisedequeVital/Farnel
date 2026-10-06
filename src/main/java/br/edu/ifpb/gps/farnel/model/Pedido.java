package br.edu.ifpb.gps.farnel.model;

import java.time.LocalDate;

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

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDate dataEntrega;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusPedido status = StatusPedido.AGENDADO;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "assinatura_id")
	private Assinatura assinatura;

}
