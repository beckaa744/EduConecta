package br.com.educonecta.dao;

import br.com.educonecta.config.ConexaoFactory;
import br.com.educonecta.model.Aluno;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {
    public void inserir(Aluno aluno) throws SQLException {
        String sqlUsuario = "INSERT INTO usuarios (nome, email, senha_hash) VALUES (?, ?, ?)";
        String sqlAluno = "INSERT INTO alunos (id_aluno, curso, periodo) VALUES (?, ?, ?)";

        try (Connection con = ConexaoFactory.getConexao()) {
            con.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, aluno.getNome());
                    ps.setString(2, aluno.getEmail());
                    ps.setString(3, aluno.getSenha());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("Não foi possível obter o ID do aluno.");
                        id = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(sqlAluno)) {
                    ps.setInt(1, id);
                    ps.setString(2, aluno.getCurso());
                    ps.setString(3, aluno.getPeriodo());
                    ps.executeUpdate();
                }
                con.commit();
                aluno.setId(id);
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public Aluno buscarPorId(int id) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nome, u.email, a.curso, a.periodo " +
                "FROM usuarios u JOIN alunos a ON a.id_aluno = u.id_usuario WHERE u.id_usuario = ?";
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montarAluno(rs) : null;
            }
        }
    }

    public List<Aluno> listar() throws SQLException {
        String sql = "SELECT u.id_usuario, u.nome, u.email, a.curso, a.periodo " +
                "FROM usuarios u JOIN alunos a ON a.id_aluno = u.id_usuario ORDER BY u.nome";
        List<Aluno> alunos = new ArrayList<>();
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) alunos.add(montarAluno(rs));
        }
        return alunos;
    }

    public void atualizar(Aluno aluno) throws SQLException {
        String sqlUsuario = "UPDATE usuarios SET nome = ?, email = ? WHERE id_usuario = ?";
        String sqlAluno = "UPDATE alunos SET curso = ?, periodo = ? WHERE id_aluno = ?";
        try (Connection con = ConexaoFactory.getConexao()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlUsuario)) {
                    ps.setString(1, aluno.getNome());
                    ps.setString(2, aluno.getEmail());
                    ps.setInt(3, aluno.getId());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(sqlAluno)) {
                    ps.setString(1, aluno.getCurso());
                    ps.setString(2, aluno.getPeriodo());
                    ps.setInt(3, aluno.getId());
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void deletar(int id) throws SQLException {
        try (Connection con = ConexaoFactory.getConexao();
             PreparedStatement ps = con.prepareStatement("DELETE FROM usuarios WHERE id_usuario = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Aluno montarAluno(ResultSet rs) throws SQLException {
        return new Aluno(rs.getInt("id_usuario"), rs.getString("nome"),
                rs.getString("email"), null, rs.getString("curso"), rs.getString("periodo"));
    }
}
