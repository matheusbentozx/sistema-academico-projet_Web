/* =========================================================
   livros.js — tela Biblioteca (CRUD de livros)
   ========================================================= */

let livrosCarregados = [];

async function carregarLivros() {
    const busca = document.getElementById('busca-livro').value.trim();
    const status = document.getElementById('filtro-status-livro').value;

    const parametros = new URLSearchParams();
    if (busca) parametros.append('busca', busca);
    if (status) parametros.append('status', status);
    const consulta = parametros.toString() ? `?${parametros}` : '';

    try {
        livrosCarregados = await requisicao('/livros' + consulta);
        renderizarLivros();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

function renderizarLivros() {
    const corpo = document.getElementById('tabela-livros');
    if (livrosCarregados.length === 0) {
        corpo.innerHTML = '<tr><td colspan="6" class="vazio">Nenhum livro encontrado.</td></tr>';
        return;
    }
    corpo.innerHTML = livrosCarregados.map(livro => `
        <tr>
            <td><strong>${esc(livro.titulo)}</strong></td>
            <td>${esc(livro.autor)}</td>
            <td>${esc(livro.isbn)}</td>
            <td>${esc(livro.categoria) || '—'}</td>
            <td>${badge(livro.status)}</td>
            <td class="acoes">
                <button class="btn pequeno" onclick="editarLivro(${livro.id})">Editar</button>
                <button class="btn pequeno perigo" onclick="excluirLivro(${livro.id})">Excluir</button>
            </td>
        </tr>`).join('');
}

async function salvarLivro(evento) {
    evento.preventDefault();
    if (!validarObrigatorios(['livro-titulo', 'livro-autor', 'livro-isbn'])) return;

    const id = document.getElementById('livro-id').value;
    const livro = {
        titulo: document.getElementById('livro-titulo').value.trim(),
        autor: document.getElementById('livro-autor').value.trim(),
        isbn: document.getElementById('livro-isbn').value.trim(),
        categoria: document.getElementById('livro-categoria').value.trim()
    };

    try {
        if (id) {
            await requisicao(`/livros/${id}`, 'PUT', livro);
            aviso('Livro atualizado com sucesso.');
        } else {
            await requisicao('/livros', 'POST', livro);
            aviso('Livro cadastrado com sucesso.');
        }
        limparFormularioLivro();
        carregarLivros();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

function editarLivro(id) {
    const livro = livrosCarregados.find(l => l.id === id);
    if (!livro) return;
    document.getElementById('livro-id').value = livro.id;
    document.getElementById('livro-titulo').value = livro.titulo;
    document.getElementById('livro-autor').value = livro.autor;
    document.getElementById('livro-isbn').value = livro.isbn;
    document.getElementById('livro-categoria').value = livro.categoria || '';
    document.getElementById('form-livro-titulo').textContent = 'Editar livro';
    document.getElementById('btn-salvar-livro').textContent = 'Salvar alterações';
    document.getElementById('btn-cancelar-livro').hidden = false;
    document.getElementById('livro-titulo').focus();
}

async function excluirLivro(id) {
    const livro = livrosCarregados.find(l => l.id === id);
    if (!confirm(`Excluir o livro "${livro ? livro.titulo : id}"?`)) return;
    try {
        await requisicao(`/livros/${id}`, 'DELETE');
        aviso('Livro excluído.');
        if (document.getElementById('livro-id').value === String(id)) limparFormularioLivro();
        carregarLivros();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

function limparFormularioLivro() {
    document.getElementById('form-livro').reset();
    document.getElementById('livro-id').value = '';
    document.getElementById('form-livro-titulo').textContent = 'Cadastrar livro';
    document.getElementById('btn-salvar-livro').textContent = 'Cadastrar';
    document.getElementById('btn-cancelar-livro').hidden = true;
    document.querySelectorAll('#form-livro .campo-erro').forEach(c => c.classList.remove('campo-erro'));
}

function iniciarLivros() {
    document.getElementById('form-livro').addEventListener('submit', salvarLivro);
    document.getElementById('btn-cancelar-livro').addEventListener('click', limparFormularioLivro);
    document.getElementById('busca-livro').addEventListener('input', debounce(carregarLivros));
    document.getElementById('filtro-status-livro').addEventListener('change', carregarLivros);
}
