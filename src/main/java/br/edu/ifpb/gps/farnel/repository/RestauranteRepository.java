package br.edu.ifpb.gps.farnel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.edu.ifpb.gps.farnel.model.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

	List<Restaurante> findAllByOrderByNomeFantasiaAsc();

	List<Restaurante> findByCategoriaIgnoreCaseOrderByNomeFantasiaAsc(String categoria);

	@Query("SELECT DISTINCT r.categoria FROM Restaurante r WHERE r.categoria IS NOT NULL AND TRIM(r.categoria) <> '' ORDER BY r.categoria ASC")
	List<String> findDistinctCategorias();

}
