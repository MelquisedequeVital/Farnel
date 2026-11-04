package br.edu.ifpb.gps.farnel.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.edu.ifpb.gps.farnel.model.Usuario;
import br.edu.ifpb.gps.farnel.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class UsuarioAdvice {

	private final UsuarioRepository usuarioRepository;

	@ModelAttribute("usuarioLogado")
	public Usuario usuarioLogado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
			return usuarioRepository.findByEmail(auth.getName()).orElse(null);
		}
		return null;
	}

	@ModelAttribute("estaAutenticado")
	public boolean estaAutenticado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
	}

}
