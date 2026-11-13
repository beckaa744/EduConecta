package br.com.educonecta.dao;

import br.com.educonecta.config.ConexaoFactory;
import br.com.educonecta.model.Monitor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonitorDAO {
    public void inserir(Monitor m) throws SQLException {
        String sqlUsuario = "INSERT INTO usuarios (nome, email, senha_hash) VALUES (?, ?, ?)";
        String sqlMonitor = "INSERT INTO monitores (id_monitor, biografia) VALUES (?, ?)";

        try (Connection con = ConexaoFactory.getConexao()){
            con.setAutoCommit(false);
            try (PreparedStatement psU = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psM = con.prepareStatement(sqlMonitor)) {

                psU.setString(1, m.getNome());
                psU.setString(2, m.getEmail());
                psU.setString(3, m.getSenha());
                psU.executeUpdate();

                try (ResultSet rs = psU.getGeneratedKeys()) {
                    if (rs.next()) {
                        m.setId(rs.getInt(1));
                    }
                }

                psM.setInt(1, m.getId());
                psM.setString(2, m.getBiografia());
                psM.executeUpdate();

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // READ: buscar por id
    public Monitor buscarPorId(int id) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nome, u.email, m.biografia, m.media_avaliacao "
                + "FROM usuarios u JOIN monitores m ON u.id_usuario = m.id_monitor "
                + "WHERE u.id_usuario = ?";

        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarMonitor(rs);
                }
            }
        }
        return null;
    }

    // READ: listar todos
    public List<Monitor> listar() throws SQLException {
        String sql = "SELECT u.id_usuario, u.nome, u.email, m.biografia, m.media_avaliacao "
                + "FROM usuarios u JOIN monitores m ON u.id_usuario = m.id_monitor";
        List<Monitor> lista = new ArrayList<>();

        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(montarMonitor(rs));
            }
        }
        return lista;
    }

    // UPDATE
    public void atualizar(Monitor m) throws SQLException {
        String sqlUsuario = "UPDATE usuarios SET nome = ?, email = ? WHERE id_usuario = ?";
        String sqlMonitor = "UPDATE monitores SET biografia = ? WHERE id_monitor = ?";

        try (Connection con = ConexaoFactory.getConexao()) {
            con.setAutoCommit(false);
            try (PreparedStatement psU = con.prepareStatement(sqlUsuario);
                 PreparedStatement psM = con.prepareStatement(sqlMonitor)) {

                psU.setString(1, m.getNome());
                psU.setString(2, m.getEmail());
                psU.setInt(3, m.getId());
                psU.executeUpdate();

                psM.setString(1, m.getBiografia());
                psM.setInt(2, m.getId());
                psM.executeUpdate();

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    // DELETE: apagar em usuarios remove também em monitores (ON DELETE CASCADE)
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // RF03: o monitor passa a dominar uma matéria
    public void vincularMateria(int idMonitor, int idMateria) throws SQLException {
        String sql = "INSERT INTO monitor_materia (id_monitor, id_materia) VALUES (?, ?)";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMonitor);
            ps.setInt(2, idMateria);
            ps.executeUpdate();
        }
    }

    public void desvincularMateria(int idMonitor, int idMateria) throws SQLException {
        String sql = "DELETE FROM monitor_materia WHERE id_monitor = ? AND id_materia = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMonitor);
            ps.setInt(2, idMateria);
            ps.executeUpdate();
        }
    }

    // RN01 (parte 1): o monitor tem ao menos uma matéria?
    public boolean possuiMateria(int idMonitor) throws SQLException {
        String sql = "SELECT 1 FROM monitor_materia WHERE id_monitor = ? LIMIT 1";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMonitor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // RN01 (parte 2): o monitor domina esta matéria específica?
    public boolean dominaMateria(int idMonitor, int idMateria) throws SQLException {
        String sql = "SELECT 1 FROM monitor_materia WHERE id_monitor = ? AND id_materia = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMonitor);
            ps.setInt(2, idMateria);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // RF04: buscar monitores por matéria
    public List<Monitor> buscarPorMateria(int idMateria) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nome, u.email, m.biografia, m.media_avaliacao "
                + "FROM usuarios u "
                + "JOIN monitores m ON u.id_usuario = m.id_monitor "
                + "JOIN monitor_materia mm ON m.id_monitor = mm.id_monitor "
                + "WHERE mm.id_materia = ?";
        List<Monitor> lista = new ArrayList<>();
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMateria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montarMonitor(rs));
            }
        }
        return lista;
    }
    private Monitor montarMonitor(ResultSet rs) throws SQLException {
        Monitor m = new Monitor();
        m.setId(rs.getInt("id_usuario"));
        m.setNome(rs.getString("nome"));
        m.setEmail(rs.getString("email"));
        m.setBiografia(rs.getString("biografia"));
        m.setMediaAvaliacao(rs.getDouble("media_avaliacao"));
        return m;
    }
}
