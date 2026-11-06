/* =========================================================
   objetos.js — tela de Achados e Perdidos
   ========================================================= */

let objetosCarregados = [];

async function carregarObjetos() {
    const busca = document.getElementById('busca-objeto').value.trim();
    const status = document.getElementById('filtro-status-objeto').value;

    const parametros = new URLSearchParams();
    if (busca) parametros.append('busca', busca);
    if (status) parametros.append('status', status);
    const consulta = parametros.toString() ? `?${parametros}` : '';

    try {
        objetosCarregados = await requisicao('/objetos' + consulta);
        renderizarObjetos();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

function renderizarObjetos() {
    const lista = document.getElementById('lista-objetos');
    if (objetosCarregados.length === 0) {
        lista.innerHTML = '<p class="vazio">Nenhum objeto encontrado.</p>';
        return;
    }
    lista.innerHTML = objetosCarregados.map(o => {
        const devolvido = o.status === 'DEVOLVIDO';
        const acoes = devolvido
            ? `<span class="meta">Entregue a <b>${esc(o.nomeProprietario)}</b> em ${formatarData(o.dataDevolucao)}</span>`
            : `
                <select aria-label="Alterar status" onchange="alterarStatusObjeto(${o.id}, this.value)">
                    <option value="ENCONTRADO" ${o.status === 'ENCONTRADO' ? 'selected' : ''}>Encontrado</option>
                    <option value="EM_ESTOQUE" ${o.status === 'EM_ESTOQUE' ? 'selected' : ''}>Em estoque</option>
                </select>
                <button class="btn pequeno sucesso" onclick="abrirDevolucaoObjeto(${o.id})">Registrar devolução</button>
                <button class="btn pequeno" onclick="editarObjeto(${o.id})">Editar</button>`;
        return `
            <article class="objeto">
                <div class="objeto-topo">
                    <strong>${esc(o.nome)}</strong>
                    ${badge(o.status)}
                </div>
                <p>${esc(o.descricao)}</p>
                <span class="meta">📍 ${esc(o.localEncontrado)} · ${formatarData(o.dataEncontrado)}</span>
                <div class="objeto-acoes">
                    ${acoes}
                    <button class="btn pequeno perigo" onclick="excluirObjeto(${o.id})">Excluir</button>
                </div>
            </article>`;
    }).join('');
}

async function salvarObjeto(evento) {
    evento.preventDefault();
    if (!validarObrigatorios(['objeto-nome', 'objeto-descricao', 'objeto-local', 'objeto-data'])) return;

    const id = document.getElementById('objeto-id').value;
    const objeto = {
        nome: document.getElementById('objeto-nome').value.trim(),
        descricao: document.getElementById('objeto-descricao').value.trim(),
        localEncontrado: document.getElementById('objeto-local').value.trim(),
        dataEncontrado: document.getElementById('objeto-data').value
    };

    try {
        if (id) {
            await requisicao(`/objetos/${id}`, 'PUT', objeto);
            aviso('Objeto atualizado.');
        } else {
            await requisicao('/objetos', 'POST', objeto);
            aviso('Objeto registrado como ENCONTRADO.');
        }
        limparFormularioObjeto();
        carregarObjetos();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

function editarObjeto(id) {
    const o = objetosCarregados.find(x => x.id === id);
    if (!o) return;
    document.getElementById('objeto-id').value = o.id;
    document.getElementById('objeto-nome').value = o.nome;
    document.getElementById('objeto-descricao').value = o.descricao;
    document.getElementById('objeto-local').value = o.localEncontrado;
    document.getElementById('objeto-data').value = o.dataEncontrado;
    document.getElementById('form-objeto-titulo').textContent = 'Editar objeto';
    document.getElementById('btn-salvar-objeto').textContent = 'Salvar alterações';
    document.getElementById('btn-cancelar-objeto').hidden = false;
    document.getElementById('objeto-nome').focus();
}

function limparFormularioObjeto() {
    document.getElementById('form-objeto').reset();
    document.getElementById('objeto-id').value = '';
    document.getElementById('objeto-data').value = hoje();
    document.getElementById('form-objeto-titulo').textContent = 'Registrar objeto encontrado';
    document.getElementById('btn-salvar-objeto').textContent = 'Registrar';
    document.getElementById('btn-cancelar-objeto').hidden = true;
    document.querySelectorAll('#form-objeto .campo-erro').forEach(c => c.classList.remove('campo-erro'));
}

async function alterarStatusObjeto(id, status) {
    try {
        await requisicao(`/objetos/${id}/status`, 'PATCH', { status });
        aviso(`Status alterado para ${ROTULOS[status]}.`);
        carregarObjetos();
    } catch (e) {
        aviso(e.message, 'erro');
        carregarObjetos();
    }
}

function abrirDevolucaoObjeto(id) {
    const o = objetosCarregados.find(x => x.id === id);
    document.getElementById('devolucao-objeto-id').value = id;
    document.getElementById('devolucao-objeto-nome').textContent = o ? `Objeto: ${o.nome}` : '';
    document.getElementById('devolucao-proprietario').value = '';
    document.getElementById('devolucao-proprietario').classList.remove('campo-erro');
    document.getElementById('dialogo-devolucao').showModal();
}

async function confirmarDevolucaoObjeto(evento) {
    evento.preventDefault();
    if (!validarObrigatorios(['devolucao-proprietario'])) return;

    const id = document.getElementById('devolucao-objeto-id').value;
    const nomeProprietario = document.getElementById('devolucao-proprietario').value.trim();
    try {
        await requisicao(`/objetos/${id}/devolucao`, 'PUT', { nomeProprietario });
        document.getElementById('dialogo-devolucao').close();
        aviso('Devolução registrada. Objeto marcado como DEVOLVIDO.');
        carregarObjetos();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

async function excluirObjeto(id) {
    const o = objetosCarregados.find(x => x.id === id);
    if (!confirm(`Excluir o registro "${o ? o.nome : id}"?`)) return;
    try {
        await requisicao(`/objetos/${id}`, 'DELETE');
        aviso('Registro excluído.');
        if (document.getElementById('objeto-id').value === String(id)) limparFormularioObjeto();
        carregarObjetos();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

function iniciarObjetos() {
    document.getElementById('objeto-data').value = hoje();
    document.getElementById('objeto-data').max = hoje();
    document.getElementById('form-objeto').addEventListener('submit', salvarObjeto);
    document.getElementById('btn-cancelar-objeto').addEventListener('click', limparFormularioObjeto);
    document.getElementById('busca-objeto').addEventListener('input', debounce(carregarObjetos));
    document.getElementById('filtro-status-objeto').addEventListener('change', carregarObjetos);
    document.getElementById('form-devolucao').addEventListener('submit', confirmarDevolucaoObjeto);
    document.getElementById('btn-fechar-dialogo').addEventListener('click',
        () => document.getElementById('dialogo-devolucao').close());
}
