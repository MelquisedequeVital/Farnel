package br.edu.ifpb.gps.farnel.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.gps.farnel.dto.ColaboradorForm;
import br.edu.ifpb.gps.farnel.dto.ContratoForm;
import br.edu.ifpb.gps.farnel.dto.SubvencaoForm;
import br.edu.ifpb.gps.farnel.model.ContratoEmpresarial;
import br.edu.ifpb.gps.farnel.model.Alergeno;
import br.edu.ifpb.gps.farnel.model.Empresa;
import br.edu.ifpb.gps.farnel.model.StatusContrato;
import br.edu.ifpb.gps.farnel.service.OpcaoPrato;
import br.edu.ifpb.gps.farnel.service.CardapioService;
import br.edu.ifpb.gps.farnel.service.ColaboradorService;
import br.edu.ifpb.gps.farnel.service.EmpresaService;
import br.edu.ifpb.gps.farnel.service.PedidoColetivoService;
import br.edu.ifpb.gps.farnel.service.RecorrenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Painel da empresa contratante (módulo B2B). */
@Controller
@RequestMapping("/empresa")
@RequiredArgsConstructor
public class EmpresaController {

	private final EmpresaService empresaService;
	private final CardapioService cardapioService;
	private final PedidoColetivoService pedidoColetivoService;
	private final ColaboradorService colaboradorService;
	private final RecorrenciaService recorrenciaService;

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
					.map(p -> new OpcaoPrato(p, contrato.dividir(p.getPreco())))
					.toList());
		}
		return "empresa/subvencao";
	}

	// ---------- RF-B2B-03: Painel de Pedidos Coletivos ----------

	@GetMapping("/pedidos")
	public String pedidos(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
			Model model) {
		Empresa empresa = empresaService.empresaLogada();
		LocalDate dia = data != null ? data : LocalDate.now();
		model.addAttribute("empresa", empresa);
		model.addAttribute("contrato", empresaService.contrato(empresa));
		model.addAttribute("data", dia);
		model.addAttribute("hoje", LocalDate.now());
		model.addAttribute("lote", pedidoColetivoService.lote(empresa, dia));
		return "empresa/pedidos";
	}

	/** Escolha da marmita no cardápio, com filtros de alérgenos, busca e restaurante. */
	@GetMapping("/pedidos/adicionar")
	public String escolherPrato(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
			@RequestParam(required = false) Long colaboradorId,
			@RequestParam(required = false) Set<Alergeno> sem,
			@RequestParam(required = false) String busca,
			@RequestParam(required = false) Long restaurante,
			Model model) {
		Empresa empresa = empresaService.empresaLogada();
		ContratoEmpresarial contrato = empresaService.contrato(empresa);
		model.addAttribute("empresa", empresa);
		model.addAttribute("contrato", contrato);
		model.addAttribute("data", data);
		model.addAttribute("colaboradores", pedidoColetivoService.colaboradoresAtivos(empresa));
		model.addAttribute("colaboradorId", colaboradorId);
		adicionarFiltros(model, sem, busca, restaurante);
		model.addAttribute("opcoes", contrato == null ? List.of()
				: pedidoColetivoService.opcoes(contrato, cardapioService.filtrarParaEscolha(sem, busca, restaurante)));
		return "empresa/escolher-prato";
	}

	private void adicionarFiltros(Model model, Set<Alergeno> sem, String busca, Long restaurante) {
		model.addAttribute("alergenos", Alergeno.values());
		model.addAttribute("restaurantes", cardapioService.listarRestaurantes());
		model.addAttribute("sem", sem == null ? Set.of() : sem);
		model.addAttribute("busca", busca);
		model.addAttribute("restauranteId", restaurante);
	}

	@PostMapping("/pedidos")
	public String adicionarPedido(@RequestParam Long colaboradorId, @RequestParam Long pratoId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data, RedirectAttributes redirect) {
		try {
			pedidoColetivoService.adicionar(empresaService.empresaLogada(), colaboradorId, pratoId, data);
			redirect.addFlashAttribute("mensagem", "Marmita adicionada ao lote.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("erro", e.getMessage());
		}
		return "redirect:/empresa/pedidos?data=" + data;
	}

	@PostMapping("/pedidos/{id}/remover")
	public String removerPedido(@PathVariable Long id,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data, RedirectAttributes redirect) {
		try {
			pedidoColetivoService.remover(empresaService.empresaLogada(), id);
			redirect.addFlashAttribute("mensagem", "Marmita removida do lote.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("erro", e.getMessage());
		}
		return "redirect:/empresa/pedidos?data=" + data;
	}

	@PostMapping("/pedidos/confirmar")
	public String confirmarRecebimento(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
			RedirectAttributes redirect) {
		try {
			int total = pedidoColetivoService.confirmarRecebimento(empresaService.empresaLogada(), data);
			redirect.addFlashAttribute("mensagem", "Recebimento confirmado: " + total + " marmitas entregues.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("erro", e.getMessage());
		}
		return "redirect:/empresa/pedidos?data=" + data;
	}

	// ---------- RF-B2B-04: Gestão do Quadro de Colaboradores ----------

	@GetMapping("/colaboradores")
	public String colaboradores(Model model) {
		return exibirColaboradores(empresaService.empresaLogada(), new ColaboradorForm(), model);
	}

	@PostMapping("/colaboradores")
	public String incluirColaborador(@Valid @ModelAttribute("colaboradorForm") ColaboradorForm form,
			BindingResult erros, Model model, RedirectAttributes redirect) {
		Empresa empresa = empresaService.empresaLogada();
		if (erros.hasErrors()) {
			return exibirColaboradores(empresa, form, model);
		}
		try {
			colaboradorService.incluir(empresa, form);
		} catch (IllegalArgumentException e) {
			erros.reject("colaborador", e.getMessage());
			return exibirColaboradores(empresa, form, model);
		}
		redirect.addFlashAttribute("mensagem", form.getNome().trim() + " incluído(a). Acesso: "
				+ form.getEmail().trim().toLowerCase() + " / senha inicial " + ColaboradorService.SENHA_INICIAL);
		return "redirect:/empresa/colaboradores";
	}

	@PostMapping("/colaboradores/{id}/pausar")
	public String pausarColaborador(@PathVariable Long id, RedirectAttributes redirect) {
		try {
			int retirados = colaboradorService.pausar(empresaService.empresaLogada(), id);
			redirect.addFlashAttribute("mensagem", "Benefício pausado." + (retirados > 0 ? " " + retirados + " marmita(s) retirada(s) dos próximos lotes." : ""));
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("erro", e.getMessage());
		}
		return "redirect:/empresa/colaboradores";
	}

	@PostMapping("/colaboradores/{id}/reativar")
	public String reativarColaborador(@PathVariable Long id, RedirectAttributes redirect) {
		try {
			colaboradorService.reativar(empresaService.empresaLogada(), id);
			redirect.addFlashAttribute("mensagem", "Benefício reativado.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("erro", e.getMessage());
		}
		return "redirect:/empresa/colaboradores";
	}

	@PostMapping("/colaboradores/{id}/remover")
	public String removerColaborador(@PathVariable Long id, RedirectAttributes redirect) {
		try {
			colaboradorService.remover(empresaService.empresaLogada(), id);
			redirect.addFlashAttribute("mensagem", "Colaborador removido do quadro.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("erro", e.getMessage());
		}
		return "redirect:/empresa/colaboradores";
	}

	private String exibirColaboradores(Empresa empresa, ColaboradorForm form, Model model) {
		model.addAttribute("empresa", empresa);
		model.addAttribute("contrato", empresaService.contrato(empresa));
		model.addAttribute("colaboradores", colaboradorService.quadro(empresa));
		model.addAttribute("totalAtivos", colaboradorService.ativos(empresa));
		model.addAttribute("recorrencias", recorrenciaService.resumoDaEmpresa(empresa));
		model.addAttribute("colaboradorForm", form);
		return "empresa/colaboradores";
	}

	private String exibirContrato(Empresa empresa, ContratoForm form, Model model) {
		model.addAttribute("empresa", empresa);
		model.addAttribute("contrato", empresaService.contrato(empresa));
		model.addAttribute("contratoForm", form);
		return "empresa/contrato";
	}

}
