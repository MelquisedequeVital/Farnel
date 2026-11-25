package br.edu.ifpb.gps.farnel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Inclusão de um colaborador no quadro do benefício. */
@Getter
@Setter
@NoArgsConstructor
public class ColaboradorForm {

	@NotBlank(message = "Informe o nome do colaborador.")
	@Size(max = 255, message = "Use no máximo 255 caracteres.")
	private String nome;

	@NotBlank(message = "Informe o e-mail corporativo.")
	@Email(message = "Informe um e-mail válido.")
	private String email;

	@Size(max = 100, message = "Use no máximo 100 caracteres.")
	private String setor;

}
