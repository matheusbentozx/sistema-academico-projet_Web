package br.edu.sistemaacademico.dto;

import java.time.LocalDateTime;

/** Formato padrão das respostas de erro da API. */
public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem
) {
    public static ErroResponse de(int status, String erro, String mensagem) {
        return new ErroResponse(LocalDateTime.now(), status, erro, mensagem);
    }
}
