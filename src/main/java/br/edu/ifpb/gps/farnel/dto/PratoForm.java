package br.edu.ifpb.gps.farnel.dto;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Set;

import br.edu.ifpb.gps.farnel.model.Alergeno;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dados do formulário de cadastro/edição de prato no painel do parceiro. */
@Getter
@Setter
@NoArgsConstructor
public class PratoForm {

	@NotNull(message = "Selecione o restaurante.")
	private Long restauranteId;

	@NotBlank(message = "Informe o nome do prato.")
	@Size(max = 255, message = "O nome deve ter no máximo 255 caracteres.")
	private String nome;

	@Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres.")
	private String descricao;

	@NotNull(message = "Informe o preço.")
	@DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
	@Digits(integer = 8, fraction = 2, message = "Use no máximo 2 casas decimais.")
	private BigDecimal preco;

	private Set<Alergeno> alergenos = EnumSet.noneOf(Alergeno.class);

	/** Alérgenos que não estão na lista de checkboxes (ex.: "Gergelim"). */
	@Size(max = 200, message = "Use no máximo 200 caracteres.")
	private String outrosAlergenos;

	private boolean destaque;

	/** Na edição, marca a remoção da foto atual. */
	private boolean removerFoto;

}
