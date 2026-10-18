package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.models.NivelContribuicao;
import br.com.fiap.greenlify.models.PerfilSustentavel;
import br.com.fiap.greenlify.models.UsuarioComum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso a dados (DAO) responsável pelas operações de
 * persistência de {@link PerfilSustentavel} na tabela
 * {@code Perfil_Sustentavel}.
 */
public class PerfilSustentavelDAO {
    private Connection conexao;

    /**
     * Cadastra um novo perfil sustentável, vinculado ao usuário e ao
     * nível de contribuição informados.
     *
     * @param perfil perfil sustentável a ser cadastrado
     */
    public void cadastrar(PerfilSustentavel perfil) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Perfil_Sustentavel (id_perfil, nr_xp, id_usuario, id_nivel) " +
                    "VALUES (?, ?, ?, ?)";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, perfil.getUsuario().getIdUsuario());
            ps.setInt(2, perfil.getXpSustentavel());
            ps.setInt(3, perfil.getUsuario().getIdUsuario());
            ps.setInt(4, perfil.getNivel().getIdNivel());

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
     * Atualiza os dados de um perfil sustentável já cadastrado.
     *
     * @param perfil perfil sustentável com os dados atualizados
     */
    public void alterar(PerfilSustentavel perfil) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE Perfil_Sustentavel SET nr_xp = ?, nr_missoes_concluidas = ?, nr_agua_economizada = ?, nr_energia_economizada = ?, nr_carbono_economizada = ?, id_usuario = ?, id_nivel = ? " +
                    "WHERE id_perfil = ?";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, perfil.getXpSustentavel());
            ps.setInt(2, perfil.getTotalMissoesConcluidas());
            ps.setDouble(3, perfil.getTotalAguaEconomizada());
            ps.setDouble(4, perfil.getTotalEnergiaEconomizada());
            ps.setDouble(5, perfil.getTotalCarbonoEconomizado());
            ps.setInt(6, perfil.getUsuario().getIdUsuario());
            ps.setInt(7, perfil.getNivel().getIdNivel());
            ps.setInt(8, perfil.getIdPerfilSustentavel());

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
     * Remove um perfil sustentável do banco de dados.
     *
     * @param idPerfil identificador do perfil sustentável a ser removido
     */
    public void excluir(int idPerfil) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("DELETE FROM Perfil_Sustentavel WHERE id_perfil = ?");
            ps.setInt(1, idPerfil);

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
     * Busca um perfil sustentável pelo seu identificador, carregando
     * também o usuário e o nível de contribuição associados.
     *
     * @param idPerfil identificador do perfil sustentável
     * @return o perfil sustentável encontrado, ou {@code null} caso não exista
     */
    public PerfilSustentavel buscarPorId(int idPerfil) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        PerfilSustentavel perfil = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Perfil_Sustentavel WHERE id_perfil = ?");
            ps.setInt(1, idPerfil);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                perfil = new PerfilSustentavel();
                perfil.setIdPerfilSustentavel(rs.getInt("id_perfil"));
                perfil.setXpSustentavel(rs.getInt("nr_xp"));
                perfil.setTotalMissoesConcluidas(rs.getInt("nr_missoes_concluidas"));
                perfil.setTotalAguaEconomizada(rs.getDouble("nr_agua_economizada"));
                perfil.setTotalEnergiaEconomizada(rs.getDouble("nr_energia_economizada"));
                perfil.setTotalCarbonoEconomizado(rs.getDouble("nr_carbono_economizada"));

                int idUsuario = rs.getInt("id_usuario");
                UsuarioComumDAO usuarioDAO = new UsuarioComumDAO();
                UsuarioComum usuario = usuarioDAO.buscarPorId(idUsuario);
                perfil.setUsuario(usuario);

                int idNivel = rs.getInt("id_nivel");
                NivelContribuicaoDAO nivelDao = new NivelContribuicaoDAO();
                NivelContribuicao nivel = nivelDao.buscarPorId(idNivel);
                perfil.setNivel(nivel);
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
        return perfil;
    }

    /**
     * Lista todos os perfis sustentáveis cadastrados no banco de dados.
     *
     * @return lista de perfis sustentáveis cadastrados
     */
    public List<PerfilSustentavel> listar() {
        conexao = ConnectionFactory.obterConexao();
        List<PerfilSustentavel> perfis = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Perfil_Sustentavel");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PerfilSustentavel perfil = new PerfilSustentavel();
                perfil.setIdPerfilSustentavel(rs.getInt("id_perfil"));
                perfil.setXpSustentavel(rs.getInt("nr_xp"));
                perfil.setTotalMissoesConcluidas(rs.getInt("nr_missoes_concluidas"));
                perfil.setTotalAguaEconomizada(rs.getDouble("nr_agua_economizada"));
                perfil.setTotalEnergiaEconomizada(rs.getDouble("nr_energia_economizada"));
                perfil.setTotalCarbonoEconomizado(rs.getDouble("nr_carbono_economizada"));

                int idUsuario = rs.getInt("id_usuario");
                UsuarioComumDAO usuarioDao = new UsuarioComumDAO();
                UsuarioComum usuario = usuarioDao.buscarPorId(idUsuario);
                perfil.setUsuario(usuario);

                int idNivel = rs.getInt("id_nivel");
                NivelContribuicaoDAO nivelDao = new NivelContribuicaoDAO();
                NivelContribuicao nivel = nivelDao.buscarPorId(idNivel);
                perfil.setNivel(nivel);

                perfis.add(perfil);
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
        return perfis;
    }
}