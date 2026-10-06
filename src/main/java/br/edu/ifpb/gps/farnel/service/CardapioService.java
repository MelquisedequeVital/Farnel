package br.edu.ifpb.gps.farnel.service;

import java.text.Normalizer;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.gps.farnel.model.Prato;
import br.edu.ifpb.gps.farnel.repository.PratoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardapioService {

	private final PratoRepository pratoRepository;

	/**
	 * Lista os pratos do catálogo, opcionalmente excluindo os que contêm o
	 * alérgeno do filtro e restringindo aos que casam com o texto de busca
	 * (nome ou descrição). A comparação ignora acentos e maiúsculas.
	 */
	public List<Prato> buscarPorFiltro(FiltroCardapio filtro, String busca) {
		String termoBusca = normalizar(busca);
		return pratoRepository.findAllByOrderByNomeAsc().stream()
				.filter(p -> filtro == null || livreDe(p, filtro))
				.filter(p -> termoBusca.isEmpty() || casaComBusca(p, termoBusca))
				.toList();
	}

	public List<Prato> listarDestaques() {
		return pratoRepository.findByDestaqueTrueOrderByNomeAsc();
	}

	public Map<FiltroCardapio, Long> contarPorFiltro() {
		List<Prato> pratos = pratoRepository.findAll();
		Map<FiltroCardapio, Long> contagem = new EnumMap<>(FiltroCardapio.class);
		for (FiltroCardapio filtro : FiltroCardapio.values()) {
			contagem.put(filtro, pratos.stream().filter(p -> livreDe(p, filtro)).count());
		}
		return contagem;
	}

	private boolean livreDe(Prato prato, FiltroCardapio filtro) {
		String alergenos = normalizar(prato.getAlergenos());
		return filtro.getTermos().stream().noneMatch(alergenos::contains);
	}

	private boolean casaComBusca(Prato prato, String termo) {
		return normalizar(prato.getNome()).contains(termo)
				|| normalizar(prato.getDescricao()).contains(termo);
	}

	private static String normalizar(String texto) {
		if (texto == null) {
			return "";
		}
		return Normalizer.normalize(texto, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")
				.toLowerCase(Locale.ROOT)
				.trim();
	}

}
