package br.com.educonecta;

import br.com.educonecta.dao.*;
import br.com.educonecta.model.*;
import br.com.educonecta.service.SessaoService;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class TesteSessaoService {

    interface Acao { void executar() throws Exception; }

    static void esperaRecusa(String titulo, Acao acao) {
        try {
            acao.executar();
            System.out.println("[ERRO] " + titulo + " deveria ter sido recusado!");
        } catch (Exception e) {
            System.out.println("[OK] " + titulo + " -> " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        AlunoDAO alunoDAO = new AlunoDAO();
        MonitorDAO monitorDAO = new MonitorDAO();
        MateriaDAO materiaDAO = new MateriaDAO();
        DisponibilidadeDAO dispDAO = new DisponibilidadeDAO();
        SessaoDAO sessaoDAO = new SessaoDAO();
        SessaoService service = new SessaoService();

        Aluno aluno = new Aluno(0, "João", "joao.svc@email.com", "123", "ADS", "3");
        alunoDAO.inserir(aluno);
        Monitor semMateria = new Monitor(0, "Ana", "ana.svc@email.com", "123", "Sem matéria");
        monitorDAO.inserir(semMateria);
        Monitor carlos = new Monitor(0, "Carlos", "carlos.svc@email.com", "123", "Monitor de BD");
        monitorDAO.inserir(carlos);
        Materia bd = new Materia(0, "Banco de Dados");
        materiaDAO.inserir(bd);
        Materia calc = new Materia(0, "Cálculo I");
        materiaDAO.inserir(calc);
        monitorDAO.vincularMateria(carlos.getId(), bd.getId());

        LocalDateTime terca15h = LocalDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.TUESDAY))
                .withHour(15).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime quarta15h = terca15h.plusDays(1);

        System.out.println("--- RN01 ---");
        esperaRecusa("Monitor sem matéria",
                () -> service.solicitar(aluno.getId(), semMateria.getId(), bd.getId(), terca15h));
        esperaRecusa("Matéria que o monitor não domina",
                () -> service.solicitar(aluno.getId(), carlos.getId(), calc.getId(), terca15h));

        System.out.println("--- Solicitar ---");
        Sessao s1 = service.solicitar(aluno.getId(), carlos.getId(), bd.getId(), terca15h);
        System.out.println("[OK] Sessão " + s1.getId() + " criada: " + s1.getStatus());      // PENDENTE

        System.out.println("--- RN02 ---");
        esperaRecusa("Confirmar sem disponibilidade cadastrada", () -> service.confirmar(s1.getId()));

        Disponibilidade d = new Disponibilidade(carlos.getId(), 2, LocalTime.of(14, 0), LocalTime.of(16, 0));
        dispDAO.inserir(d);
        Sessao confirmada = service.confirmar(s1.getId());
        System.out.println("[OK] Confirmada com disponibilidade terça 14h-16h: " + confirmada.getStatus());

        esperaRecusa("Conflito de horário (15h30)",
                () -> service.solicitar(aluno.getId(), carlos.getId(), bd.getId(), terca15h.plusMinutes(30)));

        Sessao s2 = service.solicitar(aluno.getId(), carlos.getId(), bd.getId(), quarta15h);
        esperaRecusa("Confirmar na quarta (fora da janela)", () -> service.confirmar(s2.getId()));

        System.out.println("--- Concluir ---");
        System.out.println("[OK] Concluída: " + service.concluir(s1.getId()).getStatus());

        // limpeza: sessões primeiro, porque referenciam aluno, monitor e matéria
        sessaoDAO.deletar(s1.getId());
        sessaoDAO.deletar(s2.getId());
        alunoDAO.deletar(aluno.getId());
        monitorDAO.deletar(semMateria.getId());
        monitorDAO.deletar(carlos.getId());
        materiaDAO.deletar(bd.getId());
        materiaDAO.deletar(calc.getId());
        System.out.println("Limpeza feita.");
    }
}