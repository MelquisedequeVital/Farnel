package br.edu.ifpb.gps.farnel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Credenciamento de uma empresa contratante (B2B). */
@Getter
@Setter
public class CadastroEmpresaForm {

	@NotBlank(message = "Informe a razão social.")
	private String razaoSocial;

	@NotBlank(message = "Informe o CNPJ.")
	@Pattern(regexp = "\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}", message = "Informe um CNPJ com 14 dígitos.")
	private String cnpj;

	@NotBlank(message = "Informe o nome do gestor responsável.")
	private String responsavel;

	@NotBlank(message = "Informe o e-mail de acesso.")
	@Email(message = "Informe um e-mail válido.")
	private String email;

	@NotBlank(message = "Informe uma senha.")
	@Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
	private String senha;

	@NotBlank(message = "Informe um telefone para contato.")
	private String telefone;

	@NotBlank(message = "Informe o endereço corporativo de entrega.")
	private String enderecoEntrega;

}
