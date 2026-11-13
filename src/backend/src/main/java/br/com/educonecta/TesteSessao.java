package br.com.educonecta;

import br.com.educonecta.dao.*;
import br.com.educonecta.model.*;

import java.time.LocalDateTime;

public class TesteSessao {
    public static void main(String[] args) throws Exception {
        AlunoDAO alunoDao = new AlunoDAO();
        MonitorDAO monitorDao = new MonitorDAO();
        MateriaDAO materiaDao = new MateriaDAO();
        SessaoDAO sessaoDao = new SessaoDAO();

        Aluno a = new Aluno(0, "João", "joao.teste@email.com", "123", "ADS", "3");
        alunoDao.inserir(a);
        Monitor m = new Monitor(0, "Carlos", "carlos.teste@email.com", "123", "Monitor de BD");
        monitorDao.inserir(m);
        Materia mat = new Materia(0, "Banco de Dados");
        materiaDao.inserir(mat);

        LocalDateTime horario = LocalDateTime.of(2026, 11, 17, 15, 0);
        Sessao s = new Sessao(a.getId(), m.getId(), mat.getId(), horario);
        sessaoDao.inserir(s);
        System.out.println("Sessão criada, id " + s.getId() + ", status " + s.getStatus());      // PENDENTE

        System.out.println("Conflito às 15h30? " + sessaoDao.existeConflito(m.getId(), horario.plusMinutes(30), 0)); // true
        System.out.println("Conflito às 16h? " + sessaoDao.existeConflito(m.getId(), horario.plusMinutes(60), 0));   // false

        s.confirmar();
        sessaoDao.atualizar(s);
        System.out.println("No banco: " + sessaoDao.buscarPorId(s.getId()).getStatus());          // CONFIRMADA

        s.concluir();
        sessaoDao.atualizar(s);
        System.out.println("Concluída? " + sessaoDao.buscarPorId(s.getId()).estaConcluida());     // true

        System.out.println("Sessões do aluno: " + sessaoDao.listarPorAluno(a.getId()).size());    // 1

        // limpeza: a sessão primeiro, porque ela referencia aluno, monitor e matéria
        sessaoDao.deletar(s.getId());
        alunoDao.deletar(a.getId());
        monitorDao.deletar(m.getId());
        materiaDao.deletar(mat.getId());
        System.out.println("Limpeza feita.");
    }
}