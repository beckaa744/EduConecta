package br.com.educonecta.model;

import java.util.List;
import java.time.LocalDateTime;

public class Aluno extends Usuario {

    public String visualizarPerfil() {
        return null;
    }

    public List<Monitor> buscarMonitor(Materia materia) {
        return null;
    }

    public Sessao solicitarSessao(Monitor monitor, Materia materia, LocalDateTime horario) {
        return null;
    }

    public Avaliacao avaliarMonitor(Sessao sessao, int nota, String comentario) {
        return null;
    }
}