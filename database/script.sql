-- =============================================================
-- Sistema Acadêmico — Biblioteca e Achados e Perdidos
-- Script de criação do banco (MySQL 8)
--
-- OBS.: a aplicação também cria as tabelas sozinha
-- (spring.jpa.hibernate.ddl-auto=update). Este script serve para
-- documentar o modelo e para quem preferir criar tudo pelo Workbench.
-- =============================================================

CREATE DATABASE IF NOT EXISTS sistema_academico
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE sistema_academico;

-- -------------------------------------------------------------
-- USUARIO: quem pode pegar livros emprestados
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    nome      VARCHAR(120) NOT NULL,
    matricula VARCHAR(30)  NOT NULL,
    email     VARCHAR(120) NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uk_usuario_matricula UNIQUE (matricula)
);

-- -------------------------------------------------------------
-- LIVRO: acervo da biblioteca
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS livro (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    titulo    VARCHAR(200) NOT NULL,
    autor     VARCHAR(150) NOT NULL,
    isbn      VARCHAR(20)  NOT NULL,
    categoria VARCHAR(80)  NULL,
    status    VARCHAR(20)  NOT NULL DEFAULT 'DISPONIVEL',
    CONSTRAINT pk_livro PRIMARY KEY (id),
    CONSTRAINT uk_livro_isbn UNIQUE (isbn),
    CONSTRAINT ck_livro_status CHECK (status IN ('DISPONIVEL', 'EMPRESTADO'))
);

-- -------------------------------------------------------------
-- EMPRESTIMO: histórico de empréstimos (registros nunca são apagados)
-- Relacionamentos: USUARIO 1 --- N EMPRESTIMO N --- 1 LIVRO
-- ON DELETE RESTRICT impede apagar usuário/livro que tenha histórico.
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS emprestimo (
    id              BIGINT      NOT NULL AUTO_INCREMENT,
    usuario_id      BIGINT      NOT NULL,
    livro_id        BIGINT      NOT NULL,
    data_emprestimo DATE        NOT NULL,
    data_devolucao  DATE        NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    CONSTRAINT pk_emprestimo PRIMARY KEY (id),
    CONSTRAINT fk_emprestimo_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_emprestimo_livro FOREIGN KEY (livro_id)
        REFERENCES livro (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_emprestimo_status CHECK (status IN ('ATIVO', 'DEVOLVIDO'))
);

CREATE INDEX idx_emprestimo_status ON emprestimo (status);

-- -------------------------------------------------------------
-- OBJETO_ENCONTRADO: achados e perdidos (entidade independente)
-- nome_proprietario e data_devolucao são preenchidos na devolução.
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS objeto_encontrado (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(120)  NOT NULL,
    descricao         VARCHAR(2000) NOT NULL,
    local_encontrado  VARCHAR(150)  NOT NULL,
    data_encontrado   DATE          NOT NULL,
    status            VARCHAR(20)   NOT NULL DEFAULT 'ENCONTRADO',
    nome_proprietario VARCHAR(120)  NULL,
    data_devolucao    DATE          NULL,
    CONSTRAINT pk_objeto_encontrado PRIMARY KEY (id),
    CONSTRAINT ck_objeto_status CHECK (status IN ('ENCONTRADO', 'EM_ESTOQUE', 'DEVOLVIDO'))
);

-- -------------------------------------------------------------
-- Dados de exemplo (opcional)
-- A aplicação já insere estes dados automaticamente se as tabelas
-- estiverem vazias; rode esta parte só se quiser popular manualmente.
-- -------------------------------------------------------------
INSERT INTO usuario (nome, matricula, email) VALUES
    ('Ana Souza',    '2024001', 'ana.souza@aluno.edu.br'),
    ('Bruno Lima',   '2024002', 'bruno.lima@aluno.edu.br'),
    ('Carla Mendes', '2024003', 'carla.mendes@aluno.edu.br');

INSERT INTO livro (titulo, autor, isbn, categoria, status) VALUES
    ('Engenharia de Software', 'Ian Sommerville',  '9788543024974', 'Computação',  'DISPONIVEL'),
    ('Código Limpo',           'Robert C. Martin', '9788576082675', 'Computação',  'DISPONIVEL'),
    ('Use a Cabeça! Java',     'Kathy Sierra',     '9788576081739', 'Programação', 'DISPONIVEL'),
    ('Dom Casmurro',           'Machado de Assis', '9788594318602', 'Literatura',  'DISPONIVEL');

INSERT INTO objeto_encontrado (nome, descricao, local_encontrado, data_encontrado, status) VALUES
    ('Mochila preta', 'Mochila preta, com chaveiro vermelho, contendo um caderno azul e estojo.',
     'Biblioteca - 2º andar', CURDATE() - INTERVAL 2 DAY, 'ENCONTRADO'),
    ('Garrafa térmica', 'Garrafa térmica prata de 500 ml com adesivo de banda na lateral.',
     'Cantina', CURDATE() - INTERVAL 1 DAY, 'ENCONTRADO');

-- -------------------------------------------------------------
-- Consultas úteis para a apresentação
-- -------------------------------------------------------------
-- Livros disponíveis:
--   SELECT * FROM livro WHERE status = 'DISPONIVEL';
-- Histórico completo de empréstimos:
--   SELECT e.id, u.nome, l.titulo, e.data_emprestimo, e.data_devolucao, e.status
--   FROM emprestimo e
--   JOIN usuario u ON u.id = e.usuario_id
--   JOIN livro   l ON l.id = e.livro_id
--   ORDER BY e.id DESC;
