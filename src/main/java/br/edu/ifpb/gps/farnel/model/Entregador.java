package br.edu.ifpb.gps.farnel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Entregador {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	private String telefone;

	private String veiculo;

	@Column(nullable = false)
	private boolean disponivel = true;

	public Entregador(Usuario usuario, String telefone, String veiculo) {
		this.usuario = usuario;
		this.telefone = telefone;
		this.veiculo = veiculo;
		this.disponivel = true;
	}

}
