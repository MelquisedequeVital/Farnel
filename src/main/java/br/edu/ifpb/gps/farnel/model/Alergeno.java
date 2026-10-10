package br.edu.ifpb.gps.farnel.model;

import java.util.List;

import lombok.Getter;

/**
 * Alérgenos oferecidos como checkbox no cadastro de pratos. O prato continua
 * guardando os alérgenos como texto (o rótulo de cada item, separado por
 * vírgula); os sinônimos servem para reconhecer textos já existentes.
 */
@Getter
public enum Alergeno {

	GLUTEN("Glúten", "Trigo, centeio, cevada", List.of("gluten", "trigo", "centeio", "cevada", "aveia")),
	LACTOSE("Lactose", "Leite, manteiga, queijos, caseína", List.of("lactose", "leite", "queijo", "manteiga", "caseina")),
	CASTANHAS("Castanhas e Oleaginosas", "Nozes, amêndoas, castanha de caju",
			List.of("castanha", "amendoa", "nozes", "oleaginosa", "pistache", "avela", "macadamia")),
	FRUTOS_DO_MAR("Frutos do Mar e Peixes", "Peixes, camarão, crustáceos",
			List.of("peixe", "frutos do mar", "crustaceo", "camarao", "marisco", "molusco")),
	OVOS("Ovos", "Gema, albumina, maionese", List.of("ovo")),
	SOJA("Soja", "Lecitina, óleo de soja, shoyu", List.of("soja")),
	PIMENTA("Pimenta", "Pimentas frescas ou maceradas", List.of("pimenta", "capsaicina")),
	AMENDOIM("Amendoim", "Pasta e óleo de amendoim", List.of("amendoim"));

	private final String rotulo;
	private final String exemplos;
	/** Termos normalizados (sem acento, minúsculos) que identificam o alérgeno num texto. */
	private final List<String> sinonimos;

	Alergeno(String rotulo, String exemplos, List<String> sinonimos) {
		this.rotulo = rotulo;
		this.exemplos = exemplos;
		this.sinonimos = sinonimos;
	}

}
