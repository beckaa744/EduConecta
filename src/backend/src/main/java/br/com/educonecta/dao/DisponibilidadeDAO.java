package br.com.educonecta.dao;

import br.com.educonecta.config.ConexaoFactory;
import br.com.educonecta.model.Disponibilidade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisponibilidadeDAO {

    public void inserir(Disponibilidade d) throws SQLException {
        String sql = "INSERT INTO disponibilidades (id_monitor, dia_semana, hora_inicio, hora_fim) VALUES (?,?,?,?)";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdMonitor());
            ps.setInt(2, d.getDiaSemana());
            ps.setTime(3, Time.valueOf(d.getHoraInicio()));
            ps.setTime(4, Time.valueOf(d.getHoraFim()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) d.setId(rs.getInt(1));
            }
        }
    }

    public Disponibilidade buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM disponibilidades WHERE id_disponibilidade = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return montar(rs);
            }
        }
        return null;
    }

    public List<Disponibilidade> listarPorMonitor(int idMonitor) throws SQLException {
        String sql = "SELECT * FROM disponibilidades WHERE id_monitor = ? ORDER BY dia_semana, hora_inicio";
        List<Disponibilidade> lista = new ArrayList<>();
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMonitor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }

    public void atualizar(Disponibilidade d) throws SQLException {
        String sql = "UPDATE disponibilidades SET dia_semana=?, hora_inicio=?, hora_fim=? WHERE id_disponibilidade=?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getDiaSemana());
            ps.setTime(2, Time.valueOf(d.getHoraInicio()));
            ps.setTime(3, Time.valueOf(d.getHoraFim()));
            ps.setInt(4, d.getId());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM disponibilidades WHERE id_disponibilidade = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Disponibilidade montar(ResultSet rs) throws SQLException {
        Disponibilidade d = new Disponibilidade(
                rs.getInt("id_monitor"),
                rs.getInt("dia_semana"),
                rs.getTime("hora_inicio").toLocalTime(),
                rs.getTime("hora_fim").toLocalTime());
        d.setId(rs.getInt("id_disponibilidade"));
        return d;
    }
}
