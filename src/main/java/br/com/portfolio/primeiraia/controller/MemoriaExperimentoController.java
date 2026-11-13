
package br.com.portfolio.primeiraia.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import br.com.portfolio.primeiraia.service.MemoriaService;

@RestController
@RequestMapping("/api/ai")
public class MemoriaExperimentoController {

    private final MemoriaService memoriaService;

    public MemoriaExperimentoController(
            MemoriaService memoriaService) {

        this.memoriaService = memoriaService;
    }

    @PostMapping("/memoria-ia")
    public Map<String, String> conversarComIA(
            @RequestBody Requisicao request) {

        if (request == null
                || request.conversaId() == null
                || request.conversaId().isBlank()
                || request.mensagem() == null
                || request.mensagem().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Conversa e mensagem sao obrigatorias.");
        }

        System.out.println(
                "ENDPOINT EXPERIMENTAL ACIONADO");

        String resposta = memoriaService.conversarComIA(
                request.conversaId(),
                request.mensagem());

        return Map.of(
                "resposta",
                resposta == null ? "" : resposta);
    }

    public record Requisicao(
            String conversaId,
            String mensagem) {
    }
}
