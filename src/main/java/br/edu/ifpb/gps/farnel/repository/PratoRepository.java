package br.edu.ifpb.gps.farnel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.gps.farnel.model.Prato;

public interface PratoRepository extends JpaRepository<Prato, Long> {

	List<Prato> findAllByOrderByNomeAsc();

	List<Prato> findByDestaqueTrueOrderByNomeAsc();

}
