package br.edu.ifpb.gps.farnel.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Resumo da compra fictícia exibido na página de confirmação. Nada disso é
 * gravado no banco.
 */
public record PedidoConfirmado(
		String codigo,
		List<Item> itens,
		int totalItens,
		BigDecimal total,
		DiaEntrega diaEntrega,
		String observacoes) implements Serializable {

	public record Item(String nome, int quantidade, BigDecimal subtotal) implements Serializable {
	}

}
