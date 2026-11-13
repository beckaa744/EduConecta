package br.com.educonecta.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoFactory {
    private static final String URL = System.getenv().getOrDefault(
            "EDUConecta_DB_URL", "jdbc:mysql://localhost:3306/educonecta?useSSL=false&serverTimezone=UTC");
    private static final String USUARIO = System.getenv().getOrDefault("EDUConecta_DB_USER", "root");
    private static final String SENHA = System.getenv().getOrDefault("EDUConecta_DB_PASSWORD", "");

    private ConexaoFactory() {}

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
