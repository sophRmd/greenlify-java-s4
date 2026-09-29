package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.models.ContaLuz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso a dados (DAO) responsável pelas operações de
 * persistência de {@link ContaLuz} na tabela {@code Conta_Luz}.
 */
public class ContaLuzDAO {
    private Connection conexao;

    /**
     * Cadastra uma nova conta de luz.
     *
     * @param conta conta de luz a ser cadastrada
     */
    public void cadastrar(ContaLuz conta) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Conta_Luz (id_conta_luz, ds_codigo_barra, vl_conta) VALUES (?, ?, ?)";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, conta.getIdContaLuz());
            ps.setString(2, conta.getCodigoBarras());
            ps.setDouble(3, conta.getValorConta());

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
     * Atualiza os dados de uma conta de luz já cadastrada.
     *
     * @param conta conta de luz com os dados atualizados
     */
    public void alterar(ContaLuz conta) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE Conta_Luz SET ds_codigo_barra = ?, vl_conta = ? WHERE id_conta_luz = ?";
            ps = conexao.prepareStatement(sql);
            ps.setString(1, conta.getCodigoBarras());
            ps.setDouble(2, conta.getValorConta());
            ps.setInt(3, conta.getIdContaLuz());

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
     * Remove uma conta de luz do banco de dados.
     *
     * @param idContaLuz identificador da conta de luz a ser removida
     */
    public void excluir(int idContaLuz) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("DELETE FROM Conta_Luz WHERE id_conta_luz = ?");
            ps.setInt(1, idContaLuz);
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
     * Busca uma conta de luz pelo seu identificador.
     *
     * @param idContaLuz identificador da conta de luz
     * @return a conta de luz encontrada, ou {@code null} caso não exista
     */
    public ContaLuz buscarPorId(int idContaLuz) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        ContaLuz conta = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Conta_Luz WHERE id_conta_luz = ?");
            ps.setInt(1, idContaLuz);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                conta = new ContaLuz();
                conta.setIdContaLuz(rs.getInt(1));
                conta.setCodigoBarras(rs.getString(2));
                conta.setValorConta(rs.getDouble(3));
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
        return conta;
    }

    /**
     * Lista todas as contas de luz cadastradas no banco de dados.
     *
     * @return lista de contas de luz cadastradas
     */
    public List<ContaLuz> listar() {
        conexao = ConnectionFactory.obterConexao();
        List<ContaLuz> contas = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Conta_Luz");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ContaLuz conta = new ContaLuz();
                conta.setIdContaLuz(rs.getInt(1));
                conta.setCodigoBarras(rs.getString(2));
                conta.setValorConta(rs.getDouble(3));

                contas.add(conta);
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
        return contas;
    }
}