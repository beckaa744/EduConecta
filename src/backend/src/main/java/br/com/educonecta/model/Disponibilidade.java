package br.com.educonecta.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Disponibilidade {
    public static final int DURACAO_SESSAO_MIN = 60;

    private int id;
    private int idMonitor;
    private int diaSemana;          // 1 = segunda ... 7 = domingo
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public Disponibilidade() {}

    public Disponibilidade(int idMonitor, int diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        setIdMonitor(idMonitor);
        setDiaSemana(diaSemana);
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        validarIntervalo();
    }

    /** RN02: a sessão de 60 min começa no dia certo e termina dentro da janela. */
    public boolean cobre(LocalDateTime dataHora) {
        if (dataHora == null || horaInicio == null || horaFim == null) return false;
        LocalTime inicio = dataHora.toLocalTime();
        LocalTime fim = inicio.plusMinutes(DURACAO_SESSAO_MIN);
        if (!fim.isAfter(inicio)) return false;   // passou da meia-noite
        return dataHora.getDayOfWeek().getValue() == diaSemana
                && !inicio.isBefore(horaInicio)
                && !fim.isAfter(horaFim);
    }

    private void validarIntervalo() {
        if (horaInicio == null || horaFim == null || !horaFim.isAfter(horaInicio))
            throw new IllegalArgumentException("A hora final deve ser posterior à hora inicial.");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdMonitor() { return idMonitor; }
    public void setIdMonitor(int idMonitor) {
        if (idMonitor <= 0) throw new IllegalArgumentException("ID do monitor inválido.");
        this.idMonitor = idMonitor;
    }
    public int getDiaSemana() { return diaSemana; }
    public void setDiaSemana(int diaSemana) {
        if (diaSemana < 1 || diaSemana > 7)
            throw new IllegalArgumentException("O dia da semana deve estar entre 1 e 7.");
        this.diaSemana = diaSemana;
    }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }
}
