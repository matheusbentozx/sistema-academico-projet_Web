package br.edu.sistemaacademico.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Livro do acervo da biblioteca. */
@Entity
@Table(name = "livro")
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 200, message = "O título deve ter no máximo 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    @NotBlank(message = "O autor é obrigatório")
    @Size(max = 150, message = "O autor deve ter no máximo 150 caracteres")
    @Column(nullable = false, length = 150)
    private String autor;

    @NotBlank(message = "O ISBN é obrigatório")
    @Size(max = 20, message = "O ISBN deve ter no máximo 20 caracteres")
    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Size(max = 80, message = "A categoria deve ter no máximo 80 caracteres")
    @Column(length = 80)
    private String categoria;

    /** Controlado pelo sistema: muda apenas com empréstimos e devoluções. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusLivro status = StatusLivro.DISPONIVEL;

    public Livro() {
    }

    public Livro(String titulo, String autor, String isbn, String categoria) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoria = categoria;
        this.status = StatusLivro.DISPONIVEL;
    }

    public boolean isDisponivel() {
        return status == StatusLivro.DISPONIVEL;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public StatusLivro getStatus() { return status; }
    public void setStatus(StatusLivro status) { this.status = status; }
}
