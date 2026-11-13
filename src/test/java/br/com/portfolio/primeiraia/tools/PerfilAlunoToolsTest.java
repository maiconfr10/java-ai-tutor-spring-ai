
package br.com.portfolio.primeiraia.tools;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.ai.chat.model.ToolContext;

import br.com.portfolio.primeiraia.model.PerfilAluno;
import br.com.portfolio.primeiraia.repository.PerfilAlunoRepository;

import org.springframework.dao.DataAccessResourceFailureException;

public class PerfilAlunoToolsTest {

	private PerfilAlunoRepository repository;
	private PerfilAlunoTools ferramenta;

	// EXECUTADO ANTES DE CADA TESTE
	@BeforeEach
	void prepararTeste() {

		// Simula o repositorio do MySQL
		repository = mock(PerfilAlunoRepository.class);

		// Cria a ferramenta usando o repositorio simulado
		ferramenta = new PerfilAlunoTools(repository);
	}

	// TESTE 1 - ALUNO CADASTRADO
	@Test
	void deveConsultarAlunoCadastrado() {

		// Simula um aluno
		PerfilAluno aluno = mock(PerfilAluno.class);

		when(aluno.getNome()).thenReturn("Ana");
		when(aluno.getLinguagemEstudo()).thenReturn("Python");
		when(aluno.getObjetivo()).thenReturn("Aprender automacao com IA");

		// Simula a resposta do repositorio
		when(repository.buscarPorConversaId("aluno-001")).thenReturn(Optional.of(aluno));

		// Cria o contexto da ferramenta
		ToolContext contexto = new ToolContext(Map.of("conversaId", "aluno-001"));

		// Executa a ferramenta
		String resposta = ferramenta.consultarPerfilAluno(contexto);

		// Verifica as informacoes
		assertTrue(resposta.contains("Nome: Ana"));

		assertTrue(resposta.contains("Linguagem: Python"));

		assertTrue(resposta.contains("Aprender automacao com IA"));

		// Confirma que o repositorio foi consultado
		verify(repository).buscarPorConversaId("aluno-001");
	}

	// TESTE 2 - ALUNO NAO CADASTRADO
	@Test
	void deveInformarQuandoAlunoNaoExiste() {

		// Simula uma consulta sem resultados
		when(repository.buscarPorConversaId("aluno-999")).thenReturn(Optional.empty());

		ToolContext contexto = new ToolContext(Map.of("conversaId", "aluno-999"));

		String resposta = ferramenta.consultarPerfilAluno(contexto);

		// Verifica se informou corretamente
		assertTrue(resposta.contains("PERFIL_NAO_ENCONTRADO"));

		// Garante que o metodo nao inventou um nome
		assertFalse(resposta.contains("Nome:"));
	}

	// TESTE 3 - IDENTIFICADOR INVALIDO
	@Test
	void deveRejeitarConversaIdVazio() {

		// Simula uma conversa com apenas espacos
		ToolContext contexto = new ToolContext(Map.of("conversaId", "   "));

		// Executa nossa ferramenta
		String resposta = ferramenta.consultarPerfilAluno(contexto);

		// Confirma a mensagem esperada
		assertEquals("Identificador da conversa invalido.", resposta);

		// Confirma que o banco nem foi consultado
		verifyNoInteractions(repository);
	}

	// TESTE 4 - SIMULAR FALHA NO MYSQL
	@Test
	void devePropagarErroQuandoBancoFalhar() {

		// 1. Simula uma falha no banco
		when(repository.buscarPorConversaId("aluno-erro-001"))
				.thenThrow(new DataAccessResourceFailureException("Falha simulada no MySQL"));

		// 2. Cria o contexto da conversa
		ToolContext contexto = new ToolContext(Map.of("conversaId", "aluno-erro-001"));

		// 3. Verifica se a excecao foi propagada
		DataAccessResourceFailureException erro = assertThrows(DataAccessResourceFailureException.class,
				() -> ferramenta.consultarPerfilAluno(contexto));

		// 4. Confirma a excecao esperada
		assertEquals("Falha simulada no MySQL", erro.getMessage());

		// 5. Confirma que o repositorio foi chamado
		verify(repository, times(1)).buscarPorConversaId("aluno-erro-001");
	}

}
