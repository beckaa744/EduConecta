package br.com.educonecta.dao;

import br.com.educonecta.config.ConexaoFactory;
import br.com.educonecta.model.Disponibilidade;
import br.com.educonecta.model.Sessao;
import br.com.educonecta.model.StatusSessao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessaoDAO {

    private static final String SELECT =
            "SELECT id_sessao, id_aluno, id_monitor, id_materia, data_hora, status FROM sessoes";

    public void inserir(Sessao s) throws SQLException {
        String sql = "INSERT INTO sessoes (id_aluno, id_monitor, id_materia, data_hora, status) VALUES (?,?,?,?,?)";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getIdAluno());
            ps.setInt(2, s.getIdMonitor());
            ps.setInt(3, s.getIdMateria());
            ps.setTimestamp(4, Timestamp.valueOf(s.getDataHora()));
            ps.setString(5, s.getStatus().getValor());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) s.setId(rs.getInt(1));
            }
        }
    }

    public Sessao buscarPorId(int id) throws SQLException {
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(SELECT + " WHERE id_sessao = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return montar(rs);
            }
        }
        return null;
    }

    public List<Sessao> listar() throws SQLException {
        return consultarLista(SELECT + " ORDER BY data_hora", 0);
    }

    public List<Sessao> listarPorAluno(int idAluno) throws SQLException {
        return consultarLista(SELECT + " WHERE id_aluno = ? ORDER BY data_hora", idAluno);
    }

    public List<Sessao> listarPorMonitor(int idMonitor) throws SQLException {
        return consultarLista(SELECT + " WHERE id_monitor = ? ORDER BY data_hora", idMonitor);
    }

    public void atualizar(Sessao s) throws SQLException {
        String sql = "UPDATE sessoes SET data_hora = ?, status = ? WHERE id_sessao = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(s.getDataHora()));
            ps.setString(2, s.getStatus().getValor());
            ps.setInt(3, s.getId());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement("DELETE FROM sessoes WHERE id_sessao = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Existe outra sessão ativa do monitor que se sobrepõe a este horário? */
    public boolean existeConflito(int idMonitor, LocalDateTime dataHora, int ignorarIdSessao) throws SQLException {
        String sql = "SELECT 1 FROM sessoes WHERE id_monitor = ? "
                + "AND status IN ('pendente','confirmada') AND id_sessao <> ? "
                + "AND data_hora > ? AND data_hora < ? LIMIT 1";
        int duracao = Disponibilidade.DURACAO_SESSAO_MIN;
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMonitor);
            ps.setInt(2, ignorarIdSessao);
            ps.setTimestamp(3, Timestamp.valueOf(dataHora.minusMinutes(duracao)));
            ps.setTimestamp(4, Timestamp.valueOf(dataHora.plusMinutes(duracao)));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private List<Sessao> consultarLista(String sql, int parametro) throws SQLException {
        List<Sessao> lista = new ArrayList<>();
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (sql.contains("?")) ps.setInt(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }

    private Sessao montar(ResultSet rs) throws SQLException {
        Sessao s = new Sessao(rs.getInt("id_aluno"), rs.getInt("id_monitor"),
                rs.getInt("id_materia"), rs.getTimestamp("data_hora").toLocalDateTime());
        s.setId(rs.getInt("id_sessao"));
        s.setStatus(StatusSessao.deBanco(rs.getString("status")));
        return s;
    }
}
