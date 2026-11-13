package br.com.educonecta.model;

import java.time.LocalTime;

public class Disponibilidade {
    private int id;
    private int idMonitor;
    private int diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public Disponibilidade(int idMonitor, int diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        this.idMonitor = idMonitor;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        validar();
    }

    public void validar() {
        if (diaSemana < 1 || diaSemana > 7)
            throw new IllegalArgumentException("Dia da semana deve estar entre 1 (segunda) e 7 (domingo).");
        if (horaInicio == null || horaFim == null || !horaInicio.isBefore(horaFim))
            throw new IllegalArgumentException("O horário inicial deve ser anterior ao horário final.");
    }

    public boolean contemHorario(LocalTime horario) {
        return horario != null && !horario.isBefore(horaInicio) && horario.isBefore(horaFim);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdMonitor() { return idMonitor; }
    public int getDiaSemana() { return diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }
}
