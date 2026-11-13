package br.com.educonecta;

import br.com.educonecta.config.ConexaoFactory;
import br.com.educonecta.dao.AlunoDAO;
import br.com.educonecta.dao.AvaliacaoDAO;
import br.com.educonecta.dao.DisponibilidadeDAO;
import br.com.educonecta.dao.MateriaDAO;
import br.com.educonecta.dao.MonitorDAO;
import br.com.educonecta.dao.SessaoDAO;
import br.com.educonecta.model.Aluno;
import br.com.educonecta.model.Avaliacao;
import br.com.educonecta.model.Disponibilidade;
import br.com.educonecta.model.Materia;
import br.com.educonecta.model.Monitor;
import br.com.educonecta.model.Sessao;
import br.com.educonecta.service.AvaliacaoService;
import br.com.educonecta.service.SessaoService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class TesteAvaliacao {

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
        AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
        SessaoService sessaoService = new SessaoService();
        AvaliacaoService avaliacaoService = new AvaliacaoService();

        // preparação: aluno, monitor com matéria e disponibilidade, e uma sessão pendente
        Aluno aluno = new Aluno(0, "João", "joao.av@email.com", "123", "ADS", "3");
        alunoDAO.inserir(aluno);
        Monitor monitor = new Monitor(0, "Carlos", "carlos.av@email.com", "123", "Monitor de BD");
        monitorDAO.inserir(monitor);
        Materia materia = new Materia(0, "Banco de Dados");
        materiaDAO.inserir(materia);
        monitorDAO.vincularMateria(monitor.getId(), materia.getId());
        dispDAO.inserir(new Disponibilidade(monitor.getId(), 2, LocalTime.of(14, 0), LocalTime.of(16, 0)));

        LocalDateTime terca15h = LocalDateTime.now()
                .with(TemporalAdjusters.next(DayOfWeek.TUESDAY))
                .withHour(15).withMinute(0).withSecond(0).withNano(0);

        Sessao sessao = sessaoService.solicitar(aluno.getId(), monitor.getId(), materia.getId(), terca15h);
        final int idSessao = sessao.getId();
        System.out.println("Sessão " + idSessao + " criada: " + sessao.getStatus());

        System.out.println("--- RN03: avaliar antes de concluir ---");
        esperaRecusa("Sessão pendente", () -> avaliacaoService.avaliar(idSessao, 5, "Ótimo"));
        sessaoService.confirmar(idSessao);
        esperaRecusa("Sessão confirmada, ainda não concluída", () -> avaliacaoService.avaliar(idSessao, 5, "Ótimo"));

        sessaoService.concluir(idSessao);
        System.out.println("--- Sessão concluída ---");
        esperaRecusa("Nota 0", () -> avaliacaoService.avaliar(idSessao, 0, "Ruim"));
        esperaRecusa("Nota 6", () -> avaliacaoService.avaliar(idSessao, 6, "Excelente"));

        Avaliacao avaliacao = avaliacaoService.avaliar(idSessao, 5, "Explicou muito bem");
        System.out.println("[OK] Avaliação gravada, id " + avaliacao.getId() + ", nota " + avaliacao.getNota());

        esperaRecusa("Segunda avaliação da mesma sessão", () -> avaliacaoService.avaliar(idSessao, 4, "Outra"));

        Avaliacao doBanco = avaliacaoDAO.buscarPorSessao(idSessao);
        System.out.println("[OK] Lida do banco: nota " + doBanco.getNota() + ", comentário: " + doBanco.getComentario());

        // limpeza: a avaliação primeiro, depois a sessão, que referencia aluno, monitor e matéria
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement("DELETE FROM avaliacoes WHERE id_sessao = ?")) {
            ps.setInt(1, idSessao);
            ps.executeUpdate();
        }
        sessaoDAO.deletar(idSessao);
        alunoDAO.deletar(aluno.getId());
        monitorDAO.deletar(monitor.getId());
        materiaDAO.deletar(materia.getId());
        System.out.println("Limpeza feita.");
    }
}