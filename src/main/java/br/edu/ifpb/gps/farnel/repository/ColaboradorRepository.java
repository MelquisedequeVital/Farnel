package br.edu.ifpb.gps.farnel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.gps.farnel.model.Colaborador;
import br.edu.ifpb.gps.farnel.model.Empresa;
import br.edu.ifpb.gps.farnel.model.StatusColaborador;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

	List<Colaborador> findByEmpresaAndStatusNotOrderByNomeAsc(Empresa empresa, StatusColaborador status);

	List<Colaborador> findByEmpresaAndStatusOrderByNomeAsc(Empresa empresa, StatusColaborador status);

	Optional<Colaborador> findFirstByEmailIgnoreCaseAndStatusNot(String email, StatusColaborador status);

	long countByEmpresaAndStatus(Empresa empresa, StatusColaborador status);

	boolean existsByEmpresaAndEmailIgnoreCaseAndStatusNot(Empresa empresa, String email, StatusColaborador status);

}
