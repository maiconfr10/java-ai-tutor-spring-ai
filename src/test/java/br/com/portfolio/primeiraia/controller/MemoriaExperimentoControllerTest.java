
package br.com.portfolio.primeiraia.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.portfolio.primeiraia.service.MemoriaService;

public class MemoriaExperimentoControllerTest {

    @Test
    void deveRetornar503QuandoBancoFalhar() throws Exception {

        // 1. Criar um servico simulado
        MemoriaService memoriaService =
                mock(MemoriaService.class);

        // 2. Simular falha de acesso ao banco
        when(memoriaService.conversarComIA(
                "teste-503-001",
                "Qual e meu objetivo?"
        )).thenThrow(
                new DataAccessResourceFailureException(
                        "Falha simulada no banco"
                )
        );

        // 3. Preparar o Controller e o tratador
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new MemoriaExperimentoController(
                                memoriaService
                        )
                )
                .setControllerAdvice(
                        new TratadorErrosMemoria()
                )
                .build();

        // 4. Enviar uma requisicao simulada
        mockMvc.perform(
                post("/api/ai/memoria-ia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "conversaId": "teste-503-001",
                                  "mensagem": "Qual e meu objetivo?"
                                }
                                """)
        )

        // 5. Confirmar os resultados
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.status").value(503))
        .andExpect(jsonPath("$.erro").value(
                "Servico de dados temporariamente indisponivel."
        ));

        System.out.println(
                "TESTE APROVADO: HTTP 503 RETORNADO!"
        );
    }
}
