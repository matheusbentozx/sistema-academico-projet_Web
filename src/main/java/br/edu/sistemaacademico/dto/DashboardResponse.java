package br.edu.sistemaacademico.dto;

/** Números exibidos no painel inicial. */
public record DashboardResponse(
        long totalLivros,
        long livrosDisponiveis,
        long livrosEmprestados,
        long emprestimosAtivos,
        long objetosRegistrados,
        long objetosEmAberto,
        long objetosDevolvidos
) {
}
