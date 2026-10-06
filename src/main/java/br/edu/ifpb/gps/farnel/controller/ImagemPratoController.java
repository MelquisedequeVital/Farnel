package br.edu.ifpb.gps.farnel.controller;

import java.time.Duration;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpb.gps.farnel.service.CardapioService;
import lombok.RequiredArgsConstructor;

/** Serve as fotos de pratos enviadas pelos restaurantes, que ficam guardadas no banco. */
@RestController
@RequiredArgsConstructor
public class ImagemPratoController {

	private final CardapioService cardapioService;

	@GetMapping("/pratos/{id}/imagem")
	public ResponseEntity<byte[]> imagem(@PathVariable Long id) {
		return cardapioService.buscarImagem(id)
				.map(imagem -> ResponseEntity.ok()
						.contentType(MediaType.parseMediaType(imagem.getTipo()))
						// A URL muda (?v=...) sempre que a foto é trocada, então pode ficar em cache.
						.cacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic())
						.header("X-Content-Type-Options", "nosniff")
						.body(imagem.getDados()))
				.orElse(ResponseEntity.notFound().build());
	}

}
