package br.edu.ifpb.gps.farnel.service;

import java.math.BigDecimal;

import br.edu.ifpb.gps.farnel.model.Prato;

public record ItemCarrinho(Prato prato, int quantidade) {

	public BigDecimal getSubtotal() {
		return prato.getPreco().multiply(BigDecimal.valueOf(quantidade));
	}

}
