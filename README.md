
# Java AI Tutor

API REST de um assistente inteligente para ensino de
programacao Java, utilizando Spring AI e Ollama.

## Tecnologias

- Java 21
- Spring Boot
- Spring AI
- Ollama - Qwen2.5 1.5B
- Maven
- Postman

## Funcionalidades

- Chatbot com Inteligencia Artificial
- Prompts dinamicos por nivel de conhecimento
- Respostas estruturadas em JSON
- Resumo inteligente de textos
- Validacao de entradas com HTTP 400

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

- Function Calling
- Integracao com ferramentas Java
- Memoria de conversacao
- Desenvolvimento de agentes de IA
