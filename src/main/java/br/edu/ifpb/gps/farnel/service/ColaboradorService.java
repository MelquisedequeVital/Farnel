package br.edu.ifpb.gps.farnel.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.gps.farnel.dto.ColaboradorForm;
import br.edu.ifpb.gps.farnel.model.Colaborador;
import br.edu.ifpb.gps.farnel.model.ContratoEmpresarial;
import br.edu.ifpb.gps.farnel.model.Empresa;
import br.edu.ifpb.gps.farnel.model.StatusColaborador;
import br.edu.ifpb.gps.farnel.model.StatusContrato;
import br.edu.ifpb.gps.farnel.model.StatusPedido;
import br.edu.ifpb.gps.farnel.model.TipoUsuario;
import br.edu.ifpb.gps.farnel.model.Usuario;
import br.edu.ifpb.gps.farnel.repository.ColaboradorRepository;
import br.edu.ifpb.gps.farnel.repository.ContratoEmpresarialRepository;
import br.edu.ifpb.gps.farnel.repository.PedidoColetivoRepository;
import br.edu.ifpb.gps.farnel.repository.RecorrenciaColaboradorRepository;
import br.edu.ifpb.gps.farnel.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

/**
 * RF-B2B-04: inclusão, pausa e remoção de colaboradores vinculados ao benefício,
 * sem alterar o contrato principal.
 */
@Service
@RequiredArgsConstructor
public class ColaboradorService {

	private final ColaboradorRepository colaboradorRepository;
	private final ContratoEmpresarialRepository contratoRepository;
	private final PedidoColetivoRepository pedidoRepository;
	private final UsuarioRepository usuarioRepository;
	private final RecorrenciaColaboradorRepository recorrenciaRepository;
	private final RecorrenciaService recorrenciaService;
	private final PasswordEncoder passwordEncoder;

	/** Senha do primeiro acesso do colaborador ao portal. */
	public static final String SENHA_INICIAL = "123456";

	/** Quadro atual (ativos e pausados). */
	@Transactional(readOnly = true)
	public List<Colaborador> quadro(Empresa empresa) {
		return colaboradorRepository.findByEmpresaAndStatusNotOrderByNomeAsc(empresa, StatusColaborador.REMOVIDO);
	}

	@Transactional(readOnly = true)
	public long ativos(Empresa empresa) {
		return colaboradorRepository.countByEmpresaAndStatus(empresa, StatusColaborador.ATIVO);
	}

	@Transactional
	public Colaborador incluir(Empresa empresa, ColaboradorForm form) {
		ContratoEmpresarial contrato = contratoVigente(empresa);
		String email = form.getEmail().trim().toLowerCase();
		if (colaboradorRepository.existsByEmpresaAndEmailIgnoreCaseAndStatusNot(empresa, email, StatusColaborador.REMOVIDO)) {
			throw new IllegalArgumentException("Já existe um colaborador com este e-mail no quadro.");
		}
		verificarLimite(empresa, contrato);

		criarAcesso(empresa, form.getNome().trim(), email);

		Colaborador colaborador = new Colaborador();
		colaborador.setEmpresa(empresa);
		colaborador.setNome(form.getNome().trim());
		colaborador.setEmail(email);
		colaborador.setSetor(form.getSetor() == null || form.getSetor().isBlank() ? null : form.getSetor().trim());
		return colaboradorRepository.save(colaborador);
	}

	/**
	 * Cria o login do colaborador (perfil COLABORADOR, senha inicial {@link #SENHA_INICIAL}).
	 * Se ele já teve acesso por esta empresa (foi removido e voltou), reaproveita a conta.
	 */
	private void criarAcesso(Empresa empresa, String nome, String email) {
		usuarioRepository.findByEmail(email).ifPresentOrElse(existente -> {
			boolean mesmaEmpresa = existente.getTipo() == TipoUsuario.COLABORADOR
					&& existente.getEmpresa() != null && existente.getEmpresa().getId().equals(empresa.getId());
			if (!mesmaEmpresa) {
				throw new IllegalArgumentException("Este e-mail já é usado por outra conta do Farnel.");
			}
		}, () -> {
			Usuario usuario = new Usuario(nome, email, passwordEncoder.encode(SENHA_INICIAL), TipoUsuario.COLABORADOR);
			usuario.setEmpresa(empresa);
			usuarioRepository.save(usuario);
		});
	}

	/** Colaborador do usuário autenticado, se ainda estiver no quadro (ativo ou pausado). */
	@Transactional(readOnly = true)
	public Optional<Colaborador> colaboradorLogado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			return Optional.empty();
		}
		return colaboradorRepository.findFirstByEmailIgnoreCaseAndStatusNot(auth.getName(), StatusColaborador.REMOVIDO);
	}

	/** Pausa o benefício: o colaborador sai dos lotes ainda não entregues a partir de hoje. */
	@Transactional
	public int pausar(Empresa empresa, Long id) {
		Colaborador colaborador = doQuadro(empresa, id);
		colaborador.setStatus(StatusColaborador.PAUSADO);
		return pedidoRepository.deleteFuturos(colaborador, StatusPedido.AGENDADO, LocalDate.now());
	}

	@Transactional
	public void reativar(Empresa empresa, Long id) {
		Colaborador colaborador = doQuadro(empresa, id);
		if (colaborador.isAtivo()) {
			return;
		}
		verificarLimite(empresa, contratoVigente(empresa));
		colaborador.setStatus(StatusColaborador.ATIVO);
		// as marmitas recorrentes voltam a entrar nos próximos lotes
		recorrenciaService.gerarParaColaborador(colaborador);
	}

	/** Remove do quadro; os pedidos já entregues continuam no histórico. */
	@Transactional
	public int remover(Empresa empresa, Long id) {
		Colaborador colaborador = doQuadro(empresa, id);
		colaborador.setStatus(StatusColaborador.REMOVIDO);
		recorrenciaRepository.deleteAll(recorrenciaRepository.findDoColaborador(colaborador));
		return pedidoRepository.deleteFuturos(colaborador, StatusPedido.AGENDADO, LocalDate.now());
	}

	private ContratoEmpresarial contratoVigente(Empresa empresa) {
		ContratoEmpresarial contrato = contratoRepository.findByEmpresa(empresa)
				.orElseThrow(() -> new IllegalArgumentException("Firme o contrato antes de cadastrar colaboradores."));
		if (contrato.getStatus() == StatusContrato.ENCERRADO) {
			throw new IllegalArgumentException("Contrato encerrado: não é possível alterar o quadro.");
		}
		return contrato;
	}

	private void verificarLimite(Empresa empresa, ContratoEmpresarial contrato) {
		if (ativos(empresa) >= contrato.getLimiteColaboradores()) {
			throw new IllegalArgumentException("Limite de " + contrato.getLimiteColaboradores()
					+ " colaboradores ativos do contrato atingido.");
		}
	}

	private Colaborador doQuadro(Empresa empresa, Long id) {
		Colaborador colaborador = colaboradorRepository.findById(id)
				.filter(c -> c.getEmpresa().getId().equals(empresa.getId()))
				.filter(c -> c.getStatus() != StatusColaborador.REMOVIDO)
				.orElseThrow(() -> new IllegalArgumentException("Colaborador não encontrado no quadro."));
		return colaborador;
	}

}
