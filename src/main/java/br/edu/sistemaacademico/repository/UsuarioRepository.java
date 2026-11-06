package br.edu.sistemaacademico.repository;

import br.edu.sistemaacademico.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findAllByOrderByNomeAsc();

    boolean existsByMatricula(String matricula);
}
