package br.com.fiap.greenlify.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Fábrica responsável por fornecer as conexões com o banco de dados Oracle
 * utilizado pela plataforma GreenLify.
 *
 * As conexões vêm de um pool (HikariCP) pequeno, por causa do limite de
 * sessões do Oracle. Quem chama {@link #obterConexao()} deve fechar a conexão
 * (de preferência com try-with-resources): o {@code close()} não encerra a
 * sessão no banco, apenas devolve a conexão ao pool.
 */
public class ConnectionFactory {

    private static final int POOL_MAXIMO_PADRAO = 3;

    private static volatile HikariDataSource dataSource;

    private static HikariDataSource obterDataSource() {
        HikariDataSource ds = dataSource;
        if (ds == null) {
            synchronized (ConnectionFactory.class) {
                ds = dataSource;
                if (ds == null) {
                    ds = criarDataSource();
                    dataSource = ds;
                }
            }
        }
        return ds;
    }

    private static HikariDataSource criarDataSource() {
        String url = System.getenv("SPRING_DATASOURCE_URL");
        String usuario = System.getenv("SPRING_DATASOURCE_USERNAME");
        String senha = System.getenv("SPRING_DATASOURCE_PASSWORD");

        StringBuilder ausentes = new StringBuilder();
        if (vazio(url)) ausentes.append(" SPRING_DATASOURCE_URL");
        if (vazio(usuario)) ausentes.append(" SPRING_DATASOURCE_USERNAME");
        if (vazio(senha)) ausentes.append(" SPRING_DATASOURCE_PASSWORD");
        if (ausentes.length() > 0) {
            throw new IllegalStateException(
                    "Variáveis de ambiente do Oracle não configuradas:" + ausentes
                            + ". Consulte o .env.example."
            );
        }

        int poolMaximo = lerPoolMaximo();

        HikariConfig config = new HikariConfig();
        config.setPoolName("greenlify-pool");
        config.setJdbcUrl(url);
        config.setUsername(usuario);
        config.setPassword(senha);
        config.setDriverClassName("oracle.jdbc.OracleDriver");

        config.setMaximumPoolSize(poolMaximo);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30_000);   // espera até 30s por uma conexão livre
        config.setIdleTimeout(600_000);        // 10 min
        config.setMaxLifetime(1_500_000);      // 25 min (renova antes de firewall/Oracle derrubar)

        HikariDataSource ds;
        try {
            ds = new HikariDataSource(config);
        } catch (RuntimeException e) {
            // não expõe URL/usuário/senha na mensagem; a causa original fica encadeada
            throw new IllegalStateException(
                    "Não foi possível conectar ao banco Oracle. Verifique URL, usuário, senha e rede.", e
            );
        }
        Runtime.getRuntime().addShutdownHook(new Thread(ds::close));
        return ds;
    }

    private static boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }

    /** Tamanho máximo do pool. Pode ser ajustado pela variável DB_POOL_MAX (padrão 3). */
    private static int lerPoolMaximo() {
        String valor = System.getenv("DB_POOL_MAX");
        if (valor != null) {
            try {
                int n = Integer.parseInt(valor.trim());
                if (n > 0) {
                    return n;
                }
            } catch (NumberFormatException ignorado) {
                // usa o padrão
            }
        }
        return POOL_MAXIMO_PADRAO;
    }

    public static Connection obterConexao() {
        try {
            return obterDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao conectar ao banco Oracle.", e
            );
        }
    }
}