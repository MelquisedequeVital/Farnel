package br.edu.ifpb.gps.farnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.gps.farnel.model.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

	boolean existsByCnpj(String cnpj);

}
