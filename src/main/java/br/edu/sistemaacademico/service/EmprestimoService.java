package br.edu.sistemaacademico.service;

import br.edu.sistemaacademico.dto.EmprestimoRequest;
import br.edu.sistemaacademico.exception.RecursoNaoEncontradoException;
import br.edu.sistemaacademico.exception.RegraNegocioException;
import br.edu.sistemaacademico.model.Emprestimo;
import br.edu.sistemaacademico.model.Livro;
import br.edu.sistemaacademico.model.StatusEmprestimo;
import br.edu.sistemaacademico.model.StatusLivro;
import br.edu.sistemaacademico.model.Usuario;
import br.edu.sistemaacademico.repository.EmprestimoRepository;
import br.edu.sistemaacademico.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Regras de negócio de empréstimos e devoluções.
 * Os métodos são @Transactional: o empréstimo e a mudança de status do livro
 * são gravados juntos — ou tudo é salvo, ou nada é (evita inconsistência).
 */
@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final LivroService livroService;
    private final UsuarioService usuarioService;

    public EmprestimoService(EmprestimoRepository emprestimoRepository,
                             LivroRepository livroRepository,
                             LivroService livroService,
                             UsuarioService usuarioService) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.livroService = livroService;
        this.usuarioService = usuarioService;
    }

    public List<Emprestimo> listar(StatusEmprestimo status) {
        if (status != null) {
            return emprestimoRepository.findByStatusOrderByIdDesc(status);
        }
        return emprestimoRepository.findAllByOrderByIdDesc();
    }

    public Emprestimo buscarPorId(Long id) {
        return emprestimoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empréstimo " + id + " não encontrado."));
    }

    @Transactional
    public Emprestimo registrar(EmprestimoRequest request) {
        Livro livro = livroService.buscarPorId(request.livroId());
        Usuario usuario = usuarioService.buscarPorId(request.usuarioId());

        // Regra: não é permitido emprestar um livro que já está emprestado
        if (!livro.isDisponivel()) {
            throw new RegraNegocioException("O livro \"" + livro.getTitulo() + "\" já está emprestado.");
        }

        LocalDate data = request.dataEmprestimo() != null ? request.dataEmprestimo() : LocalDate.now();
        if (data.isAfter(LocalDate.now())) {
            throw new RegraNegocioException("A data do empréstimo não pode estar no futuro.");
        }

        // Regra: ao emprestar, o livro passa para EMPRESTADO
        livro.setStatus(StatusLivro.EMPRESTADO);
        livroRepository.save(livro);

        return emprestimoRepository.save(new Emprestimo(usuario, livro, data));
    }

    @Transactional
    public Emprestimo registrarDevolucao(Long id) {
        Emprestimo emprestimo = buscarPorId(id);
        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new RegraNegocioException("Este empréstimo já foi devolvido.");
        }

        // Regra: empréstimo -> DEVOLVIDO e livro -> DISPONÍVEL
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        emprestimo.setDataDevolucao(LocalDate.now());

        Livro livro = emprestimo.getLivro();
        livro.setStatus(StatusLivro.DISPONIVEL);
        livroRepository.save(livro);

        return emprestimoRepository.save(emprestimo);
    }
}
