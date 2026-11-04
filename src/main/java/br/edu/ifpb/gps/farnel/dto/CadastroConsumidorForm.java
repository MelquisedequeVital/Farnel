package br.edu.ifpb.gps.farnel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CadastroConsumidorForm {

	@NotBlank(message = "Informe seu nome completo.")
	private String nome;

	@NotBlank(message = "Informe seu e-mail.")
	@Email(message = "Informe um e-mail válido.")
	private String email;

	@NotBlank(message = "Informe uma senha.")
	@Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
	private String senha;

	@NotBlank(message = "Informe seu telefone para contato.")
	private String telefone;

	@NotBlank(message = "Informe o endereço de entrega das refeições.")
	private String endereco;

	@NotBlank(message = "Selecione a forma de pagamento preferencial.")
	private String tipoPagamento = "Cartão de Crédito";

	private String titularCartao;

	private String numeroCartaoMascarado;

	private String validadeCartao;

}
