# TaskManager API – Gerenciador de Tarefas (Versão Spring Boot)

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-f5f5f5?style=for-the-badge&logo=junit5&logoColor=dc524a)


## Contextualização

O **TaskManager API** é uma API REST que utiliza **Spring Boot**, permitindo criar, listar, editar e remover tarefas, com persistência real em banco de dados **PostgreSQL**, validação de dados de entrada e tratamento centralizado de exceções.Ela é exposta de duas formas: como **API REST** e como interface gráfica **JavaFX**, as duas usando a mesma camada de serviço.

Inicialmente sendo concebido como meu projeto para entregar para o IRede, o TaskManager API foi desenvolvido com o intuito de desafiar e praticar conceitos de backend, ao mesmo tempo que pratico tecnologias como Springboot, Banco de dados e testes unitários e de integração. Entretanto, ao perceber que tangenciou muito do que o projeto solicitava, ao ponto de estar faltando coisas que eram exigidas, decidi parar no meio do caminho para focar em uma versão mais simples e coerente com que o projeto solicita, mantendo esse projeto como um "extra" e focando em desenvolver uma versão menor mas que eu consigo treinar e refinar os conceitos mais básicos.

O projeto foi desenvolvido como parte do Projeto Capacita IRede, na trilha de desenvolvimento java. Ele aplica conceitos de arquitetura em camadas (Controller → Service → Repository), tratamento global de erros via `@RestControllerAdvice`, validação com Bean Validation (`jakarta.validation`), e testes unitários e de integração com JUnit, Mockito e MockMvc.

Evolução do projeto (módulo intermediário)

Nesta etapa o projeto evoluiu de uma API para uma aplicação com interface gráfica, conforme a tarefa da Unidade 5 (Capítulo 1): Generics, JavaFX (com FXML, Scene Builder e CSS), acesso a banco de dados e testes com JUnit. Como o projeto já era construído sobre Spring Boot, o acesso a dados continuou com Spring Data JPA, o que foi alinhado previamente com o professor. A seção Requisitos do módulo intermediário mostra, item por item, onde cada exigência aparece no código e o que o Spring já faz por baixo.


## Funcionalidades

**API REST**
- Criar uma nova tarefa
- Criar múltiplas tarefas de uma vez
- Listar todas as tarefas e buscar uma por id
- Editar uma tarefa existente
- Remover uma tarefa (ou todas)

**Interface gráfica (JavaFX)**
- Tela principal com a tabela de tarefas (título, descrição, tipo e prioridade)
- Tela de cadastro e edição de tarefas, aberta como janela modal
- Confirmação antes de remover uma tarefa
- Validação dos campos obrigatórios antes de salvar

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
| JavaFX                    | Interface gráfica                                   |
| FXML + Scene Builder      | Telas declaradas em arquivos `.fxml`                |
| CSS                       | Estilização das telas                               

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


## Requisitos do módulo intermediário

### 1. Generics

| Exigência                              | Onde está no projeto                                                                         | Status    |
|----------------------------------------|----------------------------------------------------------------------------------------------|-----------|
| Classe genérica para listas de tarefas | `ListaTarefasGenerics<T extends Tarefas>` (pacote `model`): adicionar, remover por id, limpar, ordenar e listar | Concluído |
| Métodos genéricos                      | `adicionar` e `adicionarLista` em `TarefaServices`, declarados com `<T extends Tarefas>`     | Concluído |
| Generics no acesso a dados             | `TarefasRepo extends JpaRepository<Tarefas, Long>`                                           | Concluído |
| Curinga `?`                            | `ListaTarefasGenerics.contar(List<?> lista)`: conta elementos de uma lista de qualquer tipo  | Concluído |
| Curinga `? extends T`                  | `adicionarTodos(Collection<? extends T> origem)`: lê de uma coleção de `T` ou de subtipos    | Concluído |
| Curinga `? super T`                    | `ordenar(Comparator<? super T> criterio)`: aceita um critério escrito para `T` ou para um supertipo | Concluído |

```java
public class ListaTarefasGenerics<T extends Tarefas> {

    public void adicionarTodos(Collection<? extends T> origem) { ... }   // ? extends T
    public void ordenar(Comparator<? super T> criterio) { ... }          // ? super T
    public static int contar(List<?> lista) { ... }                      // ?
}
```

A tela principal usa essa classe para manter a lista exibida na tabela:

```java
lista.limpar();
lista.adicionarTodos(service.listar());
lista.ordenar(Comparator.comparing(Tarefas::getTitulo));
```

**O que o limite `T extends Tarefas` garante.** A lista aceita `Tarefas` e qualquer subclasse dela, e rejeita qualquer outro tipo em tempo de compilação (`ListaTarefasGenerics<String>` não compila). Por causa do limite, a classe consegue chamar `getId()` dentro de `removerPorId`.

**Métodos genéricos no serviço.**

```java
public <T extends Tarefas> T adicionar(T t) {
    return tarefaRepo.save(t);
}

public <T extends Tarefas> List<T> adicionarLista(List<T> tarefas) {
    return tarefaRepo.saveAll(tarefas);
}
```

Esses métodos devolvem o mesmo tipo que receberam, sem cast. Para as rotas e as telas nada muda: em tempo de execução o generics é apagado (type erasure), então o Spring, o Jackson e o JPA não percebem a diferença.

**Por que a entidade `Tarefas` não é genérica.** O Hibernate precisa conhecer o tipo concreto de cada coluna da tabela. Por isso a versão genérica é uma classe comum separada (`ListaTarefasGenerics`), sem `@Entity`, e a entidade fica como está.

**O Spring já usa generics.** `JpaRepository<Tarefas, Long>` informa a entidade e o tipo do id, e é por isso que `save`, `findAll` e `findById` já devolvem `Tarefas`, sem cast.

### 2. JavaFX – interface gráfica

| Exigência                                   | Onde está no projeto                                                                  | Status    |
|---------------------------------------------|---------------------------------------------------------------------------------------|-----------|
| Classe que estende `Application` + `start(Stage)` | `MainApp` (pacote `view`); o `main` fica em `Launcher`                           | Concluído |
| Troca do terminal por interface visual      | Telas JavaFX chamando a camada de serviço                                              | Concluído |
| Tela principal                              | `TelaPrincipal.fxml`: tabela de tarefas com os botões Nova tarefa, Editar e Remover    | Concluído |
| Tela de cadastro/edição                     | `TelaCadastro.fxml`, aberta como janela modal                                          | Concluído |
| Confirmação ao remover                      | `Alert` do tipo `CONFIRMATION` antes de remover                                        | Concluído |
| Componentes: Label, TextField, TextArea, Button, TableView | Usados nas duas telas                                                  | Concluído |
| Componente CheckBox / marcar tarefa         | A definir                                                                              | Pendente  |
| Layouts: VBox, HBox, GridPane, BorderPane   | `BorderPane` + `HBox` na principal; `VBox` + `GridPane` + `HBox` no cadastro           | Concluído |
| Eventos                                     | `onAction` nos botões, ligado a métodos `@FXML` dos controllers                        | Concluído |

**Como o JavaFX se encaixa no Spring.** O `Launcher` chama `Application.launch(MainApp.class)`. O `MainApp` sobe o contexto do Spring no `init()` e carrega as telas com `loader.setControllerFactory(context::getBean)`. Com isso, os controllers do JavaFX são beans do Spring e recebem o `TarefaServices` por injeção, chamando a camada de serviço diretamente, sem passar por HTTP.

**Por que existe a classe `Launcher`.** Quando a classe do `main` estende `Application`, o Java exige o JavaFX no *module path* e falha com `JavaFX runtime components are missing`. Como o JavaFX chega pelo Maven (classpath), o `main` fica numa classe comum que apenas chama `Application.launch`.

### 3. FXML e Scene Builder

| Exigência                                  | Onde está no projeto                                                              | Status    |
|--------------------------------------------|-----------------------------------------------------------------------------------|-----------|
| Telas em `.fxml`                           | `src/main/resources/fxml/TelaPrincipal.fxml` e `TelaCadastro.fxml`                | Concluído |
| Criação visual com Scene Builder           | Os arquivos abrem direto no Scene Builder                                         | Concluído |
| Lógica associada por controllers `@FXML`   | `TelaPrincipalController` e `TelaCadastroController` (pacote `view.controller`)   | Concluído |
| Estilização com `.css`                     | `src/main/resources/css/style.css`                                                | Concluído |

O botão de salvar do cadastro, por exemplo, liga o visual à lógica assim:

```xml
<Button fx:id="btnSalvar" text="Salvar" onAction="#onSalvar" styleClass="botao-primario" />
```

```java
@FXML
private void onSalvar() { ... }
```

### 4. Banco de dados (no lugar do JDBC puro)

O enunciado descreve o acesso com JDBC puro. Como o projeto já usa Spring Data JPA (alinhado com o professor), cada item continua atendido, mas pelo Spring:

| Exigência do enunciado                   | Como foi atendida                                                                                          |
|------------------------------------------|------------------------------------------------------------------------------------------------------------|
| Banco relacional                         | PostgreSQL, configurado em `application.properties`                                                        |
| Tabela de tarefas                        | A entidade `Tarefas` gera a tabela automaticamente (`ddl-auto=update`), com id, título, descrição, tipo e prioridade |
| `DriverManager` e `Conexao.java`         | O Spring Boot cria o `DataSource` (pool de conexões HikariCP) a partir do `application.properties`         |
| `PreparedStatement` e `ResultSet`        | O Hibernate os usa por baixo dos métodos do `TarefasRepo` (`save`, `findAll`, `findById`, `deleteById`)    |
| `commit()` e `rollback()`                | Os métodos do `JpaRepository` rodam em transação do Spring: commit se der certo, rollback se houver exceção |
| `TarefaDAO` isolando a persistência      | `TarefasRepo` (repositório) usado apenas pela camada `TarefaServices`                                      |

**SQL Injection.** A proteção vem dos próprios métodos de acesso a dados. O Spring Data JPA envia os valores como parâmetros vinculados, que é o mesmo princípio do `PreparedStatement`: o texto digitado é sempre tratado como dado, nunca como parte do comando SQL. O projeto não monta SQL concatenando texto do usuário em nenhum ponto. Um título como `'; DROP TABLE Tarefas; --` é salvo como texto comum.

**Validação de dados.** O Spring também já faz a validação: os campos do modelo têm `@NotBlank`, e as rotas usam `@Valid` no corpo da requisição. Se algum campo vier vazio, o Spring rejeita antes de chegar ao serviço, e o `GlobalExceptionHandler` devolve HTTP 400 com as mensagens dos campos. Na interface JavaFX, o controller do cadastro confere os mesmos campos obrigatórios (com as mesmas mensagens) antes de chamar o serviço, porque o `@Valid` só atua nas rotas.

### 5. Testes com JUnit

| Exigência                       | Onde está no projeto                                                                          | Status         |
|---------------------------------|-----------------------------------------------------------------------------------------------|----------------|
| Testar criação de tarefa        | `SalvarSucess` e `SalvarListaSucess` (serviço) e `deveCriarTarefa` (integração)               | Concluído      |
| Testar conclusão de tarefa      | `ConcluirTarefaSucess`: concluir é editar a tarefa, mudando o `tipo` para "Concluída"          | Concluído      |
| Validação de dados              | `deveFalharQuandoCamposInvalidos`: campos em branco retornam HTTP 400                          | Concluído      |
| Testar DAO com SQLite em memória | Não se aplica: a persistência é o `TarefasRepo` (Spring Data JPA), sem métodos próprios. Os métodos do repositório são exercitados pelo teste de integração, que grava e apaga tarefas no banco | Não se aplica  |
| `@Test`, `@BeforeEach`          | `@Test` e `@BeforeEach` em `TarefaServicesTest`; `@Test` em `TarefaIntegrationTest`. O `@AfterEach` não foi necessário: o Mockito recria os mocks a cada teste e o `@Transactional` desfaz as gravações do teste de integração | Concluído |
| Testes em pacotes separados     | `Services` (unitários) e `IntegrationTest` (integração)                                        | Concluído      |

Os testes de serviço usam Mockito para simular o repositório (sem banco), e verificam as regras (por exemplo, a mensagem "Tarefa não encontrada"). Os testes de integração sobem o contexto completo e chamam as rotas com MockMvc.


```
## Estrutura de Arquivos
```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── DemoApplication.java              # Classe principal (Spring Boot)
│   │   ├── controller/
│   │   │   └── TarefaController.java         # Rotas REST
│   │   ├── service/
│   │   │   └── TarefaServices.java           # Regras de negócio
│   │   ├── repository/
│   │   │   └── TarefasRepo.java              # Repositório (Spring Data JPA)
│   │   ├── model/
│   │   │   ├── Tarefas.java                  # Entidade JPA
│   │   │   └── ListaTarefasGenerics.java     # Lista genérica de tarefas (curingas)
│   │   ├── exception/
│   │   │   ├── ErrorResponse.java            # Classe abstrata base de resposta de erro
│   │   │   ├── ValidationErrorResponse.java  # Erro de validação (HTTP 400)
│   │   │   ├── RuntimeErrorResponse.java     # Erro de negócio (HTTP 500)
│   │   │   └── GlobalExceptionHandler.java   # Tratamento global de exceções
│   │   └── view/
│   │       ├── Launcher.java                 # Classe executada (chama o JavaFX)
│   │       ├── MainApp.java                  # extends Application: sobe o Spring e abre a janela
│   │       └── controller/
│   │           ├── TelaPrincipalController.java
│   │           └── TelaCadastroController.java
│   └── resources/
│       ├── application.properties            # Configuração do banco de dados
│       ├── fxml/
│       │   ├── TelaPrincipal.fxml
│       │   └── TelaCadastro.fxml
│       └── css/
│           └── style.css
└── test/java/com/example/demo/
    ├── Services/
    │   └── TarefaServicesTest.java           # Testes unitários da camada de serviço
    └── IntegrationTest/
        └── TarefaIntegrationTest.java        # Testes de integração dos endpoints
```

## Como Rodar

### Pré-requisitos
- JDK 21
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

### Dependências do JavaFX

O `pom.xml` inclui o `javafx-controls` e o `javafx-fxml` (versão 21.x), então o Maven baixa tudo automaticamente.

### Rodando a interface gráfica
1. Importe o projeto como um projeto Maven.
2. Localize a classe `Launcher.java` (pacote `view`).
3. Execute com Run As → Java Application. ou:
```
./mvnw spring-boot:run -Dspring-boot.run.main-class=com.example.demo.view.Launcher
```

Execute sempre pela `Launcher`, e não pela `MainApp` ou pela `DemoApplication`: a `MainApp` dá o erro `JavaFX runtime components are missing` e a `DemoApplication` sobe só a API, sem a janela.

### Rodando só a API
Execute a `DemoApplication.java` (Run As → Spring Boot App) ou:
```
./mvnw spring-boot:run
```
A aplicação sobe por padrão em `http://localhost:8080`.

### Endpoints disponíveis

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/adicionar` | Cria uma nova tarefa |
| POST | `/adicionarLista` | Cria múltiplas tarefas |
| GET | `/listar` | Lista todas as tarefas |
| GET | `/listarPorId/{id}` | Busca uma tarefa pelo id |
| PUT | `/editar/{id}` | Edita uma tarefa existente |
| DELETE | `/deletar/{id}` | Remove uma tarefa |
| DELETE | `/deletarTudo` | Remove todas as tarefas |

### Rodando os testes
```
./mvnw test
```