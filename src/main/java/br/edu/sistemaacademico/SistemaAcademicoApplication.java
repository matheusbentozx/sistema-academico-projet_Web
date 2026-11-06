package br.edu.sistemaacademico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicação.
 * Ao executar, o Spring Boot sobe o servidor em http://localhost:8080
 * servindo a API REST (/api/...) e o frontend (pasta resources/static).
 */
@SpringBootApplication
public class SistemaAcademicoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaAcademicoApplication.class, args);
    }
}
