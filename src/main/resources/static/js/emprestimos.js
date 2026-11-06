/* =========================================================
   emprestimos.js — tela de Empréstimos e devoluções
   ========================================================= */

/** Recarrega tudo o que a tela usa: selects e histórico. */
async function carregarTelaEmprestimos() {
    await Promise.all([carregarOpcoesEmprestimo(), carregarEmprestimos()]);
}

/** Preenche os selects de livros disponíveis e usuários. */
async function carregarOpcoesEmprestimo() {
    try {
        const [livros, usuarios] = await Promise.all([
            requisicao('/livros?status=DISPONIVEL'),
            requisicao('/usuarios')
        ]);

        const selectLivro = document.getElementById('emprestimo-livro');
        selectLivro.innerHTML = livros.length
            ? '<option value="">Selecione...</option>' +
              livros.map(l => `<option value="${l.id}">${esc(l.titulo)} — ${esc(l.autor)}</option>`).join('')
            : '<option value="">Nenhum livro disponível</option>';

        const selectUsuario = document.getElementById('emprestimo-usuario');
        selectUsuario.innerHTML = usuarios.length
            ? '<option value="">Selecione...</option>' +
              usuarios.map(u => `<option value="${u.id}">${esc(u.nome)} (${esc(u.matricula)})</option>`).join('')
            : '<option value="">Nenhum usuário cadastrado</option>';
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

async function carregarEmprestimos() {
    const status = document.getElementById('filtro-status-emprestimo').value;
    try {
        const emprestimos = await requisicao('/emprestimos' + (status ? `?status=${status}` : ''));
        renderizarEmprestimos(emprestimos);
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

function renderizarEmprestimos(emprestimos) {
    const corpo = document.getElementById('tabela-emprestimos');
    if (emprestimos.length === 0) {
        corpo.innerHTML = '<tr><td colspan="7" class="vazio">Nenhum empréstimo registrado.</td></tr>';
        return;
    }
    corpo.innerHTML = emprestimos.map(e => `
        <tr>
            <td>${e.id}</td>
            <td><strong>${esc(e.livro.titulo)}</strong></td>
            <td>${esc(e.usuario.nome)}<br><small>${esc(e.usuario.matricula)}</small></td>
            <td>${formatarData(e.dataEmprestimo)}</td>
            <td>${formatarData(e.dataDevolucao)}</td>
            <td>${badge(e.status)}</td>
            <td class="acoes">
                ${e.status === 'ATIVO'
                    ? `<button class="btn pequeno sucesso" onclick="registrarDevolucaoLivro(${e.id})">Registrar devolução</button>`
                    : ''}
            </td>
        </tr>`).join('');
}

async function registrarEmprestimo(evento) {
    evento.preventDefault();
    if (!validarObrigatorios(['emprestimo-livro', 'emprestimo-usuario', 'emprestimo-data'])) return;

    const dados = {
        livroId: Number(document.getElementById('emprestimo-livro').value),
        usuarioId: Number(document.getElementById('emprestimo-usuario').value),
        dataEmprestimo: document.getElementById('emprestimo-data').value
    };

    try {
        await requisicao('/emprestimos', 'POST', dados);
        aviso('Empréstimo registrado. O livro agora está emprestado.');
        document.getElementById('emprestimo-data').value = hoje();
        carregarTelaEmprestimos();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

async function registrarDevolucaoLivro(id) {
    if (!confirm('Confirmar a devolução deste livro?')) return;
    try {
        await requisicao(`/emprestimos/${id}/devolucao`, 'PUT');
        aviso('Devolução registrada. O livro voltou a ficar disponível.');
        carregarTelaEmprestimos();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

async function salvarUsuario(evento) {
    evento.preventDefault();
    if (!validarObrigatorios(['usuario-nome', 'usuario-matricula'])) return;

    const email = document.getElementById('usuario-email').value.trim();
    const usuario = {
        nome: document.getElementById('usuario-nome').value.trim(),
        matricula: document.getElementById('usuario-matricula').value.trim(),
        email: email || null
    };

    try {
        await requisicao('/usuarios', 'POST', usuario);
        aviso('Usuário cadastrado.');
        document.getElementById('form-usuario').reset();
        carregarOpcoesEmprestimo();
    } catch (e) {
        aviso(e.message, 'erro');
    }
}

function iniciarEmprestimos() {
    document.getElementById('emprestimo-data').value = hoje();
    document.getElementById('emprestimo-data').max = hoje();
    document.getElementById('form-emprestimo').addEventListener('submit', registrarEmprestimo);
    document.getElementById('form-usuario').addEventListener('submit', salvarUsuario);
    document.getElementById('filtro-status-emprestimo').addEventListener('change', carregarEmprestimos);
}
