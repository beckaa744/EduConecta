package br.com.educonecta;

import br.com.educonecta.dao.MonitorDao;
import br.com.educonecta.model.Monitor;

import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        MonitorDao dao = new MonitorDao();

        try {
            // CREATE
            Monitor m = new Monitor(0, "Ana Souza", "ana@email.com", "senha123", "Monitora de Java");
            dao.inserir(m);
            System.out.println("Inserido com id: " + m.getId());

            // READ
            System.out.println(dao.buscarPorId(m.getId()).visualizarPerfil());

            // UPDATE
            m.setBiografia("Monitora de Java e Banco de Dados");
            dao.atualizar(m);
            System.out.println("Atualizado: " + dao.buscarPorId(m.getId()).visualizarPerfil());

            // LIST
            System.out.println("Total de monitores: " + dao.listar().size());

            // DELETE
            dao.deletar(m.getId());
            System.out.println("Deletado. Total agora: " + dao.listar().size());

        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}