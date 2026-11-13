package br.com.educonecta.model;

import java.time.LocalDateTime;

public class Sessao {
    private int id;
    private LocalDateTime dataHora;
    private String status;

    public Sessao() {}
    public Sessao(int id, LocalDateTime dataHora, String status) {
        this.id = id;
        this.dataHora = dataHora;
        this.status = status;
    }

    public void concluir() { this.status = "concluida"; }
    public boolean estaConcluida() { return "concluida".equalsIgnoreCase(status); }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
