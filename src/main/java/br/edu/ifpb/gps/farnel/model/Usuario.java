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

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String senha;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipoUsuario tipo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurante_id")
	private Restaurante restaurante;

	/** Preenchido apenas para o gestor de uma empresa contratante (B2B). */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id")
	private Empresa empresa;

	public Usuario(String nome, String email, String senha, TipoUsuario tipo) {
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.tipo = tipo;
	}

}
