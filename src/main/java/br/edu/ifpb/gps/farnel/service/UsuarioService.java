package br.edu.ifpb.gps.farnel.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifpb.gps.farnel.dto.CadastroConsumidorForm;
import br.edu.ifpb.gps.farnel.dto.PerfilConsumidorForm;
import br.edu.ifpb.gps.farnel.model.Consumidor;
import br.edu.ifpb.gps.farnel.model.TipoUsuario;
import br.edu.ifpb.gps.farnel.model.Usuario;
import br.edu.ifpb.gps.farnel.repository.ConsumidorRepository;
import br.edu.ifpb.gps.farnel.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final ConsumidorRepository consumidorRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Usuario cadastrarConsumidor(CadastroConsumidorForm form) {
		if (usuarioRepository.existsByEmail(form.getEmail())) {
			throw new IllegalArgumentException("Já existe uma conta cadastrada com este e-mail.");
		}

		Usuario usuario = new Usuario();
		usuario.setNome(form.getNome());
		usuario.setEmail(form.getEmail());
		usuario.setSenha(passwordEncoder.encode(form.getSenha()));
		usuario.setTipo(TipoUsuario.CONSUMIDOR);
		usuario = usuarioRepository.save(usuario);

		Consumidor consumidor = new Consumidor();
		consumidor.setUsuario(usuario);
		consumidor.setNome(form.getNome());
		consumidor.setEmail(form.getEmail());
		consumidor.setTelefone(form.getTelefone());
		consumidor.setEndereco(form.getEndereco());
		consumidor.setTipoPagamento(form.getTipoPagamento());
		consumidor.setTitularCartao(form.getTitularCartao());
		consumidor.setNumeroCartaoMascarado(formatarNumeroCartao(form.getNumeroCartaoMascarado()));
		consumidor.setValidadeCartao(form.getValidadeCartao());
		consumidorRepository.save(consumidor);

		return usuario;
	}

	@Transactional(readOnly = true)
	public Consumidor obterConsumidorLogado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
		}
		String email = auth.getName();
		return consumidorRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consumidor não localizado para o e-mail: " + email));
	}

	@Transactional
	public void atualizarPerfilConsumidor(PerfilConsumidorForm form) {
		Consumidor consumidor = obterConsumidorLogado();
		consumidor.setNome(form.getNome());
		consumidor.setTelefone(form.getTelefone());
		consumidor.setEndereco(form.getEndereco());
		consumidor.setTipoPagamento(form.getTipoPagamento());
		consumidor.setTitularCartao(form.getTitularCartao());
		if (form.getNumeroCartaoMascarado() != null && !form.getNumeroCartaoMascarado().isBlank()) {
			consumidor.setNumeroCartaoMascarado(formatarNumeroCartao(form.getNumeroCartaoMascarado()));
		}
		consumidor.setValidadeCartao(form.getValidadeCartao());

		// Atualiza também na entidade Usuario associada se houver
		if (consumidor.getUsuario() != null) {
			consumidor.getUsuario().setNome(form.getNome());
			usuarioRepository.save(consumidor.getUsuario());
		}

		consumidorRepository.save(consumidor);
	}

	private String formatarNumeroCartao(String numero) {
		if (numero == null || numero.isBlank()) {
			return "•••• •••• •••• 4242";
		}
		String limpo = numero.replaceAll("\\s+", "");
		if (limpo.length() >= 4) {
			String ultimos4 = limpo.substring(limpo.length() - 4);
			return "•••• •••• •••• " + ultimos4;
		}
		return "•••• •••• •••• " + limpo;
	}

}
