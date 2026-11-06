package br.edu.sistemaacademico.repository;

import br.edu.sistemaacademico.model.Emprestimo;
import br.edu.sistemaacademico.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    List<Emprestimo> findAllByOrderByIdDesc();

    List<Emprestimo> findByStatusOrderByIdDesc(StatusEmprestimo status);

    /** Usado para impedir a exclusão de livros que têm histórico. */
    boolean existsByLivroId(Long livroId);

    long countByStatus(StatusEmprestimo status);
}
