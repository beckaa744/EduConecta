package br.com.educonecta.model;

import java.time.LocalDateTime;

public class Sessao {
    private int id;
    private int idAluno;
    private int idMonitor;
    private int idMateria;
    private LocalDateTime dataHora;
    private StatusSessao status = StatusSessao.PENDENTE;

    public Sessao() {}

    public Sessao(int idAluno, int idMonitor, int idMateria, LocalDateTime dataHora) {
        this.idAluno = idAluno;
        this.idMonitor = idMonitor;
        this.idMateria = idMateria;
        this.dataHora = dataHora;
    }

    public void confirmar() {
        if (status != StatusSessao.PENDENTE)
            throw new IllegalStateException("Somente sessões pendentes podem ser confirmadas.");
        status = StatusSessao.CONFIRMADA;
    }

    public void concluir() {
        if (status != StatusSessao.CONFIRMADA)
            throw new IllegalStateException("Somente sessões confirmadas podem ser concluídas.");
        status = StatusSessao.CONCLUIDA;
    }

    public void cancelar() {
        if (status == StatusSessao.CONCLUIDA)
            throw new IllegalStateException("Sessão concluída não pode ser cancelada.");
        status = StatusSessao.CANCELADA;
    }

    public boolean estaConcluida() { return status == StatusSessao.CONCLUIDA; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdAluno() { return idAluno; }
    public void setIdAluno(int idAluno) { this.idAluno = idAluno; }
    public int getIdMonitor() { return idMonitor; }
    public void setIdMonitor(int idMonitor) { this.idMonitor = idMonitor; }
    public int getIdMateria() { return idMateria; }
    public void setIdMateria(int idMateria) { this.idMateria = idMateria; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public StatusSessao getStatus() { return status; }
    public void setStatus(StatusSessao status) { this.status = status; }
}