package br.edu.ifpb.gps.farnel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.ifpb.gps.farnel.repository.RestauranteRepository;
import br.edu.ifpb.gps.farnel.service.CardapioService;
import br.edu.ifpb.gps.farnel.service.FiltroCardapio;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CardapioController {

	private final CardapioService cardapioService;
	private final RestauranteRepository restauranteRepository;

	@GetMapping("/")
	public String inicio() {
		return "redirect:/catalogo";
	}

	@GetMapping("/catalogo")
	public String buscar(@RequestParam(required = false) String filtro,
			@RequestParam(required = false) String busca,
			@RequestParam(required = false) Long restaurante,
			Model model) {
		FiltroCardapio filtroSelecionado = FiltroCardapio.doSlug(filtro).orElse(null);

		model.addAttribute("pratos", cardapioService.buscarPorFiltro(filtroSelecionado, busca, restaurante));
		model.addAttribute("destaques", cardapioService.listarDestaques());
		model.addAttribute("contagemPorFiltro", cardapioService.contarPorFiltro());
		model.addAttribute("filtros", FiltroCardapio.values());
		model.addAttribute("filtroSelecionado", filtroSelecionado);
		model.addAttribute("busca", busca);
		model.addAttribute("restauranteId", restaurante);

		if (restaurante != null) {
			restauranteRepository.findById(restaurante).ifPresent(r -> model.addAttribute("restauranteFiltro", r));
		}

		return "catalogo/index";
	}

}
