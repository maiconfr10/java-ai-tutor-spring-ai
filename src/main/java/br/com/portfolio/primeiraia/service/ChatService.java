
package br.com.portfolio.primeiraia.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import br.com.portfolio.primeiraia.dto.RespostaEstruturada;
import br.com.portfolio.primeiraia.dto.ResumoEstruturado;


@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder builder) {
        this.chatClient = builder


.defaultSystem("""
    Voce e um professor especialista em Java.

    Sua tarefa e ensinar programacao para iniciantes.

    REGRAS OBRIGATORIAS:
    1. Responda sempre em portugues.
    2. Explique o conceito em ate tres frases.
    3. Sempre que a pergunta for sobre Java,
       inclua um exemplo de codigo Java.
    4. O codigo deve ser pequeno e funcional.
    5. Nao omita o exemplo de codigo.

    FORMATO DA RESPOSTA:

    EXPLICACAO:
    Apresente o conceito de maneira objetiva.

    EXEMPLO JAVA:
    Apresente um exemplo de codigo.

    Utilize esse formato em todas as
    perguntas sobre programacao Java.
    
Adapte a complexidade da explicacao
ao seguinte nivel de conhecimento: {nivel}.

    """)


            .build();
    }


public String responder(String mensagem, String nivel) {

    return chatClient.prompt()
        .system(s -> s.param("nivel", nivel))
        .user(mensagem)
        .call()
        .content();
}


public RespostaEstruturada responderEstruturado(
        String mensagem, String nivel) {

    return chatClient.prompt()
        .system(s -> s.text("""
            Voce e um professor especialista em Java.
            Explique para um aluno de nivel {nivel}.

            Preencha os campos:
            conceito, explicacao e exemploJava.

            O exemploJava deve conter codigo Java.
            Responda de maneira didatica.
            """)
            .param("nivel", nivel))
        .user(mensagem)
        .call()
        .entity(RespostaEstruturada.class);
}

public ResumoEstruturado resumirTexto(String texto) {

    return chatClient.prompt()
        .system("""
            Voce e um especialista em resumir textos.

            Sua tarefa e analisar o texto enviado
            e gerar um resumo organizado.

            Preencha os seguintes campos:

            titulo: titulo curto sobre o assunto.
            resumo: resumo objetivo do texto.
            pontoPrincipal: informacao mais importante.

            Responda em portugues.
            Nao invente informacoes.
            Utilize apenas o texto recebido.
            """)
        .user(texto)
        .call()
        .entity(ResumoEstruturado.class);
}


 
}
