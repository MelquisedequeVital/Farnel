package br.edu.ifpb.gps.farnel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.ifpb.gps.farnel.service.RestauranteService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {

	private final RestauranteService restauranteService;

	@GetMapping
	public String listar(
			@RequestParam(required = false) String categoria,
			@RequestParam(required = false) String busca,
			Model model) {

		model.addAttribute("restaurantes", restauranteService.listarComFiltros(categoria, busca));
		model.addAttribute("categorias", restauranteService.listarCategorias());
		model.addAttribute("contagemPorCategoria", restauranteService.contarPorCategoria());
		model.addAttribute("categoriaSelecionada", categoria);
		model.addAttribute("busca", busca);

		return "restaurantes/index";
	}

}
