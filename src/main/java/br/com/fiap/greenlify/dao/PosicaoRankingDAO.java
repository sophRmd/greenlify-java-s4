package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.models.Grupo;
import br.com.fiap.greenlify.models.PosicaoRanking;
import br.com.fiap.greenlify.models.Ranking;
import br.com.fiap.greenlify.models.UsuarioComum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso a dados (DAO) responsável pelas operações de
 * persistência de {@link PosicaoRanking} na tabela {@code Posicao_Ranking}.
 */
public class PosicaoRankingDAO {
    private Connection conexao;

    /**
     * Cadastra uma nova posição de ranking.
     *
     * @param posicaoRanking posição de ranking a ser cadastrada
     */
    public void cadastrar(PosicaoRanking posicaoRanking) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Posicao_Ranking (id_posicao, nr_posicao, nr_pontuacao_usuario, id_usuario, id_ranking, id_grupo) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, posicaoRanking.getIdPosicao());
            ps.setInt(2, posicaoRanking.getPosicao());
            ps.setInt(3, posicaoRanking.getPontuacao());
            ps.setInt(4, posicaoRanking.getUsuario().getIdUsuario());
            ps.setInt(5, posicaoRanking.getRanking().getIdRanking());
            ps.setInt(6, posicaoRanking.getGrupo().getIdGrupo());

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
     * Atualiza os dados de uma posição de ranking já cadastrada.
     *
     * @param posicaoRanking posição de ranking com os dados atualizados
     */
    public void alterar(PosicaoRanking posicaoRanking) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE Posicao_Ranking SET nr_posicao = ?, nr_pontuacao_usuario = ?, id_usuario = ?, id_ranking = ?, id_grupo = ? " +
                    "WHERE id_posicao = ?";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, posicaoRanking.getPosicao());
            ps.setInt(2, posicaoRanking.getPontuacao());
            ps.setInt(3, posicaoRanking.getUsuario().getIdUsuario());
            ps.setInt(4, posicaoRanking.getRanking().getIdRanking());
            ps.setInt(5, posicaoRanking.getGrupo().getIdGrupo());
            ps.setInt(6, posicaoRanking.getIdPosicao());

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
     * Remove uma posição de ranking do banco de dados.
     *
     * @param idPosicao identificador da posição de ranking a ser removida
     */
    public void excluir(int idPosicao) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("DELETE FROM Posicao_Ranking WHERE id_posicao = ?");
            ps.setInt(1, idPosicao);

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
     * Busca uma posição de ranking pelo seu identificador, carregando
     * também o usuário, o ranking e o grupo associados, quando existentes.
     *
     * @param idPosicao identificador da posição de ranking
     * @return a posição de ranking encontrada, ou {@code null} caso não exista
     */
    public PosicaoRanking buscarPorId(int idPosicao) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        PosicaoRanking posicaoRanking = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Posicao_Ranking WHERE id_posicao = ?");
            ps.setInt(1, idPosicao);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                posicaoRanking = new PosicaoRanking();
                posicaoRanking.setIdPosicao(rs.getInt(1));
                posicaoRanking.setPosicao(rs.getInt(2));
                posicaoRanking.setPontuacao(rs.getInt(3));

                int idUsuario = rs.getInt(4);
                if (idUsuario > 0) {
                    UsuarioComumDAO usuarioDAO = new UsuarioComumDAO();
                    UsuarioComum usuario = usuarioDAO.buscarPorId(idUsuario);
                    posicaoRanking.setUsuario(usuario);
                }

                int idRanking = rs.getInt(5);
                if (idRanking > 0) {
                    RankingDAO rankingDAO = new RankingDAO();
                    Ranking ranking = rankingDAO.buscarPorId(idRanking);
                    posicaoRanking.setRanking(ranking);
                }

                int idGrupo = rs.getInt(6);
                if (idGrupo > 0) {
                    GrupoDAO grupoDAO = new GrupoDAO();
                    Grupo grupo = grupoDAO.buscarPorId(idGrupo);
                    posicaoRanking.setGrupo(grupo);
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
        return posicaoRanking;
    }

    /**
     * Lista todas as posições de ranking cadastradas no banco de dados.
     *
     * @return lista de posições de ranking cadastradas
     */
    public List<PosicaoRanking> listar() {
        conexao = ConnectionFactory.obterConexao();
        List<PosicaoRanking> lista = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Posicao_Ranking");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PosicaoRanking posicaoRanking = new PosicaoRanking();
                posicaoRanking.setIdPosicao(rs.getInt(1));
                posicaoRanking.setPosicao(rs.getInt(2));
                posicaoRanking.setPontuacao(rs.getInt(3));

                int idUsuario = rs.getInt(4);
                if (idUsuario > 0) {
                    UsuarioComumDAO usuarioDao = new UsuarioComumDAO();
                    UsuarioComum usuario = usuarioDao.buscarPorId(idUsuario);
                    posicaoRanking.setUsuario(usuario);
                }

                int idRanking = rs.getInt(5);
                if (idRanking > 0) {
                    RankingDAO rankingDAO = new RankingDAO();
                    Ranking ranking = rankingDAO.buscarPorId(idRanking);
                    posicaoRanking.setRanking(ranking);
                }

                int idGrupo = rs.getInt(6);
                if (idGrupo > 0) {
                    GrupoDAO grupoDAO = new GrupoDAO();
                    Grupo grupo = grupoDAO.buscarPorId(idGrupo);
                    posicaoRanking.setGrupo(grupo);
                }

                lista.add(posicaoRanking);
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
     * Obtém o identificador do ranking de grupos mais recente já
     * registrado, utilizado para filtrar a listagem do ranking atual.
     *
     * @return o identificador do ranking de grupos mais recente, ou
     *         {@code 0} caso não exista nenhum registro
     */
    public int obterUltimoIdRankingGrupo() {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id = 0;

        try {
            String sql = "SELECT MAX(id_ranking) FROM Posicao_Ranking WHERE id_grupo IS NOT NULL";
            ps = conexao.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt(1);
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
        return id;
    }

    /**
     * Obtém o identificador do ranking de usuários mais recente já
     * registrado, utilizado para filtrar a listagem do ranking atual.
     *
     * @return o identificador do ranking de usuários mais recente, ou
     *         {@code 0} caso não exista nenhum registro
     */
    public int obterUltimoIdRankingUsuario() {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id = 0;

        try {
            String sql = "SELECT MAX(id_ranking) FROM Posicao_Ranking WHERE id_usuario IS NOT NULL";
            ps = conexao.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt(1);
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
        return id;
    }
}