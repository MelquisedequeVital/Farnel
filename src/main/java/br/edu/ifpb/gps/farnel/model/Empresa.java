package br.edu.ifpb.gps.farnel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Empresa contratante (módulo B2B) credenciada na plataforma. */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Empresa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String razaoSocial;

	@Column(nullable = false, unique = true, length = 14)
	private String cnpj;

	@Column(nullable = false)
	private String responsavel;

	private String telefone;

	/** Endereço corporativo onde o lote coletivo de marmitas é entregue. */
	@Column(nullable = false)
	private String enderecoEntrega;

	public String getCnpjFormatado() {
		if (cnpj == null || cnpj.length() != 14) return cnpj;
		return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(5, 8) + "/"
				+ cnpj.substring(8, 12) + "-" + cnpj.substring(12);
	}

}
