package br.edu.sistemaacademico.service;

import br.edu.sistemaacademico.dto.EmprestimoRequest;
import br.edu.sistemaacademico.exception.RegraNegocioException;
import br.edu.sistemaacademico.model.Emprestimo;
import br.edu.sistemaacademico.model.Livro;
import br.edu.sistemaacademico.model.StatusEmprestimo;
import br.edu.sistemaacademico.model.StatusLivro;
import br.edu.sistemaacademico.model.Usuario;
import br.edu.sistemaacademico.repository.EmprestimoRepository;
import br.edu.sistemaacademico.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Testes unitários das regras de empréstimo e devolução. */
@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private LivroService livroService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private EmprestimoService emprestimoService;

    private Livro livro;
    private Usuario usuario;

    @BeforeEach
    void prepararDados() {
        livro = new Livro("Engenharia de Software", "Ian Sommerville", "9788543024974", "Computação");
        livro.setId(1L);
        usuario = new Usuario("Ana Souza", "2024001", "ana@aluno.edu.br");
        usuario.setId(1L);
    }

    @Test
    @DisplayName("RN03 - Registrar empréstimo muda o livro para EMPRESTADO")
    void registrarEmprestimoMudaLivroParaEmprestado() {
        when(livroService.buscarPorId(1L)).thenReturn(livro);
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Emprestimo emprestimo = emprestimoService.registrar(new EmprestimoRequest(1L, 1L, null));

        assertEquals(StatusLivro.EMPRESTADO, livro.getStatus());
        assertEquals(StatusEmprestimo.ATIVO, emprestimo.getStatus());
        assertEquals(LocalDate.now(), emprestimo.getDataEmprestimo());
    }

    @Test
    @DisplayName("RN04 - Não é permitido emprestar livro já emprestado")
    void naoEmprestaLivroJaEmprestado() {
        livro.setStatus(StatusLivro.EMPRESTADO);
        when(livroService.buscarPorId(1L)).thenReturn(livro);
        when(usuarioService.buscarPorId(1L)).thenReturn(usuario);

        assertThrows(RegraNegocioException.class,
                () -> emprestimoService.registrar(new EmprestimoRequest(1L, 1L, null)));
        verify(emprestimoRepository, never()).save(any(Emprestimo.class));
    }

    @Test
    @DisplayName("RN05 - Devolução marca DEVOLVIDO e libera o livro")
    void devolucaoLiberaOLivro() {
        livro.setStatus(StatusLivro.EMPRESTADO);
        Emprestimo emprestimo = new Emprestimo(usuario, livro, LocalDate.now().minusDays(3));
        emprestimo.setId(10L);
        when(emprestimoRepository.findById(10L)).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenAnswer(chamada -> chamada.getArgument(0));

        Emprestimo devolvido = emprestimoService.registrarDevolucao(10L);

        assertEquals(StatusEmprestimo.DEVOLVIDO, devolvido.getStatus());
        assertEquals(LocalDate.now(), devolvido.getDataDevolucao());
        assertEquals(StatusLivro.DISPONIVEL, livro.getStatus());
    }

    @Test
    @DisplayName("Não é permitido devolver o mesmo empréstimo duas vezes")
    void naoDevolveDuasVezes() {
        Emprestimo emprestimo = new Emprestimo(usuario, livro, LocalDate.now().minusDays(3));
        emprestimo.setId(10L);
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        when(emprestimoRepository.findById(10L)).thenReturn(Optional.of(emprestimo));

        assertThrows(RegraNegocioException.class, () -> emprestimoService.registrarDevolucao(10L));
    }
}
