package br.edu.ifpb.gps.farnel.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Dados editáveis do contrato coletivo de refeições. */
@Getter
@Setter
@NoArgsConstructor
public class ContratoForm {

	@NotNull(message = "Informe a data de início.")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dataInicio;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate dataFim;

	@NotNull(message = "Informe o limite de colaboradores.")
	@Min(value = 1, message = "O contrato deve atender pelo menos 1 colaborador.")
	@Max(value = 5000, message = "Limite máximo de 5000 colaboradores.")
	private Integer limiteColaboradores;

	@NotNull(message = "Informe o horário de entrega do lote.")
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime horarioEntrega;

}
