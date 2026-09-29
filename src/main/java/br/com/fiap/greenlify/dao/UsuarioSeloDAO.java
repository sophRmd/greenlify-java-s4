package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.models.Selo;
import br.com.fiap.greenlify.models.UsuarioComum;
import br.com.fiap.greenlify.models.UsuarioSelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da entidade UsuarioSelo, que representa a coleta de um selo
 * por um usuário em uma determinada data.
 */
public class UsuarioSeloDAO {
    private Connection conexao;

    /**
     * Registra a coleta de um selo por um usuário.
     *
     * @param usuarioSelo dados da coleta (usuário, selo e data)
     */
    public void cadastrar(UsuarioSelo usuarioSelo) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Usuario_Selo (dt_coleta, id_usuario, id_selo) VALUES (?, ?, ?)";
            ps = conexao.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(usuarioSelo.getDataColeta()));
            ps.setInt(2, usuarioSelo.getUsuario().getIdUsuario());
            ps.setInt(3, usuarioSelo.getSelo().getIdSelo());

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
     * Atualiza um registro de coleta de selo já existente.
     *
     * @param usuarioSelo dados atualizados (o id é usado como referência)
     */
    public void alterar(UsuarioSelo usuarioSelo) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE Usuario_Selo SET dt_coleta = ?, id_usuario = ?, id_selo = ? WHERE id_usuario_selo = ?";
            ps = conexao.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(usuarioSelo.getDataColeta()));
            ps.setInt(2, usuarioSelo.getUsuario().getIdUsuario());
            ps.setInt(3, usuarioSelo.getSelo().getIdSelo());
            ps.setInt(4, usuarioSelo.getIdUsuarioSelo());

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
     * Remove um registro de coleta de selo pelo id.
     *
     * @param idUsuarioSelo id do registro a ser excluído
     */
    public void excluir(int idUsuarioSelo) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("DELETE FROM Usuario_Selo WHERE id_usuario_selo = ?");
            ps.setInt(1, idUsuarioSelo);

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
     * Busca um registro de coleta de selo pelo seu id, já carregando
     * o usuário e o selo relacionados.
     *
     * @param idUsuarioSelo id do registro procurado
     * @return o registro encontrado, ou {@code null} se não existir
     */
    public UsuarioSelo buscarPorId(int idUsuarioSelo) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        UsuarioSelo usuarioSelo = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Usuario_Selo WHERE id_usuario_selo = ?");
            ps.setInt(1, idUsuarioSelo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                usuarioSelo = new UsuarioSelo();
                usuarioSelo.setIdUsuarioSelo(rs.getInt(1));
                usuarioSelo.setDataColeta(rs.getDate(2).toLocalDate());

                int idUsuario = rs.getInt(3);
                UsuarioComumDAO usuarioDAO = new UsuarioComumDAO();
                UsuarioComum usuario = usuarioDAO.buscarPorId(idUsuario);
                usuarioSelo.setUsuario(usuario);

                int idSelo = rs.getInt(4);
                SeloDAO seloDAO = new SeloDAO();
                Selo selo = seloDAO.buscarPorId(idSelo);
                usuarioSelo.setSelo(selo);

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
        return usuarioSelo;
    }

    /**
     * Lista os selos coletados por um usuário específico.
     * Diferente de {@link #buscarPorId}, aqui o objeto usuário não é carregado
     * dentro de cada UsuarioSelo (só o selo), já que o id já foi informado.
     *
     * @param idUsuario id do usuário cujos selos serão listados
     * @return lista de coletas de selo desse usuário (pode vir vazia, nunca nula)
     */
    public List<UsuarioSelo> buscarPorIdUsuario(int idUsuario) {
        conexao = ConnectionFactory.obterConexao();
        List<UsuarioSelo> lista = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Usuario_Selo WHERE id_usuario = ?");
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UsuarioSelo usuarioSelo = new UsuarioSelo();
                usuarioSelo.setIdUsuarioSelo(rs.getInt(1));
                usuarioSelo.setDataColeta(rs.getDate(2).toLocalDate());

                int idSelo = rs.getInt(3);
                SeloDAO seloDAO = new SeloDAO();
                Selo selo = seloDAO.buscarPorId(idSelo);
                usuarioSelo.setSelo(selo);

                lista.add(usuarioSelo);
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
     * Lista todos os registros de coleta de selo cadastrados, cada um já
     * com o usuário e o selo correspondentes carregados.
     *
     * @return lista completa de coletas (pode vir vazia, nunca nula)
     */
    public List<UsuarioSelo> listar() {
        conexao = ConnectionFactory.obterConexao();
        List<UsuarioSelo> lista = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Usuario_Selo");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UsuarioSelo usuarioSelo = new UsuarioSelo();
                usuarioSelo.setIdUsuarioSelo(rs.getInt(1));
                usuarioSelo.setDataColeta(rs.getDate(2).toLocalDate());

                int idUsuario = rs.getInt(3);
                UsuarioComumDAO usuarioDao = new UsuarioComumDAO();
                UsuarioComum usuario = usuarioDao.buscarPorId(idUsuario);
                usuarioSelo.setUsuario(usuario);

                int idSelo = rs.getInt(4);
                SeloDAO seloDAO = new SeloDAO();
                Selo selo = seloDAO.buscarPorId(idSelo);
                usuarioSelo.setSelo(selo);

                lista.add(usuarioSelo);
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