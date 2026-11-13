
package br.com.portfolio.primeiraia.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.portfolio.primeiraia.model.PerfilAluno;
import br.com.portfolio.primeiraia.repository.PerfilAlunoRepository;

@RestController
@RequestMapping("/api/perfil")
public class PerfilAlunoController {

    private final PerfilAlunoRepository repository;

    public PerfilAlunoController(PerfilAlunoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{conversaId}")
    public ResponseEntity<PerfilAluno> buscarPerfil(
            @PathVariable("conversaId") String conversaId) {

        return repository.buscarPorConversaId(conversaId)
                .map(perfil -> ResponseEntity.ok(perfil))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
