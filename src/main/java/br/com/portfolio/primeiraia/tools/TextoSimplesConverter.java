
package br.com.portfolio.primeiraia.tools;

import java.lang.reflect.Type;

import org.springframework.ai.tool.execution.ToolCallResultConverter;

public class TextoSimplesConverter
        implements ToolCallResultConverter {

    @Override
    public String convert(Object resultado, Type tipoRetorno) {

        if (resultado == null) {
            return "";
        }

        return resultado.toString();
    }
}
