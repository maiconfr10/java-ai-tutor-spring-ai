
Tutor de IA em Java
API REST de um assistente inteligente para ensino de programação Java, utilizando Spring AI e Ollama.

Tecnologias
Java 21
Spring Boot
Spring AI
Ollama - Qwen2.5 1.5B
Maven
Carteiro
Funcionalidades
Chatbot com Inteligência Artificial
Solicita dinâmica por nível de conhecimento
Respostas estruturadas em JSON
Resumo inteligente de textos
Validação de entradas com HTTP 400
Pontos de extremidade
POST /api/ai/chat

Recebe uma pergunta e o nível do aluno.

POST /api/ai/estruturado

Retorno conceito, explicação e exemploJava.

POST /api/ai/resumir

Voltar ao título, resumo e pontoPrincipal.

Como executar
Instale o JDK 21 eo Ollama.

Baixe o modelo:

ollama pull qwen2.5:1.5b

Configure o application.properties

Execute o projeto pelo Spring Boot.

Teste os endpoints no Postman.

O projeto utiliza o Ollama localmente e pode executar o modelo pela CPU.

Objetivo
Projeto desenvolvido durante a primeira semana de estudos em desenvolvimento de agentes de IA.

O foco e aprender integração de LLMs com Java, engenharia de prompts, APIs REST e Structured Output.

Próximos passos
Chamada de função
Integração com ferramentas Java
Memória de conversação
Desenvolvimento de agentes de IA
## Semana 2 - Agente de IA com Function Calling

Nesta etapa, o projeto evoluiu de um chatbot

para um agente capaz de executar ferramentas Java

utilizando Spring AI e Ollama.

### Tecnologias

- Java 21

- Spring Boot

- Spring AI

- Ollama

- Qwen3 1.7B

- Maven

- Carteiro

### Ferramentas implementadas

1. consultarEstoque()

Consulte a quantidade de um produto.

2. listarProdutosEstoqueBaixo()

Identifica produtos com estoque baixo

ou esgotados

3. gerarRelatórioEstoque()

Gera um relatório geral contendo:

- Produtos cadastrados

- Total de unidades

- Produtos com estoque baixo

- Produtos esgotados

### Conceitos aplicados

- Chamada de função

- Anotações @Tool e @ToolParam

- Ferramentas Multiplas

- Engenharia de prompts

- Normalização de parâmetros

- Validação de entradas

- retornoDireto

- ToolCallResultConverter

- Integração com API REST

### Ponto final

POST /api/ai/estouque

Exemplo de requisicão:

{

&#x20; "mensagem": "Gere um relatorio geral do estoque."

}
### Resultado esperado

- Produtos cadastrados: 3

- Total de unidades: 17

- Produtos com estoque baixo: 1

- Produtos esgotados: 1

### Execução

1. Instale o Ollama.

2. Execute: ollama pull qwen3:1.7b

3. Configure o arquivo application.properties.

4. Inicie o aplicativo Spring Boot.

5. Teste os endpoints pelo Postman.

O modelo e executado localmente, sem exigência

uma API de IA paga.

### Aprendizado

A IA interpreta a solicitação e seleciona

a ferramenta adequada

O Spring AI executa o método Java autorizado.

As regras de negociação e os cálculos permanecem

no backend Java

# Agente de IA com Memória Persistente e Ferramentas Java

Projeto de estudo e portfólio desenvolvido com **Java 21, Spring Boot, Spring AI, Ollama (Qwen3:1.7b) e MySQL**. Demonstra um agente conversacional em português com memória persistente, uso de ferramentas Java (*Function Calling*), consultas de perfis e tratamento de erros.

> Projeto desenvolvido durante a formação prática em Desenvolvimento de Agentes de IA. A interface de testes utilizada é o Postman.

## Funcionalidades

- Conversas com um modelo de linguagem executado localmente pelo Ollama.
- Histórico de conversação persistido em MySQL e separado por `conversaId`.
- Consulta de perfis de alunos por ferramenta Java (`consultarPerfilAluno`).
- Ferramentas de estoque para consulta e relatórios (`EstoqueTools`).
- Validação de requisições e tratamento centralizado de erros HTTP.
- Monitoramento da aplicação com Spring Boot Actuator e métricas de IA.
- Testes automatizados com JUnit e Mockito.

## Tecnologias

- Java 21
- Spring Boot e Spring AI
- Ollama e modelo `qwen3:1.7b`
- MySQL e Spring Data JPA/JDBC para os recursos correspondentes
- Maven
- JUnit, Mockito e MockMvc
- Postman

## Arquitetura resumida

```text
Postman
   |
   v
Controllers REST (Spring Boot)
   |
   v
MemoriaService + ChatClient (Spring AI)
   |             |               |
   v             v               v
Ollama/Qwen   Tools Java      Memória (MySQL)
                 |
                 v
          PerfilAlunoRepository
                 |
                 v
               MySQL
```

O modelo solicita a execução de ferramentas; o código Java é responsável por executá-las. O modelo não consulta o banco de dados diretamente.

## Pré-requisitos

1. JDK 21.
2. Maven (ou suporte Maven do Eclipse/Spring Tools).
3. MySQL em execução e banco `agente_ia_memoria` configurado.
4. Ollama instalado e em execução.
5. Modelo local disponível:

```powershell
ollama pull qwen3:1.7b
```

## Configuração local

O arquivo `src/main/resources/application.properties` precisa apontar para o Ollama e o MySQL. **Nunca grave senhas reais no repositório.** Para o MySQL, utilize uma variável de ambiente:

```properties
spring.datasource.password=${DB_PASSWORD}
```

No PowerShell da sessão em que a aplicação será executada:

```powershell
$env:DB_PASSWORD="SUA_SENHA_LOCAL"
```

Use também as demais configurações do projeto (modelo Qwen, memória JDBC, ferramentas e Actuator). A variável precisa estar disponível ao processo que inicia o Spring Boot; o Eclipse pode exigir configurá-la nas variáveis de ambiente da configuração de execução.

**Atenção:** confirme que o banco e suas tabelas de perfil já existem conforme o projeto. O esquema de memória é administrado pela configuração específica do Spring AI; isso não significa que todas as tabelas de negócio sejam criadas automaticamente.

## Executar a aplicação

Com o Ollama e o MySQL ativos e as variáveis de ambiente configuradas:

```powershell
mvn spring-boot:run
```

A aplicação foi configurada para a porta **8080** e o Ollama para **11434**.

## Endpoints utilizados nos testes

### Conversa com IA e ferramentas

`POST http://localhost:8080/api/ai/memoria-ia`

```json
{
  "conversaId": "exemplo-001",
  "mensagem": "Explique em duas frases o que e um agente de IA."
}
```

### Consulta de perfil por ferramenta

`POST http://localhost:8080/api/ai/memoria-ia`

```json
{
  "conversaId": "exemplo-com-perfil",
  "mensagem": "Use consultarPerfilAluno e informe meu nome e objetivo."
}
```

A resposta depende dos perfis efetivamente cadastrados. O uso da ferramenta pelo modelo deve ser confirmado nos logs.

### Consulta direta de perfil

`GET http://localhost:8080/api/perfil/{conversaId}`

### Monitoramento

- `GET http://localhost:8080/actuator/health`
- `GET http://localhost:8080/actuator/info`
- `GET http://localhost:8080/actuator/metrics`

## Testes

Foram desenvolvidos quatro testes de `PerfilAlunoToolsTest` e um teste de tratamento HTTP em `MemoriaExperimentoControllerTest`. Para executar ambos:

```powershell
mvn "-Dtest=PerfilAlunoToolsTest,MemoriaExperimentoControllerTest" test
```

Os testes cobrem consulta simulada de perfil, perfil inexistente, identificador inválido, exceção simulada de banco e transformação de falha em HTTP 503 na camada web.

Além disso, foram realizados testes manuais via Postman para memória, isolamento de conversas, persistência após reinício, consultas de perfil e estoque e validação HTTP 400.

## Cuidados de segurança

- Não publique credenciais do MySQL, tokens, senhas ou dados pessoais reais.
- Verifique também os arquivos já rastreados pelo Git e o histórico de commits: `.gitignore` não remove segredos de versões anteriores.
- Restrinja o acesso ao Actuator e aos endpoints administrativos antes de implantar o projeto fora do ambiente local.
- Os resultados de Function Calling dependem do comportamento do modelo; valide ferramentas e dados em operações importantes.

## Próximas melhorias

- Automatizar testes adicionais da memória e do fluxo completo com ferramentas.
- Melhorar a avaliação de confiabilidade das respostas do modelo.
- Adicionar documentação de implantação e monitoramento para ambientes não locais.

---

**Status:** projeto educacional e de portfólio, validado localmente durante a Semana 3 da formação.
