package br.edu.ifpb.gps.farnel.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.gps.farnel.model.Colaborador;
import br.edu.ifpb.gps.farnel.model.ContratoEmpresarial;
import br.edu.ifpb.gps.farnel.model.Empresa;
import br.edu.ifpb.gps.farnel.model.Prato;
import br.edu.ifpb.gps.farnel.model.RecorrenciaColaborador;
import br.edu.ifpb.gps.farnel.model.StatusContrato;
import br.edu.ifpb.gps.farnel.model.StatusPedido;
import br.edu.ifpb.gps.farnel.repository.ContratoEmpresarialRepository;
import br.edu.ifpb.gps.farnel.repository.PedidoColetivoRepository;
import br.edu.ifpb.gps.farnel.repository.PratoRepository;
import br.edu.ifpb.gps.farnel.repository.RecorrenciaColaboradorRepository;
import lombok.RequiredArgsConstructor;

/**
 * Marmitas recorrentes do colaborador B2B. Cada recorrência (prato + dia da semana) gera
 * automaticamente as marmitas dos próximos {@link #DIAS_GERADOS} dias nos lotes da empresa.
 */
@Service
@RequiredArgsConstructor
public class RecorrenciaService {

	/** Quantos dias à frente (contando hoje) os lotes são preenchidos pelas recorrências. */
	public static final int DIAS_GERADOS = 7;

	/** Dias oferecidos para a recorrência corporativa (dias úteis). */
	public static final List<DayOfWeek> DIAS_UTEIS = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
			DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

	private final RecorrenciaColaboradorRepository recorrenciaRepository;
	private final PedidoColetivoRepository pedidoRepository;
	private final ContratoEmpresarialRepository contratoRepository;
	private final PratoRepository pratoRepository;
	private final PedidoColetivoService pedidoColetivoService;

	/** Opção de dia para o formulário. */
	public record DiaOpcao(DayOfWeek dia, String rotulo, String sigla) {
	}

	public List<DiaOpcao> diasDisponiveis() {
		return DIAS_UTEIS.stream()
				.map(d -> new DiaOpcao(d, RecorrenciaColaborador.rotulo(d), RecorrenciaColaborador.sigla(d)))
				.toList();
	}

	@Transactional(readOnly = true)
	public List<RecorrenciaColaborador> doColaborador(Colaborador colaborador) {
		return recorrenciaRepository.findDoColaborador(colaborador).stream()
				.sorted(Comparator.comparing(RecorrenciaColaborador::getDiaSemana))
				.toList();
	}

	/** Resumo por colaborador para a tela da empresa, ex.: {id -> "Seg, Qua, Sex"}. */
	@Transactional(readOnly = true)
	public Map<Long, String> resumoDaEmpresa(Empresa empresa) {
		return recorrenciaRepository.findDaEmpresa(empresa).stream()
				.sorted(Comparator.comparing(RecorrenciaColaborador::getDiaSemana))
				.collect(Collectors.groupingBy(r -> r.getColaborador().getId(),
						Collectors.mapping(RecorrenciaColaborador::getDiaSigla, Collectors.joining(", "))));
	}

	/**
	 * Define o prato recorrente para os dias marcados (substitui o prato que já existia nesses dias)
	 * e já preenche os próximos lotes.
	 */
	@Transactional
	public void salvar(Colaborador colaborador, Long pratoId, Set<DayOfWeek> dias) {
		if (dias == null || dias.isEmpty()) {
			throw new IllegalArgumentException("Marque pelo menos um dia da semana.");
		}
		if (!colaborador.isAtivo()) {
			throw new IllegalArgumentException("Seu benefício está pausado.");
		}
		ContratoEmpresarial contrato = contratoRepository.findByEmpresa(colaborador.getEmpresa())
				.filter(c -> c.getStatus() == StatusContrato.ATIVO)
				.orElseThrow(() -> new IllegalArgumentException("O contrato da empresa não está ativo."));
		Prato prato = pratoRepository.findById(pratoId)
				.orElseThrow(() -> new IllegalArgumentException("Prato não encontrado."));

		for (DayOfWeek dia : dias) {
			RecorrenciaColaborador r = recorrenciaRepository.findByColaboradorAndDiaSemana(colaborador, dia)
					.orElseGet(RecorrenciaColaborador::new);
			r.setColaborador(colaborador);
			r.setDiaSemana(dia);
			r.setPrato(prato);
			recorrenciaRepository.save(r);
		}
		descartarGeradosFuturos(colaborador, dias);
		gerarParaColaborador(contrato, colaborador);
	}

	/** Encerra a recorrência de um dia e tira dos próximos lotes as marmitas que ela gerou. */
	@Transactional
	public void remover(Colaborador colaborador, DayOfWeek dia) {
		RecorrenciaColaborador r = recorrenciaRepository.findByColaboradorAndDiaSemana(colaborador, dia)
				.orElseThrow(() -> new IllegalArgumentException("Recorrência não encontrada."));
		recorrenciaRepository.delete(r);
		descartarGeradosFuturos(colaborador, Set.of(dia));
	}

	/** Preenche os próximos lotes do colaborador (ex.: após reativar o benefício). */
	@Transactional
	public void gerarParaColaborador(Colaborador colaborador) {
		contratoRepository.findByEmpresa(colaborador.getEmpresa())
				.ifPresent(contrato -> gerarParaColaborador(contrato, colaborador));
	}

	/** Rotina diária: preenche os lotes dos próximos dias com todas as recorrências. */
	@Transactional
	public int gerarTodos() {
		int gerados = 0;
		for (RecorrenciaColaborador r : recorrenciaRepository.findTodasComDados()) {
			ContratoEmpresarial contrato = contratoRepository.findByEmpresa(r.getColaborador().getEmpresa()).orElse(null);
			gerados += gerar(contrato, r);
		}
		return gerados;
	}

	private void gerarParaColaborador(ContratoEmpresarial contrato, Colaborador colaborador) {
		recorrenciaRepository.findDoColaborador(colaborador).forEach(r -> gerar(contrato, r));
	}

	private int gerar(ContratoEmpresarial contrato, RecorrenciaColaborador r) {
		Colaborador colaborador = r.getColaborador();
		if (contrato == null || contrato.getStatus() != StatusContrato.ATIVO || !colaborador.isAtivo()) {
			return 0;
		}
		int gerados = 0;
		LocalDate hoje = LocalDate.now();
		for (int i = 0; i < DIAS_GERADOS; i++) {
			LocalDate data = hoje.plusDays(i);
			boolean vigente = !data.isBefore(contrato.getDataInicio())
					&& (contrato.getDataFim() == null || !data.isAfter(contrato.getDataFim()));
			if (data.getDayOfWeek() == r.getDiaSemana() && vigente
					&& pedidoColetivoService.gerarRecorrente(contrato, colaborador, r.getPrato(), data)) {
				gerados++;
			}
		}
		return gerados;
	}

	private void descartarGeradosFuturos(Colaborador colaborador, Set<DayOfWeek> dias) {
		pedidoRepository.findRecorrentesFuturos(colaborador, StatusPedido.AGENDADO, LocalDate.now()).stream()
				.filter(p -> dias.contains(p.getDataEntrega().getDayOfWeek()))
				.forEach(pedidoRepository::delete);
		pedidoRepository.flush();
	}

}
