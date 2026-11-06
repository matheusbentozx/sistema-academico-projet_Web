package br.edu.sistemaacademico.dto;

import br.edu.sistemaacademico.model.StatusObjeto;
import jakarta.validation.constraints.NotNull;

/** Corpo da requisição para alterar o status de um objeto. */
public record StatusObjetoRequest(
        @NotNull(message = "Informe o novo status") StatusObjeto status
) {
}
