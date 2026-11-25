package br.edu.ifpb.gps.farnel.model;

public enum StatusContrato {
	ATIVO("Ativo"),
	SUSPENSO("Suspenso"),
	ENCERRADO("Encerrado");

	private final String rotulo;

	StatusContrato(String rotulo) {
		this.rotulo = rotulo;
	}

	public String getRotulo() {
		return rotulo;
	}
}
