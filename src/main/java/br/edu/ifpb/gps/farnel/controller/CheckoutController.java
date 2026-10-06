package br.edu.ifpb.gps.farnel.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.gps.farnel.service.CheckoutService;
import br.edu.ifpb.gps.farnel.service.ItemCarrinho;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

	private final CheckoutService checkoutService;

	@GetMapping("/checkout")
	public String exibir(Model model) {
		List<ItemCarrinho> itens = checkoutService.listarItens();
		model.addAttribute("itens", itens);
		model.addAttribute("totalPorcoes", itens.stream().mapToInt(ItemCarrinho::quantidade).sum());
		model.addAttribute("total", checkoutService.calcularTotal(itens));
		model.addAttribute("diasEntrega", checkoutService.proximosDiasDeEntrega());
		return "checkout/index";
	}

	/** Adiciona um prato a partir do catálogo e volta para a mesma posição da página de origem. */
	@PostMapping("/carrinho/adicionar")
	public String adicionar(@RequestParam Long pratoId,
			@RequestHeader(value = "Referer", required = false) String referer,
			RedirectAttributes redirect) {
		String nome = checkoutService.adicionar(pratoId);
		redirect.addFlashAttribute("mensagem", "\"" + nome + "\" incluído no seu plano.");
		return "redirect:" + caminhoLocal(referer, "/catalogo") + "#prato-" + pratoId;
	}

	@PostMapping("/carrinho/{pratoId}/incrementar")
	public String incrementar(@PathVariable Long pratoId) {
		checkoutService.adicionar(pratoId);
		return "redirect:/checkout";
	}

	@PostMapping("/carrinho/{pratoId}/decrementar")
	public String decrementar(@PathVariable Long pratoId) {
		checkoutService.decrementar(pratoId);
		return "redirect:/checkout";
	}

	@PostMapping("/carrinho/{pratoId}/remover")
	public String remover(@PathVariable Long pratoId) {
		checkoutService.remover(pratoId);
		return "redirect:/checkout";
	}

	@PostMapping("/checkout/finalizar")
	public String finalizar(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEntrega,
			@RequestParam(required = false) String observacoes,
			RedirectAttributes redirect) {
		try {
			redirect.addFlashAttribute("pedido", checkoutService.finalizar(dataEntrega, observacoes));
			return "redirect:/checkout/confirmacao";
		} catch (IllegalStateException | IllegalArgumentException e) {
			redirect.addFlashAttribute("erro", e.getMessage());
			redirect.addFlashAttribute("observacoes", observacoes);
			return "redirect:/checkout";
		}
	}

	@GetMapping("/checkout/confirmacao")
	public String confirmacao(Model model) {
		// O pedido só existe como flash attribute logo após finalizar; recarregar a página volta ao catálogo.
		if (!model.containsAttribute("pedido")) {
			return "redirect:/catalogo";
		}
		return "checkout/confirmacao";
	}

	/** Usa só o caminho do Referer, para nunca redirecionar para outro site. */
	private static String caminhoLocal(String referer, String padrao) {
		if (referer == null) {
			return padrao;
		}
		try {
			URI uri = URI.create(referer);
			String caminho = uri.getRawPath();
			if (caminho == null || !caminho.startsWith("/") || caminho.startsWith("//")) {
				return padrao;
			}
			return uri.getRawQuery() == null ? caminho : caminho + "?" + uri.getRawQuery();
		} catch (IllegalArgumentException e) {
			return padrao;
		}
	}

}
