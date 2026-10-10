package br.edu.ifpb.gps.farnel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.gps.farnel.dto.PratoForm;
import br.edu.ifpb.gps.farnel.model.Alergeno;
import br.edu.ifpb.gps.farnel.model.Prato;
import br.edu.ifpb.gps.farnel.service.CardapioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Painel do parceiro: lista, cadastro e edição dos pratos dos restaurantes. */
@Controller
@RequestMapping("/parceiro")
@RequiredArgsConstructor
public class ParceiroPratoController {

	private final CardapioService cardapioService;

	@GetMapping
	public String inicio() {
		return "redirect:/parceiro/pratos";
	}

	@GetMapping("/pratos")
	public String listar(@RequestParam(required = false) Long restaurante, Model model) {
		model.addAttribute("pratos", cardapioService.listarPratos(restaurante));
		model.addAttribute("restaurantes", cardapioService.listarRestaurantes());
		model.addAttribute("restauranteSelecionado", restaurante);
		return "parceiro/pratos/lista";
	}

	@GetMapping("/pratos/novo")
	public String novo(@RequestParam(required = false) Long restaurante, Model model) {
		PratoForm form = new PratoForm();
		form.setRestauranteId(restaurante);
		return exibirFormulario(form, null, model);
	}

	@PostMapping("/pratos")
	public String cadastrar(@Valid @ModelAttribute("pratoForm") PratoForm form, BindingResult erros,
			@RequestParam(required = false) MultipartFile foto, Model model, RedirectAttributes redirect) {
		validarFoto(foto, erros);
		if (erros.hasErrors()) {
			return exibirFormulario(form, null, model);
		}
		Prato prato;
		try {
			prato = cardapioService.cadastrarPrato(form, foto);
		} catch (IllegalArgumentException e) {
			erros.reject("cadastro", e.getMessage());
			return exibirFormulario(form, null, model);
		}
		redirect.addFlashAttribute("mensagem", "\"" + prato.getNome() + "\" cadastrado no cardápio.");
		return "redirect:/parceiro/pratos?restaurante=" + prato.getRestaurante().getId();
	}

	@GetMapping("/pratos/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		Prato prato = cardapioService.buscarPrato(id);
		return exibirFormulario(cardapioService.formParaEdicao(prato), prato, model);
	}

	@PostMapping("/pratos/{id}")
	public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("pratoForm") PratoForm form,
			BindingResult erros, @RequestParam(required = false) MultipartFile foto, Model model,
			RedirectAttributes redirect) {
		Prato prato = cardapioService.buscarPrato(id);
		validarFoto(foto, erros);
		if (erros.hasErrors()) {
			return exibirFormulario(form, prato, model);
		}
		try {
			cardapioService.atualizarPrato(id, form, foto);
		} catch (IllegalArgumentException e) {
			erros.reject("edicao", e.getMessage());
			return exibirFormulario(form, prato, model);
		}
		redirect.addFlashAttribute("mensagem", "\"" + form.getNome().trim() + "\" atualizado.");
		return "redirect:/parceiro/pratos?restaurante=" + form.getRestauranteId();
	}

	private void validarFoto(MultipartFile foto, BindingResult erros) {
		if (foto == null || foto.isEmpty()) {
			return;
		}
		try {
			cardapioService.validarFoto(foto);
		} catch (IllegalArgumentException e) {
			erros.reject("foto", e.getMessage());
		}
	}

	/** {@code prato} é nulo no cadastro e preenchido na edição. */
	private String exibirFormulario(PratoForm form, Prato prato, Model model) {
		model.addAttribute("pratoForm", form);
		model.addAttribute("prato", prato);
		model.addAttribute("restaurantes", cardapioService.listarRestaurantes());
		model.addAttribute("alergenos", Alergeno.values());
		return "parceiro/pratos/form";
	}

}
