
package br.com.portfolio.primeiraia.dto;

public class MemoriaRequest {

    private String conversaId;
    private String mensagem;

    public MemoriaRequest() {
    }

    public String getConversaId() {
        return conversaId;
    }

    public void setConversaId(String conversaId) {
        this.conversaId = conversaId;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
