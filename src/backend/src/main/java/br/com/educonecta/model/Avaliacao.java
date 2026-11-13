package br.com.educonecta.model;

public class Avaliacao {
    private int id;
    private int idSessao;
    private int nota;
    private String comentario;

    public Avaliacao() {}

    public Avaliacao(int idSessao, int nota, String comentario) {
        this.idSessao = idSessao;
        this.nota = nota;
        this.comentario = comentario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdSessao() { return idSessao; }
    public void setIdSessao(int idSessao) { this.idSessao = idSessao; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}