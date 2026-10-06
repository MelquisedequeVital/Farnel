package br.edu.ifpb.gps.farnel.model;

import java.math.BigDecimal;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Prato {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nome;

	@Column(length = 1000)
	private String descricao;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal preco;

	private String alergenos;

	/** Caminho da imagem servida a partir de static/, ex.: /img/pratos/lasanha-bolonhesa.jpg */
	private String imagem;

	/** Pratos em destaque aparecem no banner do catálogo. */
	@ColumnDefault("false")
	@Column(nullable = false)
	private boolean destaque;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "restaurante_id")
	private Restaurante restaurante;

}
