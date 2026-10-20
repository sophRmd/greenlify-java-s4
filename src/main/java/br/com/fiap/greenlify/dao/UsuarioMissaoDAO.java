package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.models.Missao;
import br.com.fiap.greenlify.models.Usuario;
import br.com.fiap.greenlify.models.UsuarioMissao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO da entidade UsuarioMissao, que registra a relação entre um usuário e uma missão
 * (quantidade de tentativas e progresso atual) na tabela Usuario_Missao.
 *
 * Ordem das colunas assumida na tabela, usada na leitura por índice:
 * 1 id_usuario_missao, 2 nr_tentativas, 3 nr_progresso_atual, 4 id_usuario, 5 id_missao.
 */
public class UsuarioMissaoDAO {
    /**
     * Cadastra um novo registro de missão para um usuário. O id_usuario_missao é gerado
     * pelo banco (auto increment), então fica de fora do INSERT e, ao final,
     * é preenchido no próprio objeto.
     *
     * @param usuarioMissao dados do registro a ser cadastrado
     */
    public void cadastrar(UsuarioMissao usuarioMissao) {
        Connection conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Usuario_Missao (nr_tentativas, nr_progresso_atual, id_usuario, id_missao) " +
                    "VALUES (?, ?, ?, ?)";
            ps = conexao.prepareStatement(sql, new String[]{"ID_USUARIO_MISSAO"});
            ps.setInt(1, usuarioMissao.getTentativas());
            ps.setInt(2, usuarioMissao.getProgressoAtual());
            ps.setInt(3, usuarioMissao.getUsuario().getIdUsuario());
            ps.setInt(4, usuarioMissao.getMissao().getIdMissao());

            ps.executeUpdate();

            // Recupera o id_usuario_missao gerado automaticamente (IDENTITY) e preenche no objeto
            ResultSet rsGerado = ps.getGeneratedKeys();
            if (rsGerado.next()) {
                usuarioMissao.setIdUsuarioMissao(rsGerado.getInt(1));
            }
            rsGerado.close();

            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Atualiza os dados de um registro de usuário/missão já existente
     * (normalmente tentativas e progresso atual).
     *
     * @param usuarioMissao dados atualizados do registro (o id é usado como referência)
     */
    public void alterar(UsuarioMissao usuarioMissao) {
        Connection conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE Usuario_Missao SET nr_tentativas = ?, nr_progresso_atual = ?, " +
                    "id_usuario = ?, id_missao = ? WHERE id_usuario_missao = ?";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, usuarioMissao.getTentativas());
            ps.setInt(2, usuarioMissao.getProgressoAtual());
            ps.setInt(3, usuarioMissao.getUsuario().getIdUsuario());
            ps.setInt(4, usuarioMissao.getMissao().getIdMissao());
            ps.setInt(5, usuarioMissao.getIdUsuarioMissao());

            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Exclui um registro de usuário/missão pelo id.
     *
     * @param idUsuarioMissao id do registro a ser removido
     */
    public void excluir(int idUsuarioMissao) {
        Connection conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("DELETE FROM Usuario_Missao WHERE id_usuario_missao = ?");
            ps.setInt(1, idUsuarioMissao);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Busca um registro de usuário/missão pelo id.
     *
     * @param idUsuarioMissao id do registro procurado
     * @return o registro encontrado, ou {@code null} caso não exista
     */
    public UsuarioMissao buscarPorId(int idUsuarioMissao) {
        Connection conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        UsuarioMissao usuarioMissao = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Usuario_Missao WHERE id_usuario_missao = ?");
            ps.setInt(1, idUsuarioMissao);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usuarioMissao = new UsuarioMissao();
                usuarioMissao.setIdUsuarioMissao(rs.getInt(1));
                usuarioMissao.setTentativas(rs.getInt(2));
                usuarioMissao.setProgressoAtual(rs.getInt(3));

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt(4));
                usuarioMissao.setUsuario(usuario);

                Missao missao = new Missao();
                missao.setIdMissao(rs.getInt(5));
                usuarioMissao.setMissao(missao);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return usuarioMissao;
    }

    /**
     * Lista todos os registros de usuário/missão cadastrados.
     *
     * @return lista de registros (pode vir vazia, nunca nula)
     */
    public List<UsuarioMissao> listar() {
        Connection conexao = ConnectionFactory.obterConexao();
        List<UsuarioMissao> usuarioMissoes = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Usuario_Missao");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UsuarioMissao usuarioMissao = new UsuarioMissao();

                usuarioMissao.setIdUsuarioMissao(rs.getInt(1));
                usuarioMissao.setTentativas(rs.getInt(2));
                usuarioMissao.setProgressoAtual(rs.getInt(3));

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt(4));
                usuarioMissao.setUsuario(usuario);

                Missao missao = new Missao();
                missao.setIdMissao(rs.getInt(5));
                usuarioMissao.setMissao(missao);

                usuarioMissoes.add(usuarioMissao);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return usuarioMissoes;
    }

    /**
     * Lista todas as missões associadas a um usuário.
     *
     * @param idUsuario id do usuário dono dos registros
     * @return lista de registros do usuário (pode vir vazia, nunca nula)
     */
    public List<UsuarioMissao> listarPorUsuario(int idUsuario) {
        Connection conexao = ConnectionFactory.obterConexao();
        List<UsuarioMissao> usuarioMissoes = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Usuario_Missao WHERE id_usuario = ?");
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UsuarioMissao usuarioMissao = new UsuarioMissao();

                usuarioMissao.setIdUsuarioMissao(rs.getInt(1));
                usuarioMissao.setTentativas(rs.getInt(2));
                usuarioMissao.setProgressoAtual(rs.getInt(3));

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt(4));
                usuarioMissao.setUsuario(usuario);

                Missao missao = new Missao();
                missao.setIdMissao(rs.getInt(5));
                usuarioMissao.setMissao(missao);

                usuarioMissoes.add(usuarioMissao);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return usuarioMissoes;
    }

    // ------------------------------------------------------------------
    // Missões concluídas (usadas pelos rankings)
    // ------------------------------------------------------------------

    /**
     * Missão concluída = progresso atingiu o limite de realizações (contínua)
     * ou a meta (mensal), a mesma regra usada no ResumoService.
     */
    private static final String SQL_CONCLUIDAS =
            "SELECT um.id_usuario, COUNT(*) AS total "
                    + "FROM Usuario_Missao um "
                    + "LEFT JOIN Missao_Continua mc ON mc.id_missao = um.id_missao "
                    + "LEFT JOIN Missao_Mensal mm ON mm.id_missao = um.id_missao "
                    + "WHERE ((mc.id_missao IS NOT NULL AND mc.nr_limite_realizacoes > 0 "
                    + "AND um.nr_progresso_atual >= mc.nr_limite_realizacoes) "
                    + "OR (mm.id_missao IS NOT NULL AND mm.vl_meta > 0 "
                    + "AND um.nr_progresso_atual >= mm.vl_meta)) ";

    /**
     * Conta, por usuário, todas as missões já concluídas (ranking all time).
     *
     * @return mapa idUsuario -> total de missões concluídas (só usuários com pelo menos uma)
     */
    public Map<Integer, Integer> contarConcluidasPorUsuario() {
        return consultarConcluidas(SQL_CONCLUIDAS + "GROUP BY um.id_usuario", null, null);
    }

    /**
     * Conta, por usuário, as missões concluídas dentro de um período
     * (usa {@code dt_conclusao}, gravada no momento da conclusão).
     *
     * @param inicio primeiro dia do período (inclusive)
     * @param fim    último dia do período (inclusive)
     * @return mapa idUsuario -> total de missões concluídas no período
     */
    public Map<Integer, Integer> contarConcluidasPorUsuario(LocalDate inicio, LocalDate fim) {
        return consultarConcluidas(SQL_CONCLUIDAS
                + "AND um.dt_conclusao >= ? AND um.dt_conclusao <= ? GROUP BY um.id_usuario", inicio, fim);
    }

    /**
     * Grava a data em que o usuário concluiu a missão. Só preenche se ainda
     * estiver vazia, então a primeira conclusão é a que vale.
     *
     * @param idUsuarioMissao id do registro em Usuario_Missao
     * @param data            dia da conclusão
     */
    public void registrarConclusao(int idUsuarioMissao, LocalDate data) {
        String sql = "UPDATE Usuario_Missao SET dt_conclusao = ? "
                + "WHERE id_usuario_missao = ? AND dt_conclusao IS NULL";

        try (Connection con = ConnectionFactory.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(data));
            ps.setInt(2, idUsuarioMissao);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Executa a contagem com uma conexão local (não usa o campo compartilhado). */
    private Map<Integer, Integer> consultarConcluidas(String sql, LocalDate inicio, LocalDate fim) {
        Map<Integer, Integer> totais = new HashMap<>();

        try (Connection con = ConnectionFactory.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (inicio != null && fim != null) {
                ps.setDate(1, Date.valueOf(inicio));
                ps.setDate(2, Date.valueOf(fim));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totais.put(rs.getInt("id_usuario"), rs.getInt("total"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totais;
    }
}