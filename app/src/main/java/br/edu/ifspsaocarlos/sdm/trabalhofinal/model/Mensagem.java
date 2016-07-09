package br.edu.ifspsaocarlos.sdm.trabalhofinal.model;

import java.io.Serializable;

public class Mensagem implements Serializable {
    private Integer id;
    private String origem;
    private String destino;
    private String assunto;
    private String corpo;

    public Mensagem() {
    }

    public Mensagem(Integer id, String origem, String destino, String assunto, String corpo) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.assunto = assunto;
        this.corpo = corpo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mensagem mensagem = (Mensagem) o;

        return id.equals(mensagem.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
