
package br.com.portfolio.primeiraia.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import br.com.portfolio.primeiraia.tools.EstoqueTools;

import br.com.portfolio.primeiraia.dto.RespostaEstruturada;
import br.com.portfolio.primeiraia.dto.ResumoEstruturado;

@Service
public class ChatService {

	private final ChatClient chatClient;
	private final EstoqueTools estoqueTools;

	public ChatService(ChatClient.Builder builder, EstoqueTools estoqueTools) {
		this.estoqueTools = estoqueTools;
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

		return chatClient.prompt().system(s -> s.param("nivel", nivel)).user(mensagem).call().content();
	}

	public RespostaEstruturada responderEstruturado(String mensagem, String nivel) {

		return chatClient.prompt().system(s -> s.text("""
				Voce e um professor especialista em Java.
				Explique para um aluno de nivel {nivel}.

				Preencha os campos:
				conceito, explicacao e exemploJava.

				O exemploJava deve conter codigo Java.
				Responda de maneira didatica.
				""").param("nivel", nivel)).user(mensagem).call().entity(RespostaEstruturada.class);
	}

	public ResumoEstruturado resumirTexto(String texto) {

		return chatClient.prompt().system("""
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
				""").user(texto).call().entity(ResumoEstruturado.class);
	}


public String consultarEstoqueComIA(String mensagem) {

    return chatClient.prompt()
        .system("""
            Voce e um agente brasileiro
            especializado em controle de estoque.

            FERRAMENTAS DISPONIVEIS:

            1. consultarEstoque:
            Use quando o usuario perguntar
            sobre a quantidade ou disponibilidade
            de um produto especifico.
            Reconheca nomes no singular e plural.

            2. listarProdutosEstoqueBaixo:
            Use quando o usuario perguntar
            quais produtos precisam de reposicao,
            estao com estoque baixo ou esgotados.

            3. gerarRelatorioEstoque:
            Use quando o usuario solicitar
            um relatorio geral ou resumo completo
            do estoque.

            Esta ferramenta apresenta:
            - Quantidade de produtos cadastrados.
            - Total de unidades em estoque.
            - Produtos com estoque baixo.
            - Produtos esgotados.

            REGRAS OBRIGATORIAS:

            1. Para consultas de estoque, utilize
               a ferramenta correspondente.

            2. Nunca invente quantidades.

            3. Quando houver varios produtos,
               preserve todas as informacoes.

            4. Nunca omita produtos esgotados.

            5. Diferencie produto nao cadastrado
               de produto com quantidade zero.

            6. Nao solicite novamente informacoes
               que o usuario ja forneceu.

            7. Responda exclusivamente em
               portugues brasileiro.

            8. Utilize os dados retornados
               pelas ferramentas.

            9. Nunca diga que vai consultar depois.

            10. Seja claro, direto e objetivo.
            """)
        .user(mensagem)
        .tools(estoqueTools)
        .call()
        .content();
}


}
