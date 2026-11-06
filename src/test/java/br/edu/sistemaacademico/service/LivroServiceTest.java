package br.edu.sistemaacademico.service;

import br.edu.sistemaacademico.exception.RegraNegocioException;
import br.edu.sistemaacademico.model.Livro;
import br.edu.sistemaacademico.model.StatusLivro;
import br.edu.sistemaacademico.repository.EmprestimoRepository;
import br.edu.sistemaacademico.repository.LivroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários das regras de negócio de livros.
 * Os repositórios são "falsos" (mocks do Mockito): não precisa de banco.
 */
@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @InjectMocks
    private LivroService livroService;

    @Test
    @DisplayName("RN01 - Livro novo deve entrar como DISPONIVEL")
    void livroNovoDeveEntrarDisponivel() {
        Livro livro = new Livro("Código Limpo", "Robert C. Martin", "9788576082675", "Computação");
        livro.setStatus(StatusLivro.EMPRESTADO); // tentativa de cadastrar já emprestado
        when(livroRepository.existsByIsbn("9788576082675")).thenReturn(false);
        when(livroRepository.save(any(Livro.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Livro salvo = livroService.criar(livro);

        assertEquals(StatusLivro.DISPONIVEL, salvo.getStatus());
    }

    @Test
    @DisplayName("RN02 - ISBN duplicado deve ser recusado")
    void isbnDuplicadoDeveSerRecusado() {
        Livro livro = new Livro("Outro título", "Outro autor", "9788576082675", "Computação");
        when(livroRepository.existsByIsbn("9788576082675")).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> livroService.criar(livro));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    @DisplayName("RN07 - Livro com empréstimos não pode ser excluído")
    void livroComHistoricoNaoPodeSerExcluido() {
        Livro livro = new Livro("Dom Casmurro", "Machado de Assis", "9788594318602", "Literatura");
        livro.setId(1L);
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsByLivroId(1L)).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> livroService.excluir(1L));
        verify(livroRepository, never()).delete(any(Livro.class));
    }
}
