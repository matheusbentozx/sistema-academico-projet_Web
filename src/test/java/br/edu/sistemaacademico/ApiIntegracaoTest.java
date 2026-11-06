package br.edu.sistemaacademico;

import br.edu.sistemaacademico.model.Livro;
import br.edu.sistemaacademico.model.Usuario;
import br.edu.sistemaacademico.repository.LivroRepository;
import br.edu.sistemaacademico.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integração: sobe a aplicação inteira (com banco H2 em memória)
 * e chama a API REST de verdade, como o frontend faz.
 * @Transactional desfaz as alterações ao fim de cada teste.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@Transactional
class ApiIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("GET /api/livros responde 200 com uma lista em JSON")
    void listarLivrosRetornaJson() throws Exception {
        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].titulo").exists())
                .andExpect(jsonPath("$[0].status").exists());
    }

    @Test
    @DisplayName("Emprestar o mesmo livro duas vezes: 201 e depois 409")
    void emprestarDuasVezesRetornaConflito() throws Exception {
        Livro livro = livroRepository.save(new Livro("Livro de Teste", "Autor Teste", "TESTE-0001", "Teste"));
        Usuario usuario = usuarioRepository.save(new Usuario("Aluno Teste", "TESTE-0001", "teste@aluno.edu.br"));
        String corpo = "{\"livroId\": " + livro.getId() + ", \"usuarioId\": " + usuario.getId() + "}";

        mockMvc.perform(post("/api/emprestimos").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andExpect(jsonPath("$.livro.status").value("EMPRESTADO"));

        mockMvc.perform(post("/api/emprestimos").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    @DisplayName("GET de livro inexistente responde 404")
    void livroInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/livros/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").exists());
    }
}
