package br.com.educonecta;

import br.com.educonecta.dao.AlunoDAO;
import br.com.educonecta.dao.DisponibilidadeDAO;
import br.com.educonecta.dao.MonitorDAO;
import br.com.educonecta.model.Aluno;
import br.com.educonecta.model.Disponibilidade;
import br.com.educonecta.model.Monitor;

import java.time.LocalTime;

public class TesteCrud {

    static void conferir(String titulo, boolean ok) {
        System.out.println((ok ? "[OK] " : "[ERRO] ") + titulo);
    }

    public static void main(String[] args) throws Exception {
        MonitorDAO monitorDAO = new MonitorDAO();
        AlunoDAO alunoDAO = new AlunoDAO();
        DisponibilidadeDAO dispDAO = new DisponibilidadeDAO();

        Monitor m = new Monitor(0, "Ana Souza", "monitor.crud@email.com", "123", "Monitora de Java");
        Aluno a = new Aluno(0, "João Lima", "aluno.crud@email.com", "123", "ADS", "3º");

        try {
            // ===== MONITOR =====
            System.out.println("=== CRUD de Monitor ===");
            monitorDAO.inserir(m);
            conferir("CREATE: monitor inserido com id " + m.getId(), m.getId() > 0);

            Monitor lido = monitorDAO.buscarPorId(m.getId());
            conferir("READ: buscarPorId devolve o monitor", lido != null && "Ana Souza".equals(lido.getNome()));
            if (lido != null) System.out.println("     " + lido.visualizarPerfil());

            conferir("READ: listar contém o monitor",
                    monitorDAO.listar().stream().anyMatch(x -> x.getId() == m.getId()));

            m.setNome("Ana Souza Lima");
            m.setBiografia("Monitora de Java e Banco de Dados");
            monitorDAO.atualizar(m);
            lido = monitorDAO.buscarPorId(m.getId());
            conferir("UPDATE: nome e biografia alterados no banco",
                    lido != null && "Ana Souza Lima".equals(lido.getNome())
                            && "Monitora de Java e Banco de Dados".equals(lido.getBiografia()));

            // ===== ALUNO =====
            System.out.println("=== CRUD de Aluno ===");
            alunoDAO.inserir(a);
            conferir("CREATE: aluno inserido com id " + a.getId(), a.getId() > 0);

            Aluno alunoLido = alunoDAO.buscarPorId(a.getId());
            conferir("READ: buscarPorId devolve o aluno", alunoLido != null && "João Lima".equals(alunoLido.getNome()));
            if (alunoLido != null) System.out.println("     " + alunoLido.visualizarPerfil());

            conferir("READ: listar contém o aluno",
                    alunoDAO.listar().stream().anyMatch(x -> x.getId() == a.getId()));

            a.setCurso("Engenharia de Software");
            a.setPeriodo("4º");
            alunoDAO.atualizar(a);
            alunoLido = alunoDAO.buscarPorId(a.getId());
            conferir("UPDATE: curso e período alterados no banco",
                    alunoLido != null && "Engenharia de Software".equals(alunoLido.getCurso())
                            && "4º".equals(alunoLido.getPeriodo()));

            alunoDAO.deletar(a.getId());
            conferir("DELETE: aluno removido", alunoDAO.buscarPorId(a.getId()) == null);

            // ===== DISPONIBILIDADE =====
            System.out.println("=== CRUD de Disponibilidade ===");
            Disponibilidade d = new Disponibilidade(m.getId(), 2, LocalTime.of(14, 0), LocalTime.of(16, 0));
            dispDAO.inserir(d);
            conferir("CREATE: disponibilidade inserida com id " + d.getId(), d.getId() > 0);

            conferir("READ: listarPorMonitor devolve 1 janela", dispDAO.listarPorMonitor(m.getId()).size() == 1);

            d.setDiaSemana(3);
            d.setHoraInicio(LocalTime.of(9, 0));
            d.setHoraFim(LocalTime.of(11, 0));
            dispDAO.atualizar(d);
            Disponibilidade dLida = dispDAO.buscarPorId(d.getId());
            conferir("UPDATE: dia e horários alterados no banco",
                    dLida != null && dLida.getDiaSemana() == 3
                            && LocalTime.of(9, 0).equals(dLida.getHoraInicio())
                            && LocalTime.of(11, 0).equals(dLida.getHoraFim()));

            dispDAO.deletar(d.getId());
            conferir("DELETE: disponibilidade removida", dispDAO.buscarPorId(d.getId()) == null);

            // ===== EXCLUSÃO EM CASCATA =====
            System.out.println("=== Exclusão em cascata ===");
            Disponibilidade d2 = new Disponibilidade(m.getId(), 4, LocalTime.of(10, 0), LocalTime.of(12, 0));
            dispDAO.inserir(d2);

            monitorDAO.deletar(m.getId());
            conferir("DELETE: monitor removido", monitorDAO.buscarPorId(m.getId()) == null);
            conferir("CASCADE: a disponibilidade do monitor sumiu junto", dispDAO.buscarPorId(d2.getId()) == null);

        } finally {
            // limpeza: roda mesmo que algum passo acima tenha dado erro
            if (m.getId() > 0) monitorDAO.deletar(m.getId());
            if (a.getId() > 0) alunoDAO.deletar(a.getId());
            System.out.println("Limpeza feita.");
        }
    }
}
