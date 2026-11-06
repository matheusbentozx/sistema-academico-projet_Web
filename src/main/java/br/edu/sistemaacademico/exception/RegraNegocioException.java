package br.edu.sistemaacademico.exception;

/**
 * Lançada quando uma regra de negócio é violada
 * (ex.: emprestar livro já emprestado). Vira HTTP 409.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
