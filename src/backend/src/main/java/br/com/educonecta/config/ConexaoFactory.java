package br.com.educonecta.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConexaoFactory {

    private static final String URL = "jdbc:mysql://localhost:3306/educonecta";
    private static final String USUARIO = "root";
    private static final String SENHA = "sua_senha";

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
