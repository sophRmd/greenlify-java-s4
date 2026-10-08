package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.models.MissaoMensal;
import br.com.fiap.greenlify.models.Recompensa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pelo acesso a dados da entidade Recompensa.
 * Contém as operações básicas de CRUD (cadastrar, alterar, excluir, buscar e listar)
 * feitas diretamente via JDBC, sem uso de ORM.
 */
public class RecompensaDAO {
    private Connection conexao;

    /**
     * Insere uma nova recompensa no banco de dados.
     *
     * @param recompensa objeto contendo os dados da recompensa a ser cadastrada
     */
    public void cadastrar(Recompensa recompensa) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Recompensa (id_recompensa, nr_bonus_percentual, nr_pontos_bonus, ds_recompensa) " +
                    "VALUES (?, ?, ?)";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, recompensa.getIdRecompensa());
            ps.setDouble(2, recompensa.getBonusPercentual());
            ps.setInt(3, recompensa.getBonusPontos());
            ps.setString(4, recompensa.getDescricao());

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
     * Atualiza os dados de uma recompensa já existente, com base no id informado.
     *
     * @param recompensa objeto com os dados atualizados (o id é usado como referência)
     */
    public void alterar(Recompensa recompensa) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE Recompensa SET nr_bonus_percentual = ?, nr_bonus_pontos, ds_recompensa = ? " +
                    "WHERE id_recompensa = ?";
            ps = conexao.prepareStatement(sql);
            ps.setDouble(1, recompensa.getBonusPercentual());
            ps.setInt(2, recompensa.getBonusPercentual());
            ps.setString(3, recompensa.getDescricao());
            ps.setInt(4, recompensa.getIdRecompensa());

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
     * Remove uma recompensa do banco a partir do seu id.
     *
     * @param idRecompensa id da recompensa a ser excluída
     */
    public void excluir(int idRecompensa) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("DELETE FROM Recompensa WHERE id_recompensa = ?");
            ps.setInt(1, idRecompensa);
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
     * Busca uma recompensa pelo id.
     *
     * @param idRecompensa id da recompensa procurada
     * @return a recompensa encontrada, ou {@code null} caso não exista nenhuma com esse id
     */
    public Recompensa buscarPorId(int idRecompensa) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        Recompensa recompensa = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Recompensa WHERE id_recompensa = ?");
            ps.setInt(1, idRecompensa);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                recompensa = new Recompensa();
                recompensa.setIdRecompensa(rs.getInt(1));
                recompensa.setBonusPercentual(rs.getInt(2));
                recompensa.setDescricao(rs.getString(3));
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
        return recompensa;
    }

    /**
     * Lista todas as recompensas cadastradas.
     * Internamente busca primeiro os ids e depois reaproveita {@link #buscarPorId(int)}
     * para montar cada objeto completo.
     *
     * @return lista com todas as recompensas cadastradas (pode vir vazia, nunca nula)
     */
    public List<Recompensa> listar() {
        conexao = ConnectionFactory.obterConexao();
        List<Recompensa> recompensas = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Recompensa");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idRecompensa = rs.getInt(1);
                Recompensa recompensa = buscarPorId(idRecompensa);
                recompensas.add(recompensa);
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
        return recompensas;
    }
}