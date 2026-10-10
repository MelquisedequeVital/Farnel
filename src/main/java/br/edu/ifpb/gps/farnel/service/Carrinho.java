package br.edu.ifpb.gps.farnel.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Carrinho do usuário guardado na sessão HTTP. Armazena apenas o id do prato e
 * a quantidade; os dados do prato são sempre lidos do banco.
 */
@Component
@SessionScope
public class Carrinho implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<Long, Integer> itens = new LinkedHashMap<>();

	public void adicionar(Long pratoId) {
		itens.merge(pratoId, 1, Integer::sum);
	}

	public void decrementar(Long pratoId) {
		itens.computeIfPresent(pratoId, (id, quantidade) -> quantidade > 1 ? quantidade - 1 : null);
	}

	public void remover(Long pratoId) {
		itens.remove(pratoId);
	}

	public void limpar() {
		itens.clear();
	}

	public Map<Long, Integer> getItens() {
		return Collections.unmodifiableMap(itens);
	}

	public int getTotalItens() {
		return itens.values().stream().mapToInt(Integer::intValue).sum();
	}

}
