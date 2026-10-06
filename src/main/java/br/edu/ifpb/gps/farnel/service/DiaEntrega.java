package br.edu.ifpb.gps.farnel.service;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public record DiaEntrega(LocalDate data) implements Serializable {

	private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

	/** Ex.: "Ter" */
	public String getSigla() {
		return capitalizar(data.getDayOfWeek().getDisplayName(TextStyle.SHORT, PT_BR).replace(".", ""));
	}

	/** Ex.: "11/Mar" */
	public String getDiaMes() {
		String mes = capitalizar(data.getMonth().getDisplayName(TextStyle.SHORT, PT_BR).replace(".", ""));
		return String.format("%02d/%s", data.getDayOfMonth(), mes);
	}

	/** Ex.: "terça-feira, 11 de março" */
	public String getDescricao() {
		return data.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", PT_BR));
	}

	private static String capitalizar(String texto) {
		return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
	}

}
