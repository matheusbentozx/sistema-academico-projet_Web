package br.edu.sistemaacademico.controller;

import br.edu.sistemaacademico.model.Livro;
import br.edu.sistemaacademico.model.StatusLivro;
import br.edu.sistemaacademico.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Endpoints REST de livros.
 * O controller só recebe a requisição e devolve a resposta;
 * as regras ficam no LivroService.
 */
@RestController
@RequestMapping("/api/livros")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    /** GET /api/livros?busca=texto&status=DISPONIVEL (parâmetros opcionais) */
    @GetMapping
    public List<Livro> listar(@RequestParam(name = "busca", required = false) String busca,
                              @RequestParam(name = "status", required = false) StatusLivro status) {
        return livroService.listar(busca, status);
    }

    @GetMapping("/{id}")
    public Livro buscarPorId(@PathVariable("id") Long id) {
        return livroService.buscarPorId(id);
    }

    /** Consulta rápida de disponibilidade de um livro. */
    @GetMapping("/{id}/disponibilidade")
    public Map<String, Object> disponibilidade(@PathVariable("id") Long id) {
        Livro livro = livroService.buscarPorId(id);
        return Map.of(
                "livroId", livro.getId(),
                "titulo", livro.getTitulo(),
                "status", livro.getStatus(),
                "disponivel", livro.isDisponivel()
        );
    }

    @PostMapping
    public ResponseEntity<Livro> criar(@Valid @RequestBody Livro livro) {
        Livro salvo = livroService.criar(livro);
        return ResponseEntity.created(URI.create("/api/livros/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public Livro atualizar(@PathVariable("id") Long id, @Valid @RequestBody Livro livro) {
        return livroService.atualizar(id, livro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        livroService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
