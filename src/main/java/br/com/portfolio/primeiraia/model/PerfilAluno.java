
package br.com.portfolio.primeiraia.model;

public class PerfilAluno {

    private Long id;
    private String conversaId;
    private String nome;
    private String linguagemEstudo;
    private String objetivo;

    public PerfilAluno(
            Long id,
            String conversaId,
            String nome,
            String linguagemEstudo,
            String objetivo) {

        this.id = id;
        this.conversaId = conversaId;
        this.nome = nome;
        this.linguagemEstudo = linguagemEstudo;
        this.objetivo = objetivo;
    }

    public Long getId() {
        return id;
    }

    public String getConversaId() {
        return conversaId;
    }

    public String getNome() {
        return nome;
    }

    public String getLinguagemEstudo() {
        return linguagemEstudo;
    }

    public String getObjetivo() {
        return objetivo;
    }
}
