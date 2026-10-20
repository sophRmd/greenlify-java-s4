package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.enums.StatusContaEnum;
import br.com.fiap.greenlify.models.Grupo;
import br.com.fiap.greenlify.models.Recompensa;
import br.com.fiap.greenlify.models.Resgate;
import br.com.fiap.greenlify.models.UsuarioComum;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável pela persistência da entidade Resgate, que representa
 * a concessão de uma recompensa a um usuário.
 */
public class ResgateDAO {

    /**
     * Cadastra um novo resgate, vinculando um usuário a uma recompensa
     * em uma determinada data de concessão.
     *
     * @param resgate dados do resgate a ser gravado
     */
    public void cadastrar(Resgate resgate) {
        Connection conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Resgate (dt_concessao, id_usuario, id_recompensa)" +
                    "VALUES (?, ?, ?)";
            ps = conexao.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(resgate.getDataConcessao()));
            ps.setInt(2, resgate.getUsuario().getIdUsuario());
            ps.setInt(3, resgate.getRecompensa().getIdRecompensa());

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
     * Atualiza um resgate já existente.
     *
     * @param resgate dados atualizados do resgate (o id é usado para localizar o registro)
     */
    public void alterar(Resgate resgate) {
        Connection conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE Resgate SET dt_concessao = ?, id_usuario = ?, id_recompensa = ? " +
                    "WHERE id_resgate = ?";
            ps = conexao.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(resgate.getDataConcessao()));
            ps.setInt(2, resgate.getUsuario().getIdUsuario());
            ps.setInt(3, resgate.getRecompensa().getIdRecompensa());
            ps.setInt(4, resgate.getIdResgate());

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
     * Exclui um resgate pelo id.
     *
     * @param id id do resgate a ser removido
     */
    public void excluir(int id) {
        Connection conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM Resgate WHERE id_resgate = ?";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, id);

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

    /** Resgate + Recompensa (rc_) + Usuário (u_) em uma única consulta. */
    private static final String SELECT_COMPLETO =
            "SELECT r.id_resgate, r.dt_concessao, "
                    + "       rc.id_recompensa AS rc_id, rc.nr_bonus_percentual AS rc_percentual, "
                    + "       rc.nr_pontos_bonus AS rc_pontos, rc.ds_recompensa AS rc_descricao, "
                    + "       u.id_usuario AS u_id, u.nm_usuario AS u_nome, u.ds_cpf AS u_cpf, "
                    + "       u.dt_nascimento AS u_nascimento, u.ds_email AS u_email, u.ds_senha AS u_senha, "
                    + "       u.nr_nivel_acesso AS u_nivel, u.ds_status_conta AS u_status, u.id_grupo AS u_grupo "
                    + "FROM Resgate r "
                    + "LEFT JOIN Recompensa rc ON rc.id_recompensa = r.id_recompensa "
                    + "LEFT JOIN Usuario u ON u.id_usuario = r.id_usuario AND u.nr_nivel_acesso = 0 ";

    /**
     * Busca um resgate pelo id, já carregando o usuário e a recompensa relacionados,
     * em uma única consulta.
     *
     * @param id id do resgate procurado
     * @return o resgate encontrado (com usuário e recompensa preenchidos), ou {@code null} se não existir
     */
    public Resgate buscarPorId(int id) {
        Resgate resgate = null;

        try (Connection con = ConnectionFactory.obterConexao();
             PreparedStatement ps = con.prepareStatement(SELECT_COMPLETO + "WHERE r.id_resgate = ?")) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resgate = mapear(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resgate;
    }

    /**
     * Retorna todos os resgates cadastrados, cada um já com o usuário
     * e a recompensa correspondentes carregados, em uma única consulta.
     *
     * @return lista de resgates (pode vir vazia, nunca nula)
     */
    public List<Resgate> listar() {
        List<Resgate> lista = new ArrayList<>();

        try (Connection con = ConnectionFactory.obterConexao();
             PreparedStatement ps = con.prepareStatement(SELECT_COMPLETO + "ORDER BY r.id_resgate");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Resgate mapear(ResultSet rs) throws SQLException {
        Resgate resgate = new Resgate();
        resgate.setIdResgate(rs.getInt("id_resgate"));

        Date dtConcessao = rs.getDate("dt_concessao");
        if (dtConcessao != null) {
            resgate.setDataConcessao(dtConcessao.toLocalDate());
        }

        resgate.setUsuario(mapearUsuario(rs));
        resgate.setRecompensa(mapearRecompensa(rs));
        return resgate;
    }

    /** Monta a recompensa a partir das colunas rc_*; devolve null se não existir. */
    private Recompensa mapearRecompensa(ResultSet rs) throws SQLException {
        int idRecompensa = rs.getInt("rc_id");
        if (rs.wasNull()) {
            return null;
        }
        Recompensa recompensa = new Recompensa();
        recompensa.setIdRecompensa(idRecompensa);
        recompensa.setBonusPercentual(rs.getInt("rc_percentual"));
        recompensa.setBonusPontos(rs.getInt("rc_pontos"));
        recompensa.setDescricao(rs.getString("rc_descricao"));
        return recompensa;
    }

    /** Monta o usuário comum a partir das colunas u_*; devolve null se não existir. */
    private UsuarioComum mapearUsuario(ResultSet rs) throws SQLException {
        int idUsuario = rs.getInt("u_id");
        if (rs.wasNull()) {
            return null;
        }
        UsuarioComum usuario = new UsuarioComum();
        usuario.setIdUsuario(idUsuario);
        usuario.setNome(rs.getString("u_nome"));
        usuario.setCpf(rs.getString("u_cpf"));
        usuario.setDataNascimento(rs.getDate("u_nascimento").toLocalDate());
        usuario.setEmail(rs.getString("u_email"));
        usuario.setSenha(rs.getString("u_senha"));
        usuario.setNivelAcesso(rs.getInt("u_nivel"));
        usuario.setStatusConta(StatusContaEnum.valueOf(rs.getString("u_status")));

        // Igual ao UsuarioComumDAO.buscarPorId: só guarda o id do grupo do usuário.
        int idGrupo = rs.getInt("u_grupo");
        if (idGrupo > 0) {
            Grupo grupo = new Grupo();
            grupo.setIdGrupo(idGrupo);
            usuario.setGrupo(grupo);
        }
        return usuario;
    }
}