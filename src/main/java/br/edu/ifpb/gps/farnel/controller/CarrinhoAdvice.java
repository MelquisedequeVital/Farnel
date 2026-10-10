package br.edu.ifpb.gps.farnel.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.edu.ifpb.gps.farnel.service.Carrinho;
import lombok.RequiredArgsConstructor;

/** Disponibiliza a quantidade de itens do carrinho para o header de todas as páginas. */
@ControllerAdvice
@RequiredArgsConstructor
public class CarrinhoAdvice {

	private final Carrinho carrinho;

	@ModelAttribute("totalItensCarrinho")
	public int totalItensCarrinho() {
		return carrinho.getTotalItens();
	}

}
