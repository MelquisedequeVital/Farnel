package br.edu.ifpb.gps.farnel.model;

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

/** Funcionário de uma empresa contratante vinculado ao benefício de alimentação. */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Colaborador {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "empresa_id")
	private Empresa empresa;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false)
	private String email;

	private String setor;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusColaborador status = StatusColaborador.ATIVO;

	public boolean isAtivo() {
		return status == StatusColaborador.ATIVO;
	}

}
