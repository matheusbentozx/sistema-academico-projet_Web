package br.edu.sistemaacademico.repository;

import br.edu.sistemaacademico.model.ObjetoEncontrado;
import br.edu.sistemaacademico.model.StatusObjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ObjetoEncontradoRepository extends JpaRepository<ObjetoEncontrado, Long> {

    List<ObjetoEncontrado> findAllByOrderByDataEncontradoDescIdDesc();

    /** Pesquisa o termo no nome, na descrição livre e no local. */
    @Query("""
            SELECT o FROM ObjetoEncontrado o
            WHERE LOWER(o.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(o.descricao) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(o.localEncontrado) LIKE LOWER(CONCAT('%', :termo, '%'))
            ORDER BY o.dataEncontrado DESC, o.id DESC
            """)
    List<ObjetoEncontrado> pesquisar(@Param("termo") String termo);

    long countByStatus(StatusObjeto status);
}
