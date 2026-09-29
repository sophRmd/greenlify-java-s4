package br.com.fiap.greenlify.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fábrica responsável por criar as conexões com o banco de dados Oracle
 * utilizado pela plataforma GreenLify.
 */
public class ConnectionFactory {

    /**
     * Abre e retorna uma nova conexão com o banco de dados Oracle da FIAP.
     *
     * @return uma conexão ativa com o banco de dados
     * @throws RuntimeException caso ocorra falha ao estabelecer a conexão
     */
    public static Connection obterConexao() {
        Connection conexao = null;
        try {
            conexao = DriverManager.getConnection(
                    "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl",
                    "rm573823", "041207");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conexao;
    }
}