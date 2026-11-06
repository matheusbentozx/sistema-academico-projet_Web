package br.edu.sistemaacademico.config;

import br.edu.sistemaacademico.model.Livro;
import br.edu.sistemaacademico.model.ObjetoEncontrado;
import br.edu.sistemaacademico.model.Usuario;
import br.edu.sistemaacademico.repository.LivroRepository;
import br.edu.sistemaacademico.repository.ObjetoEncontradoRepository;
import br.edu.sistemaacademico.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Popula o banco com dados de exemplo na PRIMEIRA execução (tabelas vazias),
 * para que a apresentação já comece com informações na tela.
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final LivroRepository livroRepository;
    private final ObjetoEncontradoRepository objetoRepository;

    public DadosIniciais(UsuarioRepository usuarioRepository,
                         LivroRepository livroRepository,
                         ObjetoEncontradoRepository objetoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.livroRepository = livroRepository;
        this.objetoRepository = objetoRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.saveAll(List.of(
                    new Usuario("Ana Souza", "2024001", "ana.souza@aluno.edu.br"),
                    new Usuario("Bruno Lima", "2024002", "bruno.lima@aluno.edu.br"),
                    new Usuario("Carla Mendes", "2024003", "carla.mendes@aluno.edu.br")
            ));
        }

        if (livroRepository.count() == 0) {
            livroRepository.saveAll(List.of(
                    new Livro("Engenharia de Software", "Ian Sommerville", "9788543024974", "Computação"),
                    new Livro("Código Limpo", "Robert C. Martin", "9788576082675", "Computação"),
                    new Livro("Use a Cabeça! Java", "Kathy Sierra", "9788576081739", "Programação"),
                    new Livro("Dom Casmurro", "Machado de Assis", "9788594318602", "Literatura")
            ));
        }

        if (objetoRepository.count() == 0) {
            objetoRepository.saveAll(List.of(
                    new ObjetoEncontrado("Mochila preta",
                            "Mochila preta, com chaveiro vermelho, contendo um caderno azul e estojo.",
                            "Biblioteca - 2º andar", LocalDate.now().minusDays(2)),
                    new ObjetoEncontrado("Garrafa térmica",
                            "Garrafa térmica prata de 500 ml com adesivo de banda na lateral.",
                            "Cantina", LocalDate.now().minusDays(1))
            ));
        }
    }
}
