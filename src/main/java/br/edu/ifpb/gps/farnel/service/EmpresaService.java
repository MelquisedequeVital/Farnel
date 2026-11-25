package br.edu.ifpb.gps.farnel.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.edu.ifpb.gps.farnel.dto.CadastroEmpresaForm;
import br.edu.ifpb.gps.farnel.dto.ContratoForm;
import br.edu.ifpb.gps.farnel.model.ContratoEmpresarial;
import br.edu.ifpb.gps.farnel.model.Empresa;
import br.edu.ifpb.gps.farnel.model.StatusContrato;
import br.edu.ifpb.gps.farnel.model.TipoUsuario;
import br.edu.ifpb.gps.farnel.model.Usuario;
import br.edu.ifpb.gps.farnel.repository.ContratoEmpresarialRepository;
import br.edu.ifpb.gps.farnel.repository.EmpresaRepository;
import br.edu.ifpb.gps.farnel.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

/** Regras do módulo B2B: credenciamento da empresa e contrato coletivo. */
@Service
@RequiredArgsConstructor
public class EmpresaService {

	private final EmpresaRepository empresaRepository;
	private final ContratoEmpresarialRepository contratoRepository;
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Empresa credenciar(CadastroEmpresaForm form) {
		String cnpj = form.getCnpj().replaceAll("\\D", "");
		if (empresaRepository.existsByCnpj(cnpj)) {
			throw new IllegalArgumentException("Já existe uma empresa credenciada com este CNPJ.");
		}
		if (usuarioRepository.existsByEmail(form.getEmail())) {
			throw new IllegalArgumentException("Já existe uma conta cadastrada com este e-mail.");
		}

		Empresa empresa = new Empresa();
		empresa.setRazaoSocial(form.getRazaoSocial().trim());
		empresa.setCnpj(cnpj);
		empresa.setResponsavel(form.getResponsavel().trim());
		empresa.setTelefone(form.getTelefone());
		empresa.setEnderecoEntrega(form.getEnderecoEntrega().trim());
		empresa = empresaRepository.save(empresa);

		Usuario gestor = new Usuario(form.getResponsavel().trim(), form.getEmail(),
				passwordEncoder.encode(form.getSenha()), TipoUsuario.EMPRESA);
		gestor.setEmpresa(empresa);
		usuarioRepository.save(gestor);
		return empresa;
	}

	/** Empresa do gestor autenticado. */
	@Transactional(readOnly = true)
	public Empresa empresaLogada() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario usuario = usuarioRepository.findByEmail(auth.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		if (usuario.getEmpresa() == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário sem empresa vinculada.");
		}
		return empresaRepository.findById(usuario.getEmpresa().getId()).orElseThrow();
	}

	/** Contrato da empresa, ou {@code null} se ainda não foi firmado. */
	@Transactional(readOnly = true)
	public ContratoEmpresarial contrato(Empresa empresa) {
		return contratoRepository.findByEmpresa(empresa).orElse(null);
	}

	public ContratoForm formDoContrato(ContratoEmpresarial contrato) {
		ContratoForm form = new ContratoForm();
		if (contrato == null) {
			form.setDataInicio(LocalDate.now());
			form.setLimiteColaboradores(20);
			form.setHorarioEntrega(LocalTime.of(12, 0));
			return form;
		}
		form.setDataInicio(contrato.getDataInicio());
		form.setDataFim(contrato.getDataFim());
		form.setLimiteColaboradores(contrato.getLimiteColaboradores());
		form.setHorarioEntrega(contrato.getHorarioEntrega());
		return form;
	}

	@Transactional
	public ContratoEmpresarial salvarContrato(Empresa empresa, ContratoForm form) {
		if (form.getDataFim() != null && form.getDataFim().isBefore(form.getDataInicio())) {
			throw new IllegalArgumentException("A data de término não pode ser anterior à data de início.");
		}
		ContratoEmpresarial contrato = contratoRepository.findByEmpresa(empresa).orElseGet(() -> {
			ContratoEmpresarial novo = new ContratoEmpresarial();
			novo.setEmpresa(empresa);
			return novo;
		});
		if (contrato.getStatus() == StatusContrato.ENCERRADO) {
			throw new IllegalArgumentException("Contrato encerrado não pode ser alterado.");
		}
		contrato.setDataInicio(form.getDataInicio());
		contrato.setDataFim(form.getDataFim());
		contrato.setLimiteColaboradores(form.getLimiteColaboradores());
		contrato.setHorarioEntrega(form.getHorarioEntrega());
		return contratoRepository.save(contrato);
	}

	@Transactional
	public void alterarStatusContrato(Empresa empresa, StatusContrato novoStatus) {
		ContratoEmpresarial contrato = contratoRepository.findByEmpresa(empresa)
				.orElseThrow(() -> new IllegalArgumentException("Nenhum contrato firmado."));
		if (contrato.getStatus() == StatusContrato.ENCERRADO) {
			throw new IllegalArgumentException("Contrato encerrado não pode ser reativado.");
		}
		contrato.setStatus(novoStatus);
		if (novoStatus == StatusContrato.ENCERRADO && contrato.getDataFim() == null) {
			contrato.setDataFim(LocalDate.now());
		}
	}

}
