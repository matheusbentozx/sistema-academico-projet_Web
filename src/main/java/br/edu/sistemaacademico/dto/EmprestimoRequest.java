package br.edu.sistemaacademico.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Dados enviados pelo frontend para registrar um empréstimo.
 * Se dataEmprestimo vier vazia, o sistema usa a data de hoje.
 */
public record EmprestimoRequest(
        @NotNull(message = "Selecione o livro") Long livroId,
        @NotNull(message = "Selecione o usuário") Long usuarioId,
        LocalDate dataEmprestimo
) {
}
