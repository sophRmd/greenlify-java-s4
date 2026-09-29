package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.models.Resgate;

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
    private Connection conexao;

    /**
     * Cadastra um novo resgate, vinculando um usuário a uma recompensa
     * em uma determinada data de concessão.
     *
     * @param resgate dados do resgate a ser gravado
     */
    public void cadastrar(Resgate resgate) {
        conexao = ConnectionFactory.obterConexao();
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
        conexao = ConnectionFactory.obterConexao();
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
        conexao = ConnectionFactory.obterConexao();
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

    /**
     * Busca um resgate pelo id, já carregando o usuário e a recompensa relacionados.
     *
     * @param id id do resgate procurado
     * @return o resgate encontrado (com usuário e recompensa preenchidos), ou {@code null} se não existir
     */
    public Resgate buscarPorId(int id) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Resgate resgate = null;

        try {
            String sql = "SELECT * FROM Resgate WHERE id_resgate = ?";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                resgate = new Resgate();
                resgate.setIdResgate(rs.getInt(1));

                Date dtConcessao = rs.getDate(2);
                if (dtConcessao != null) {
                    resgate.setDataConcessao(dtConcessao.toLocalDate());
                }

                int idUsuario = rs.getInt(3);
                UsuarioComumDAO usuarioDAO = new UsuarioComumDAO();
                resgate.setUsuario(usuarioDAO.buscarPorId(idUsuario));

                int idRecompensa = rs.getInt(4);
                RecompensaDAO recompensaDAO = new RecompensaDAO();
                resgate.setRecompensa(recompensaDAO.buscarPorId(idRecompensa));
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

        return resgate;
    }

    /**
     * Retorna todos os resgates cadastrados, cada um já com o usuário
     * e a recompensa correspondentes carregados.
     *
     * @return lista de resgates (pode vir vazia, nunca nula)
     */
    public List<Resgate> listar() {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Resgate> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM Resgate";
            ps = conexao.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Resgate resgate = new Resgate();
                resgate.setIdResgate(rs.getInt(1));

                Date dtConcessao = rs.getDate(2);
                if (dtConcessao != null) {
                    resgate.setDataConcessao(dtConcessao.toLocalDate());
                }

                int idUsuario = rs.getInt(3);
                UsuarioComumDAO usuarioDAO = new UsuarioComumDAO();
                resgate.setUsuario(usuarioDAO.buscarPorId(idUsuario));

                int idRecompensa = rs.getInt(4);
                RecompensaDAO recompensaDAO = new RecompensaDAO();
                resgate.setRecompensa(recompensaDAO.buscarPorId(idRecompensa));

                lista.add(resgate);
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

        return lista;
    }
}