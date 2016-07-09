package br.edu.ifspsaocarlos.sdm.trabalhofinal;

/**
 * Created by AvellB155MAX on 06/07/2016.
 */
public class Contato {
    private Integer id;
    private String nome;
    private String apelido;

    public Contato(Integer id, String nome, String apelid) {
        this.id = id;
        this.nome = nome;
        this.apelido = apelid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelid) {
        this.apelido = apelid;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

