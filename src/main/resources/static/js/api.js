/* =========================================================
   api.js — funções compartilhadas por todas as telas
   ========================================================= */

/*
 * Endereço da API. Se a página for servida pelo próprio Spring Boot
 * (http://localhost:8080) usa caminho relativo; se for aberta por outro
 * servidor (ex.: Live Server na porta 5500) aponta para o backend.
 */
const API_URL = (location.protocol.startsWith('http') && location.port === '8080')
    ? '/api'
    : 'http://localhost:8080/api';

/**
 * Faz uma requisição HTTP com a Fetch API e devolve o JSON da resposta.
 * Em caso de erro, lança uma Error com a mensagem enviada pelo backend.
 */
async function requisicao(caminho, metodo = 'GET', corpo = null) {
    const opcoes = { method: metodo, headers: {} };
    if (corpo !== null) {
        opcoes.headers['Content-Type'] = 'application/json';
        opcoes.body = JSON.stringify(corpo);
    }

    let resposta;
    try {
        resposta = await fetch(API_URL + caminho, opcoes);
    } catch (e) {
        throw new Error('Não foi possível conectar ao servidor. O backend está em execução?');
    }

    if (resposta.status === 204) return null;

    const dados = await resposta.json().catch(() => null);
    if (!resposta.ok) {
        throw new Error((dados && dados.mensagem) || `Erro ${resposta.status}`);
    }
    return dados;
}

/** Mostra um aviso temporário no canto da tela. */
function aviso(mensagem, tipo = 'sucesso') {
    const caixa = document.createElement('div');
    caixa.className = 'aviso' + (tipo === 'erro' ? ' erro' : '');
    caixa.textContent = mensagem;
    document.getElementById('avisos').appendChild(caixa);
    setTimeout(() => caixa.remove(), tipo === 'erro' ? 6000 : 3500);
}

/** Escapa texto antes de inserir no HTML (evita quebrar a página com <, >, etc.). */
function esc(valor) {
    if (valor === null || valor === undefined) return '';
    return String(valor)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

/** "2026-10-30" -> "30/10/2026" */
function formatarData(iso) {
    if (!iso) return '—';
    const [ano, mes, dia] = iso.split('-');
    return `${dia}/${mes}/${ano}`;
}

/** Data de hoje no formato do <input type="date"> (horário local). */
function hoje() {
    const d = new Date();
    const mes = String(d.getMonth() + 1).padStart(2, '0');
    const dia = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}-${mes}-${dia}`;
}

/** Textos exibidos para cada status. */
const ROTULOS = {
    DISPONIVEL: 'Disponível',
    EMPRESTADO: 'Emprestado',
    ATIVO: 'Ativo',
    DEVOLVIDO: 'Devolvido',
    ENCONTRADO: 'Encontrado',
    EM_ESTOQUE: 'Em estoque'
};

/** Gera o HTML do "selo" colorido de status. */
function badge(status) {
    return `<span class="status status-${esc(status)}">${esc(ROTULOS[status] || status)}</span>`;
}

/**
 * Valida campos obrigatórios no navegador antes de enviar.
 * Recebe uma lista de ids; marca em vermelho os vazios e retorna true/false.
 */
function validarObrigatorios(ids) {
    let valido = true;
    ids.forEach(id => {
        const campo = document.getElementById(id);
        const vazio = !campo.value || !campo.value.trim();
        campo.classList.toggle('campo-erro', vazio);
        if (vazio) valido = false;
    });
    if (!valido) aviso('Preencha os campos obrigatórios (*).', 'erro');
    return valido;
}

/** Atraso para não consultar a API a cada tecla digitada na busca. */
function debounce(funcao, espera = 300) {
    let temporizador;
    return (...args) => {
        clearTimeout(temporizador);
        temporizador = setTimeout(() => funcao(...args), espera);
    };
}
