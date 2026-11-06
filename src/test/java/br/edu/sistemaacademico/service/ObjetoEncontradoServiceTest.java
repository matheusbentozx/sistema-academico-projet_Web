package br.edu.sistemaacademico.service;

import br.edu.sistemaacademico.exception.RegraNegocioException;
import br.edu.sistemaacademico.model.ObjetoEncontrado;
import br.edu.sistemaacademico.model.StatusObjeto;
import br.edu.sistemaacademico.repository.ObjetoEncontradoRepository;
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

/** Testes unitários das regras do Achados e Perdidos. */
@ExtendWith(MockitoExtension.class)
class ObjetoEncontradoServiceTest {

    @Mock
    private ObjetoEncontradoRepository repository;

    @InjectMocks
    private ObjetoEncontradoService service;

    private ObjetoEncontrado mochila;

    @BeforeEach
    void prepararDados() {
        mochila = new ObjetoEncontrado("Mochila preta",
                "Mochila preta, com chaveiro vermelho, contendo um caderno azul e estojo.",
                "Biblioteca", LocalDate.now());
        mochila.setId(5L);
    }

    @Test
    @DisplayName("RN09 - Objeto novo deve entrar como ENCONTRADO")
    void objetoNovoDeveEntrarEncontrado() {
        mochila.setStatus(StatusObjeto.DEVOLVIDO); // tentativa de cadastrar já devolvido
        when(repository.save(any(ObjetoEncontrado.class))).thenAnswer(chamada -> chamada.getArgument(0));

        ObjetoEncontrado salvo = service.registrar(mochila);

        assertEquals(StatusObjeto.ENCONTRADO, salvo.getStatus());
    }

    @Test
    @DisplayName("RN11 - DEVOLVIDO só pode ser definido pela operação de devolução")
    void statusDevolvidoSoPelaDevolucao() {
        when(repository.findById(5L)).thenReturn(Optional.of(mochila));

        assertThrows(RegraNegocioException.class,
                () -> service.atualizarStatus(5L, StatusObjeto.DEVOLVIDO));
        assertEquals(StatusObjeto.ENCONTRADO, mochila.getStatus());
        verify(repository, never()).save(any(ObjetoEncontrado.class));
    }

    @Test
    @DisplayName("Devolução registra o nome de quem retirou e a data")
    void devolucaoRegistraDonoEData() {
        when(repository.findById(5L)).thenReturn(Optional.of(mochila));
        when(repository.save(any(ObjetoEncontrado.class))).thenAnswer(chamada -> chamada.getArgument(0));

        ObjetoEncontrado devolvido = service.registrarDevolucao(5L, "João Pereira");

        assertEquals(StatusObjeto.DEVOLVIDO, devolvido.getStatus());
        assertEquals("João Pereira", devolvido.getNomeProprietario());
        assertEquals(LocalDate.now(), devolvido.getDataDevolucao());
    }
}
