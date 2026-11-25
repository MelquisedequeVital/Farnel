package br.edu.ifpb.gps.farnel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.gps.farnel.dto.ContratoForm;
import br.edu.ifpb.gps.farnel.dto.SubvencaoForm;
import br.edu.ifpb.gps.farnel.model.ContratoEmpresarial;
import br.edu.ifpb.gps.farnel.model.ContratoEmpresarial.DivisaoRefeicao;
import br.edu.ifpb.gps.farnel.model.Empresa;
import br.edu.ifpb.gps.farnel.model.Prato;
import br.edu.ifpb.gps.farnel.model.StatusContrato;
import br.edu.ifpb.gps.farnel.service.CardapioService;
import br.edu.ifpb.gps.farnel.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Painel da empresa contratante (módulo B2B). */
@Controller
@RequestMapping("/empresa")
@RequiredArgsConstructor
public class EmpresaController {

	private final EmpresaService empresaService;
	private final CardapioService cardapioService;

	@GetMapping
	public String inicio() {
		return "redirect:/empresa/contrato";
	}

	// ---------- RF-B2B-01: Gestão de Contratos Empresariais ----------

	@GetMapping("/contrato")
	public String contrato(Model model) {
		Empresa empresa = empresaService.empresaLogada();
		return exibirContrato(empresa, empresaService.formDoContrato(empresaService.contrato(empresa)), model);
	}

	@PostMapping("/contrato")
	public String salvarContrato(@Valid @ModelAttribute("contratoForm") ContratoForm form, BindingResult erros,
			Model model, RedirectAttributes redirect) {
		Empresa empresa = empresaService.empresaLogada();
		if (erros.hasErrors()) {
			return exibirContrato(empresa, form, model);
		}
		try {
			empresaService.salvarContrato(empresa, form);
		} catch (IllegalArgumentException e) {
			erros.reject("contrato", e.getMessage());
			return exibirContrato(empresa, form, model);
		}
		redirect.addFlashAttribute("mensagem", "Contrato salvo com sucesso.");
		return "redirect:/empresa/contrato";
	}

	@PostMapping("/contrato/status")
	public String alterarStatus(@RequestParam StatusContrato status, RedirectAttributes redirect) {
		try {
			empresaService.alterarStatusContrato(empresaService.empresaLogada(), status);
			redirect.addFlashAttribute("mensagem", "Contrato agora está " + status.getRotulo().toLowerCase() + ".");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("mensagem", e.getMessage());
		}
		return "redirect:/empresa/contrato";
	}

	// ---------- RF-B2B-02: Subvenção de Refeições ----------

	/** Linha da simulação: um prato do catálogo e como seu valor é dividido. */
	public record SimulacaoPrato(Prato prato, DivisaoRefeicao divisao) {
	}

	@GetMapping("/subvencao")
	public String subvencao(Model model) {
		Empresa empresa = empresaService.empresaLogada();
		ContratoEmpresarial contrato = empresaService.contrato(empresa);
		SubvencaoForm form = contrato == null ? new SubvencaoForm() : empresaService.formDaSubvencao(contrato);
		return exibirSubvencao(empresa, contrato, form, model);
	}

	@PostMapping("/subvencao")
	public String salvarSubvencao(@Valid @ModelAttribute("subvencaoForm") SubvencaoForm form, BindingResult erros,
			Model model, RedirectAttributes redirect) {
		Empresa empresa = empresaService.empresaLogada();
		ContratoEmpresarial contrato = empresaService.contrato(empresa);
		if (erros.hasErrors()) {
			return exibirSubvencao(empresa, contrato, form, model);
		}
		try {
			empresaService.salvarSubvencao(empresa, form);
		} catch (IllegalArgumentException e) {
			erros.reject("subvencao", e.getMessage());
			return exibirSubvencao(empresa, contrato, form, model);
		}
		redirect.addFlashAttribute("mensagem", "Regra de subvenção atualizada.");
		return "redirect:/empresa/subvencao";
	}

	private String exibirSubvencao(Empresa empresa, ContratoEmpresarial contrato, SubvencaoForm form, Model model) {
		model.addAttribute("empresa", empresa);
		model.addAttribute("contrato", contrato);
		model.addAttribute("subvencaoForm", form);
		if (contrato != null) {
			model.addAttribute("simulacao", cardapioService.listarPratos(null).stream()
					.map(p -> new SimulacaoPrato(p, contrato.dividir(p.getPreco())))
					.toList());
		}
		return "empresa/subvencao";
	}

	private String exibirContrato(Empresa empresa, ContratoForm form, Model model) {
		model.addAttribute("empresa", empresa);
		model.addAttribute("contrato", empresaService.contrato(empresa));
		model.addAttribute("contratoForm", form);
		return "empresa/contrato";
	}

}
