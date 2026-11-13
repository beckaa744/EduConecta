package br.com.educonecta.dao;

import br.com.educonecta.config.ConexaoFactory;
import br.com.educonecta.model.Materia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MateriaDAO {

    public void inserir(Materia m) throws SQLException {
        String sql = "INSERT INTO materias (nome) VALUES (?)";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getNome());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) m.setId(rs.getInt(1));
            }
        }
    }

    public Materia buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_materia, nome FROM materias WHERE id_materia = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return montar(rs);
            }
        }
        return null;
    }

    public List<Materia> listar() throws SQLException {
        String sql = "SELECT id_materia, nome FROM materias ORDER BY nome";
        List<Materia> lista = new ArrayList<>();
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    public void atualizar(Materia m) throws SQLException {
        String sql = "UPDATE materias SET nome = ? WHERE id_materia = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getNome());
            ps.setInt(2, m.getId());
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM materias WHERE id_materia = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Materia montar(ResultSet rs) throws SQLException {
        return new Materia(rs.getInt("id_materia"), rs.getString("nome"));
    }
}
