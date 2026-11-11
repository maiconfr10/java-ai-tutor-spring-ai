
package br.com.portfolio.primeiraia.tools;

import java.util.Map;
import java.util.Locale;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class EstoqueTools {

    // Estoque simulado
    private final Map<String, Integer> estoque = Map.of(
        "mouse", 12,
        "teclado", 5,
        "monitor", 0
    );

    // Nomes alternativos dos produtos
    private final Map<String, String> aliases = Map.of(
        "mouses", "mouse",
        "teclados", "teclado",
        "monitores", "monitor",
        "impressoras", "impressora"
    );

    // FERRAMENTA 1 - CONSULTAR PRODUTO
    @Tool(
        description = """
            Consulta a quantidade disponivel de um
            produto especifico no estoque.
            Utilize quando o usuario perguntar
            sobre quantidade ou disponibilidade.
            """,
        returnDirect = true,
        resultConverter = TextoSimplesConverter.class
    )
    public String consultarEstoque(
            @ToolParam(description = "Nome do produto")
            String produto) {

        System.out.println(
            "FERRAMENTA EXECUTADA: consultarEstoque"
        );

        // Validacao do parametro
        if (produto == null || produto.isBlank()) {
            return "Produto invalido.";
        }

        // Normaliza o nome informado pela IA
        String nome = produto.trim()
                .toLowerCase(Locale.ROOT);

        System.out.println(
            "Produto recebido da IA: " + nome
        );

        // Converte plural para singular quando necessario
        nome = aliases.getOrDefault(nome, nome);

        System.out.println(
            "Produto normalizado: " + nome
        );

        // Consulta os dados do estoque
        Integer quantidade = estoque.get(nome);

        if (quantidade == null) {
            return "O produto " + nome
                    + " nao esta cadastrado no estoque.";
        }

        return "Produto: " + nome
                + " | Quantidade: " + quantidade;
    }

    // FERRAMENTA 2 - LISTAR ESTOQUE BAIXO
    @Tool(
        description = """
            Lista todos os produtos com estoque baixo
            ou esgotado para reposicao.

            Utilize quando o usuario perguntar
            quais produtos precisam ser repostos.
            """,
        returnDirect = true,
        resultConverter = TextoSimplesConverter.class
    )
    public String listarProdutosEstoqueBaixo() {

        System.out.println(
            "FERRAMENTA EXECUTADA: listarProdutosEstoqueBaixo"
        );

        int limite = 5;

        StringBuilder resultado = new StringBuilder();

        // Percorre todos os produtos
        for (Map.Entry<String, Integer> item
                : estoque.entrySet()) {

            String produto = item.getKey();
            int quantidade = item.getValue();

            // Produto esgotado
            if (quantidade == 0) {

                resultado.append(produto)
                    .append(": ESGOTADO (0 unidades)\n");

            // Produto com estoque baixo
            } else if (quantidade <= limite) {

                resultado.append(produto)
                    .append(": ESTOQUE BAIXO (")
                    .append(quantidade)
                    .append(" unidades)\n");
            }
        }

        // Nenhum produto com estoque baixo
        if (resultado.isEmpty()) {
            return "Nenhum produto com estoque baixo.";
        }

        return resultado.toString();
    }
    

@Tool(
    description = """
        Gera um relatorio geral do estoque,
        mostrando produtos cadastrados,
        unidades totais, estoque baixo
        e produtos esgotados.
        """,
    returnDirect = true,
    resultConverter = TextoSimplesConverter.class
)

public String gerarRelatorioEstoque() {

    System.out.println(
        "FERRAMENTA EXECUTADA: gerarRelatorioEstoque"
    );

    // Passo 1 - Criar as variaveis
    int totalProdutos = estoque.size();
    int totalUnidades = 0;
    int produtosBaixos = 0;
    int produtosEsgotados = 0;

    // Passo 2 - Percorrer o estoque
    for (Map.Entry<String, Integer> item
            : estoque.entrySet()) {

        int quantidade = item.getValue();

        // Somar as unidades
        totalUnidades += quantidade;

        // Verificar os produtos
        if (quantidade == 0) {

            produtosEsgotados++;

        } else if (quantidade <= 5) {

            produtosBaixos++;
        }
    }

    // Passo 3 - Construir o relatorio
    StringBuilder relatorio = new StringBuilder();

    relatorio.append("RELATORIO GERAL DO ESTOQUE\n");
    relatorio.append("--------------------------\n");

    relatorio.append("Produtos cadastrados: ")
             .append(totalProdutos).append("\n");

    relatorio.append("Total de unidades: ")
             .append(totalUnidades).append("\n");

    relatorio.append("Produtos com estoque baixo: ")
             .append(produtosBaixos).append("\n");

    relatorio.append("Produtos esgotados: ")
             .append(produtosEsgotados);

    // Passo 4 - Retornar o texto
    return relatorio.toString();
}


}
