package br.com.educonecta.model;

public class Aluno extends Usuario {
    private String curso;
    private String periodo;

    public Aluno() {}

    public Aluno(int id, String nome, String email, String senha, String curso, String periodo) {
        super(id, nome, email, senha);
        this.curso = curso;
        this.periodo = periodo;
    }

    @Override
    public String visualizarPerfil() {
        return "Aluno: " + getNome() + " | Curso: " + curso + " | Período: " + periodo;
    }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
}
