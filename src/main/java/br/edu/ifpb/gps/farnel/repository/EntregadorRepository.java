package br.edu.ifpb.gps.farnel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.gps.farnel.model.Entregador;
import br.edu.ifpb.gps.farnel.model.Usuario;

public interface EntregadorRepository extends JpaRepository<Entregador, Long> {

	Optional<Entregador> findByUsuario(Usuario usuario);

}
