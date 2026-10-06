package br.edu.ifpb.gps.farnel.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Uploads acima de spring.servlet.multipart.max-file-size são rejeitados antes
 * de chegar ao controller; aqui voltamos para o formulário com uma mensagem.
 */
@ControllerAdvice
public class UploadAdvice {

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String arquivoGrandeDemais(HttpServletRequest request, RedirectAttributes redirect) {
		redirect.addFlashAttribute("erro", "A foto deve ter no máximo 5 MB.");
		String uri = request.getRequestURI();
		// POST /parceiro/pratos -> formulário de novo prato; POST /parceiro/pratos/{id} -> edição
		if (uri.matches(".*/parceiro/pratos/\\d+")) {
			return "redirect:" + uri.substring(uri.indexOf("/parceiro")) + "/editar";
		}
		return "redirect:/parceiro/pratos/novo";
	}

}
