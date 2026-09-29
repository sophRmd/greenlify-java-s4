package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.models.Carteira;
import br.com.fiap.greenlify.models.UsuarioComum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso a dados (DAO) responsável pelas operações de
 * persistência de {@link Carteira} na tabela {@code Carteira}.
 */
public class CarteiraDAO {
    private Connection conexao;

    /**
     * Cadastra uma nova carteira, vinculada ao usuário informado.
     *
     * @param carteira carteira a ser cadastrada
     */
    public void cadastrar(Carteira carteira) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Carteira (id_carteira, nr_saldo_pontos, vl_pontos_convertidos, id_usuario) " +
                    "VALUES (?, ?, ?, ?)";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, carteira.getUsuario().getIdUsuario());
            ps.setInt(2, carteira.getQtdPontos());
            ps.setDouble(3, carteira.getValorReal());
            ps.setInt(4, carteira.getUsuario().getIdUsuario());

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
     * Atualiza os dados de uma carteira já cadastrada.
     *
     * @param carteira carteira com os dados atualizados
     */
    public void alterar(Carteira carteira) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            if (carteira.getUsuario() != null) {
                String sql = "UPDATE Carteira SET nr_saldo_pontos = ?, vl_pontos_convertidos = ?, id_usuario = ? " +
                        "WHERE id_carteira = ?";
                ps = conexao.prepareStatement(sql);
                ps.setInt(1, carteira.getQtdPontos());
                ps.setDouble(2, carteira.getValorReal());
                ps.setInt(3, carteira.getUsuario().getIdUsuario());
                ps.setInt(4, carteira.getIdCarteira());
            } else {
                String sql = "UPDATE Carteira SET nr_saldo_pontos = ?, vl_pontos_convertidos = ? " +
                        "WHERE id_carteira = ?";
                ps = conexao.prepareStatement(sql);
                ps.setInt(1, carteira.getQtdPontos());
                ps.setDouble(2, carteira.getValorReal());
                ps.setInt(3, carteira.getIdCarteira());
            }

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
     * Remove uma carteira do banco de dados.
     *
     * @param idCarteira identificador da carteira a ser removida
     */
    public void excluir(int idCarteira) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("DELETE FROM Carteira WHERE id_carteira = ?");
            ps.setInt(1, idCarteira);

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
     * Busca uma carteira pelo seu identificador.
     *
     * @param idCarteira identificador da carteira
     * @return a carteira encontrada, ou {@code null} caso não exista
     */
    public Carteira buscarPorId(int idCarteira) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        Carteira carteira = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Carteira WHERE id_carteira = ?");
            ps.setInt(1, idCarteira);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                carteira = new Carteira();
                carteira.setIdCarteira(rs.getInt(1));
                carteira.setQtdPontos(rs.getInt(2));
                carteira.setValorReal(rs.getDouble(3));

                int idUsuario = rs.getInt("id_usuario");
                if (idUsuario > 0) {
                    UsuarioComumDAO usuarioDao = new UsuarioComumDAO();
                    UsuarioComum usuario = usuarioDao.buscarPorId(idUsuario);
                    carteira.setUsuario(usuario);
                }
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
        return carteira;
    }

    /**
     * Lista todas as carteiras cadastradas no banco de dados.
     *
     * @return lista de carteiras cadastradas
     */
    public List<Carteira> listar() {
        conexao = ConnectionFactory.obterConexao();
        List<Carteira> lista = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Carteira");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Carteira carteira = new Carteira();
                carteira.setIdCarteira(rs.getInt("id_carteira"));
                carteira.setQtdPontos(rs.getInt("nr_saldo_pontos"));
                carteira.setValorReal(rs.getDouble("vl_pontos_convertidos"));

                int idUsuario = rs.getInt("id_usuario");
                if (idUsuario > 0) {
                    UsuarioComumDAO usuarioDao = new UsuarioComumDAO();
                    UsuarioComum usuario = usuarioDao.buscarPorId(idUsuario);
                    carteira.setUsuario(usuario);
                }

                lista.add(carteira);
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

    /**
     * Busca a carteira associada a um determinado usuário.
     *
     * @param idUsuario identificador do usuário
     * @return a carteira do usuário, ou {@code null} caso não exista
     */
    public Carteira buscarPorIdUsuario(int idUsuario) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Carteira carteira = null;

        try {
            String sql = "SELECT * FROM Carteira WHERE id_usuario = ?";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();

            if (rs.next()) {
                carteira = new Carteira();
                carteira.setIdCarteira(rs.getInt("id_carteira"));
                carteira.setQtdPontos(rs.getInt("nr_saldo_pontos"));
                carteira.setValorReal(rs.getDouble("vl_pontos_convertidos"));

                UsuarioComumDAO usuarioDao = new UsuarioComumDAO();
                UsuarioComum usuario = usuarioDao.buscarPorId(idUsuario);
                carteira.setUsuario(usuario);
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
        return carteira;
    }

    /**
     * Atualiza apenas o saldo de pontos e o respectivo valor convertido em
     * reais de uma carteira, sem alterar o vínculo com o usuário.
     *
     * @param carteira carteira com o novo saldo de pontos
     */
    public void atualizarSaldoPontos(Carteira carteira) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE Carteira SET nr_saldo_pontos = ?, vl_pontos_convertidos = ? " +
                    "WHERE id_carteira = ?";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, carteira.getQtdPontos());
            ps.setDouble(2, carteira.getValorReal());
            ps.setInt(3, carteira.getIdCarteira());

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
}