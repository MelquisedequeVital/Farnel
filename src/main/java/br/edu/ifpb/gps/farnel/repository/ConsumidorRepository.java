package br.edu.ifpb.gps.farnel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.gps.farnel.model.Consumidor;

public interface ConsumidorRepository extends JpaRepository<Consumidor, Long> {

	Optional<Consumidor> findFirstByOrderByIdAsc();

	Optional<Consumidor> findByEmail(String email);

}
