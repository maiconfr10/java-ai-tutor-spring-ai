
package br.com.portfolio.primeiraia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import br.com.portfolio.primeiraia.dto.MemoriaRequest;
import br.com.portfolio.primeiraia.dto.ChatResponse;
import br.com.portfolio.primeiraia.service.MemoriaService;

@RestController
@RequestMapping("/api/ai/memoria")
public class MemoriaController {

    private final MemoriaService memoriaService;

    public MemoriaController(
            MemoriaService memoriaService) {

        this.memoriaService = memoriaService;
    }

    @PostMapping
    public ChatResponse conversar(
            @RequestBody MemoriaRequest request) {

        if (request == null
                || request.getMensagem() == null
                || request.getMensagem().isBlank()
                || request.getConversaId() == null
                || request.getConversaId().isBlank()) {

            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Mensagem e conversaId sao obrigatorios"
            );
        }

        String resposta = memoriaService.conversar(
                request.getConversaId().trim(),
                request.getMensagem()
        );

        if (resposta == null || resposta.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "A IA retornou uma resposta vazia"
            );
        }

        return new ChatResponse(resposta);
    }
}
