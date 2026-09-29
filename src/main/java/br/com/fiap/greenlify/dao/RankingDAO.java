package br.com.fiap.greenlify.dao;

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

    /**
     * Cadastra um novo período de ranking.
     *
     * @param ranking ranking a ser cadastrado
     */
    public void cadastrar(Ranking ranking) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Ranking (id_ranking, dt_inicio, dt_fim) VALUES (?, ?, ?)";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, ranking.getIdRanking());
            ps.setDate(2, Date.valueOf(ranking.getDataInicio()));
            ps.setDate(3, Date.valueOf(ranking.getDataFim()));

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
     * Atualiza os dados de um período de ranking já cadastrado.
     *
     * @param ranking ranking com os dados atualizados
     */
    public void alterar(Ranking ranking) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE Ranking SET dt_inicio = ?, dt_fim = ? WHERE id_ranking = ?";
            ps = conexao.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(ranking.getDataInicio()));
            ps.setDate(2, Date.valueOf(ranking.getDataFim()));
            ps.setInt(3, ranking.getIdRanking());

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
     * Remove um período de ranking do banco de dados.
     *
     * @param idRanking identificador do ranking a ser removido
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
            ps = conexao.prepareStatement("SELECT * FROM Ranking WHERE id_ranking = ?");
            ps.setInt(1, idRanking);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ranking = new Ranking();
                ranking.setIdRanking(rs.getInt(1));

                if (rs.getDate(2) != null) {
                    ranking.setDataInicio(rs.getDate(2).toLocalDate());
                }
                if (rs.getDate(3) != null) {
                    ranking.setDataFim(rs.getDate(3).toLocalDate());
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
        return ranking;
    }

    /**
     * Lista todos os períodos de ranking cadastrados no banco de dados.
     *
     * @return lista de rankings cadastrados
     */
    public List<Ranking> listar() {
        conexao = ConnectionFactory.obterConexao();
        List<Ranking> rankings = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Ranking");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idRanking = rs.getInt(1);
                Ranking ranking = buscarPorId(idRanking);
                rankings.add(ranking);
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
        return rankings;
    }
}