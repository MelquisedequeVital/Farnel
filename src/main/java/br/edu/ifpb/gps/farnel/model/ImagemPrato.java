package br.edu.ifpb.gps.farnel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Foto enviada pelo restaurante, guardada no banco (bytea). Fica numa tabela
 * separada para que listar pratos não carregue os bytes das imagens.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ImagemPrato {

	@Id
	private Long id;

	@MapsId
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	private Prato prato;

	@Column(nullable = false)
	private byte[] dados;

	@Column(nullable = false, length = 50)
	private String tipo;

}
