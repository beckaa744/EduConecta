package br.com.educonecta;

import br.com.educonecta.dao.MateriaDAO;
import br.com.educonecta.dao.MonitorDAO;
import br.com.educonecta.model.Materia;
import br.com.educonecta.model.Monitor;

public class TesteMateria {
    public static void main(String[] args) throws Exception {
        MateriaDAO materiaDao = new MateriaDAO();
        MonitorDAO monitorDao = new MonitorDAO();

        // ===== Parte 1: CRUD de Materia =====
        Materia m = new Materia(0, "Banco de Dados");
        materiaDao.inserir(m);
        System.out.println("Inserida com id: " + m.getId());

        m.setNome("Banco de Dados I");
        materiaDao.atualizar(m);
        System.out.println("Atualizada: " + materiaDao.buscarPorId(m.getId()).getNome());
        System.out.println("Total: " + materiaDao.listar().size());

        // ===== Parte 2: ligação monitor <-> matéria =====
        Monitor mon = new Monitor(0, "Ana", "ana.teste@email.com", "123", "Monitora");
        monitorDao.inserir(mon);

        System.out.println("Antes, tem matéria? " + monitorDao.possuiMateria(mon.getId()));              // false
        monitorDao.vincularMateria(mon.getId(), m.getId());
        System.out.println("Depois, tem matéria? " + monitorDao.possuiMateria(mon.getId()));             // true
        System.out.println("Domina a matéria? " + monitorDao.dominaMateria(mon.getId(), m.getId()));     // true
        System.out.println("Monitores da matéria: " + monitorDao.buscarPorMateria(m.getId()).size());    // 1

        monitorDao.desvincularMateria(mon.getId(), m.getId());
        System.out.println("Após desvincular: " + monitorDao.possuiMateria(mon.getId()));               // false

        // ===== Limpeza =====
        monitorDao.deletar(mon.getId());
        materiaDao.deletar(m.getId());
        System.out.println("Limpeza feita.");
    }
}