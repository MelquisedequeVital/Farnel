package br.edu.ifpb.gps.farnel.service;

import java.text.Normalizer;
import java.util.Locale;

final class Textos {

	private Textos() {
	}

	/** Remove acentos, converte para minúsculas e apara espaços. Nulo vira texto vazio. */
	static String normalizar(String texto) {
		if (texto == null) {
			return "";
		}
		return Normalizer.normalize(texto, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.toLowerCase(Locale.ROOT)
				.trim();
	}

}
