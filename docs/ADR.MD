# 0001. Template Method na hierarquia de respostas de erro

- Status: aceito
- Data: 2026-11-13

## Contexto e problema

A API precisa devolver erros em situações diferentes: dados inválidos (campos em branco) e falhas de regra de negócio (por exemplo, tarefa não encontrada). Sem um padrão, cada ponto de falha montaria sua própria resposta (um `Map` ad hoc, uma `String`), com campos e códigos HTTP diferentes. O cliente da API teria de tratar cada erro de um jeito, e uma mudança no formato exigiria alterar vários lugares.

Queremos que **todo erro tenha o mesmo formato** e que o que difere entre os tipos de erro (código HTTP e nome do tipo) fique isolado e fácil de estender.

## Direcionadores da decisão

- Formato de erro único e previsível para quem consome a API.
- Adicionar um novo tipo de erro sem alterar o código dos tipos existentes (aberto para extensão, fechado para modificação).
- Tratamento centralizado, fora de controllers e serviços.
- Facilidade de teste: o status esperado de cada erro é verificável nos testes de integração.

## Opções consideradas

1. **Resposta ad hoc em cada ponto de falha** (`Map.of(...)` ou `String`), como já fazem as respostas de sucesso.
2. **Uma única classe `ErrorResponse` com `status` e `tipo` passados por parâmetro** (ou enum).
3. **Template Method:** classe abstrata `ErrorResponse` que define o molde fixo da resposta, com subclasses que definem só as partes variáveis.

## Decisão

Adotamos a opção 3.

`ErrorResponse` (abstrata) define a estrutura invariável de toda resposta de erro: `timestamp`, `status`, `error` e `message`. As partes que variam são **métodos abstratos** (os "ganchos" do template) implementados por cada subclasse:

- `getStatus()`: código HTTP do erro.
- `getTipoErro()`: nome do tipo de erro.

Subclasses atuais:

| Subclasse | Quando ocorre | HTTP |
|---|---|---|
| `ValidationErrorResponse` | `MethodArgumentNotValidException` (campos inválidos) | 400 |
| `RuntimeErrorResponse` | `RuntimeException` não tratada de forma mais específica | 500 |

O fluxo é: o serviço lança uma exceção; o `GlobalExceptionHandler` (`@RestControllerAdvice`) a captura e chama o `ErrorResponseFactory`, que instancia a subclasse adequada e a envolve em um `ResponseEntity` com o status que ela define. Controllers e serviços não montam respostas de erro.

**Por que não a opção 1:** espalha o formato de erro pelo código e permite divergências silenciosas. **Por que não a opção 2:** funciona, mas cada novo erro passa a exigir editar a classe central ou o enum, e nada impede combinações incoerentes de status e tipo; com subclasses, cada erro carrega seu próprio par de valores em um único lugar.

## Consequências

Positivas:
- Formato de erro idêntico em toda a API.
- Novo tipo de erro = nova subclasse, sem modificar as existentes.
- Status e tipo de cada erro ficam encapsulados na subclasse correspondente.
- Tratamento centralizado no `GlobalExceptionHandler`, com controllers e serviços limpos.

Negativas e pontos de atenção:
- É uma aplicação **leve** do Template Method: o que o molde fixa é principalmente a estrutura dos dados, não um algoritmo longo com vários passos. Isso é suficiente para o problema, mas não deve ser vendido como o uso clássico do padrão.
- Uma subclasse por tipo de erro aumenta o número de classes; se os tipos se multiplicarem, vale reavaliar um enum.
- O código HTTP é definido em dois lugares quando o `ErrorResponseFactory` também escolhe o status do `ResponseEntity`; as duas definições precisam permanecer coerentes (os testes de integração verificam o status HTTP).
