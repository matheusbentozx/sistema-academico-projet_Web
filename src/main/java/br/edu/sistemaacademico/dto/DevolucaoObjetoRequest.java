package br.edu.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corpo da requisição para registrar a devolução de um objeto ao dono. */
public record DevolucaoObjetoRequest(
        @NotBlank(message = "Informe o nome de quem retirou o objeto")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nomeProprietario
) {
}
