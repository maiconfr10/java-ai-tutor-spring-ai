
package br.com.portfolio.primeiraia.controller;

import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(
    assignableTypes = MemoriaExperimentoController.class
)
public class TratadorErrosMemoria {

    // ERROS DE VALIDACAO - HTTP 400
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> tratarValidacao(
            ResponseStatusException erro) {

        String mensagem = erro.getReason();

        if (mensagem == null || mensagem.isBlank()) {
            mensagem = "Requisicao invalida.";
        }

        return ResponseEntity
                .status(erro.getStatusCode())
                .body(Map.of(
                        "status", erro.getStatusCode().value(),
                        "erro", mensagem
                ));
    }

    // FALHAS TECNICAS - HTTP 503 OU 500
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> tratarFalhaTecnica(
            RuntimeException erro) {

        Throwable causa = erro;

        // Procura falhas de acesso a dados
        // mesmo quando foram encapsuladas.
        while (causa != null) {

            if (causa instanceof DataAccessException) {

                System.err.println(
                        "Falha de acesso ao banco de dados."
                );

                return ResponseEntity
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of(
                                "status", 503,
                                "erro",
                                "Servico de dados temporariamente indisponivel."
                        ));
            }

            causa = causa.getCause();
        }

        // Outros erros inesperados
        System.err.println(
                "Erro inesperado: "
                + erro.getClass().getSimpleName()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "status", 500,
                        "erro",
                        "Erro interno ao processar a requisicao."
                ));
    }
}
