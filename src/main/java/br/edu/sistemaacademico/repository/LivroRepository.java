package br.edu.sistemaacademico.repository;

import br.edu.sistemaacademico.model.Livro;
import br.edu.sistemaacademico.model.StatusLivro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * O Spring Data gera o SQL a partir do nome do método.
 * Ex.: findByTituloContainingIgnoreCase... -> WHERE lower(titulo) LIKE %?% ...
 */
public interface LivroRepository extends JpaRepository<Livro, Long> {

    List<Livro> findAllByOrderByTituloAsc();

    List<Livro> findByStatusOrderByTituloAsc(StatusLivro status);

    List<Livro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrderByTituloAsc(String titulo, String autor);

    long countByStatus(StatusLivro status);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);
}
