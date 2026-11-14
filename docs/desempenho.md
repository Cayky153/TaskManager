# Desempenho

Este documento descreve a meta de desempenho do TaskManager API (SLO), como a aplicação é instrumentada, a linha de base observada em repouso e como a meta pode ser verificada.

## 1. Objetivo (SLO)

> 95% das requisições a `POST /adicionar` respondem em menos de **500 ms** (p95), com taxa de erro abaixo de **1%**.

- **p95** é o tempo abaixo do qual ficam 95% das requisições: ele ignora os 5% mais lentos e descreve a experiência típica melhor que a média.
- `POST /adicionar` foi escolhido por ser o fluxo principal do CRUD e o que escreve no banco a cada chamada.
- O limite de 500 ms é uma meta inicial para uma API simples com banco local, e pode ser revisada quando houver medições.

**Status:** o SLO foi cumprido na execução registrada na seção 5 (p95 de 43,48 ms contra a meta de 500 ms, com 0% de erros), em condições leves de smoke test. Isso não garante o cumprimento sob carga maior (ver limitações, seção 6).

## 2. Instrumentação da aplicação

A aplicação expõe métricas pelo Spring Boot Actuator e Micrometer em `http://localhost:8080/actuator/prometheus`, em formato de texto Prometheus (evidência: [`provas/prometheus.png`](provas/prometheus.png)).

| Métrica | O que mostra |
|---|---|
| `http_server_requests_seconds_*` | Contagem e latência por rota, com histograma (buckets em 300 ms e 500 ms) |
| `tarefas_criadas_total` | Contador de tarefas criadas (zera quando a aplicação reinicia) |
| `hikaricp_connections*` | Estado do pool de conexões com o banco (total, ativas, tempo de espera por conexão) |
| `application_started_time_seconds` / `application_ready_time_seconds` | Tempo de subida da aplicação |
| `executor_*` | Estado do executor de tarefas do Spring (`applicationTaskExecutor`) |

O projeto **não inclui um servidor Prometheus**. Se um for conectado, o p95 do endpoint pode ser calculado com:

```
histogram_quantile(0.95,
  sum(rate(http_server_requests_seconds_bucket{uri="/adicionar"}[5m])) by (le))
```

## 3. Linha de base (aplicação em repouso)

Leitura do endpoint `/actuator/prometheus` logo após a subida, em Windows, com a API executada pela IDE (Eclipse) em `localhost:8080` e **antes de qualquer tráfego**. Não é resultado de carga.

| Indicador | Valor observado | Leitura |
|---|---|---|
| Tempo de subida (`application_started_time_seconds`) | 8,144 s | Inclui contexto Spring, Hibernate e conexão com o banco |
| Pronta para atender (`application_ready_time_seconds`) | 8,26 s | Requisições só são atendidas depois desse ponto |
| Pool de conexões (`hikaricp_connections`, `HikariPool-1`) | 10 | Pool aberto, o que confirma a conexão com o Postgres |
| Esperas por conexão (`hikaricp_connections_acquire_seconds_count`) | 0 | Nenhuma requisição tinha usado o banco ainda |
| Tempo máximo de espera por conexão (`..._acquire_seconds_max`) | 0,0 s | Sem contenção em repouso |
| Executor `applicationTaskExecutor`: threads ativas / núcleo | 0 / 8 | Sem tarefas assíncronas em andamento |
| Espaço em disco livre / total | cerca de 40,5 GB / 255 GB | Sem pressão de disco na máquina |

Observação: o pool tem 10 conexões, número maior que os 5 usuários virtuais do teste definido abaixo, então, em princípio, o pool não deve ser o gargalo. É uma inferência a partir dos valores acima, não uma medição sob carga.

## 4. Teste definido para verificar o SLO

Smoke test com [k6](https://k6.io), em `k6/smoke.js`.

| Parâmetro | Valor |
|---|---|
| Ferramenta | k6 |
| Endpoint | `POST /adicionar` |
| Usuários virtuais (VUs) | 5 |
| Duração | 30 s |
| Pausa entre iterações | 1 s por VU (cerca de 150 requisições no total) |
| Corpo da requisição | tarefa válida (`titulo`, `descricao`, `tipo`, `prioridade`) |
| Verificação | status HTTP 201 |

Os limites do SLO estão no script como `thresholds`; se forem violados, o k6 termina com erro:

```javascript
thresholds: {
  http_req_failed: ['rate<0.01'],
  http_req_duration: ['p(95)<500'],
}
```

### Como executar

```bash
# com a API no ar em localhost:8080 (Docker Compose ou IDE)
k6 run k6/smoke.js

# alternativa sem instalar o k6
docker run --rm -i -v "%cd%\k6":/scripts -e BASE_URL=http://host.docker.internal:8080 grafana/k6 run /scripts/smoke.js
```

No final da saída, as linhas `http_req_duration` (com `p(95)`) e `http_req_failed` indicam se a meta foi cumprida. Cada iteração cria uma tarefa real; para limpar depois, use `DELETE /deletarTudo` ou `docker compose down -v`.

## 5. Resultado da execução

Execução de `k6 run k6/smoke.js` contra `POST /adicionar`, em Windows, com cliente e API na mesma máquina (API em `localhost:8080`). Evidências: [`provas/k6.1.png`](provas/k6.1.png) (resumo completo) e [`provas/k6.2.png`](provas/k6.2.png) (limites do SLO).

| Métrica | Resultado | Limite (SLO) | Status |
|---|---|---|---|
| p(95) de `http_req_duration` | **43,48 ms** | < 500 ms | Cumprido |
| `http_req_failed` | **0,00%** (0 de 145) | < 1% | Cumprido |
| p(90) | 42,4 ms | n/a | n/a |
| Média | 34,71 ms | n/a | n/a |
| Mediana | 37,34 ms | n/a | n/a |
| Mínimo / máximo | 15,93 ms / 48,41 ms | n/a | n/a |
| Verificações (`status 201`) | 100% (145 de 145) | n/a | n/a |
| Requisições totais | 145 em 30,1 s (cerca de 4,8 por segundo) | n/a | n/a |
| Usuários virtuais | 5 (constante) | n/a | n/a |

**Leitura dos números**

- O p95 ficou cerca de 11 vezes abaixo do limite, e a dispersão é pequena (de 16 a 48 ms entre o mínimo e o máximo). Não houve pico de latência na execução.
- Os cerca de 4,8 requisições por segundo **não medem a capacidade da API**: cada usuário virtual espera 1 s entre iterações, e cada iteração dura cerca de 1,03 s (1 s de pausa mais cerca de 35 ms de requisição). A vazão aqui é consequência do desenho do teste.
- A margem grande indica que o limite de 500 ms é folgado para este cenário. Apertá-lo só faz sentido depois de um teste com mais carga.
- O teste criou 145 tarefas reais no banco.
