# TaskManager API – Gerenciador de Tarefas (Versão Spring Boot)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-f5f5f5?style=for-the-badge&logo=junit5&logoColor=dc524a)


## Contextualização

O **TaskManager API** é uma API REST que utiliza **Spring Boot**, permitindo criar, listar, editar e remover tarefas, com persistência real em banco de dados **PostgreSQL**, validação de dados de entrada e tratamento centralizado de exceções.

Inicialmente sendo concebido como meu projeto para entregar para o IRede, o TaskManager API foi desenvolvido com o intuito de desafiar e praticar conceitos de backend, ao mesmo tempo que pratico tecnologias como Springboot, Banco de dados e testes unitários e de integração. Entretanto, ao perceber que tangenciou muito do que o projeto solicitava, ao ponto de estar faltando coisas que eram exigidas, decidi parar no meio do caminho para focar em uma versão mais simples e coerente com que o projeto solicita, mantendo esse projeto como um "extra" e focando em desenvolver uma versão menor mas que eu consigo treinar e refinar os conceitos mais básicos.

O projeto foi desenvolvido como parte do Projeto Capacita IRede, na trilha de desenvolvimento java. Ele aplica conceitos de arquitetura em camadas (Controller → Service → Repository), tratamento global de erros via `@RestControllerAdvice`, validação com Bean Validation (`jakarta.validation`), e testes unitários e de integração com JUnit, Mockito e MockMvc.


## Funcionalidades

A API expõe endpoints REST para:

- Criar uma nova tarefa
- Criar múltiplas tarefas de uma vez
- Listar todas as tarefas
- Editar uma tarefa existente
- Remover uma tarefa

| Tecnologia               | Uso no projeto                                    |
|---------------------------|----------------------------------------------------|
| Java                      | Linguagem principal                                |
| Spring Boot               | Framework base da aplicação                        |
| Spring Web (MVC)          | Criação dos endpoints REST                         |
| Spring Data JPA           | Persistência e acesso ao banco de dados            |
| PostgreSQL                | Banco de dados relacional                          |
| Hibernate                 | Implementação JPA (ORM)                            |
| JUnit 5                   | Framework de testes                                |
| Mockito                   | Mock de dependências em testes unitários           |
| MockMvc                   | Testes de integração dos endpoints HTTP            |


## Requisitos

| Requisito                          | Descrição                                                                          | Status    |
|-------------------------------------|--------------------------------------------------------------------------------------|-----------|
| Entidade `Tarefas`                  | Modelo JPA com título, descrição, tipo e prioridade                                  | Concluído |
| Validação de campos                 | Campos obrigatórios validados com `@NotBlank`                                        | Concluído |
| Repositório `TarefasRepo`           | Interface `JpaRepository` para acesso ao banco                                       | Concluído |
| Camada de serviço `TarefaServices`  | Lógica de negócio: salvar, listar, editar, deletar                                   | Concluído |
| Endpoint de criação                 | `POST /adicionarTarefa`                                                              | Concluído |
| Endpoint de criação em lote         | `POST /adicionarListaTarefa`                                                         | Concluído |
| Endpoint de listagem                | `GET /listagem`                                                                       | Concluído |
| Endpoint de edição                  | `PUT /editar/{id}`                                                                    | Concluído |
| Endpoint de remoção                 | `DELETE /delete/{id}`                                                                 | Concluído |
| Tratamento global de exceções       | `GlobalExceptionHandler` com `@RestControllerAdvice`                                 | Concluído |
| Resposta de erro padronizada        | Classe abstrata `ErrorResponse`, especializada em `ValidationErrorResponse` e `RuntimeErrorResponse` | Concluído |
| Exceção de validação                | Retorno HTTP 400 para campos inválidos                                               | Concluído |
| Exceção de regra de negócio         | Retorno HTTP 500 para tarefa não encontrada                                          | Concluído |
| Testes unitários de serviço         | Cobertura de `TarefaServices` com Mockito                                            | Concluído |
| Testes de integração                | Cobertura dos endpoints com MockMvc                                                  | Concluído |

## Estrutura de Arquivos
```
src/
└── main/
    ├── java/
    │   └── com/
    │       └── example/
    │           └── demo/
    │               ├── DemoApplication.java              # Classe principal (Spring Boot)
    │               ├── Tarefas.java                       # Entidade JPA
    │               ├── TarefasRepo.java                   # Repositório (Spring Data JPA)
    │               ├── Services/
    │               │   └── TarefaServices.java            # Regras de negócio
    │               ├── TarefaControllers/
    │               │   ├── AdicionarController.java       # POST /adicionarTarefa
    │               │   ├── AdicionarListaController.java  # POST /adicionarListaTarefa
    │               │   ├── EditarController.java          # PUT /editar/{id}
    │               │   ├── DeletarController.java         # DELETE /delete/{id}
    │               │   └── VerTudoController.java         # GET /listagem
    │               └── exception/
    │                   ├── ErrorResponse.java             # Classe abstrata base de resposta de erro
    │                   ├── ValidationErrorResponse.java   # Erro de validação (HTTP 400)
    │                   ├── RuntimeErrorResponse.java      # Erro de negócio (HTTP 500)
    │                   └── GlobalExceptionHandler.java    # Tratamento global de exceções
    └── resources/
        └── application.properties                        # Configuração do banco de dados
test/
└── java/
    └── com/
        └── example/
            └── demo/
                ├── Services/
                │   └── TarefaServicesTest.java            # Testes unitários da camada de serviço
                └── IntegrationTest/
                    └── TarefaIntegrationTest.java          # Testes de integração dos endpoints
```
## Como Rodar

### Pré-requisitos
- JDK 17 ou superior
- Maven (ou o wrapper `./mvnw` incluso no projeto)
- PostgreSQL instalado e rodando localmente
- Um banco de dados criado com o nome `tarefas`

### Configuração do banco

O Hibernate cria e atualiza as tabelas automaticamente (`spring.jpa.hibernate.ddl-auto=update`), então você não precisa escrever nenhum `CREATE TABLE`. **Porém, o banco de dados em si precisa existir antes de rodar a aplicação** — o PostgreSQL não cria isso sozinho.

Crie o banco `tarefas` usando uma das opções abaixo:

**Via terminal:**
```bash
psql -U postgres -c "CREATE DATABASE tarefas;"
```

**Via pgAdmin4 (opcional, caso prefira interface gráfica):**
Databases → botão direito → Create → Database... → nomeie como `tarefas`.

Depois, ajuste as credenciais em `src/main/resources/application.properties` conforme seu ambiente:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tarefas
spring.datasource.username=postgres
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```


### Rodando pela IDE
1. Importe o projeto como um projeto Maven.
2. Localize a classe `DemoApplication.java`.
3. Execute como aplicação Spring Boot (Run As → Spring Boot App).

### Rodando via terminal
```
./mvnw spring-boot:run
```
A aplicação sobe por padrão em `http://localhost:8080`.

### Endpoints disponíveis

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/adicionarTarefa` | Cria uma nova tarefa |
| POST | `/adicionarListaTarefa` | Cria múltiplas tarefas |
| GET | `/listagem` | Lista todas as tarefas |
| PUT | `/editar/{id}` | Edita uma tarefa existente |
| DELETE | `/delete/{id}` | Remove uma tarefa |

### Rodando os testes
```
./mvnw test
```
