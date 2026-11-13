
package br.com.portfolio.primeiraia.tools;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import br.com.portfolio.primeiraia.model.PerfilAluno;
import br.com.portfolio.primeiraia.repository.PerfilAlunoRepository;

@Component
public class PerfilAlunoTools {

    private final PerfilAlunoRepository repository;

    // CONSTRUTOR
    public PerfilAlunoTools(PerfilAlunoRepository repository) {
        this.repository = repository;
    }

    // FERRAMENTA DE CONSULTA
    @Tool(description = """
            Consulta o perfil cadastrado do aluno
            no banco de dados MySQL.

            Utilize quando o usuario perguntar
            seu nome, linguagem de estudo,
            objetivo ou perfil completo.

            Se o perfil nao existir, informe
            que ele nao foi encontrado.

            Nunca invente informacoes.
            """)
    public String consultarPerfilAluno(ToolContext contexto) {

        System.out.println(
                ">>> FUNCTION CALLING EXECUTADO: consultarPerfilAluno"
        );

        // PRIMEIRO: validar o contexto
        if (contexto == null || contexto.getContext() == null) {
            return "Identificador da conversa invalido.";
        }

        Object id = contexto.getContext().get("conversaId");

        if (!(id instanceof String conversaId)
                || conversaId.isBlank()) {

            return "Identificador da conversa invalido.";
        }

        String idValidado = conversaId.trim();

        // SEGUNDO: tentar consultar o MySQL
        try {

            System.out.println(
                    "FERRAMENTA EXECUTADA: consultarPerfilAluno"
            );

            return repository.buscarPorConversaId(idValidado)
                    .map(this::formatarPerfil)
                    .orElse(
                            "PERFIL_NAO_ENCONTRADO: "
                            + "O perfil do aluno nao foi encontrado."
                    );

        } catch (DataAccessException erro) {

            // TERCEIRO: registrar e propagar falhas do banco
            System.err.println(
                    "ERRO TECNICO NA CONSULTA DO PERFIL."
            );

            System.err.println(
                    "Tipo: " + erro.getClass().getSimpleName()
            );

            // Permite que a camada HTTP trate a falha
            // sem revelar detalhes internos do banco.
            throw erro;
        }
    }

    // FORMATA OS DADOS DO ALUNO
    private String formatarPerfil(PerfilAluno perfil) {

        return "Nome: " + perfil.getNome()
                + "\nLinguagem: " + perfil.getLinguagemEstudo()
                + "\nObjetivo: " + perfil.getObjetivo();
    }
}
