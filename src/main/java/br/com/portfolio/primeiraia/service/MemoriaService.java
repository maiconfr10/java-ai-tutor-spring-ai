package br.com.portfolio.primeiraia.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.stereotype.Service;

import br.com.portfolio.primeiraia.tools.EstoqueTools;
import br.com.portfolio.primeiraia.tools.PerfilAlunoTools;

@Service
public class MemoriaService {

    // Os valores do estoque continuam somente dentro de EstoqueTools.
    private static final Pattern NOMES_PRODUTOS = Pattern.compile(
            "\\b(mouses?|teclados?|monitor(?:es)?|impressoras?)\\b");

    private static final Pattern CAMPO_QUANTIDADE = Pattern.compile(
            "Quantidade\\s*:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    private final ChatClient chatClient;
    private final ChatMemory memoria;
    private final EstoqueTools estoqueTools;
    private final PerfilAlunoTools perfilAlunoTools;

    public MemoriaService(ChatModel chatModel,
                          ChatMemoryRepository repository,
                          EstoqueTools estoqueTools,
                          PerfilAlunoTools perfilAlunoTools) {

        this.estoqueTools = estoqueTools;
        this.perfilAlunoTools = perfilAlunoTools;

        this.memoria = MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10)
                .build();

        System.out.println("REPOSITORIO DE MEMORIA: "
                + repository.getClass().getSimpleName());

        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem("""
                        Voce e um assistente brasileiro especializado em educacao
                        e controle de estoque.

                        REGRAS:
                        - Responda sempre em portugues brasileiro.
                        - Seja claro, objetivo e didatico.
                        - Nao invente informacoes nem quantidades.
                        - Use o historico para compreender o contexto.
                        - Use voce, seu e sua ao falar com o usuario.
                        - Para quantidades atuais, use as ferramentas Java.
                        - Para reposicao, use listarProdutosEstoqueBaixo.
                        - Para relatorios gerais, use gerarRelatorioEstoque.
                        - Para dados cadastrais do aluno, use consultarPerfilAluno.
                        - Nunca solicite nome ou ID se ja forem conhecidos no sistema.
                        - Caso nao encontre um dado, explique que nao foi encontrado.
                        """)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(this.memoria).build())
                .build();
    }

    public String conversar(String conversaId, String mensagem) {
        if (conversaId == null || conversaId.isBlank()) {
            throw new IllegalArgumentException("O ID da conversa e obrigatorio.");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("A mensagem nao pode estar vazia.");
        }

        final String idConversa = conversaId.trim();
        final String texto = mensagem.trim();

        System.out.println("\n=========================");
        System.out.println("INICIANDO CONVERSA: " + idConversa);
        mostrarHistorico(idConversa, "ANTES DA IA");

        // Passo 1: o Java decide quando a pergunta exige uma consulta direta.
        String resposta = responderPerfilSeAplicavel(idConversa, texto);
        if (resposta == null) {
            resposta = responderEstoqueSeAplicavel(idConversa, texto);
        }

        if (resposta != null) {
            // Uma resposta direta nao passou pelo advisor, entao salvamos
            // somente nesta rota as duas mensagens na memoria persistente.
            memoria.add(idConversa, new UserMessage(texto));
            memoria.add(idConversa, new AssistantMessage(resposta));
            System.out.println("ROTA: CONSULTA DIRETA PELO JAVA");
        } else {
            // Para perguntas gerais, o advisor gerencia a memoria sozinho.
            // Nao chamar memoria.add() novamente nesta rota.
            resposta = chatClient.prompt()
                    .user(texto)
                    .tools(estoqueTools, perfilAlunoTools)
                    .toolContext(Map.of("conversaId", idConversa))
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, idConversa))
                    .call()
                    .content();

            System.out.println("ROTA: CHATCLIENT COM MEMORIA E TOOLS");
        }

        System.out.println("\nRESPOSTA DO SISTEMA:\n" + resposta);
        mostrarHistorico(idConversa, "DEPOIS DA IA");
        return resposta;
    }

    // Consulta os dados cadastrais pelo ID da conversa, sem depender do LLM.
    // Se o usuario pedir varios campos, todos serao incluidos na mesma resposta.
    private String responderPerfilSeAplicavel(String conversaId, String mensagem) {
        String frase = normalizar(mensagem);

        boolean perguntaPerfil = contemAlgum(frase,
                "meu perfil", "minha ficha", "perfil cadastrado",
                "perfil do aluno", "meu nome", "nome cadastrado",
                "minha linguagem", "linguagem de estudo", "linguagem estou estudando",
                "meu objetivo", "objetivo de estudo", "objetivo cadastrado",
                "o que estou estudando", "meus dados cadastrais",
                "meus dados cadastrados", "qual linguagem eu estudo",
                "qual e meu nome");

        if (!perguntaPerfil) {
            return null; // Segue para estoque ou para o ChatClient.
        }

        System.out.println("CONSULTA JAVA: consultarPerfilAluno | conversaId=" + conversaId);

        // O ID vem da API pelo Java; o modelo nao precisa adivinha-lo.
        String dados = perfilAlunoTools.consultarPerfilAluno(
                new ToolContext(Map.of("conversaId", conversaId)));

        if (dados == null || dados.isBlank()) {
            return "Perfil do aluno nao encontrado.";
        }

        // Formato retornado pela ferramenta:
        // Nome: Pedro\nLinguagem: Java\nObjetivo: Aprender a desenvolver agentes de IA
        String nome = extrairCampo(dados, "Nome:");
        String linguagem = extrairCampo(dados, "Linguagem:");
        String objetivo = extrairCampo(dados, "Objetivo:");

        // Se a ferramenta retornar um aviso/erro, preserva a mensagem original.
        if (nome == null && linguagem == null && objetivo == null) {
            return dados;
        }

        // Cada campo e verificado separadamente: nao ha return antecipado.
        boolean pediuNome = frase.contains("nome");
        boolean pediuLinguagem = contemAlgum(frase,
                "linguagem", "o que estou estudando", "qual linguagem eu estudo",
                "qual tecnologia estou estudando");
        boolean pediuObjetivo = contemAlgum(frase,
                "objetivo", "minha meta de estudo", "meta cadastrada");

        // Quando pede apenas "meu perfil", mostra todos os campos.
        if (!pediuNome && !pediuLinguagem && !pediuObjetivo) {
            pediuNome = true;
            pediuLinguagem = true;
            pediuObjetivo = true;
        }

        List<String> partes = new ArrayList<>();

        if (pediuNome) {
            partes.add(nome != null && !nome.isBlank()
                    ? "Seu nome é " + comPontoFinal(nome)
                    : "Seu nome não está cadastrado.");
        }

        if (pediuLinguagem) {
            partes.add(linguagem != null && !linguagem.isBlank()
                    ? "Você está estudando " + comPontoFinal(linguagem)
                    : "Sua linguagem de estudo não está cadastrada.");
        }

        if (pediuObjetivo) {
            partes.add(objetivo != null && !objetivo.isBlank()
                    ? "Seu objetivo de estudo é " + comPontoFinal(objetivo)
                    : "Seu objetivo de estudo não está cadastrado.");
        }

        return String.join(" ", partes);
    }

    private static String extrairCampo(String texto, String prefixo) {
        for (String linha : texto.split("\\R")) {
            if (linha.stripLeading().regionMatches(true, 0, prefixo, 0, prefixo.length())) {
                return linha.stripLeading().substring(prefixo.length()).trim();
            }
        }
        return null;
    }

    private static String comPontoFinal(String texto) {
        String valor = texto.trim();
        return valor.endsWith(".") ? valor : valor + ".";
    }

    private String responderEstoqueSeAplicavel(String conversaId, String mensagem) {
        String frase = normalizar(mensagem);
        List<String> produtos = extrairProdutos(frase);

        if ((frase.contains("relatorio") || frase.contains("resumo geral")
                || frase.contains("resumo completo"))
                && (frase.contains("estoque") || frase.contains("produto"))) {
            System.out.println("CONSULTA JAVA: gerarRelatorioEstoque");
            return estoqueTools.gerarRelatorioEstoque();
        }

        if (contemAlgum(frase, "reposicao", "repor", "estoque baixo",
                "esgotad", "produtos em falta")) {
            System.out.println("CONSULTA JAVA: listarProdutosEstoqueBaixo");
            return estoqueTools.listarProdutosEstoqueBaixo();
        }

        boolean comparacao = contemAlgum(frase,
                "qual dos dois", "qual deles", "compar", "menos unidades",
                "mais unidades", "maior quantidade", "menor quantidade");
        boolean assuntoEstoque = contemAlgum(frase,
                "produto", "unidade", "estoque") || !produtos.isEmpty();

        if (comparacao && assuntoEstoque) {
            return compararProdutos(conversaId, produtos, frase);
        }

        boolean perguntaQuantidade = contemAlgum(frase,
                "quantos", "quantas", "estoque", "unidade", "disponiv",
                "temos", "possui", "existe");

        if (perguntaQuantidade && !produtos.isEmpty()) {
            List<String> respostas = new ArrayList<>();
            for (String produto : produtos) {
                respostas.add(consultarProduto(produto));
            }
            return String.join("\n", respostas);
        }

        return null;
    }

    private String compararProdutos(String conversaId,
                                    List<String> produtosDoPedido,
                                    String pergunta) {
        Set<String> encontrados = new LinkedHashSet<>(produtosDoPedido);
        List<Message> historico = memoria.get(conversaId);

        for (int i = historico.size() - 1; i >= 0 && encontrados.size() < 2; i--) {
            Message anterior = historico.get(i);
            if (anterior instanceof UserMessage) {
                encontrados.addAll(extrairProdutos(anterior.getText()));
            }
        }

        if (encontrados.size() < 2) {
            return "Preciso que voce informe quais dois produtos deseja comparar.";
        }

        List<String> nomes = new ArrayList<>(encontrados);
        String primeiro = nomes.get(0);
        String segundo = nomes.get(1);
        Integer quantidadePrimeiro = quantidadeAtual(primeiro);
        Integer quantidadeSegundo = quantidadeAtual(segundo);

        if (quantidadePrimeiro == null || quantidadeSegundo == null) {
            return "Nao consegui confirmar a quantidade dos dois produtos no estoque.";
        }

        String resumo = "O produto " + primeiro + " tem " + quantidadePrimeiro
                + " unidades, e o produto " + segundo + " tem "
                + quantidadeSegundo + " unidades. ";

        if (quantidadePrimeiro.equals(quantidadeSegundo)) {
            return resumo + "Os dois possuem a mesma quantidade.";
        }

        boolean pedeMaior = contemAlgum(pergunta, "mais", "maior");
        String escolhido = pedeMaior
                ? (quantidadePrimeiro > quantidadeSegundo ? primeiro : segundo)
                : (quantidadePrimeiro < quantidadeSegundo ? primeiro : segundo);

        return resumo + "Portanto, " + escolhido + " possui "
                + (pedeMaior ? "mais" : "menos") + " unidades.";
    }

    private String consultarProduto(String produto) {
        Integer quantidade = quantidadeAtual(produto);
        if (quantidade == null) {
            return "O produto " + produto + " nao esta cadastrado no estoque "
                    + "ou nao foi possivel confirmar sua quantidade.";
        }
        if (quantidade == 0) {
            return "O produto " + produto + " esta esgotado (0 unidades).";
        }
        return "O produto " + produto + " possui " + quantidade
                + (quantidade == 1 ? " unidade" : " unidades") + " no estoque.";
    }

    private Integer quantidadeAtual(String produto) {
        System.out.println("CONSULTA JAVA: consultarEstoque(" + produto + ")");
        String dados = estoqueTools.consultarEstoque(produto);
        if (dados == null) {
            return null;
        }
        Matcher matcher = CAMPO_QUANTIDADE.matcher(dados);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : null;
    }

    private List<String> extrairProdutos(String texto) {
        Matcher matcher = NOMES_PRODUTOS.matcher(normalizar(texto));
        Set<String> encontrados = new LinkedHashSet<>();

        while (matcher.find()) {
            String nome = matcher.group(1);
            String singular = switch (nome) {
                case "mouses" -> "mouse";
                case "teclados" -> "teclado";
                case "monitores" -> "monitor";
                case "impressoras" -> "impressora";
                default -> nome;
            };
            encontrados.add(singular);
        }
        return new ArrayList<>(encontrados);
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private static boolean contemAlgum(String texto, String... trechos) {
        for (String trecho : trechos) {
            if (texto.contains(trecho)) {
                return true;
            }
        }
        return false;
    }

    private void mostrarHistorico(String conversaId, String momento) {
        List<Message> mensagens = memoria.get(conversaId);
        System.out.println("\n=== HISTORICO " + momento + " ===");
        System.out.println("Quantidade de mensagens: " + mensagens.size());
        if (mensagens.isEmpty()) {
            System.out.println("Historico vazio.");
        }
        for (Message msg : mensagens) {
            System.out.println(msg.getMessageType() + ": " + msg.getText());
        }
        System.out.println("=========================");
    }

    // ============================================================
    // AULA 8 - MODO EXPERIMENTAL: QWEN ESCOLHE AS FERRAMENTAS
    // ============================================================
    public String conversarComIA(String conversaId, String mensagem) {
        if (conversaId == null || conversaId.isBlank()) {
            throw new IllegalArgumentException("O ID da conversa e obrigatorio.");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("A mensagem nao pode estar vazia.");
        }

        final String idConversa = conversaId.trim();
        final String texto = mensagem.trim();

        System.out.println("MODO EXPERIMENTAL: IA + FERRAMENTAS");
        System.out.println("Conversa ID: " + idConversa);

        // Diferentemente de conversar(), nao ha consulta de perfil
        // feita diretamente por condicoes Java neste metodo.
        // O Qwen recebe as ferramentas e pode solicitar sua execucao.
        String resposta = chatClient.prompt()
                .system("""
                    Voce e um agente de IA brasileiro especializado em estudantes.
                    Voce possui ferramentas Java capazes de consultar dados reais no MySQL.

                    REGRAS OBRIGATORIAS:

                    1. Quando o usuario perguntar sobre nome, linguagem de estudo,
                       objetivo ou perfil cadastrado, solicite a execucao da
                       ferramenta consultarPerfilAluno.

                    2. Consulte a ferramenta antes de responder perguntas
                       sobre o perfil cadastrado, mesmo que o historico
                       contenha informacoes anteriores sobre o aluno.

                    3. O identificador da conversa ja e fornecido pelo sistema
                       ao codigo Java por meio do ToolContext.
                       Nao tente inventar ou solicitar esse identificador.

                    4. Nao solicite o nome do aluno para consultar o proprio
                       perfil. A ferramenta localiza o cadastro pelo ID da conversa.

                    5. Nunca invente informacoes ou quantidades.

                    6. Para responder sobre o perfil, use somente os dados
                       retornados por consultarPerfilAluno nesta requisicao.

                    7. Se a ferramenta informar que nao ha perfil cadastrado,
                       diga claramente que o perfil nao foi encontrado.

                    8. Responda sempre em portugues brasileiro, utilizando
                       voce, seu e sua ao se dirigir ao usuario.

                    9. Quando a pergunta for sobre estoque, utilize a ferramenta
                       apropriada de estoque para consultar dados atuais.

                    IMPORTANTE:
                    A ferramenta consultarPerfilAluno esta disponivel.
                    Nao diga apenas que precisa consultar o banco.
                    Solicite a execucao da ferramenta quando necessario.
                    """)
                .user(texto)
                .tools(perfilAlunoTools, estoqueTools)
                .toolContext(Map.of("conversaId", idConversa))
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID, idConversa))
                .call()
                .content();

        System.out.println("RESPOSTA DO MODO EXPERIMENTAL: " + resposta);
        return resposta;
    }
}
