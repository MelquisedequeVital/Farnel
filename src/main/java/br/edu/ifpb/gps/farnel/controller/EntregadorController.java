package br.edu.ifpb.gps.farnel.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/entregas")
public class EntregadorController {

	public record RotaEntregaItem(
			String codigo,
			String restauranteOrigem,
			String enderecoDestino,
			String cliente,
			String prato,
			String distanciaKm,
			BigDecimal valorCorrida,
			String horarioPrevisto,
			String status
	) {}

	@GetMapping
	public String painelEntregas(Model model) {
		List<RotaEntregaItem> rotas = List.of(
				new RotaEntregaItem(
						"ROT-101",
						"Cozinha Bella Massa",
						"Av. Epitácio Pessoa, 1000 - Tambauzinho",
						"Ana Souza",
						"Lasanha à Bolonhesa",
						"3.4 km",
						new BigDecimal("15.50"),
						"12:15",
						"Pronto para Coleta"
				),
				new RotaEntregaItem(
						"ROT-102",
						"Sabor do Mar",
						"Rua Almirante Barroso, 450 - Centro",
						"Roberto Lima",
						"Salmão Grelhado com Arroz e Legumes",
						"5.8 km",
						new BigDecimal("22.00"),
						"12:40",
						"Em Rota de Entrega"
				),
				new RotaEntregaItem(
						"ROT-103",
						"Terra & Fogo",
						"Av. Cabo Branco, 2100 - Cabo Branco",
						"Mariana Dias",
						"Curry de Frango com Arroz",
						"4.1 km",
						new BigDecimal("18.00"),
						"13:05",
						"Aguardando Preparo"
				)
		);

		BigDecimal totalGanhosDia = rotas.stream()
				.map(RotaEntregaItem::valorCorrida)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		model.addAttribute("rotas", rotas);
		model.addAttribute("totalGanhosDia", totalGanhosDia);
		model.addAttribute("totalEntregasDia", rotas.size());
		return "entregas/index";
	}

}
