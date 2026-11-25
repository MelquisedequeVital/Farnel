package br.edu.ifpb.gps.farnel.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.gps.farnel.model.Colaborador;
import br.edu.ifpb.gps.farnel.model.ContratoEmpresarial;
import br.edu.ifpb.gps.farnel.model.ContratoEmpresarial.DivisaoRefeicao;
import br.edu.ifpb.gps.farnel.model.Empresa;
import br.edu.ifpb.gps.farnel.model.PedidoColetivo;
import br.edu.ifpb.gps.farnel.model.Prato;
import br.edu.ifpb.gps.farnel.model.StatusColaborador;
import br.edu.ifpb.gps.farnel.model.StatusContrato;
import br.edu.ifpb.gps.farnel.model.StatusPedido;
import br.edu.ifpb.gps.farnel.repository.ColaboradorRepository;
import br.edu.ifpb.gps.farnel.repository.ContratoEmpresarialRepository;
import br.edu.ifpb.gps.farnel.repository.PedidoColetivoRepository;
import br.edu.ifpb.gps.farnel.repository.PratoRepository;
import lombok.RequiredArgsConstructor;

/**
 * RF-B2B-03: lote consolidado de marmitas dos colaboradores entregue no endereço da empresa.
 * Marmitas canceladas ficam gravadas como CANCELADO (fora do lote) para que a geração
 * automática das recorrências não as recrie.
 */
@Service
@RequiredArgsConstructor
public class PedidoColetivoService {

	private final PedidoColetivoRepository pedidoRepository;
	private final ColaboradorRepository colaboradorRepository;
	private final ContratoEmpresarialRepository contratoRepository;
	private final PratoRepository pratoRepository;

	/** Quantidade de marmitas de um mesmo prato dentro do lote. */
	public record ItemLote(String prato, String restaurante, long quantidade) {
	}

	/** Resumo consolidado do lote de uma data. */
	public record Lote(List<PedidoColetivo> pedidos, List<ItemLote> itens, BigDecimal totalEmpresa,
			BigDecimal totalColaboradores, BigDecimal totalGeral, boolean entregue) {

		public int getTotalMarmitas() {
			return pedidos.size();
		}
	}

	@Transactional(readOnly = true)
	public Lote lote(Empresa empresa, LocalDate data) {
		List<PedidoColetivo> pedidos = pedidosValidos(empresa, data);

		Map<String, ItemLote> porPrato = new LinkedHashMap<>();
		for (PedidoColetivo p : pedidos) {
			porPrato.merge(p.getPrato().getNome(),
					new ItemLote(p.getPrato().getNome(), p.getPrato().getRestaurante().getNomeFantasia(), 1),
					(a, b) -> new ItemLote(a.prato(), a.restaurante(), a.quantidade() + 1));
		}

		BigDecimal empresaTotal = somar(pedidos.stream().map(PedidoColetivo::getValorEmpresa).toList());
		BigDecimal colaboradoresTotal = somar(pedidos.stream().map(PedidoColetivo::getValorColaborador).toList());
		boolean entregue = !pedidos.isEmpty() && pedidos.stream().allMatch(p -> p.getStatus() == StatusPedido.ENTREGUE);

		return new Lote(pedidos, List.copyOf(porPrato.values()), empresaTotal, colaboradoresTotal,
				empresaTotal.add(colaboradoresTotal), entregue);
	}

	/** Colaboradores que podem receber marmita (apenas os ativos). */
	@Transactional(readOnly = true)
	public List<Colaborador> colaboradoresAtivos(Empresa empresa) {
		return colaboradorRepository.findByEmpresaAndStatusOrderByNomeAsc(empresa, StatusColaborador.ATIVO);
	}

	/** Pratos com a divisão do valor calculada pela subvenção do contrato. */
	public List<OpcaoPrato> opcoes(ContratoEmpresarial contrato, List<Prato> pratos) {
		return pratos.stream().map(p -> new OpcaoPrato(p, contrato.dividir(p.getPreco()))).toList();
	}

	/** Inclusão manual de uma marmita no lote (pela empresa ou pelo próprio colaborador). */
	@Transactional
	public PedidoColetivo adicionar(Empresa empresa, Long colaboradorId, Long pratoId, LocalDate data) {
		ContratoEmpresarial contrato = contratoRepository.findByEmpresa(empresa)
				.orElseThrow(() -> new IllegalArgumentException("Firme o contrato antes de registrar pedidos."));
		if (contrato.getStatus() != StatusContrato.ATIVO) {
			throw new IllegalArgumentException("O contrato precisa estar ativo para registrar pedidos.");
		}
		if (data.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Não é possível registrar pedidos em datas passadas.");
		}
		Colaborador colaborador = colaboradorDaEmpresa(empresa, colaboradorId);
		if (!colaborador.isAtivo()) {
			throw new IllegalArgumentException(colaborador.getNome() + " está pausado e não pode usar o benefício.");
		}
		Optional<PedidoColetivo> existente = pedidoRepository.findDoDia(colaborador, data);
		if (existente.isPresent()) {
			if (existente.get().getStatus() != StatusPedido.CANCELADO) {
				throw new IllegalArgumentException(colaborador.getNome() + " já possui marmita neste lote.");
			}
			pedidoRepository.delete(existente.get());
			pedidoRepository.flush();
		}
		Prato prato = pratoRepository.findById(pratoId)
				.orElseThrow(() -> new IllegalArgumentException("Prato não encontrado."));
		return pedidoRepository.save(novoPedido(contrato, colaborador, prato, data, false));
	}

	/**
	 * Gera a marmita de uma recorrência, se o dia ainda não tiver pedido (nem mesmo cancelado).
	 * Retorna {@code true} quando um pedido foi criado.
	 */
	@Transactional
	public boolean gerarRecorrente(ContratoEmpresarial contrato, Colaborador colaborador, Prato prato, LocalDate data) {
		if (pedidoRepository.existsByColaboradorAndDataEntrega(colaborador, data)) {
			return false;
		}
		pedidoRepository.save(novoPedido(contrato, colaborador, prato, data, true));
		return true;
	}

	// ---------- Escolha feita pelo próprio colaborador ----------

	/** Marmita do dia ainda válida (ignora as canceladas). */
	@Transactional(readOnly = true)
	public PedidoColetivo pedidoDoDia(Colaborador colaborador, LocalDate data) {
		return pedidoRepository.findDoDia(colaborador, data)
				.filter(p -> p.getStatus() != StatusPedido.CANCELADO)
				.orElse(null);
	}

	/** O colaborador escolhe (ou troca) a própria marmita do dia. */
	@Transactional
	public PedidoColetivo escolher(Colaborador colaborador, Long pratoId, LocalDate data) {
		pedidoRepository.findDoDia(colaborador, data).ifPresent(atual -> {
			if (atual.getStatus() == StatusPedido.ENTREGUE) {
				throw new IllegalArgumentException("A marmita deste dia já foi entregue e não pode ser trocada.");
			}
			pedidoRepository.delete(atual);
			pedidoRepository.flush();
		});
		return adicionar(colaborador.getEmpresa(), colaborador.getId(), pratoId, data);
	}

	@Transactional
	public void cancelar(Colaborador colaborador, LocalDate data) {
		PedidoColetivo pedido = pedidoRepository.findDoDia(colaborador, data)
				.filter(p -> p.getStatus() != StatusPedido.CANCELADO)
				.orElseThrow(() -> new IllegalArgumentException("Você não tem marmita neste dia."));
		if (pedido.getStatus() != StatusPedido.AGENDADO) {
			throw new IllegalArgumentException("A marmita deste dia já foi entregue.");
		}
		pedido.setStatus(StatusPedido.CANCELADO);
	}

	@Transactional
	public void remover(Empresa empresa, Long pedidoId) {
		PedidoColetivo pedido = pedidoRepository.findById(pedidoId)
				.orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado."));
		if (!pedido.getColaborador().getEmpresa().getId().equals(empresa.getId())) {
			throw new IllegalArgumentException("Pedido não pertence à sua empresa.");
		}
		if (pedido.getStatus() != StatusPedido.AGENDADO) {
			throw new IllegalArgumentException("Só é possível remover marmitas agendadas.");
		}
		pedido.setStatus(StatusPedido.CANCELADO);
	}

	/** Confirma o recebimento coletivo: todas as marmitas do lote passam a "entregue". */
	@Transactional
	public int confirmarRecebimento(Empresa empresa, LocalDate data) {
		if (data.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("Só é possível confirmar o recebimento no dia da entrega.");
		}
		List<PedidoColetivo> pedidos = pedidosValidos(empresa, data);
		if (pedidos.isEmpty()) {
			throw new IllegalArgumentException("Não há marmitas neste lote.");
		}
		pedidos.forEach(p -> p.setStatus(StatusPedido.ENTREGUE));
		return pedidos.size();
	}

	private List<PedidoColetivo> pedidosValidos(Empresa empresa, LocalDate data) {
		return pedidoRepository.findLote(empresa, data).stream()
				.filter(p -> p.getStatus() != StatusPedido.CANCELADO)
				.toList();
	}

	private static PedidoColetivo novoPedido(ContratoEmpresarial contrato, Colaborador colaborador, Prato prato,
			LocalDate data, boolean recorrente) {
		DivisaoRefeicao divisao = contrato.dividir(prato.getPreco());
		PedidoColetivo pedido = new PedidoColetivo();
		pedido.setColaborador(colaborador);
		pedido.setPrato(prato);
		pedido.setDataEntrega(data);
		pedido.setValorTotal(divisao.total());
		pedido.setValorEmpresa(divisao.empresa());
		pedido.setValorColaborador(divisao.colaborador());
		pedido.setRecorrente(recorrente);
		return pedido;
	}

	private Colaborador colaboradorDaEmpresa(Empresa empresa, Long colaboradorId) {
		Colaborador colaborador = colaboradorRepository.findById(colaboradorId)
				.orElseThrow(() -> new IllegalArgumentException("Colaborador não encontrado."));
		if (!colaborador.getEmpresa().getId().equals(empresa.getId())) {
			throw new IllegalArgumentException("Colaborador não pertence à sua empresa.");
		}
		return colaborador;
	}

	private static BigDecimal somar(List<BigDecimal> valores) {
		return valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	}

}
