package br.edu.sistemaacademico.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Aluno/servidor que pode pegar livros emprestados. */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "A matrícula é obrigatória")
    @Size(max = 30, message = "A matrícula deve ter no máximo 30 caracteres")
    @Column(nullable = false, unique = true, length = 30)
    private String matricula;

    @Email(message = "E-mail inválido")
    @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres")
    @Column(length = 120)
    private String email;

    public Usuario() {
    }

    public Usuario(String nome, String matricula, String email) {
        this.nome = nome;
        this.matricula = matricula;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
