package br.edu.ifpb.gps.farnel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.gps.farnel.model.ContratoEmpresarial;
import br.edu.ifpb.gps.farnel.model.Empresa;

public interface ContratoEmpresarialRepository extends JpaRepository<ContratoEmpresarial, Long> {

	Optional<ContratoEmpresarial> findByEmpresa(Empresa empresa);

}
