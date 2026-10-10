package br.edu.ifpb.gps.farnel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.gps.farnel.model.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

	List<Restaurante> findAllByOrderByNomeFantasiaAsc();

}
