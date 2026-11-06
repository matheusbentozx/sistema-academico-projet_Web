package br.edu.sistemaacademico.controller;

import br.edu.sistemaacademico.dto.DevolucaoObjetoRequest;
import br.edu.sistemaacademico.dto.StatusObjetoRequest;
import br.edu.sistemaacademico.model.ObjetoEncontrado;
import br.edu.sistemaacademico.model.StatusObjeto;
import br.edu.sistemaacademico.service.ObjetoEncontradoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/** Endpoints REST do Achados e Perdidos. */
@RestController
@RequestMapping("/api/objetos")
public class ObjetoEncontradoController {

    private final ObjetoEncontradoService service;

    public ObjetoEncontradoController(ObjetoEncontradoService service) {
        this.service = service;
    }

    /** GET /api/objetos?busca=mochila&status=EM_ESTOQUE (opcionais) */
    @GetMapping
    public List<ObjetoEncontrado> listar(@RequestParam(name = "busca", required = false) String busca,
                                         @RequestParam(name = "status", required = false) StatusObjeto status) {
        return service.listar(busca, status);
    }

    @GetMapping("/{id}")
    public ObjetoEncontrado buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<ObjetoEncontrado> registrar(@Valid @RequestBody ObjetoEncontrado objeto) {
        ObjetoEncontrado salvo = service.registrar(objeto);
        return ResponseEntity.created(URI.create("/api/objetos/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public ObjetoEncontrado atualizar(@PathVariable("id") Long id, @Valid @RequestBody ObjetoEncontrado objeto) {
        return service.atualizar(id, objeto);
    }

    /** Altera o status entre ENCONTRADO e EM_ESTOQUE. */
    @PatchMapping("/{id}/status")
    public ObjetoEncontrado atualizarStatus(@PathVariable("id") Long id, @Valid @RequestBody StatusObjetoRequest request) {
        return service.atualizarStatus(id, request.status());
    }

    /** Registra a entrega ao proprietário (status -> DEVOLVIDO). */
    @PutMapping("/{id}/devolucao")
    public ObjetoEncontrado registrarDevolucao(@PathVariable("id") Long id,
                                               @Valid @RequestBody DevolucaoObjetoRequest request) {
        return service.registrarDevolucao(id, request.nomeProprietario());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
