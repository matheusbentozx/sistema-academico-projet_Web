package br.edu.sistemaacademico.service;

import br.edu.sistemaacademico.exception.RecursoNaoEncontradoException;
import br.edu.sistemaacademico.exception.RegraNegocioException;
import br.edu.sistemaacademico.model.ObjetoEncontrado;
import br.edu.sistemaacademico.model.StatusObjeto;
import br.edu.sistemaacademico.repository.ObjetoEncontradoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Regras de negócio do Achados e Perdidos. */
@Service
public class ObjetoEncontradoService {

    private final ObjetoEncontradoRepository repository;

    public ObjetoEncontradoService(ObjetoEncontradoRepository repository) {
        this.repository = repository;
    }

    /** Lista objetos, opcionalmente pesquisando e filtrando por status. */
    public List<ObjetoEncontrado> listar(String busca, StatusObjeto status) {
        List<ObjetoEncontrado> objetos = (busca != null && !busca.isBlank())
                ? repository.pesquisar(busca.trim())
                : repository.findAllByOrderByDataEncontradoDescIdDesc();
        if (status != null) {
            objetos = objetos.stream().filter(o -> o.getStatus() == status).toList();
        }
        return objetos;
    }

    public ObjetoEncontrado buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Objeto " + id + " não encontrado."));
    }

    /** Regra: todo objeto novo começa como ENCONTRADO. */
    @Transactional
    public ObjetoEncontrado registrar(ObjetoEncontrado objeto) {
        objeto.setId(null);
        objeto.setStatus(StatusObjeto.ENCONTRADO);
        objeto.setNomeProprietario(null);
        objeto.setDataDevolucao(null);
        return repository.save(objeto);
    }

    /** Edita os dados descritivos (não altera status). */
    @Transactional
    public ObjetoEncontrado atualizar(Long id, ObjetoEncontrado dados) {
        ObjetoEncontrado objeto = buscarPorId(id);
        objeto.setNome(dados.getNome());
        objeto.setDescricao(dados.getDescricao());
        objeto.setLocalEncontrado(dados.getLocalEncontrado());
        objeto.setDataEncontrado(dados.getDataEncontrado());
        return repository.save(objeto);
    }

    /**
     * Alterna entre ENCONTRADO e EM_ESTOQUE.
     * DEVOLVIDO só pode ser definido pela operação de devolução (que exige o nome do dono).
     */
    @Transactional
    public ObjetoEncontrado atualizarStatus(Long id, StatusObjeto novoStatus) {
        ObjetoEncontrado objeto = buscarPorId(id);
        if (objeto.getStatus() == StatusObjeto.DEVOLVIDO) {
            throw new RegraNegocioException("Objeto já devolvido ao proprietário; o status não pode mais ser alterado.");
        }
        if (novoStatus == StatusObjeto.DEVOLVIDO) {
            throw new RegraNegocioException("Use a opção \"Registrar devolução\" para marcar o objeto como devolvido.");
        }
        objeto.setStatus(novoStatus);
        return repository.save(objeto);
    }

    /** Regra: após a entrega ao proprietário, o objeto passa para DEVOLVIDO. */
    @Transactional
    public ObjetoEncontrado registrarDevolucao(Long id, String nomeProprietario) {
        ObjetoEncontrado objeto = buscarPorId(id);
        if (objeto.getStatus() == StatusObjeto.DEVOLVIDO) {
            throw new RegraNegocioException("Este objeto já foi devolvido.");
        }
        objeto.setStatus(StatusObjeto.DEVOLVIDO);
        objeto.setNomeProprietario(nomeProprietario.trim());
        objeto.setDataDevolucao(LocalDate.now());
        return repository.save(objeto);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarPorId(id));
    }
}
