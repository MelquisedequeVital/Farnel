package br.edu.ifpb.gps.farnel.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Consumidor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false, unique = true)
	private String email;

	private String telefone;

	private String endereco;

	// Forma de pagamento simulada (o consumidor tem apenas uma cadastrada)
	private String tipoPagamento;

	private String titularCartao;

	private String numeroCartaoMascarado;

	private String validadeCartao;

	@OneToMany(mappedBy = "consumidor")
	private List<Assinatura> assinaturas = new ArrayList<>();

}
