package br.edu.ifpb.gps.farnel.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ifpb.gps.farnel.model.Colaborador;
import br.edu.ifpb.gps.farnel.model.Empresa;
import br.edu.ifpb.gps.farnel.model.PedidoColetivo;
import br.edu.ifpb.gps.farnel.model.StatusPedido;

public interface PedidoColetivoRepository extends JpaRepository<PedidoColetivo, Long> {

	/** Lote do dia: todas as marmitas dos colaboradores da empresa para a data. */
	@Query("SELECT p FROM PedidoColetivo p JOIN FETCH p.colaborador c JOIN FETCH p.prato pr JOIN FETCH pr.restaurante "
			+ "WHERE c.empresa = :empresa AND p.dataEntrega = :data ORDER BY c.nome ASC")
	List<PedidoColetivo> findLote(@Param("empresa") Empresa empresa, @Param("data") LocalDate data);

	boolean existsByColaboradorAndDataEntrega(Colaborador colaborador, LocalDate dataEntrega);

	@Query("SELECT p FROM PedidoColetivo p JOIN FETCH p.prato pr JOIN FETCH pr.restaurante "
			+ "WHERE p.colaborador = :colaborador AND p.dataEntrega = :data")
	Optional<PedidoColetivo> findDoDia(@Param("colaborador") Colaborador colaborador, @Param("data") LocalDate data);

	/** Marmitas geradas por recorrência ainda não entregues, a partir da data. */
	@Query("SELECT p FROM PedidoColetivo p WHERE p.colaborador = :colaborador AND p.recorrente = true "
			+ "AND p.status = :status AND p.dataEntrega >= :data")
	List<PedidoColetivo> findRecorrentesFuturos(@Param("colaborador") Colaborador colaborador,
			@Param("status") StatusPedido status, @Param("data") LocalDate data);

	/** Tira o colaborador dos lotes ainda não entregues a partir da data (pausa ou remoção). */
	@Modifying
	@Query("DELETE FROM PedidoColetivo p WHERE p.colaborador = :colaborador AND p.status = :status AND p.dataEntrega >= :data")
	int deleteFuturos(@Param("colaborador") Colaborador colaborador, @Param("status") StatusPedido status,
			@Param("data") LocalDate data);

}
