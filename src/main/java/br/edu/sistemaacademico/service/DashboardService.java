package br.edu.sistemaacademico.service;

import br.edu.sistemaacademico.dto.DashboardResponse;
import br.edu.sistemaacademico.model.StatusEmprestimo;
import br.edu.sistemaacademico.model.StatusLivro;
import br.edu.sistemaacademico.model.StatusObjeto;
import br.edu.sistemaacademico.repository.EmprestimoRepository;
import br.edu.sistemaacademico.repository.LivroRepository;
import br.edu.sistemaacademico.repository.ObjetoEncontradoRepository;
import org.springframework.stereotype.Service;

/** Calcula os indicadores do painel inicial. */
@Service
public class DashboardService {

    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final ObjetoEncontradoRepository objetoRepository;

    public DashboardService(LivroRepository livroRepository,
                            EmprestimoRepository emprestimoRepository,
                            ObjetoEncontradoRepository objetoRepository) {
        this.livroRepository = livroRepository;
        this.emprestimoRepository = emprestimoRepository;
        this.objetoRepository = objetoRepository;
    }

    public DashboardResponse resumo() {
        long objetosDevolvidos = objetoRepository.countByStatus(StatusObjeto.DEVOLVIDO);
        long objetosTotal = objetoRepository.count();
        return new DashboardResponse(
                livroRepository.count(),
                livroRepository.countByStatus(StatusLivro.DISPONIVEL),
                livroRepository.countByStatus(StatusLivro.EMPRESTADO),
                emprestimoRepository.countByStatus(StatusEmprestimo.ATIVO),
                objetosTotal,
                objetosTotal - objetosDevolvidos,
                objetosDevolvidos
        );
    }
}
