package br.edu.sistemaacademico.controller;

import br.edu.sistemaacademico.dto.EmprestimoRequest;
import br.edu.sistemaacademico.model.Emprestimo;
import br.edu.sistemaacademico.model.StatusEmprestimo;
import br.edu.sistemaacademico.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

/** Endpoints REST de empréstimos e devoluções. */
@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    /** GET /api/emprestimos?status=ATIVO (opcional) — histórico completo. */
    @GetMapping
    public List<Emprestimo> listar(@RequestParam(name = "status", required = false) StatusEmprestimo status) {
        return emprestimoService.listar(status);
    }

    @GetMapping("/{id}")
    public Emprestimo buscarPorId(@PathVariable("id") Long id) {
        return emprestimoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Emprestimo> registrar(@Valid @RequestBody EmprestimoRequest request) {
        Emprestimo emprestimo = emprestimoService.registrar(request);
        return ResponseEntity.created(URI.create("/api/emprestimos/" + emprestimo.getId())).body(emprestimo);
    }

    @PutMapping("/{id}/devolucao")
    public Emprestimo registrarDevolucao(@PathVariable("id") Long id) {
        return emprestimoService.registrarDevolucao(id);
    }
}
