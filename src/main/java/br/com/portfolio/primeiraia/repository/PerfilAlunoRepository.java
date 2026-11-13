
package br.com.portfolio.primeiraia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.portfolio.primeiraia.model.PerfilAluno;

@Repository
public class PerfilAlunoRepository {

    private final JdbcTemplate jdbcTemplate;

    // Recebe a ferramenta de acesso ao MySQL
    public PerfilAlunoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Busca o perfil pelo ID da conversa
    public Optional<PerfilAluno> buscarPorConversaId(
            String conversaId) {

        String sql = """
                SELECT id,
                       conversa_id,
                       nome,
                       linguagem_estudo,
                       objetivo
                FROM perfil_aluno
                WHERE conversa_id = ?
                LIMIT 1
                """;

        List<PerfilAluno> perfis = jdbcTemplate.query(
                sql,
                (rs, linha) -> new PerfilAluno(
                        rs.getLong("id"),
                        rs.getString("conversa_id"),
                        rs.getString("nome"),
                        rs.getString("linguagem_estudo"),
                        rs.getString("objetivo")
                ),
                conversaId
        );

        return perfis.stream().findFirst();
    }
}
