package br.edu.sistemaacademico.exception;

/** Lançada quando um registro procurado não existe (vira HTTP 404). */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
