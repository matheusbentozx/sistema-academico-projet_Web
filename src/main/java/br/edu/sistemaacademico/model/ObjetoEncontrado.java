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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Objeto registrado no Achados e Perdidos. */
@Entity
@Table(name = "objeto_encontrado")
public class ObjetoEncontrado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do objeto é obrigatório")
    @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nome;

    /** Texto livre com as características do objeto. */
    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
    @Column(nullable = false, length = 2000)
    private String descricao;

    @NotBlank(message = "O local onde foi encontrado é obrigatório")
    @Size(max = 150, message = "O local deve ter no máximo 150 caracteres")
    @Column(name = "local_encontrado", nullable = false, length = 150)
    private String localEncontrado;

    @NotNull(message = "A data em que foi encontrado é obrigatória")
    @PastOrPresent(message = "A data não pode estar no futuro")
    @Column(name = "data_encontrado", nullable = false)
    private LocalDate dataEncontrado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusObjeto status = StatusObjeto.ENCONTRADO;

    /** Preenchidos somente na devolução ao proprietário. */
    @Column(name = "nome_proprietario", length = 120)
    private String nomeProprietario;

    @Column(name = "data_devolucao")
    private LocalDate dataDevolucao;

    public ObjetoEncontrado() {
    }

    public ObjetoEncontrado(String nome, String descricao, String localEncontrado, LocalDate dataEncontrado) {
        this.nome = nome;
        this.descricao = descricao;
        this.localEncontrado = localEncontrado;
        this.dataEncontrado = dataEncontrado;
        this.status = StatusObjeto.ENCONTRADO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getLocalEncontrado() { return localEncontrado; }
    public void setLocalEncontrado(String localEncontrado) { this.localEncontrado = localEncontrado; }

    public LocalDate getDataEncontrado() { return dataEncontrado; }
    public void setDataEncontrado(LocalDate dataEncontrado) { this.dataEncontrado = dataEncontrado; }

    public StatusObjeto getStatus() { return status; }
    public void setStatus(StatusObjeto status) { this.status = status; }

    public String getNomeProprietario() { return nomeProprietario; }
    public void setNomeProprietario(String nomeProprietario) { this.nomeProprietario = nomeProprietario; }

    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }
}
