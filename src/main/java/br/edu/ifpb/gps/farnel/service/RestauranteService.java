package br.edu.ifpb.gps.farnel.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifpb.gps.farnel.model.Restaurante;
import br.edu.ifpb.gps.farnel.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestauranteService {

	private final RestauranteRepository restauranteRepository;

	public List<Restaurante> listarComFiltros(String categoria, String busca) {
		String buscaNormalizada = Textos.normalizar(busca);

		List<Restaurante> lista = (categoria != null && !categoria.isBlank())
				? restauranteRepository.findByCategoriaIgnoreCaseOrderByNomeFantasiaAsc(categoria.trim())
				: restauranteRepository.findAllByOrderByNomeFantasiaAsc();

		if (buscaNormalizada.isEmpty()) {
			return lista;
		}

		return lista.stream()
				.filter(r -> Textos.normalizar(r.getNomeFantasia()).contains(buscaNormalizada)
						|| (r.getDescricao() != null && Textos.normalizar(r.getDescricao()).contains(buscaNormalizada))
						|| (r.getCategoria() != null && Textos.normalizar(r.getCategoria()).contains(buscaNormalizada)))
				.toList();
	}

	public List<String> listarCategorias() {
		return restauranteRepository.findDistinctCategorias();
	}

	public Map<String, Long> contarPorCategoria() {
		List<Restaurante> todos = restauranteRepository.findAll();
		return todos.stream()
				.filter(r -> r.getCategoria() != null && !r.getCategoria().isBlank())
				.collect(Collectors.groupingBy(Restaurante::getCategoria, Collectors.counting()));
	}

	public Restaurante buscarPorId(Long id) {
		return restauranteRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não encontrado"));
	}

}
