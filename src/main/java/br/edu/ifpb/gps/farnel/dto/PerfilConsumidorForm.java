package br.edu.ifpb.gps.farnel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerfilConsumidorForm {

	@NotBlank(message = "Informe seu nome completo.")
	private String nome;

	private String email;

	@NotBlank(message = "Informe seu telefone para contato.")
	private String telefone;

	@NotBlank(message = "Informe o endereço de entrega das refeições.")
	private String endereco;

	@NotBlank(message = "Selecione a forma de pagamento.")
	private String tipoPagamento;

	private String titularCartao;

	private String numeroCartaoMascarado;

	private String validadeCartao;

}
