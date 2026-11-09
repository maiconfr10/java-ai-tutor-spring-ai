
package br.com.portfolio.primeiraia.controller;

import br.com.portfolio.primeiraia.dto.ChatRequest;
import br.com.portfolio.primeiraia.dto.ChatResponse;
import br.com.portfolio.primeiraia.service.ChatService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import br.com.portfolio.primeiraia.dto.RespostaEstruturada;
import br.com.portfolio.primeiraia.dto.ResumoRequest;
import br.com.portfolio.primeiraia.dto.ResumoEstruturado;


@RestController
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ChatResponse conversar(
            @RequestBody ChatRequest request) {

        if (request.mensagem() == null ||
            request.mensagem().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A mensagem nao pode estar vazia"
            );
        }
        


String nivel = request.nivel();

if (nivel == null || nivel.isBlank()) {
    nivel = "iniciante";
}

nivel = nivel.toLowerCase(java.util.Locale.ROOT);

if (!java.util.List.of(
        "iniciante",
        "intermediario",
        "avancado"
    ).contains(nivel)) {

    throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Nivel invalido"
    );
}

String resposta = chatService
    .responder(request.mensagem(), nivel);


        return new ChatResponse(resposta);
    }

@PostMapping("/estruturado")
public RespostaEstruturada responderEstruturado(
        @RequestBody ChatRequest request) {


String mensagem = request.mensagem();
String nivel = request.nivel();


    if (mensagem == null || mensagem.isBlank()) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "A mensagem nao pode estar vazia"
        );
    }

    if (nivel == null || nivel.isBlank()) {
        nivel = "iniciante";
    }

    nivel = nivel.trim().toLowerCase(
        java.util.Locale.ROOT
    );

    if (!java.util.List.of(
            "iniciante", "intermediario", "avancado"
        ).contains(nivel)) {

        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Nivel invalido"
        );
    }

    return chatService.responderEstruturado(
        mensagem, nivel
    );
}

@PostMapping("/resumir")
public ResumoEstruturado resumir(
        @RequestBody ResumoRequest request) {

    String texto = request.getTexto();

    if (texto == null || texto.isBlank()) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "O texto nao pode estar vazio"
        );
    }

    return chatService.resumirTexto(texto);
}



    
}
