package br.edu.ifpb.gps.farnel.service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifpb.gps.farnel.model.Prato;
import br.edu.ifpb.gps.farnel.repository.PratoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckoutService {

	private static final int DIAS_DE_ENTREGA_OFERECIDOS = 5;

	private final Carrinho carrinho;
	private final PratoRepository pratoRepository;

	/** Adiciona o prato ao carrinho e devolve o nome dele para a mensagem de feedback. */
	public String adicionar(Long pratoId) {
		Prato prato = pratoRepository.findById(pratoId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado"));
		carrinho.adicionar(prato.getId());
		return prato.getNome();
	}

	public void decrementar(Long pratoId) {
		carrinho.decrementar(pratoId);
	}

	public void remover(Long pratoId) {
		carrinho.remover(pratoId);
	}

	/** Itens do carrinho, na ordem em que foram adicionados. Pratos que não existem mais no banco são ignorados. */
	public List<ItemCarrinho> listarItens() {
		Map<Long, Integer> itens = carrinho.getItens();
		Map<Long, Prato> pratos = pratoRepository.findAllById(itens.keySet()).stream()
				.collect(Collectors.toMap(Prato::getId, Function.identity()));
		return itens.entrySet().stream()
				.filter(e -> pratos.containsKey(e.getKey()))
				.map(e -> new ItemCarrinho(pratos.get(e.getKey()), e.getValue()))
				.toList();
	}

	public BigDecimal calcularTotal(List<ItemCarrinho> itens) {
		return itens.stream().map(ItemCarrinho::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/** Próximos dias úteis a partir de amanhã. */
	public List<DiaEntrega> proximosDiasDeEntrega() {
		List<DiaEntrega> dias = new ArrayList<>();
		LocalDate data = LocalDate.now();
		while (dias.size() < DIAS_DE_ENTREGA_OFERECIDOS) {
			data = data.plusDays(1);
			if (data.getDayOfWeek() != DayOfWeek.SATURDAY && data.getDayOfWeek() != DayOfWeek.SUNDAY) {
				dias.add(new DiaEntrega(data));
			}
		}
		return dias;
	}

	/**
	 * Finaliza a compra fictícia: não há integração com pagamento nem gravação no
	 * banco. Apenas monta o resumo e esvazia o carrinho.
	 */
	public PedidoConfirmado finalizar(LocalDate dataEntrega, String observacoes) {
		List<ItemCarrinho> itens = listarItens();
		if (itens.isEmpty()) {
			throw new IllegalStateException("Seu carrinho está vazio.");
		}
		DiaEntrega diaEntrega = proximosDiasDeEntrega().stream()
				.filter(d -> d.data().equals(dataEntrega))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Escolha um dia de entrega válido."));

		PedidoConfirmado pedido = new PedidoConfirmado(
				"FRN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(),
				itens.stream()
						.map(i -> new PedidoConfirmado.Item(i.prato().getNome(), i.quantidade(), i.getSubtotal()))
						.toList(),
				itens.stream().mapToInt(ItemCarrinho::quantidade).sum(),
				calcularTotal(itens),
				diaEntrega,
				observacoes == null || observacoes.isBlank() ? null : observacoes.trim());

		carrinho.limpar();
		return pedido;
	}

}
