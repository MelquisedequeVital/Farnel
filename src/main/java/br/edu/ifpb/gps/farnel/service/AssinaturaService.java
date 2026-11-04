package br.edu.ifpb.gps.farnel.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifpb.gps.farnel.model.Assinatura;
import br.edu.ifpb.gps.farnel.model.Consumidor;
import br.edu.ifpb.gps.farnel.model.Prato;
import br.edu.ifpb.gps.farnel.model.StatusAssinatura;
import br.edu.ifpb.gps.farnel.repository.AssinaturaRepository;
import br.edu.ifpb.gps.farnel.repository.ConsumidorRepository;
import br.edu.ifpb.gps.farnel.repository.PratoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssinaturaService {

	private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

	private final AssinaturaRepository assinaturaRepository;
	private final ConsumidorRepository consumidorRepository;
	private final PratoRepository pratoRepository;

	@Transactional
	public Consumidor obterConsumidorAtivo() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
			Optional<Consumidor> logado = consumidorRepository.findByEmail(auth.getName());
			if (logado.isPresent()) {
				return logado.get();
			}
		}
		return consumidorRepository.findFirstByOrderByIdAsc()
				.orElseGet(() -> {
					Consumidor novo = new Consumidor();
					novo.setNome("Ana Souza");
					novo.setEmail("ana.souza@farnel.com.br");
					novo.setEndereco("Av. Epitácio Pessoa, 1000 - Tambauzinho");
					return consumidorRepository.save(novo);
				});
	}

	public List<Assinatura> listarAssinaturasAtivas() {
		Consumidor consumidor = obterConsumidorAtivo();
		return assinaturaRepository.findByConsumidorAndStatusComPratoERestaurante(consumidor, StatusAssinatura.ATIVA);
	}

	public Assinatura buscarPorId(Long id) {
		return assinaturaRepository.findByIdComPratoERestaurante(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marmita não encontrada"));
	}

	@Transactional
	public Assinatura criarAssinatura(Long pratoId, String diaSemana, LocalTime horario) {
		Prato prato = pratoRepository.findById(pratoId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado"));

		Consumidor consumidor = obterConsumidorAtivo();

		Assinatura assinatura = new Assinatura();
		assinatura.setConsumidor(consumidor);
		assinatura.setPrato(prato);
		assinatura.setValor(prato.getPreco());
		assinatura.setStatus(StatusAssinatura.ATIVA);
		assinatura.setDiasSemana(diaSemana != null && !diaSemana.isBlank() ? diaSemana : "Terça-feira");
		assinatura.setHorarioEntrega(horario != null ? horario : LocalTime.of(12, 30));

		return assinaturaRepository.save(assinatura);
	}

	@Transactional
	public Assinatura atualizarAssinatura(Long id, String diaSemana, LocalTime horario) {
		Assinatura assinatura = buscarPorId(id);
		if (diaSemana != null && !diaSemana.isBlank()) {
			assinatura.setDiasSemana(diaSemana.trim());
		}
		if (horario != null) {
			assinatura.setHorarioEntrega(horario);
		}
		return assinaturaRepository.save(assinatura);
	}

	@Transactional
	public void cancelarAssinatura(Long id) {
		Assinatura assinatura = buscarPorId(id);
		assinatura.setStatus(StatusAssinatura.CANCELADA);
		assinaturaRepository.save(assinatura);
	}

	@Transactional
	public void criarAssinaturasDoCarrinho(List<ItemCarrinho> itens, LocalDate dataEntrega) {
		if (itens == null || itens.isEmpty()) return;

		String diaSemana = "Terça-feira";
		if (dataEntrega != null) {
			DayOfWeek dow = dataEntrega.getDayOfWeek();
			String nomeDia = dow.getDisplayName(TextStyle.FULL, PT_BR);
			diaSemana = Character.toUpperCase(nomeDia.charAt(0)) + nomeDia.substring(1);
		}

		Consumidor consumidor = obterConsumidorAtivo();
		for (ItemCarrinho item : itens) {
			for (int i = 0; i < item.quantidade(); i++) {
				Assinatura a = new Assinatura();
				a.setConsumidor(consumidor);
				a.setPrato(item.prato());
				a.setValor(item.prato().getPreco());
				a.setStatus(StatusAssinatura.ATIVA);
				a.setDiasSemana(diaSemana);
				a.setHorarioEntrega(LocalTime.of(12, 30));
				assinaturaRepository.save(a);
			}
		}
	}

	public long contarAtivas() {
		Consumidor consumidor = obterConsumidorAtivo();
		return assinaturaRepository.countByConsumidorAndStatus(consumidor, StatusAssinatura.ATIVA);
	}

}
