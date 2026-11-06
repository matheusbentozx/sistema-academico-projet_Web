/* =========================================================
   app.js — navegação entre as telas e inicialização
   ========================================================= */

/** Função que recarrega os dados de cada tela ao ser aberta. */
const CARREGADORES = {
    painel: carregarPainel,
    biblioteca: carregarLivros,
    emprestimos: carregarTelaEmprestimos,
    achados: carregarObjetos
};

function abrirSecao(nome) {
    if (!CARREGADORES[nome]) nome = 'painel';

    document.querySelectorAll('.secao').forEach(s => s.classList.toggle('ativa', s.id === nome));
    document.querySelectorAll('.menu-item').forEach(b => b.classList.toggle('ativo', b.dataset.secao === nome));

    if (location.hash !== '#' + nome) history.replaceState(null, '', '#' + nome);
    CARREGADORES[nome]();
}

document.addEventListener('DOMContentLoaded', () => {
    iniciarLivros();
    iniciarEmprestimos();
    iniciarObjetos();

    document.querySelectorAll('.menu-item').forEach(botao =>
        botao.addEventListener('click', () => abrirSecao(botao.dataset.secao)));

    abrirSecao(location.hash.replace('#', '') || 'painel');
});
