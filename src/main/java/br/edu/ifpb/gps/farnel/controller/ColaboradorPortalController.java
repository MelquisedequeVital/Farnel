package br.edu.ifpb.gps.farnel.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.gps.farnel.model.Alergeno;
import br.edu.ifpb.gps.farnel.model.Colaborador;
import br.edu.ifpb.gps.farnel.model.ContratoEmpresarial;
import br.edu.ifpb.gps.farnel.service.CardapioService;
import br.edu.ifpb.gps.farnel.service.ColaboradorService;
import br.edu.ifpb.gps.farnel.service.EmpresaService;
import br.edu.ifpb.gps.farnel.service.PedidoColetivoService;
import br.edu.ifpb.gps.farnel.service.RecorrenciaService;
import lombok.RequiredArgsConstructor;

/** Portal do colaborador B2B: escolhe a própria marmita do lote da empresa. */
@Controller
@RequestMapping("/colaborador")
@RequiredArgsConstructor
public class ColaboradorPortalController {

	private final ColaboradorService colaboradorService;
	private final EmpresaService empresaService;
	private final PedidoColetivoService pedidoColetivoService;
	private final CardapioService cardapioService;
	private final RecorrenciaService recorrenciaService;

	@GetMapping
	public String inicio(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
			@RequestParam(required = false) Set<Alergeno> sem,
			@RequestParam(required = false) String busca,
			@RequestParam(required = false) Long restaurante,
			Model model) {
		LocalDate hoje = LocalDate.now();
		LocalDate dia = data == null || data.isBefore(hoje) ? hoje : data;
		model.addAttribute("data", dia);
		model.addAttribute("hoje", hoje);

		Optional<Colaborador> logado = colaboradorService.colaboradorLogado();
		if (logado.isEmpty()) {
			model.addAttribute("semAcesso", true);
			return "colaborador/index";
		}
		Colaborador colaborador = logado.get();
		ContratoEmpresarial contrato = empresaService.contrato(colaborador.getEmpresa());

		model.addAttribute("colaborador", colaborador);
		model.addAttribute("empresa", empresaService.empresaLogada());
		model.addAttribute("contrato", contrato);
		model.addAttribute("pedido", pedidoColetivoService.pedidoDoDia(colaborador, dia));
		model.addAttribute("recorrencias", recorrenciaService.doColaborador(colaborador));
		model.addAttribute("diasSemana", recorrenciaService.diasDisponiveis());
		model.addAttribute("diaDaData", dia.getDayOfWeek());
		model.addAttribute("alergenos", Alergeno.values());
		model.addAttribute("restaurantes", cardapioService.listarRestaurantes());
		model.addAttribute("sem", sem == null ? Set.of() : sem);
		model.addAttribute("busca", busca);
		model.addAttribute("restauranteId", restaurante);
		model.addAttribute("opcoes", contrato == null ? List.of()
				: pedidoColetivoService.opcoes(contrato, cardapioService.filtrarParaEscolha(sem, busca, restaurante)));
		return "colaborador/index";
	}

	@PostMapping("/escolher")
	public String escolher(@RequestParam Long pratoId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data, RedirectAttributes redirect) {
		try {
			Colaborador colaborador = colaboradorService.colaboradorLogado()
					.orElseThrow(() -> new IllegalArgumentException("Seu acesso ao benefício foi encerrado."));
			pedidoColetivoService.escolher(colaborador, pratoId, data);
			redirect.addFlashAttribute("mensagem", "Marmita escolhida! Ela chega junto com o lote da empresa.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("erro", e.getMessage());
		}
		return "redirect:/colaborador?data=" + data;
	}

	// ---------- Marmitas recorrentes ----------

	@PostMapping("/recorrencia")
	public String salvarRecorrencia(@RequestParam Long pratoId, @RequestParam(required = false) Set<DayOfWeek> dias,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data, RedirectAttributes redirect) {
		try {
			Colaborador colaborador = colaboradorService.colaboradorLogado()
					.orElseThrow(() -> new IllegalArgumentException("Seu acesso ao benefício foi encerrado."));
			recorrenciaService.salvar(colaborador, pratoId, dias);
			redirect.addFlashAttribute("mensagem", "Marmita recorrente salva! Os próximos lotes já foram preenchidos.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("erro", e.getMessage());
		}
		return "redirect:/colaborador?data=" + data;
	}

	@PostMapping("/recorrencia/remover")
	public String removerRecorrencia(@RequestParam DayOfWeek dia,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data, RedirectAttributes redirect) {
		try {
			Colaborador colaborador = colaboradorService.colaboradorLogado()
					.orElseThrow(() -> new IllegalArgumentException("Seu acesso ao benefício foi encerrado."));
			recorrenciaService.remover(colaborador, dia);
			redirect.addFlashAttribute("mensagem", "Recorrência encerrada.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("erro", e.getMessage());
		}
		return "redirect:/colaborador?data=" + data;
	}

	@PostMapping("/cancelar")
	public String cancelar(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
			RedirectAttributes redirect) {
		try {
			Colaborador colaborador = colaboradorService.colaboradorLogado()
					.orElseThrow(() -> new IllegalArgumentException("Seu acesso ao benefício foi encerrado."));
			pedidoColetivoService.cancelar(colaborador, data);
			redirect.addFlashAttribute("mensagem", "Marmita cancelada.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("erro", e.getMessage());
		}
		return "redirect:/colaborador?data=" + data;
	}

}
