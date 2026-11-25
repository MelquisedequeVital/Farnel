package br.edu.ifpb.gps.farnel.repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ifpb.gps.farnel.model.Colaborador;
import br.edu.ifpb.gps.farnel.model.Empresa;
import br.edu.ifpb.gps.farnel.model.RecorrenciaColaborador;

public interface RecorrenciaColaboradorRepository extends JpaRepository<RecorrenciaColaborador, Long> {

	@Query("SELECT r FROM RecorrenciaColaborador r JOIN FETCH r.prato p JOIN FETCH p.restaurante WHERE r.colaborador = :colaborador")
	List<RecorrenciaColaborador> findDoColaborador(@Param("colaborador") Colaborador colaborador);

	Optional<RecorrenciaColaborador> findByColaboradorAndDiaSemana(Colaborador colaborador, DayOfWeek diaSemana);

	/** Todas as recorrências (com colaborador e prato), usadas na geração automática dos lotes. */
	@Query("SELECT r FROM RecorrenciaColaborador r JOIN FETCH r.colaborador c JOIN FETCH c.empresa JOIN FETCH r.prato")
	List<RecorrenciaColaborador> findTodasComDados();

	@Query("SELECT r FROM RecorrenciaColaborador r JOIN FETCH r.colaborador c WHERE c.empresa = :empresa")
	List<RecorrenciaColaborador> findDaEmpresa(@Param("empresa") Empresa empresa);

}
