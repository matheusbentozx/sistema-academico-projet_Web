package br.edu.sistemaacademico.service;

import br.edu.sistemaacademico.exception.RecursoNaoEncontradoException;
import br.edu.sistemaacademico.exception.RegraNegocioException;
import br.edu.sistemaacademico.model.Usuario;
import br.edu.sistemaacademico.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByNomeAsc();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário " + id + " não encontrado."));
    }

    @Transactional
    public Usuario criar(Usuario usuario) {
        usuario.setId(null);
        usuario.setNome(usuario.getNome().trim());
        usuario.setMatricula(usuario.getMatricula().trim());
        if (usuarioRepository.existsByMatricula(usuario.getMatricula())) {
            throw new RegraNegocioException("Já existe um usuário com a matrícula " + usuario.getMatricula() + ".");
        }
        return usuarioRepository.save(usuario);
    }
}
