package br.edu.ifpb.gps.farnel.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Regra de divisão do valor das refeições entre empresa e colaborador. */
@Getter
@Setter
@NoArgsConstructor
public class SubvencaoForm {

	@NotNull(message = "Informe a porcentagem paga pela empresa.")
	@Min(value = 0, message = "A porcentagem mínima é 0%.")
	@Max(value = 100, message = "A porcentagem máxima é 100%.")
	private Integer percentualSubsidio;

	@NotNull(message = "Informe o teto do subsídio por refeição.")
	@DecimalMin(value = "0.00", message = "O teto não pode ser negativo.")
	@Digits(integer = 8, fraction = 2, message = "Use no máximo 2 casas decimais.")
	private BigDecimal tetoSubsidio;

}
