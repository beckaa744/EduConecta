package br.com.educonecta.dao;

import br.com.educonecta.config.ConexaoFactory;
import br.com.educonecta.model.Avaliacao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvaliacaoDAO {
    public void inserir(Avaliacao avaliacao) throws SQLException {
        String sql = "INSERT INTO avaliacoes (id_sessao, nota, comentario) VALUES (?, ?, ?)";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, avaliacao.getIdSessao());
            ps.setInt(2, avaliacao.getNota());
            ps.setString(3, avaliacao.getComentario());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) avaliacao.setId(rs.getInt(1));
            }
        }
    }

    public Avaliacao buscarPorSessao(int idSessao) throws SQLException {
        String sql = "SELECT id_avaliacao, id_sessao, nota, comentario FROM avaliacoes WHERE id_sessao = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSessao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montarAvaliacao(rs) : null;
            }
        }
    }

    public List<Avaliacao> listar() throws SQLException {
        List<Avaliacao> lista = new ArrayList<>();
        String sql = "SELECT id_avaliacao, id_sessao, nota, comentario FROM avaliacoes ORDER BY id_avaliacao DESC";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montarAvaliacao(rs));
        }
        return lista;
    }

    private Avaliacao montarAvaliacao(ResultSet rs) throws SQLException {
        Avaliacao a = new Avaliacao(rs.getInt("id_sessao"), rs.getInt("nota"), rs.getString("comentario"));
        a.setId(rs.getInt("id_avaliacao"));
        return a;
    }
}
