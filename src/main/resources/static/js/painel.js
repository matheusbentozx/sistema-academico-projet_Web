/* =========================================================
   painel.js — indicadores da tela inicial
   ========================================================= */

async function carregarPainel() {
    try {
        const d = await requisicao('/dashboard');
        document.getElementById('ind-total-livros').textContent = d.totalLivros;
        document.getElementById('ind-disponiveis').textContent = d.livrosDisponiveis;
        document.getElementById('ind-emprestados').textContent = d.livrosEmprestados;
        document.getElementById('ind-objetos').textContent = d.objetosRegistrados;
        document.getElementById('ind-objetos-aberto').textContent = d.objetosEmAberto;
        document.getElementById('ind-objetos-devolvidos').textContent = d.objetosDevolvidos;
    } catch (e) {
        aviso(e.message, 'erro');
    }
}
