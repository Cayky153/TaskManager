# TaskManager API

![Java](https://img.shields.io/badge/java_21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring_boot_4-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Maven](https://img.shields.io/badge/maven-%23C71A36.svg?style=for-the-badge&logo=apachemaven&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres_17-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/docker_compose-%232496ED.svg?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github_actions_(CI)-%232088FF.svg?style=for-the-badge&logo=githubactions&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-f5f5f5?style=for-the-badge&logo=junit5&logoColor=dc524a)
![SonarQube](https://img.shields.io/badge/sonarqube-4E9BCD?style=for-the-badge&logo=sonarqubeserver&logoColor=white)
![Swagger](https://img.shields.io/badge/openapi-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Prometheus](https://img.shields.io/badge/prometheus_metrics-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)
![k6](https://img.shields.io/badge/k6-7D64FF?style=for-the-badge&logo=k6&logoColor=white)
![JavaFX](https://img.shields.io/badge/javafx_(complemento)-%23000000.svg?style=for-the-badge&logo=openjdk&logoColor=white)

## 1. O que o sistema faz

Nível selecionado: **NIVEL B**

O **TaskManager API** é uma API REST em Spring Boot, desenvolvida individualmente, para criar, listar, editar e remover tarefas, com persistência em PostgreSQL, validação de entrada e respostas de erro padronizadas. O foco é o **backend** (qualidade, testes, operação e documentação); a interface JavaFX é apenas um complemento que reaproveita a mesma camada de serviço.

> **Origem e foco.** O projeto foi desenvolvido individualmente no contexto da disciplina de Engenharia de Software e posteriormente incrementado com práticas de engenharia: CI, cobertura mínima, métricas, logs estruturados e documentação. O frontend (JavaFX) foi desenvolvido como complemento exigido na entrega da disciplina. Por isso, os testes, a cobertura e a análise de qualidade priorizam o backend, e a camada `view` fica de fora da cobertura (decisão registrada abaixo).

## 2. Como rodar localmente

**Pré-requisitos:** Docker com Docker Compose. (Para rodar sem Docker: JDK 21, Maven e um PostgreSQL local com o banco `tarefas`.)

```bash
# 1. clone e entre na pasta
git clone https://github.com/Cayky153/TaskManager.git
cd TaskManager

# 2. crie o .env a partir do exemplo e troque a senha
cp .env.example .env        # Windows (CMD): copy .env.example .env

# 3. suba banco e API
docker compose up --build
```

Quando o log mostrar `Started DemoApplication`, a API estará disponível em `http://localhost:8080`:

| O quê | URL |
|---|---|
| Documentação interativa (Swagger UI) | http://localhost:8080/swagger-ui.html |
| Especificação OpenAPI | http://localhost:8080/v3/api-docs (exportada em [`openapi.yaml`](openapi.yaml)) |
| Saúde | http://localhost:8080/actuator/health |
| Métricas (Prometheus) | http://localhost:8080/actuator/prometheus |

Para recomeçar do zero (apagando o volume do banco): `docker compose down -v`.

**Sem Docker (API direto pelo Maven):** com o PostgreSQL em execução e as variáveis da seção 4 definidas no terminal:

```bash
./mvnw spring-boot:run
```

**Interface JavaFX (opcional):** execute a classe `Launcher` (pacote `view`) pela IDE, com as mesmas variáveis de ambiente, ou:

```bash
./mvnw spring-boot:run -Dspring-boot.run.main-class=com.example.demo.view.Launcher
```

A execução deve ser feita pela `Launcher`: a `App` falha com `JavaFX runtime components are missing`, e a `DemoApplication` inicia apenas a API, sem abrir a janela.

> O manual das rotas não fica mais neste README: ele é gerado a partir do código pelo OpenAPI (links acima), evitando documentação desatualizada.

## 3. Como rodar os testes

```bash
./mvnw clean verify
```

Esse comando executa, em ordem: Checkstyle (fase `validate`), testes unitários e de integração, empacotamento e a **checagem de cobertura mínima** do JaCoCo. Se qualquer etapa falhar, o build também falha. Esse é o mesmo comando executado pelo CI.

- **Testes unitários** (`Services/`): `TarefaServices` com Mockito, cobrindo sucesso e, onde há caminho de erro, também falha.
- **Testes de integração** (`IntegrationTest/`): todas as rotas via MockMvc, com contexto Spring completo e `@Transactional`.
- **Banco nos testes:** H2 em memória (`src/test/resources/application.properties`). Os testes e o CI **não precisam de PostgreSQL nem de senha**.
- **Relatório de cobertura:** `target/site/jacoco/index.html`.

**Como esse requisito é atendido e justificado**

- *Cobertura mínima declarada e respeitada:* o mínimo está no `pom.xml` (JaCoCo, regra `check` na fase `verify`) e o build falha se ficar abaixo do limite. A cobertura exclui `com.example.demo.view/**` (JavaFX) e `DemoApplication` (bootstrap). A exclusão é deliberada: a UI é um complemento sem regra de negócio, e testar JavaFX exigiria TestFX, com testes lentos e frágeis para pouco retorno. Excluir somente o que não tem lógica é legítimo; excluir serviços ou exceções para aumentar o número não seria.
- *Lint, em duas camadas com papéis diferentes:*
  - **Checkstyle no CI** é o lint **estético**: verifica convenções de código (imports não usados ou duplicados, `import x.*`, chaves obrigatórias, `;` solto). Falhas interrompem o pipeline.
  - **SonarQube** é o lint de **qualidade e segurança**, executado **localmente** (não está integrado ao CI). O relatório inicial apontou 4 issues: injeção de dependência por campo (`@Autowired`) em duas classes e entidade JPA exposta diretamente no `@RequestBody` (risco de *mass assignment*, regra java:S4684) em duas rotas. As duas causas foram corrigidas no código: injeção por construtor e DTO `TarefaRequest`. Os prints do relatório estão em [`provas/`](provas/) (`sonar1.png` a `sonar5.png`).
  - Essas ferramentas não são equivalentes ao ESLint isoladamente: em Java, estilo (Checkstyle), qualidade (Sonar/PMD) e bugs (SpotBugs) são áreas distintas, e a combinação foi escolhida considerando o custo-benefício.
- *Segredos fora do repositório:* `.env` no `.gitignore`, `.env.example` versionado, `application.properties` lendo variáveis de ambiente sem senha padrão. Uma verificação com **gitleaks** no histórico (14 commits na data da verificação) não encontrou vazamentos, e uma busca direta (`git log -S`) pela senha antiga também retornou vazio.

## 4. Variáveis de ambiente

Copie `.env.example` para `.env`. Os nomes são fixos; os valores devem ser definidos conforme o ambiente.

| Variável | Obrigatória | Quem usa | Descrição |
|---|---|---|---|
| `POSTGRES_DB` | Sim (Compose) | imagem `postgres` | Nome do banco criado (`tarefas`) |
| `POSTGRES_USER` | Sim (Compose) | imagem `postgres` | Usuário do banco |
| `POSTGRES_PASSWORD` | Sim (Compose) | imagem `postgres` | Senha do banco |
| `SPRING_DATASOURCE_URL` | Só fora do Compose | Spring | URL JDBC (dentro do Compose usa o host `db`) |
| `SPRING_DATASOURCE_USERNAME` | Só fora do Compose | Spring | Usuário |
| `SPRING_DATASOURCE_PASSWORD` | **Sim, sem padrão** | Spring | Senha. Sem ela a API não inicia, de propósito |
| `DB_PORT`, `APP_PORT` | Não | referência de portas | Portas padrão 5432 e 8080 |

O PostgreSQL só lê `POSTGRES_*` na **primeira criação do volume**. Se a senha for alterada posteriormente, será necessário executar `docker compose down -v` para recriar o volume.

## 5. Decisões arquiteturais

Os ADRs ficam nesta pasta (`docs/`), no formato MADR.

- [ADR 0001: Template Method na hierarquia de respostas de erro](ADR.MD)

**Resumo da decisão defendida:** toda resposta de erro da API segue o mesmo molde (`ErrorResponse`, classe abstrata com `timestamp`, `status`, `error` e `message`), e cada tipo de erro define apenas o que varia (`getStatus()` e `getTipoErro()`). Assim, a API devolve sempre o mesmo formato de erro, e a criação de um novo tipo de erro exige apenas uma subclasse, sem alterações nas existentes.

Outras decisões registradas:

| Decisão | Motivo |
|---|---|
| Camadas Controller → Service → Repository | Rotas finas, regra em um lugar, serviço testável sem Spring |
| DTO (`TarefaRequest`) na entrada | Evita *mass assignment* e desacopla API de banco |
| Injeção por construtor | Dependências explícitas, campos `final`, teste sem reflexão |
| `TarefaNaoEncontradaException` → HTTP 404 | 500 indica erro do servidor, não de quem chamou |
| Testes com H2, aplicação com PostgreSQL | CI sem infraestrutura e testes rápidos |
| JavaFX fora da cobertura | UI é complemento; foco da entrega é o backend |

### Operação: logs, métrica e SLO

- **Logs estruturados:** `logging.structured.format.console=logstash` (recurso nativo do Spring Boot). Cada evento é emitido como uma linha JSON, pronta para ser indexada por qualquer agregador ([prova](provas/logs-estruturados.png)).
- **Métricas:** Spring Boot Actuator + Micrometer, expostas em `/actuator/prometheus` ([prova](provas/prometheus.png)). Duas fontes: a métrica nativa `http_server_requests_seconds` (contagem e latência por rota, com histograma) e a métrica própria **`tarefas_criadas_total`** (contador no `TarefaServices`, incrementado a cada criação, via REST ou JavaFX). Por ser um contador, ela mostra tarefas criadas desde a inicialização da API e zera no restart; não representa o total existente no banco.
- **SLO:** 95% das requisições a `POST /adicionar` respondem em menos de 500 ms (p95), com taxa de erro 5xx abaixo de 1%.
- **Como o SLO é verificado:** smoke test k6 em `k6/smoke.js` (5 usuários virtuais, 30 s), com os limites configurados como `thresholds`, de modo que o próprio k6 falha se o SLO for violado. Resultado: **p95 = 43,48 ms** (meta: 500 ms) e **0% de erros** em 145 requisições. Detalhes, metodologia e limitações em [`desempenho.md`](desempenho.md).

### Documentação da API (OpenAPI)

A API é documentada pelo padrão **OpenAPI 3**, gerado **a partir do código** com springdoc, e não escrito à mão. A especificação exportada está em [`openapi.yaml`](openapi.yaml) e a versão interativa fica em `/swagger-ui.html`.

**Por que gerar do código.** Um contrato escrito à parte envelhece: basta alguém mudar uma rota e esquecer o arquivo. Gerado do código, o contrato acompanha o que a API realmente aceita. Por isso o manual de rotas saiu deste README: ele seria uma segunda cópia da mesma informação, sujeita a divergir.

**Endpoint principal: `POST /adicionar`.** A rota de criação foi escolhida como endpoint principal porque é o fluxo central do CRUD (nada existe sem ela) e a única que escreve no banco a cada chamada, o que também a torna o alvo do SLO e do teste de carga. Todas as rotas aparecem na especificação, porque o springdoc as descreve automaticamente, mas o `/adicionar` recebeu a documentação detalhada, escrita de propósito:

| O que a especificação traz | Como o `/adicionar` atende | Onde está |
|---|---|---|
| Resumo e descrição da operação | `@Operation(summary, description)` | `TarefaController` |
| Schema nomeado e reutilizável (`$ref`) | Entidade anotada com `@Schema(name = "Tarefa")`, que vira `components/schemas/Tarefa` | `Tarefas` |
| Campos obrigatórios (`required`) | `@NotBlank` no `TarefaRequest`, que o springdoc converte em `required` | `TarefaRequest` |
| Tipos e detalhes por campo | `@Schema` com descrição e exemplo em cada campo | `TarefaRequest` |
| Resposta de sucesso com schema | `201` com `content`, `schema` e exemplo completo | `@ApiResponses` |
| Resposta de erro descrita | `400` (campos em branco) com exemplo do corpo de erro, e `500` | `@ApiResponses` |


Para regenerar o arquivo exportado, com a API no ar:

```bash
curl http://localhost:8080/v3/api-docs.yaml -o docs/openapi.yaml
```

### Evidências

Prints que sustentam as afirmações deste README, em [`provas/`](provas/):

| Afirmação | Prova |
|---|---|
| Prints de problemas resolvidos no Sonar | [`sonar1.png`](provas/sonar1.png), [`sonar2.png`](provas/sonar2.png), [`sonar3.png`](provas/sonar3.png), [`sonar4.png`](provas/sonar4.png), [`sonar5.png`](provas/sonar5.png) |
| SLO verificado pelo k6 (resumo da execução) | [`k6.1.png`](provas/k6.1.png) |
| Limites do SLO cumpridos (`thresholds`) | [`k6.2.png`](provas/k6.2.png) |
| Métricas expostas pelo Actuator | [`prometheus.png`](provas/prometheus.png) |
| Logs estruturados em JSON | [`logs-estruturados.png`](provas/logs-estruturados.png) |

### Integração contínua

O workflow `.github/workflows/ci.yml` é executado a cada push e pull request: instala dependências, executa o Checkstyle e roda `mvn clean verify` (testes e cobertura mínima).

## 6. Contato

Antonio Pedro Cayky do Nascimento Pereira, estudante de Sistemas e Mídias Digitais na Universidade Federal do Ceará.

GitHub: [@Cayky153](https://github.com/Cayky153) · Dúvidas sobre o projeto: abra uma *issue* no repositório.

---

# Aprofundamento

## Sobre este README

Este README segue, nas seis primeiras seções, a ordem pedida pelo roteiro da disciplina: o que o sistema realiza (em duas frases); como inicializá-lo localmente com comandos que funcionem de primeira; como executar a bateria de testes; quais variáveis de ambiente são obrigatórias; onde se encontram as decisões arquiteturais; e a quem recorrer em caso de dúvidas. Como o projeto também faz parte do meu portfólio, o restante do documento detalha o projeto mais a fundo e defende como cada requisito do trabalho final foi atendido.

## Contextualização

O TaskManager foi concebido na disciplina de Engenharia de Software, com desenvolvimento individual e aplicação de arquitetura em camadas (Controller → Service → Repository), tratamento global de erros via `@RestControllerAdvice`, validação com Bean Validation e testes com JUnit, Mockito e MockMvc. Após a entrega da disciplina, o projeto foi incrementado com Docker Compose, CI, cobertura mínima, análise estática, logs estruturados, métricas, OpenAPI e teste de carga, com o objetivo de praticar aspectos operacionais frequentemente deixados de lado em projetos acadêmicos.

Na etapa intermediária da disciplina, foi adicionada uma interface gráfica (JavaFX, FXML, Scene Builder e CSS), mantendo Spring Data JPA no acesso a dados. Essa interface **não consome a API por HTTP**: os controllers JavaFX são beans do Spring e chamam o `TarefaServices` diretamente. Trata-se de um complemento, não do foco principal do projeto.

## Funcionalidades

**API REST** (detalhes e contratos no Swagger)

- Criar uma tarefa ou várias de uma vez.
- Listar todas as tarefas e buscar uma pelo ID.
- Editar uma tarefa existente.
- Remover uma tarefa ou todas.

**Interface gráfica (JavaFX), complemento**

- Tela principal com a tabela de tarefas (título, descrição, tipo e prioridade).
- Tela de cadastro e edição aberta como janela modal.
- Confirmação antes de remover.
- Validação dos campos obrigatórios antes de salvar.

## Stack

| Tecnologia | Uso no projeto |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot 4 | Framework base |
| Spring Web (MVC) | Endpoints REST |
| Spring Data JPA / Hibernate | Persistência e ORM |
| PostgreSQL 17 | Banco relacional (aplicação) |
| H2 | Banco em memória (testes e CI) |
| Docker e Docker Compose | Subir banco e API com um comando |
| GitHub Actions | CI: install, lint, test |
| JUnit 5, Mockito, MockMvc | Testes unitários e de integração |
| JaCoCo | Cobertura com mínimo exigido no build |
| Checkstyle | Lint de convenções no CI |
| SonarQube | Análise de qualidade e segurança (local) |
| gitleaks | Varredura de segredos no histórico |
| springdoc OpenAPI / Swagger UI | Documentação gerada do código |
| Actuator e Micrometer (Prometheus) | Métricas |
| Logstash JSON (nativo do Spring) | Logs estruturados |
| k6 | Smoke test com p95 |
| JavaFX, FXML, Scene Builder, CSS | Interface gráfica (complemento) |

## Requisitos

| Requisito | Descrição | Status |
|---|---|---|
| Entidade `Tarefas` | Modelo JPA com título, descrição, tipo e prioridade | Concluído |
| Validação de campos | `@NotBlank` na entidade e no DTO `TarefaRequest` | Concluído |
| Repositório `TarefasRepo` | `JpaRepository` para acesso ao banco | Concluído |
| Camada de serviço `TarefaServices` | Salvar, listar, buscar por ID, editar e deletar | Concluído |
| Endpoints do CRUD | Criação, criação em lote, listagem, busca por ID, edição e remoção (ver Swagger) | Concluído |
| Tratamento global de exceções | `GlobalExceptionHandler` com `@RestControllerAdvice` | Concluído |
| Resposta de erro padronizada | `ErrorResponse` abstrata, com `ValidationErrorResponse` e `RuntimeErrorResponse` (ver [ADR 0001](ADR.MD)) | Concluído |
| Validação | HTTP 400 para campos inválidos | Concluído |
| Tarefa não encontrada | HTTP 404 via `TarefaNaoEncontradaException` (antes era 500) | Concluído |
| Testes unitários de serviço | `TarefaServices` com Mockito, sucesso e falha | Concluído |
| Testes de integração | Todas as rotas com MockMvc | Concluído |

## Estrutura de arquivos

```text
├── .github/workflows/ci.yml        # CI: install, lint, test
├── compose.yaml                    # Postgres + API
├── Dockerfile                      # build multi-stage da API
├── checkstyle.xml                  # regras do lint de convenções
├── k6/smoke.js                     # smoke test com thresholds (SLO)
├── .env.example                    # variáveis (sem segredos)
├── docs
│   ├── README.md                   # este arquivo
│   ├── ADR.MD                      # decisão arquitetural (MADR)
│   ├── desempenho.md               # SLO, metodologia e resultado do k6
│   ├── openapi.yaml                # especificação exportada
│   └── provas/                     # prints: Sonar, k6, métricas e logs
└── src
    ├── main/java/com/example/demo
    │   ├── DemoApplication.java
    │   ├── controller/TarefaController.java      # rotas REST
    │   ├── dto/TarefaRequest.java                # entrada da API
    │   ├── service/TarefaServices.java           # regras de negócio e métricas
    │   ├── repository/TarefasRepo.java
    │   ├── model/Tarefas.java                    # entidade JPA
    │   ├── exception
    │   │   ├── ErrorResponse.java                # molde abstrato (Template Method)
    │   │   ├── ValidationErrorResponse.java      # HTTP 400
    │   │   ├── RuntimeErrorResponse.java         # erro de negócio
    │   │   ├── ErrorResponseFactory.java         # monta o ResponseEntity
    │   │   ├── TarefaNaoEncontradaException.java # HTTP 404
    │   │   └── GlobalExceptionHandler.java
    │   └── view/                                 # JavaFX (complemento, fora da cobertura)
    │       ├── Launcher.java
    │       ├── App.java
    │       └── controller
    ├── main/resources/ (application.properties, fxml/, css/)
    └── test
        ├── java/com/example/demo/Services/TarefaServicesTest.java
        ├── java/com/example/demo/IntegrationTest/TarefaIntegrationTest.java
        └── resources/application.properties      # H2 para testes
```