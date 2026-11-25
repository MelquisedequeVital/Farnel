package br.edu.ifpb.gps.farnel.model;

public enum StatusColaborador {
	ATIVO("Ativo"),
	PAUSADO("Pausado"),
	/** Desligado do benefício: some do quadro, mas o histórico de pedidos é mantido. */
	REMOVIDO("Removido");

	private final String rotulo;

	StatusColaborador(String rotulo) {
		this.rotulo = rotulo;
	}

	public String getRotulo() {
		return rotulo;
	}
}
