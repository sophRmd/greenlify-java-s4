package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.models.Grupo;
import br.com.fiap.greenlify.models.PosicaoRanking;
import br.com.fiap.greenlify.models.Ranking;
import br.com.fiap.greenlify.models.UsuarioComum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe de acesso a dados (DAO) responsável pelas operações de
 * persistência de {@link PosicaoRanking} na tabela {@code Posicao_Ranking}.
 *
 * <p>Cada posição pertence a um usuário OU a um grupo (nunca aos dois), como
 * exige o CHECK da tabela.</p>
 */
public class PosicaoRankingDAO {
    private Connection conexao;

    private static final String COLUNAS =
            "id_posicao, nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo";

    /**
     * Cadastra uma nova posição de ranking e preenche o id gerado pelo banco.
     *
     * @param posicaoRanking posição de ranking a ser cadastrada (com usuário OU grupo)
     */
    public void cadastrar(PosicaoRanking posicaoRanking) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Posicao_Ranking (nr_posicao, nr_pontuacao, id_ranking, id_usuario, id_grupo) "
                    + "VALUES (?, ?, ?, ?, ?)";
            ps = conexao.prepareStatement(sql, new String[]{"ID_POSICAO"});
            ps.setInt(1, posicaoRanking.getPosicao());
            ps.setInt(2, posicaoRanking.getPontuacao());
            ps.setInt(3, posicaoRanking.getRanking().getIdRanking());
            definirInteiroOuNulo(ps, 4, posicaoRanking.getUsuario() != null
                    ? posicaoRanking.getUsuario().getIdUsuario() : null);
            definirInteiroOuNulo(ps, 5, posicaoRanking.getGrupo() != null
                    ? posicaoRanking.getGrupo().getIdGrupo() : null);

            ps.executeUpdate();

            ResultSet rsGerado = ps.getGeneratedKeys();
            if (rsGerado.next()) {
                posicaoRanking.setIdPosicao(rsGerado.getInt(1));
            }
            rsGerado.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            fechar();
        }
    }

    /**
     * Exclui uma posição de ranking pelo seu identificador.
     *
     * @param idPosicao identificador da posição
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
            fechar();
        }
    }

    /**
     * Exclui todas as posições de um ranking (usado ao recalcular o ranking).
     *
     * @param idRanking identificador do ranking
     */
    public void excluirPorRanking(int idRanking) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("DELETE FROM Posicao_Ranking WHERE id_ranking = ?");
            ps.setInt(1, idRanking);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            fechar();
        }
    }

    /**
     * Busca uma posição de ranking pelo identificador.
     *
     * @param idPosicao identificador da posição
     * @return a posição encontrada, ou {@code null} se não existir
     */
    public PosicaoRanking buscarPorId(int idPosicao) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        PosicaoRanking posicaoRanking = null;

        try {
            ps = conexao.prepareStatement("SELECT " + COLUNAS + " FROM Posicao_Ranking WHERE id_posicao = ?");
            ps.setInt(1, idPosicao);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                posicaoRanking = mapear(rs, new HashMap<>());
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            fechar();
        }
        return posicaoRanking;
    }

    /**
     * Lista todas as posições de todos os rankings.
     *
     * @return lista de posições (pode vir vazia, nunca nula)
     */
    public List<PosicaoRanking> listar() {
        return consultar("SELECT " + COLUNAS + " FROM Posicao_Ranking ORDER BY id_ranking, nr_posicao", null);
    }

    /**
     * Lista as posições de um único ranking, em ordem de colocação.
     *
     * @param idRanking identificador do ranking
     * @return lista de posições (pode vir vazia, nunca nula)
     */
    public List<PosicaoRanking> listarPorRanking(int idRanking) {
        return consultar("SELECT " + COLUNAS + " FROM Posicao_Ranking WHERE id_ranking = ? ORDER BY nr_posicao",
                idRanking);
    }

    /**
     * Obtém o id do ranking de grupos mais recente que já tem posições.
     *
     * @return o id, ou 0 se não houver
     */
    public int obterUltimoIdRankingGrupo() {
        return obterMaximo("SELECT MAX(id_ranking) FROM Posicao_Ranking WHERE id_grupo IS NOT NULL");
    }

    /**
     * Obtém o id do ranking de usuários mais recente que já tem posições.
     *
     * @return o id, ou 0 se não houver
     */
    public int obterUltimoIdRankingUsuario() {
        return obterMaximo("SELECT MAX(id_ranking) FROM Posicao_Ranking WHERE id_usuario IS NOT NULL");
    }

    private int obterMaximo(String sql) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id = 0;

        try {
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
            fechar();
        }
        return id;
    }

    private List<PosicaoRanking> consultar(String sql, Integer idRanking) {
        conexao = ConnectionFactory.obterConexao();
        List<PosicaoRanking> lista = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement(sql);
            if (idRanking != null) {
                ps.setInt(1, idRanking);
            }
            ResultSet rs = ps.executeQuery();

            Map<Integer, Ranking> cacheRankings = new HashMap<>();
            while (rs.next()) {
                lista.add(mapear(rs, cacheRankings));
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            fechar();
        }
        return lista;
    }

    /**
     * Monta a posição a partir da linha atual (colunas lidas pelo nome). O
     * ranking é carregado uma única vez por consulta (cache por id).
     */
    private PosicaoRanking mapear(ResultSet rs, Map<Integer, Ranking> cacheRankings) throws SQLException {
        PosicaoRanking posicaoRanking = new PosicaoRanking();
        posicaoRanking.setIdPosicao(rs.getInt("id_posicao"));
        posicaoRanking.setPosicao(rs.getInt("nr_posicao"));
        posicaoRanking.setPontuacao(rs.getInt("nr_pontuacao"));

        int idRanking = rs.getInt("id_ranking");
        if (idRanking > 0) {
            if (!cacheRankings.containsKey(idRanking)) {
                cacheRankings.put(idRanking, new RankingDAO().buscarPorId(idRanking));
            }
            posicaoRanking.setRanking(cacheRankings.get(idRanking));
        }

        int idUsuario = rs.getInt("id_usuario");
        if (idUsuario > 0) {
            UsuarioComumDAO usuarioDAO = new UsuarioComumDAO();
            UsuarioComum usuario = usuarioDAO.buscarPorId(idUsuario);
            posicaoRanking.setUsuario(usuario);
        }

        int idGrupo = rs.getInt("id_grupo");
        if (idGrupo > 0) {
            GrupoDAO grupoDAO = new GrupoDAO();
            Grupo grupo = grupoDAO.buscarPorId(idGrupo);
            posicaoRanking.setGrupo(grupo);
        }
        return posicaoRanking;
    }

    private void definirInteiroOuNulo(PreparedStatement ps, int indice, Integer valor) throws SQLException {
        if (valor == null) {
            ps.setNull(indice, Types.INTEGER);
        } else {
            ps.setInt(indice, valor);
        }
    }

    private void fechar() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
