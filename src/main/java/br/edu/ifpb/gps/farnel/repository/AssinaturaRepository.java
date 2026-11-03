package br.edu.ifpb.gps.farnel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ifpb.gps.farnel.model.Assinatura;
import br.edu.ifpb.gps.farnel.model.Consumidor;
import br.edu.ifpb.gps.farnel.model.StatusAssinatura;

public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {

	@Query("SELECT a FROM Assinatura a JOIN FETCH a.prato p JOIN FETCH p.restaurante WHERE a.consumidor = :consumidor AND a.status = :status ORDER BY a.id ASC")
	List<Assinatura> findByConsumidorAndStatusComPratoERestaurante(
			@Param("consumidor") Consumidor consumidor,
			@Param("status") StatusAssinatura status);

	@Query("SELECT a FROM Assinatura a JOIN FETCH a.prato p JOIN FETCH p.restaurante WHERE a.id = :id")
	Optional<Assinatura> findByIdComPratoERestaurante(@Param("id") Long id);

	long countByConsumidorAndStatus(Consumidor consumidor, StatusAssinatura status);

}
