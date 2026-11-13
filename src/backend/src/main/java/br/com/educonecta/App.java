package br.com.educonecta;

import br.com.educonecta.dao.DisponibilidadeDAO;
import br.com.educonecta.dao.MonitorDAO;
import br.com.educonecta.model.Disponibilidade;
import br.com.educonecta.model.Monitor;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class App {
    public static void main(String[] args) {
        MonitorDAO monitorDao = new MonitorDAO();
        DisponibilidadeDAO dd = new DisponibilidadeDAO();

        try {
            // cria um monitor para ter um id válido
            Monitor m = new Monitor(0, "Carlos", "carlos@email.com", "123", "Monitor de BD");
            monitorDao.inserir(m);
            System.out.println("Monitor criado com id: " + m.getId());

            // cria uma janela: terça, das 14h às 16h
            Disponibilidade d = new Disponibilidade(m.getId(), 2, LocalTime.of(14, 0), LocalTime.of(16, 0));
            dd.inserir(d);
            System.out.println("Disponibilidade criada com id: " + d.getId());

            // testa a RN02 (sem banco, só a regra)
            System.out.println("Terça 15h cabe? " + d.cobre(LocalDateTime.of(2026, 11, 17, 15, 0)));   // true
            System.out.println("Quarta 15h cabe? " + d.cobre(LocalDateTime.of(2026, 11, 18, 15, 0))); // false

            // lê do banco
            System.out.println("Janelas do monitor: " + dd.listarPorMonitor(m.getId()).size());     // 1

            // limpa: apagar o monitor apaga a disponibilidade (ON DELETE CASCADE)
            monitorDao.deletar(m.getId());
            System.out.println("Limpeza feita.");

        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}