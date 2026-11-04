package br.edu.ifpb.gps.farnel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.gps.farnel.dto.PerfilConsumidorForm;
import br.edu.ifpb.gps.farnel.model.Consumidor;
import br.edu.ifpb.gps.farnel.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

	private final UsuarioService usuarioService;

	@GetMapping
	public String exibirPerfil(Model model) {
		Consumidor consumidor = usuarioService.obterConsumidorLogado();
		if (!model.containsAttribute("form")) {
			PerfilConsumidorForm form = new PerfilConsumidorForm();
			form.setNome(consumidor.getNome());
			form.setEmail(consumidor.getEmail());
			form.setTelefone(consumidor.getTelefone());
			form.setEndereco(consumidor.getEndereco());
			form.setTipoPagamento(consumidor.getTipoPagamento());
			form.setTitularCartao(consumidor.getTitularCartao());
			form.setNumeroCartaoMascarado(consumidor.getNumeroCartaoMascarado());
			form.setValidadeCartao(consumidor.getValidadeCartao());
			model.addAttribute("form", form);
		}
		model.addAttribute("consumidor", consumidor);
		return "perfil/index";
	}

	@PostMapping
	public String salvarPerfil(
			@Valid @ModelAttribute("form") PerfilConsumidorForm form,
			BindingResult bindingResult,
			RedirectAttributes redirect,
			Model model) {
		if (bindingResult.hasErrors()) {
			Consumidor consumidor = usuarioService.obterConsumidorLogado();
			model.addAttribute("consumidor", consumidor);
			return "perfil/index";
		}

		usuarioService.atualizarPerfilConsumidor(form);
		redirect.addFlashAttribute("mensagem", "Dados do perfil atualizados com sucesso!");
		return "redirect:/perfil";
	}

}
