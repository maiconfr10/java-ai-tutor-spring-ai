# Java AI Tutor

API REST de um assistente inteligente para ensino de
programacao Java, utilizando Spring AI e Ollama.

## Tecnologias

* Java 21
* Spring Boot
* Spring AI
* Ollama - Qwen2.5 1.5B
* Maven
* Postman

## Funcionalidades

* Chatbot com Inteligencia Artificial
* Prompts dinamicos por nivel de conhecimento
* Respostas estruturadas em JSON
* Resumo inteligente de textos
* Validacao de entradas com HTTP 400

## Endpoints

POST /api/ai/chat

Recebe uma pergunta e o nivel do aluno.

POST /api/ai/estruturado

Retorna conceito, explicacao e exemploJava.

POST /api/ai/resumir

Retorna titulo, resumo e pontoPrincipal.

## Como executar

1. Instale o JDK 21 e o Ollama.
2. Baixe o modelo:

   ollama pull qwen2.5:1.5b

3. Configure o application.properties.
4. Execute o projeto pelo Spring Boot.
5. Teste os endpoints no Postman.

O projeto utiliza o Ollama localmente e pode
executar o modelo pela CPU.

## Objetivo

Projeto desenvolvido durante a primeira semana
de estudos em desenvolvimento de agentes de IA.

O foco e aprender integracao de LLMs com Java,
engenharia de prompts, APIs REST e Structured Output.

## Proximos passos

* Function Calling
* Integracao com ferramentas Java
* Memoria de conversacao
* Desenvolvimento de agentes de IA





\## Semana 2 - Agente de IA com Function Calling



Nesta etapa, o projeto evoluiu de um chatbot

para um agente capaz de executar ferramentas Java

utilizando Spring AI e Ollama.



\### Tecnologias



\- Java 21

\- Spring Boot

\- Spring AI

\- Ollama

\- Qwen3 1.7B

\- Maven

\- Postman



\### Ferramentas implementadas



1\. consultarEstoque()

&#x20;  Consulta a quantidade de um produto.



2\. listarProdutosEstoqueBaixo()

&#x20;  Identifica produtos com estoque baixo

&#x20;  ou esgotados.



3\. gerarRelatorioEstoque()

&#x20;  Gera um relatório geral contendo:

&#x20;  - Produtos cadastrados

&#x20;  - Total de unidades

&#x20;  - Produtos com estoque baixo

&#x20;  - Produtos esgotados



\### Conceitos aplicados



\- Function Calling

\- Anotacoes @Tool e @ToolParam

\- Multiplas ferramentas

\- Engenharia de prompts

\- Normalizacao de parametros

\- Validacao de entradas

\- returnDirect

\- ToolCallResultConverter

\- Integracao com API REST



\### Endpoint



POST /api/ai/estoque



Exemplo de requisicao:



```json

{

&#x20; "mensagem": "Gere um relatorio geral do estoque."

}

```



\### Resultado esperado



\- Produtos cadastrados: 3

\- Total de unidades: 17

\- Produtos com estoque baixo: 1

\- Produtos esgotados: 1



\### Execucao



1\. Instale o Ollama.

2\. Execute: ollama pull qwen3:1.7b

3\. Configure o application.properties.

4\. Inicie a aplicacao Spring Boot.

5\. Teste os endpoints pelo Postman.



O modelo e executado localmente, sem exigir

uma API de IA paga.



\### Aprendizado



A IA interpreta a solicitacao e seleciona

a ferramenta adequada.



O Spring AI executa o metodo Java autorizado.



As regras de negocio e os calculos permanecem

no backend Java.



