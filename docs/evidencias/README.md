# Evidências de execução e testes

Prints que comprovam o funcionamento do sistema (critério D4).
Salve as imagens nesta pasta com os nomes abaixo.

| Arquivo | O que mostra |
|---|---|
| `01-testes-verdes.png` | Painel **Testing** do VS Code com os 13 testes aprovados |
| `02-terminal-started.png` | Terminal com `Started SistemaAcademicoApplication` |
| `03-api-livros-json.png` | Navegador em `http://localhost:8080/api/livros` exibindo o JSON |
| `04-painel.png` | Tela Painel com os indicadores |
| `05-biblioteca.png` | Tela Biblioteca com a lista de livros |
| `06-emprestimo.png` | Tela Empréstimos com um empréstimo ativo e o histórico |
| `07-achados.png` | Tela Achados e Perdidos com objetos em status diferentes |
| `08-erro-409.png` | Aviso de regra de negócio (ex.: excluir livro com histórico) |
| `09-network-fetch.png` | F12 → Network mostrando as chamadas `fetch()` para `/api` |

## Resultado dos casos de teste manuais

Marque cada caso após executá-lo (lista completa em `../documentacao-tecnica.md`, seção 8).

| Caso | Descrição | Resultado |
|---|---|---|
| CT01 | Cadastrar livro válido | ☐ OK |
| CT03 | ISBN duplicado → 409 | ☐ OK |
| CT06 | Registrar empréstimo → livro Emprestado | ☐ OK |
| CT07 | Emprestar livro já emprestado → 409 | ☐ OK |
| CT09 | Registrar devolução → livro Disponível | ☐ OK |
| CT11 | Excluir livro com histórico → bloqueado | ☐ OK |
| CT13 | Registrar objeto → Encontrado | ☐ OK |
| CT14 | Mover objeto para Em estoque | ☐ OK |
| CT15 | Devolver objeto ao dono → Devolvido | ☐ OK |
| CT18 | Painel atualizado após as operações | ☐ OK |

Executado por: __________________  Data: ____/____/2026
