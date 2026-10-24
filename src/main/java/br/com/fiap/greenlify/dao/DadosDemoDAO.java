package br.com.fiap.greenlify.dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO que restaura os dados de demonstração do banco: apaga as tabelas de movimento,
 * reinicia os IDs identity em 1 e recarrega o conjunto de dados de exemplo.
 * <p>
 * Os comandos ficam no arquivo {@code src/main/resources/db/seed_demo.sql}; este DAO só lê o
 * arquivo e executa cada comando, na ordem, pelo mesmo pool de conexões dos demais DAOs.
 * <p>
 * <b>Atenção:</b> o script apaga dados de verdade (uploads, fingerprints, missões dos usuários,
 * selos, ranking, resgates, pagamentos, perfis e sequências).
 */
public class DadosDemoDAO {

    private static final String ARQUIVO_SCRIPT = "/db/seed_demo.sql";

    /**
     * Executa todo o script de dados de demonstração.
     * <p>
     * Os {@code COMMIT} do script são respeitados. Os {@code ALTER TABLE} do Oracle confirmam a
     * transação sozinhos (DDL), então não dá para desfazer tudo se algo falhar no meio: se um
     * comando der erro, o que veio depois do último commit/DDL é desfeito, mas as tabelas já
     * apagadas continuam vazias. Basta chamar de novo para recarregar.
     *
     * @return quantidade de comandos executados (sem contar os COMMIT)
     * @throws IllegalStateException se o script não for encontrado ou o banco falhar
     */
    public int restaurar() {
        List<String> comandos = lerComandos();
        int executados = 0;

        try (Connection conexao = ConnectionFactory.obterConexao()) {
            boolean confirmado = false;
            try {
                conexao.setAutoCommit(false);

                try (Statement st = conexao.createStatement()) {
                    for (String comando : comandos) {
                        if (comando.equalsIgnoreCase("COMMIT")) {
                            conexao.commit();
                            continue;
                        }
                        try {
                            st.execute(comando);
                        } catch (SQLException e) {
                            // mostra qual comando falhou, sem expor mais nada
                            String resumo = comando.length() > 80 ? comando.substring(0, 80) + "..." : comando;
                            throw new IllegalStateException("Falha ao executar: " + resumo, e);
                        }
                        executados++;
                    }
                }

                conexao.commit();
                confirmado = true;
            } finally {
                // Desfaz antes de devolver a conexão ao pool (no Oracle, ligar o autocommit com a
                // transação aberta confirmaria tudo).
                if (!confirmado) {
                    conexao.rollback();
                }
                conexao.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException("Não foi possível restaurar os dados de demonstração.", e);
        }
        return executados;
    }

    /** Lê o arquivo do script e separa os comandos (sem comentários e sem o ';' final). */
    private List<String> lerComandos() {
        String script;
        try (InputStream in = DadosDemoDAO.class.getResourceAsStream(ARQUIVO_SCRIPT)) {
            if (in == null) {
                throw new IllegalStateException("Script de dados de demonstração não encontrado: " + ARQUIVO_SCRIPT);
            }
            script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler o script de dados de demonstração.", e);
        }
        return separarComandos(script);
    }

    /**
     * Divide o script em comandos: ignora comentários de linha ({@code --}) e só separa no ';'
     * quando ele está fora de texto entre aspas simples (então um ';' ou '--' dentro de um
     * texto não atrapalha).
     */
    static List<String> separarComandos(String script) {
        List<String> comandos = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        boolean emTexto = false;

        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);

            if (!emTexto && c == '-' && i + 1 < script.length() && script.charAt(i + 1) == '-') {
                while (i < script.length() && script.charAt(i) != '\n') {
                    i++;
                }
                atual.append('\n');
                continue;
            }
            if (c == '\'') {
                emTexto = !emTexto;
            }
            if (c == ';' && !emTexto) {
                adicionar(comandos, atual);
                continue;
            }
            atual.append(c);
        }
        adicionar(comandos, atual); // último comando sem ';'
        return comandos;
    }

    private static void adicionar(List<String> comandos, StringBuilder atual) {
        String comando = atual.toString().trim();
        if (!comando.isEmpty()) {
            comandos.add(comando);
        }
        atual.setLength(0);
    }
}