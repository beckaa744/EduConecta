package br.com.educonecta.model;

public class Monitor extends Usuario {
    private String biografia;
    private double mediaAvaliacao;

    public Monitor() {}

    public Monitor(int id, String nome, String email, String senha, String biografia) {
        super(id, nome, email, senha);
        this.biografia = biografia;
    }

    @Override
    public String visualizarPerfil() {
        return "Monitor: " + getNome() + " | " + biografia + " | Média: " + mediaAvaliacao;
    }

    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
    public double getMediaAvaliacao() { return mediaAvaliacao; }
    public void setMediaAvaliacao(double mediaAvaliacao) { this.mediaAvaliacao = mediaAvaliacao; }
}