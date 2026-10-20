package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.models.Grupo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso a dados (DAO) responsável pelas operações de
 * persistência de {@link Grupo} na tabela {@code Grupo}.
 */
public class GrupoDAO {
    /**
     * Cadastra um novo grupo, preenchendo o identificador gerado
     * automaticamente pelo banco de dados no próprio objeto.
     *
     * @param grupo grupo a ser cadastrado
     */
    public void cadastrar(Grupo grupo) {
        Connection conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Grupo (nm_grupo, dt_criacao, nr_status_grupo) " +
                    "VALUES (?, ?, ?)";
            ps = conexao.prepareStatement(sql, new String[]{"ID_GRUPO"});
            ps.setString(1, grupo.getNome());
            ps.setDate(2, Date.valueOf(String.valueOf(grupo.getDataCriacao())));
            ps.setInt(3, grupo.getStatusGrupo());

            ps.executeUpdate();

            ResultSet rsGerado = ps.getGeneratedKeys();
            if (rsGerado.next()) {
                grupo.setIdGrupo(rsGerado.getInt(1));
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
     * Atualiza os dados de um grupo já cadastrado.
     *
     * @param grupo grupo com os dados atualizados
     */
    public void alterar(Grupo grupo) {
        Connection conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE Grupo SET nm_grupo = ?, dt_criacao = ?, nr_usuarios = ?, nr_status_grupo = ? " +
                    "WHERE id_grupo = ?";
            ps = conexao.prepareStatement(sql);
            ps.setString(1, grupo.getNome());
            ps.setDate(2, Date.valueOf(String.valueOf(grupo.getDataCriacao())));
            ps.setInt(3, grupo.getQtdUsuarios());
            ps.setInt(4, grupo.getStatusGrupo());
            ps.setInt(5, grupo.getIdGrupo());

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
     * Remove um grupo do banco de dados.
     *
     * @param idGrupo identificador do grupo a ser removido
     */
    public void excluir(int idGrupo) {
        Connection conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("DELETE FROM Grupo WHERE id_grupo = ?");
            ps.setInt(1, idGrupo);

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
     * Busca um grupo pelo seu identificador.
     *
     * @param idGrupo identificador do grupo
     * @return o grupo encontrado, ou {@code null} caso não exista
     */
    public Grupo buscarPorId(int idGrupo) {
        Connection conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        Grupo grupo = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Grupo WHERE id_grupo = ?");
            ps.setInt(1, idGrupo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                grupo = new Grupo();
                grupo.setIdGrupo(rs.getInt(1));
                grupo.setNome(rs.getString(2));
                grupo.setDataCriacao(rs.getDate(3).toLocalDate());
                grupo.setQtdUsuarios(rs.getInt(4));
                grupo.setStatusGrupo(rs.getInt(5));
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
        return grupo;
    }

    /**
     * Lista todos os grupos cadastrados no banco de dados.
     *
     * @return lista de grupos cadastrados
     */
    public List<Grupo> listar() {
        Connection conexao = ConnectionFactory.obterConexao();
        List<Grupo> lista = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Grupo");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Grupo grupo = new Grupo();
                grupo.setIdGrupo(rs.getInt(1));
                grupo.setNome(rs.getString(2));
                grupo.setDataCriacao(rs.getDate(3).toLocalDate());
                grupo.setQtdUsuarios(rs.getInt(4));
                grupo.setStatusGrupo(rs.getInt(5));

                lista.add(grupo);
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