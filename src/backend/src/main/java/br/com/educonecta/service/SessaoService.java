package br.com.educonecta.service;

import br.com.educonecta.dao.AlunoDAO;
import br.com.educonecta.dao.DisponibilidadeDAO;
import br.com.educonecta.dao.MonitorDAO;
import br.com.educonecta.dao.SessaoDAO;
import br.com.educonecta.model.Aluno;
import br.com.educonecta.model.Disponibilidade;
import br.com.educonecta.model.Monitor;
import br.com.educonecta.model.Sessao;
import br.com.educonecta.model.StatusSessao;
import br.com.educonecta.notificacao.Notificacao;
import br.com.educonecta.notificacao.NotificacaoEmail;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/** Regras de negócio RN01 e RN02 do ciclo de vida da sessão. */
public class SessaoService {
    private final SessaoDAO sessaoDAO;
    private final AlunoDAO alunoDAO;
    private final MonitorDAO monitorDAO;
    private final DisponibilidadeDAO disponibilidadeDAO;
    private final Notificacao notificacao;

    public SessaoService() {
        this(new SessaoDAO(), new AlunoDAO(), new MonitorDAO(),
                new DisponibilidadeDAO(), new NotificacaoEmail());
    }

    public SessaoService(SessaoDAO sessaoDAO, AlunoDAO alunoDAO, MonitorDAO monitorDAO,
                         DisponibilidadeDAO disponibilidadeDAO, Notificacao notificacao) {
        this.sessaoDAO = sessaoDAO;
        this.alunoDAO = alunoDAO;
        this.monitorDAO = monitorDAO;
        this.disponibilidadeDAO = disponibilidadeDAO;
        this.notificacao = notificacao;
    }

    /** Cria a sessão como PENDENTE, aplicando a RN01. */
    public Sessao solicitar(int idAluno, int idMonitor, int idMateria, LocalDateTime dataHora)
            throws SQLException {
        if (dataHora == null)
            throw new IllegalArgumentException("Data e hora são obrigatórias.");
        if (dataHora.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("O horário solicitado já passou.");

        Aluno aluno = alunoDAO.buscarPorId(idAluno);
        if (aluno == null) throw new IllegalArgumentException("Aluno não encontrado.");
        Monitor monitor = monitorDAO.buscarPorId(idMonitor);
        if (monitor == null) throw new IllegalArgumentException("Monitor não encontrado.");

        // RN01: o monitor precisa ter matéria cadastrada e dominar a escolhida
        if (!monitorDAO.possuiMateria(idMonitor))
            throw new IllegalStateException("RN01: o monitor ainda não tem matéria de domínio cadastrada.");
        if (!monitorDAO.dominaMateria(idMonitor, idMateria))
            throw new IllegalStateException("RN01: o monitor não domina a matéria escolhida.");

        if (sessaoDAO.existeConflito(idMonitor, dataHora, 0))
            throw new IllegalStateException("O monitor já tem outra sessão nesse horário.");

        Sessao sessao = new Sessao(idAluno, idMonitor, idMateria, dataHora);
        sessaoDAO.inserir(sessao);

        notificacao.enviar(monitor.getEmail(),
                aluno.getNome() + " solicitou uma sessão para " + dataHora + ".");
        return sessao;
    }

    /** Confirma a sessão, aplicando a RN02. */
    public Sessao confirmar(int idSessao) throws SQLException {
        Sessao sessao = buscar(idSessao);
        if (sessao.getStatus() != StatusSessao.PENDENTE)
            throw new IllegalStateException("Somente sessões pendentes podem ser confirmadas.");

        List<Disponibilidade> janelas = disponibilidadeDAO.listarPorMonitor(sessao.getIdMonitor());
        validarDisponibilidade(sessao, janelas);

        if (sessaoDAO.existeConflito(sessao.getIdMonitor(), sessao.getDataHora(), idSessao))
            throw new IllegalStateException("O monitor já tem outra sessão nesse horário.");

        sessao.confirmar();
        sessaoDAO.atualizar(sessao);

        Aluno aluno = alunoDAO.buscarPorId(sessao.getIdAluno());
        if (aluno != null)
            notificacao.enviar(aluno.getEmail(),
                    "Sua sessão de " + sessao.getDataHora() + " foi confirmada.");
        return sessao;
    }

    /** RN02 pura: não acessa o banco, por isso pode ser testada com JUnit. */
    public static void validarDisponibilidade(Sessao sessao, List<Disponibilidade> janelas) {
        boolean coberto = janelas != null && janelas.stream()
                .anyMatch(d -> d.cobre(sessao.getDataHora()));
        if (!coberto)
            throw new IllegalStateException("RN02: horário fora da disponibilidade do monitor.");
    }

    public Sessao concluir(int idSessao) throws SQLException {
        Sessao sessao = buscar(idSessao);
        sessao.concluir();
        sessaoDAO.atualizar(sessao);
        return sessao;
    }

    public Sessao cancelar(int idSessao) throws SQLException {
        Sessao sessao = buscar(idSessao);
        sessao.cancelar();
        sessaoDAO.atualizar(sessao);
        return sessao;
    }

    public Sessao buscar(int idSessao) throws SQLException {
        Sessao sessao = sessaoDAO.buscarPorId(idSessao);
        if (sessao == null) throw new IllegalArgumentException("Sessão não encontrada.");
        return sessao;
    }
}