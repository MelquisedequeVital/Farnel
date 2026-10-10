package br.edu.ifpb.gps.farnel.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifpb.gps.farnel.dto.PratoForm;
import br.edu.ifpb.gps.farnel.model.Alergeno;
import br.edu.ifpb.gps.farnel.model.ImagemPrato;
import br.edu.ifpb.gps.farnel.model.Prato;
import br.edu.ifpb.gps.farnel.model.Restaurante;
import br.edu.ifpb.gps.farnel.repository.ImagemPratoRepository;
import br.edu.ifpb.gps.farnel.repository.PratoRepository;
import br.edu.ifpb.gps.farnel.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardapioService {

	private final PratoRepository pratoRepository;
	private final RestauranteRepository restauranteRepository;
	private final ImagemPratoRepository imagemPratoRepository;

	/**
	 * Lista os pratos do catálogo, opcionalmente excluindo os que contêm o
	 * alérgeno do filtro e restringindo aos que casam com o texto de busca
	 * (nome ou descrição). A comparação ignora acentos e maiúsculas.
	 */
	public List<Prato> buscarPorFiltro(FiltroCardapio filtro, String busca) {
		String termoBusca = Textos.normalizar(busca);
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

	// ---- Painel do parceiro ----

	public List<Restaurante> listarRestaurantes() {
		return restauranteRepository.findAllByOrderByNomeFantasiaAsc();
	}

	/** Pratos de um restaurante, ou de todos quando {@code restauranteId} é nulo. */
	public List<Prato> listarPratos(Long restauranteId) {
		return pratoRepository.findAllByOrderByNomeAsc().stream()
				.filter(p -> restauranteId == null || p.getRestaurante().getId().equals(restauranteId))
				.toList();
	}

	public Prato buscarPrato(Long id) {
		return pratoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado"));
	}

	public Optional<ImagemPrato> buscarImagem(Long pratoId) {
		return imagemPratoRepository.findById(pratoId);
	}

	/** Preenche o formulário de edição; alérgenos fora da lista de checkboxes vão para "outros". */
	public PratoForm formParaEdicao(Prato prato) {
		PratoForm form = new PratoForm();
		form.setRestauranteId(prato.getRestaurante().getId());
		form.setNome(prato.getNome());
		form.setDescricao(prato.getDescricao());
		form.setPreco(prato.getPreco());
		form.setDestaque(prato.isDestaque());

		Set<Alergeno> marcados = EnumSet.noneOf(Alergeno.class);
		List<String> outros = new ArrayList<>();
		for (String termo : separarAlergenos(prato.getAlergenos())) {
			String normalizado = Textos.normalizar(termo);
			List<Alergeno> reconhecidos = Arrays.stream(Alergeno.values())
					.filter(a -> a.getSinonimos().stream().anyMatch(normalizado::contains))
					.toList();
			if (reconhecidos.isEmpty()) {
				outros.add(termo);
			} else {
				marcados.addAll(reconhecidos);
			}
		}
		form.setAlergenos(marcados);
		form.setOutrosAlergenos(outros.isEmpty() ? null : String.join(", ", outros));
		return form;
	}

	@Transactional
	public Prato cadastrarPrato(PratoForm form, MultipartFile foto) {
		Prato prato = new Prato();
		preencher(prato, form);
		prato = pratoRepository.save(prato);
		salvarFoto(prato, foto);
		return prato;
	}

	@Transactional
	public Prato atualizarPrato(Long id, PratoForm form, MultipartFile foto) {
		Prato prato = buscarPrato(id);
		preencher(prato, form);
		if (temArquivo(foto)) {
			salvarFoto(prato, foto);
		} else if (form.isRemoverFoto()) {
			imagemPratoRepository.findById(prato.getId()).ifPresent(imagemPratoRepository::delete);
			prato.setImagem(null);
		}
		return prato;
	}

	/**
	 * Confere se o arquivo é uma imagem JPG, PNG ou WEBP pelos primeiros bytes,
	 * sem confiar no tipo informado pelo navegador.
	 *
	 * @return o tipo MIME detectado
	 * @throws IllegalArgumentException se não for um desses formatos
	 */
	public String validarFoto(MultipartFile foto) {
		byte[] dados = lerBytes(foto);
		return tipoDaImagem(dados)
				.orElseThrow(() -> new IllegalArgumentException("A foto deve ser uma imagem JPG, PNG ou WEBP."));
	}

	private void preencher(Prato prato, PratoForm form) {
		Restaurante restaurante = restauranteRepository.findById(form.getRestauranteId())
				.orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado."));
		prato.setRestaurante(restaurante);
		prato.setNome(form.getNome().trim());
		prato.setDescricao(form.getDescricao() == null || form.getDescricao().isBlank() ? null : form.getDescricao().trim());
		prato.setPreco(form.getPreco());
		prato.setDestaque(form.isDestaque());
		prato.setAlergenos(montarTextoAlergenos(form));
	}

	/** Junta os checkboxes (na ordem do enum) e os "outros" num texto, ex.: "Glúten, Ovos, Gergelim". */
	private static String montarTextoAlergenos(PratoForm form) {
		List<String> partes = new ArrayList<>();
		Arrays.stream(Alergeno.values())
				.filter(form.getAlergenos()::contains)
				.map(Alergeno::getRotulo)
				.forEach(partes::add);
		partes.addAll(separarAlergenos(form.getOutrosAlergenos()));
		return partes.isEmpty() ? null : String.join(", ", partes);
	}

	private static List<String> separarAlergenos(String texto) {
		if (texto == null || texto.isBlank()) {
			return List.of();
		}
		return Arrays.stream(texto.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.toList());
	}

	private void salvarFoto(Prato prato, MultipartFile foto) {
		if (!temArquivo(foto)) {
			return;
		}
		byte[] dados = lerBytes(foto);
		String tipo = tipoDaImagem(dados)
				.orElseThrow(() -> new IllegalArgumentException("A foto deve ser uma imagem JPG, PNG ou WEBP."));

		ImagemPrato imagem = imagemPratoRepository.findById(prato.getId()).orElseGet(ImagemPrato::new);
		imagem.setPrato(prato);
		imagem.setDados(dados);
		imagem.setTipo(tipo);
		imagemPratoRepository.save(imagem);
		// O parâmetro "v" muda a cada envio para o navegador não mostrar a foto antiga do cache.
		prato.setImagem("/pratos/" + prato.getId() + "/imagem?v=" + System.currentTimeMillis());
	}

	private static boolean temArquivo(MultipartFile foto) {
		return foto != null && !foto.isEmpty();
	}

	private static byte[] lerBytes(MultipartFile foto) {
		try {
			return foto.getBytes();
		} catch (IOException e) {
			throw new UncheckedIOException("Não foi possível ler a foto enviada", e);
		}
	}

	private static Optional<String> tipoDaImagem(byte[] d) {
		if (d.length >= 3 && (d[0] & 0xFF) == 0xFF && (d[1] & 0xFF) == 0xD8 && (d[2] & 0xFF) == 0xFF) {
			return Optional.of("image/jpeg");
		}
		if (d.length >= 8 && (d[0] & 0xFF) == 0x89 && d[1] == 'P' && d[2] == 'N' && d[3] == 'G'
				&& d[4] == 0x0D && d[5] == 0x0A && d[6] == 0x1A && d[7] == 0x0A) {
			return Optional.of("image/png");
		}
		if (d.length >= 12 && d[0] == 'R' && d[1] == 'I' && d[2] == 'F' && d[3] == 'F'
				&& d[8] == 'W' && d[9] == 'E' && d[10] == 'B' && d[11] == 'P') {
			return Optional.of("image/webp");
		}
		return Optional.empty();
	}

	private boolean livreDe(Prato prato, FiltroCardapio filtro) {
		String alergenos = Textos.normalizar(prato.getAlergenos());
		return filtro.getTermos().stream().noneMatch(alergenos::contains);
	}

	private boolean casaComBusca(Prato prato, String termo) {
		return Textos.normalizar(prato.getNome()).contains(termo)
				|| Textos.normalizar(prato.getDescricao()).contains(termo);
	}

}
