package br.edu.ifpb.gps.farnel.controller;

import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.gps.farnel.model.Assinatura;
import br.edu.ifpb.gps.farnel.service.AssinaturaService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MarmitaController {

	private final AssinaturaService assinaturaService;

	@GetMapping("/minhas-marmitas")
	public String listar(Model model) {
		List<Assinatura> marmitas = assinaturaService.listarAssinaturasAtivas();
		model.addAttribute("marmitas", marmitas);
		model.addAttribute("totalMarmitas", marmitas.size());
		return "marmitas/index";
	}

	@GetMapping("/minhas-marmitas/{id}")
	public String detalhes(@PathVariable Long id, Model model) {
		Assinatura assinatura = assinaturaService.buscarPorId(id);
		model.addAttribute("assinatura", assinatura);
		return "marmitas/detalhes";
	}

	@PostMapping("/minhas-marmitas/{id}/salvar")
	public String salvar(
			@PathVariable Long id,
			@RequestParam String diaSemana,
			@RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime horarioEntrega,
			RedirectAttributes redirect) {
		assinaturaService.atualizarAssinatura(id, diaSemana, horarioEntrega);
		redirect.addFlashAttribute("mensagem", "Plano da marmita atualizado com sucesso!");
		return "redirect:/minhas-marmitas";
	}

	@PostMapping("/minhas-marmitas/{id}/cancelar")
	public String cancelar(@PathVariable Long id, RedirectAttributes redirect) {
		assinaturaService.cancelarAssinatura(id);
		redirect.addFlashAttribute("mensagem", "Marmita cancelada com sucesso.");
		return "redirect:/minhas-marmitas";
	}

	@PostMapping("/minhas-marmitas/criar")
	public String criar(
			@RequestParam Long pratoId,
			@RequestParam(required = false) String diaSemana,
			@RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime horarioEntrega,
			RedirectAttributes redirect) {
		Assinatura nova = assinaturaService.criarAssinatura(pratoId, diaSemana, horarioEntrega);
		redirect.addFlashAttribute("mensagem", "\"" + nova.getPrato().getNome() + "\" adicionado às suas marmitas!");
		return "redirect:/minhas-marmitas/" + nova.getId();
	}

}
