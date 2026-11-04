package br.edu.ifpb.gps.farnel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.gps.farnel.dto.CadastroConsumidorForm;
import br.edu.ifpb.gps.farnel.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AutenticacaoController {

	private final UsuarioService usuarioService;

	@GetMapping("/login")
	public String login(
			@RequestParam(required = false) String error,
			@RequestParam(required = false) String logout,
			@RequestParam(required = false) String cadastrado,
			Model model) {
		if (error != null) {
			model.addAttribute("erroLogin", "E-mail ou senha incorretos.");
		}
		if (logout != null) {
			model.addAttribute("mensagemLogout", "Você saiu da sua conta com segurança.");
		}
		if (cadastrado != null) {
			model.addAttribute("mensagemSucesso", "Conta criada com sucesso! Faça login para continuar.");
		}
		return "auth/login";
	}

	@GetMapping("/cadastro")
	public String exibirCadastro(Model model) {
		if (!model.containsAttribute("form")) {
			model.addAttribute("form", new CadastroConsumidorForm());
		}
		return "auth/cadastro";
	}

	@PostMapping("/cadastro")
	public String cadastrar(
			@Valid @ModelAttribute("form") CadastroConsumidorForm form,
			BindingResult bindingResult,
			RedirectAttributes redirect,
			Model model) {
		if (bindingResult.hasErrors()) {
			return "auth/cadastro";
		}

		try {
			usuarioService.cadastrarConsumidor(form);
			redirect.addFlashAttribute("mensagem", "Conta criada com sucesso! Bem-vindo(a) ao Farnel.");
			return "redirect:/login?cadastrado=true";
		} catch (IllegalArgumentException e) {
			bindingResult.rejectValue("email", "email.duplicado", e.getMessage());
			return "auth/cadastro";
		}
	}

}
