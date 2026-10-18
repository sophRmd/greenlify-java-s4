package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.enums.StatusRankingEnum;
import br.com.fiap.greenlify.enums.TipoRankingEnum;
import br.com.fiap.greenlify.models.Ranking;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso a dados (DAO) responsável pelas operações de
 * persistência de {@link Ranking} na tabela {@code Ranking}.
 */
public class RankingDAO {
    private Connection conexao;

    private static final String COLUNAS = "id_ranking, ds_tipo, dt_inicio, dt_fim, ds_status";

    /**
     * Cadastra um novo período de ranking e preenche o id gerado pelo banco
     * (coluna IDENTITY) no próprio objeto. Se a gravação falhar, o id permanece 0.
     *
     * @param ranking ranking a ser cadastrado
     */
    public void cadastrar(Ranking ranking) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Ranking (ds_tipo, dt_inicio, dt_fim, ds_status) VALUES (?, ?, ?, ?)";
            ps = conexao.prepareStatement(sql, new String[]{"ID_RANKING"});
            ps.setString(1, String.valueOf(ranking.getTipo()));
            ps.setDate(2, Date.valueOf(ranking.getDataInicio()));
            ps.setDate(3, Date.valueOf(ranking.getDataFim()));
            ps.setString(4, String.valueOf(ranking.getStatus()));

            ps.executeUpdate();

            ResultSet rsGerado = ps.getGeneratedKeys();
            if (rsGerado.next()) {
                ranking.setIdRanking(rsGerado.getInt(1));
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
     * Atualiza os dados de um período de ranking existente.
     *
     * @param ranking ranking com os novos dados (o id define qual será atualizado)
     */
    public void alterar(Ranking ranking) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE Ranking SET ds_tipo = ?, dt_inicio = ?, dt_fim = ?, ds_status = ? WHERE id_ranking = ?";
            ps = conexao.prepareStatement(sql);
            ps.setString(1, String.valueOf(ranking.getTipo()));
            ps.setDate(2, Date.valueOf(ranking.getDataInicio()));
            ps.setDate(3, Date.valueOf(ranking.getDataFim()));
            ps.setString(4, String.valueOf(ranking.getStatus()));
            ps.setInt(5, ranking.getIdRanking());

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
     * Exclui um período de ranking pelo seu identificador.
     *
     * @param idRanking identificador do ranking a ser excluído
     */
    public void excluir(int idRanking) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("DELETE FROM Ranking WHERE id_ranking = ?");
            ps.setInt(1, idRanking);
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
     * Busca um período de ranking pelo seu identificador.
     *
     * @param idRanking identificador do ranking
     * @return o ranking encontrado, ou {@code null} caso não exista
     */
    public Ranking buscarPorId(int idRanking) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        Ranking ranking = null;

        try {
            ps = conexao.prepareStatement("SELECT " + COLUNAS + " FROM Ranking WHERE id_ranking = ?");
            ps.setInt(1, idRanking);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ranking = mapear(rs);
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
        return ranking;
    }

    /**
     * Lista todos os períodos de ranking cadastrados.
     *
     * @return lista de rankings (pode vir vazia, nunca nula)
     */
    public List<Ranking> listar() {
        conexao = ConnectionFactory.obterConexao();
        List<Ranking> lista = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT " + COLUNAS + " FROM Ranking ORDER BY id_ranking");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapear(rs));
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

    /** Monta o ranking a partir da linha atual (colunas lidas pelo nome). */
    private Ranking mapear(ResultSet rs) throws SQLException {
        Ranking ranking = new Ranking();
        ranking.setIdRanking(rs.getInt("id_ranking"));
        ranking.setTipo(TipoRankingEnum.valueOf(rs.getString("ds_tipo")));
        if (rs.getDate("dt_inicio") != null) {
            ranking.setDataInicio(rs.getDate("dt_inicio").toLocalDate());
        }
        if (rs.getDate("dt_fim") != null) {
            ranking.setDataFim(rs.getDate("dt_fim").toLocalDate());
        }
        ranking.setStatus(StatusRankingEnum.valueOf(rs.getString("ds_status")));
        return ranking;
    }
}
