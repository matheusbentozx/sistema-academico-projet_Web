package br.edu.sistemaacademico.service;

import br.edu.sistemaacademico.exception.RecursoNaoEncontradoException;
import br.edu.sistemaacademico.exception.RegraNegocioException;
import br.edu.sistemaacademico.model.Livro;
import br.edu.sistemaacademico.model.StatusLivro;
import br.edu.sistemaacademico.repository.EmprestimoRepository;
import br.edu.sistemaacademico.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Regras de negócio do cadastro de livros. */
@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final EmprestimoRepository emprestimoRepository;

    public LivroService(LivroRepository livroRepository, EmprestimoRepository emprestimoRepository) {
        this.livroRepository = livroRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    /** Lista livros, opcionalmente filtrando por texto (título/autor) e status. */
    public List<Livro> listar(String busca, StatusLivro status) {
        List<Livro> livros;
        if (busca != null && !busca.isBlank()) {
            String termo = busca.trim();
            livros = livroRepository
                    .findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrderByTituloAsc(termo, termo);
            if (status != null) {
                livros = livros.stream().filter(l -> l.getStatus() == status).toList();
            }
        } else if (status != null) {
            livros = livroRepository.findByStatusOrderByTituloAsc(status);
        } else {
            livros = livroRepository.findAllByOrderByTituloAsc();
        }
        return livros;
    }

    public Livro buscarPorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro " + id + " não encontrado."));
    }

    /** Regra: todo livro novo começa como DISPONÍVEL e o ISBN não pode repetir. */
    @Transactional
    public Livro criar(Livro livro) {
        livro.setId(null);
        livro.setIsbn(livro.getIsbn().trim());
        if (livroRepository.existsByIsbn(livro.getIsbn())) {
            throw new RegraNegocioException("Já existe um livro cadastrado com o ISBN " + livro.getIsbn() + ".");
        }
        livro.setStatus(StatusLivro.DISPONIVEL);
        return livroRepository.save(livro);
    }

    /**
     * Atualiza apenas os dados cadastrais. O status NÃO é editável aqui:
     * ele só muda por meio de empréstimos e devoluções.
     */
    @Transactional
    public Livro atualizar(Long id, Livro dados) {
        Livro livro = buscarPorId(id);
        String isbn = dados.getIsbn().trim();
        if (livroRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new RegraNegocioException("Já existe outro livro com o ISBN " + isbn + ".");
        }
        livro.setTitulo(dados.getTitulo());
        livro.setAutor(dados.getAutor());
        livro.setIsbn(isbn);
        livro.setCategoria(dados.getCategoria());
        return livroRepository.save(livro);
    }

    /** Regra: livros com empréstimos registrados não podem ser excluídos (preserva o histórico). */
    @Transactional
    public void excluir(Long id) {
        Livro livro = buscarPorId(id);
        if (emprestimoRepository.existsByLivroId(id)) {
            throw new RegraNegocioException(
                    "O livro \"" + livro.getTitulo() + "\" possui empréstimos registrados e não pode ser excluído, "
                            + "para preservar o histórico.");
        }
        livroRepository.delete(livro);
    }
}
